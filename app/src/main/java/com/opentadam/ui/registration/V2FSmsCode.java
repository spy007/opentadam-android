/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.ui.registration;


import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusPreloadMenu;
import com.opentadam.data.DialogClient;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Confirmed;
import com.opentadam.ui.BaseFr;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class V2FSmsCode extends BaseFr {
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    private static final String PHONE = "phone";
    private static final String ID = "id";
    private static final String PHONE_INFO = "phone_info";
    private static final String IS_PROFIL = "isProfil";
    private static final String IS_RESTART = "isRestart";
    @InjectView(R.id.reg_edit_code)
    EditText regEditCode;
    @InjectView(R.id.sms_value_phone)
    TextView smsValuePhone;
    @InjectView(R.id.reg_del_code)
    ImageView regDelCode;
    @InjectView(R.id.v2_title)
    TextView v2Title;
    @InjectView(R.id.reg_get_code)
    TextView regGetCode;
    @InjectView(R.id.reg_get_call)
    TextView regGetCall;
    private boolean isRestart = false;
    private boolean isProfil = false;
    private String mPhone;
    private String mPhoneInfo;
    private long mId;
    private MaskedWatcher mMaskedWatcher;
    private String mMask;
    private boolean isSendToServersSMS = false;
    private boolean isSendToServersCALL = false;
    private Timer timerRefresher;
    private int periodBlockSMS = 59;
    private int periodBlockCALL = 59;
    private boolean isSendCode = false;

    public static Fragment newInstance(String phone, long id, String value, boolean isProfil, boolean isRestart) {
        return new V2FSmsCode().withViewId(R.layout.f_sms_code)
                .withArgument(PHONE, phone)
                .withArgument(ID, id)
                .withArgument(PHONE_INFO, value)
                .withArgument(IS_PROFIL, isProfil)
                .withArgument(IS_RESTART, isRestart);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getArguments() != null) {
            mPhone = getArguments().getString(PHONE);
            mId = getArguments().getLong(ID);
            mPhoneInfo = getArguments().getString(PHONE_INFO);
            isProfil = getArguments().getBoolean(IS_PROFIL, false);
            isRestart = getArguments().getBoolean(IS_RESTART, false);
        }
    }

    @OnTextChanged(R.id.reg_edit_code)
    public void afterTextChangedEditCode(Editable editable) {
        int length = editable.length();
        if (length == 4)
            validateCode();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        V2KeyNumber v2KeyNumber = (V2KeyNumber) getChildFragmentManager()
                .findFragmentById(R.id.keyboard_number);
        v2KeyNumber.initEdit(regEditCode, true);

        startTime();
        v2Title.setText(R.string.title_set_code);
        mMask = "####";
        setMaskedWatcher(mMask);

        smsValuePhone.setText(mPhone);

    }

    private void validateCode() {

        if (regEditCode == null || regEditCode.getText() == null)
            return;

        String mCodeUser = regEditCode.getText().toString();
        if (mCodeUser.length() != mMask.length()) {
            DialogClient.alertInfo(getResources().getString(R.string.error_code), getAWork());
            return;
        }

        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/

        if (!isSendCode) {
            isSendCode = true;
            restConnect.sendToServers(mId, mCodeUser, new IGetApiResponse() {
                @Override
                public void getApiResponse(ApiResponse apiResponse) {

                    if (!isVisible())
                        return;

                    if (Constants.PATH_REG_CODE.equals(apiResponse.path)) {
                        if (apiResponse.error != null) {
                            DialogClient.alertInfo(getResources().getString(R.string.error_val_code)
                                    , getAWork());
                            isSendCode = false;
                            regDelCode();
                        } else {

                            Confirmed confirmed = apiResponse.confirmed;
                            if (confirmed == null)
                                return;

                            long id = confirmed.id;
                            String key = confirmed.key;


                            Injector.getSettingsStore().writeString(Constants.REG_PHONE_CLIENT, mPhoneInfo);
                            Injector.getSettingsStore().writeLong(Constants.REG_ID_CLIENT, id);
                            Injector.getSettingsStore().writeString(Constants.REG_KEY_CLIENT, key);
                            refreshedToken();
                            getAWork().setObjectRestoryV3Route(null);

                            App.bus.post(new BusPreloadMenu());

                         //   refreshedToken();
                            getAWork().setObjectRestoryV3Route(null);
                            getAWork().restartAll();

                        }
                    }
                }
            });

        }
    }

    private void refreshedToken() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }

 /*               Log.d("MyFirebaseIIDService", "Refreshed token1: "
                        + FirebaseInstanceId.getInstance().getToken());
*/
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getAWork()
                        , new OnSuccessListener<InstanceIdResult>() {

                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String mToken = instanceIdResult.getToken();
                                Log.e("MyFirebaseIIDService", "Refreshed token: " + mToken);
                                String readRegKey = Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null);
                                RESTConnect rc = Injector.getRC();
                                if (readRegKey == null)
                                    return;
                                rc.sendRegistrationToServer(mToken);
                            }
                        });

            }
        });
    }

    private void startTime() {
        if (timerRefresher == null)
            timerRefresher = new Timer();

        timerRefresher.schedule(new TimerTask() {
            @Override
            public void run() {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (timerRefresher != null)
                            refresh();
                    }
                });
            }
        }, 1000, 1000);
    }

    void refresh() {
        if (isSendToServersSMS || isSendToServersCALL) {
            periodBlockSMS--;
            periodBlockCALL--;
            regGetCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            regGetCode.setText(String.format("00:%s", periodBlockSMS < 10 ? "0" + periodBlockSMS : "" + periodBlockSMS));
            regGetCall.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            regGetCall.setText(String.format("00:%s", periodBlockCALL < 10 ? "0" + periodBlockSMS : "" + periodBlockSMS));
        }
        if (periodBlockSMS == 0) {
            regGetCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            regGetCode.setText(getString(R.string.request_code));
            regGetCall.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            regGetCall.setText(getString(R.string.button_get_call));
            periodBlockSMS = 59;
            isSendToServersSMS = false;
            isSendToServersCALL = false;
        }

    }

    @OnClick(R.id.reg_get_code)
    public void onRegGetCode() {
        if (isSendToServersCALL || isSendToServersSMS)
            return;

        DialogClient.alertInfo(getString(R.string.send_ok_code), getAWork());
        isSendToServersSMS = true;
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.replaceCode(mId, "sms");
    }

    @OnClick(R.id.reg_get_call)
    public void onRegGetCall() {
        if (isSendToServersCALL || isSendToServersSMS)
            return;

        DialogClient.alertInfo(getString(R.string.send_ok), getAWork());
        isSendToServersCALL = true;
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.replaceCode(mId, "voice");

    }

    @OnClick(R.id.reg_get_in)
    public void onRegGetIn() {
        hideKeyboard(regEditCode);
        validateCode();
    }

    @OnClick(R.id.reg_del_code)
    public void regDelCode() {
        regEditCode.setText("");
    }

    private void setMaskedWatcher(String mask) {
        if (mMaskedWatcher != null)
            regEditCode.removeTextChangedListener(mMaskedWatcher);
        mMaskedWatcher = new MaskedWatcher(mask);
        regEditCode.addTextChangedListener(mMaskedWatcher);
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV2FRegistration(isProfil, isRestart);
        return true;
    }

    @OnClick(R.id.v2_bask)
    public void onv2Bask() {
        getAWork().showV2FRegistration(isProfil, isRestart);
    }

    @Override
    public void onDestroyView() {
        if (timerRefresher != null) {
            timerRefresher.cancel();
            timerRefresher = null;
        }
        getAWork().hideWorkProgress();
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideKeyboard(regEditCode);
        super.onDestroyView();
    }


}
