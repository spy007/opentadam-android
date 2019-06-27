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


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.Countries;
import com.opentadam.data.DialogClient;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Country;
import com.opentadam.network.rest.SubmitRequest;
import com.opentadam.network.rest.Submitted;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;


public class V2FRegistration extends BaseFr {
    private static final String IS_PROFIL = "isProfil";
    private static final String IS_RESTART = "isRestart";
    @InjectView(R.id.reg_del_phone)
    ImageView regDelPhone;
    @InjectView(R.id.reg_edit_phone)
    EditText regEditPhone;
    ///
    @InjectView(R.id.reg_prefix_phone)
    TextView regPrefixPhone;
    @InjectView(R.id.v2_title)
    TextView v2Title;
    @InjectView(R.id.reg_send_phone)
    TextView regSendPhone;
    @InjectView(R.id.cont_reg)
    LinearLayout contReg;
    @InjectView(R.id.login_country_select)
    FrameLayout loginCountrySelect;
    @InjectView(R.id.cont_edit)
    FrameLayout contEdit;
    @InjectView(R.id.login_countries_list)
    LinearLayout loginCountriesList;
    @InjectView(R.id.login_flag_icon)
    ImageView flagIcon;
    @InjectView(R.id.sub_info_conf)
    TextView infoAgreement;
    @InjectView(R.id.info_conf_two_sub)
    TextView infoPolicyPrivacy;
    @InjectView(R.id.freg_body)
    View fregBody;
    @InjectView(R.id.freg_progress)
    com.opentadam.ui_payemnts_metods.ProgressView progressView;
    private String phoneUser;
    private MaskedWatcher maskedWatcher;
    private boolean isProfil = false;
    private boolean isRestart = false;
    private String mMask;
    private String titleDef;
    private V2KeyNumber v2KeyNumber;

    public static Fragment newInstance(boolean isProfil, boolean isRestart) {
        return new V2FRegistration().withViewId(R.layout.f_registration)
                .withArgument(IS_PROFIL, isProfil)
                .withArgument(IS_RESTART, isRestart);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getArguments() != null) {
            isProfil = getArguments().getBoolean(IS_PROFIL, false);
            isRestart = getArguments().getBoolean(IS_RESTART, false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bodyUI();
    }

    @InjectView(R.id.login_flag_icon)
    View loginFlagIcon;
    @InjectView(R.id.login_flag_select)
    View loginFlagSelect;


    private void bodyUI() {
        loginFlagIcon.setVisibility(Injector.getCountryList() == null ? View.INVISIBLE : View.VISIBLE);
        loginFlagSelect.setVisibility(Injector.getCountryList() == null ? View.INVISIBLE : View.VISIBLE);
        v2KeyNumber = (V2KeyNumber) getChildFragmentManager()
                .findFragmentById(R.id.keyboard_number);

        v2KeyNumber.initEdit(regEditPhone, true);

        titleDef = getString(isProfil ? R.string.navigation_phone :
                R.string.registration_title);

        v2Title.setText(titleDef);

        String textStart = getString(R.string.value_info_conf)
                + " ";
        String textUrl = getString(R.string.sub_info_conf);
        String textFin = " " + getString(R.string.sub_info_conf_dop);

        SpannableStringBuilder builder = initSpannableStringBuilder(textStart, textUrl, textFin);

        ///////
        infoAgreement.setText(builder, TextView.BufferType.SPANNABLE);
        //    infoAgreement.setText(str);

        textStart = getString(R.string.freg_sub_policy_info)
                + " ";
        textUrl = getString(R.string.value_info_conf_two_sub);

        builder = initSpannableStringBuilder(textStart, textUrl, null);
        infoPolicyPrivacy.setText(builder, TextView.BufferType.SPANNABLE);

        initUI();
    }

    @NonNull
    private SpannableStringBuilder initSpannableStringBuilder(String textStart
            , String textUrl, String textFin) {
        if (textUrl == null) {
            return new SpannableStringBuilder("");
        }
        Typeface font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                , "fonts/Roboto/Roboto-Bold.ttf");
        Typeface font1 = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                , "fonts/Roboto/Roboto-Light.ttf");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString redSpannable = new SpannableString(textStart);
        redSpannable.setSpan(new CustomTypefaceSpan(font1)
                , 0, textStart.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        redSpannable.setSpan(new ForegroundColorSpan(initColor(R.color.text_default_color))
                , 0, textStart.length(), 0);
        builder.append(redSpannable);

        ///////
        if (textUrl == null) {
            return builder;
        }

        SpannableString whiteSpannable = new SpannableString(textUrl);
        whiteSpannable.setSpan(new ForegroundColorSpan(initColor(R.color.colorPrimary))
                , 0, textUrl.length(), 0);


        whiteSpannable.setSpan(new CustomTypefaceSpan(font)
                , 0, textUrl.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.append(whiteSpannable);
        ////////
        if (textFin == null) {
            return builder;
        }
        SpannableString blueSpannable = new SpannableString(textFin);


        blueSpannable.setSpan(new CustomTypefaceSpan(font1)
                , 0, textFin.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        blueSpannable.setSpan(new ForegroundColorSpan(initColor(R.color.text_default_color))
                , 0, textFin.length(), 0);
        builder.append(blueSpannable);
        return builder;
    }

    private String[] getCountriesList() {
        List<Country> countryList = Injector.getCountryList();
        if (countryList == null) {
            countryList = new ArrayList<>();
        }
        int size = countryList.size();
        String[] countriesList = new String[size];
        for (int i = 0; i < size; i++) {
            Country country = countryList.get(i);
            countriesList[i] = country.isoCode;
        }

        return countriesList;
    }

    private void initUI() {
        String countries = Countries.getRegCountries(getCountriesList());

        Integer countryFlag = Countries.getCountryFlag(countries);

        if (countryFlag != null)
            flagIcon.setImageResource(countryFlag);

        String countryPhonePrefix = Countries.getCountryPhonePrefix(countries);
        regPrefixPhone.setText(countryPhonePrefix);
        mMask = Countries.getCountryPhoneMask(countries);
// коррекция отступа от префикса:

        switch (countryPhonePrefix.length()) {
            case 1:
                regEditPhone.setPadding(getPX(76), 0, 0, 0);
                break;
            case 2:
                regEditPhone.setPadding(getPX(86), 0, 0, 0);
                break;
            case 3:
                regEditPhone.setPadding(getPX(96), 0, 0, 0);
                break;
            case 4:
                regEditPhone.setPadding(getPX(110), 0, 0, 0);
                break;
        }


        String hintMask = mMask.substring(0, mMask.length() - Countries.ADD_MASK.length());

        regEditPhone.setHint(hintMask);
        mMask = mMask.replace("X", "#");
        setMaskedWatcher(mMask);
    }


    @OnClick(R.id.select_prefix)
    public void onSelectCountryClicked() {
        if (Injector.getCountryList() == null)
            return;

        v2KeyNumber.setVisibilityKeyboord(View.GONE);
        loginCountrySelect.setVisibility(View.VISIBLE);
        loginCountriesList.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(loginCountriesList.getContext());

        for (final String countryAcronim : getCountriesList()) {
            LinearLayout countryItem = (LinearLayout) layoutInflater
                    .inflate(R.layout.i_login_country, loginCountriesList, false);
            CountryViewHolder holder = new CountryViewHolder(countryItem);

            Integer countryFlag = Countries.getCountryFlag(countryAcronim);

            if (countryFlag != null)
                holder.iLoginCountryFlag.setImageResource(countryFlag);

            holder.iLoginCountryName.setText(Countries.getCountryName(countryAcronim));
            holder.iLoginCountryPrefix.setText(Countries.getCountryPhonePrefix(countryAcronim));


            loginCountriesList.addView(countryItem);

            countryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Injector.getSettingsStore().writeString("countries", countryAcronim);
                    loginCountrySelect.setVisibility(View.GONE);
                    loginCountriesList.removeAllViews();
                    v2KeyNumber.setVisibilityKeyboord(View.VISIBLE);
                    initUI();
                }
            });

        }
    }

    @OnClick(R.id.sub_info_conf)
    public void subInfoConf() {
        String urlUserAgreement = App.app.hashBC.URL_USER_AGREEMENT;
        getUrl(urlUserAgreement);
    }

    @OnClick(R.id.info_conf_two_sub)
    public void infoConfTwoSub() {
        String urlPolicyPrivacy = App.app.hashBC.URL_POLICY_PRIVACY;
        getUrl(urlPolicyPrivacy);
    }

    private void getUrl(String url) {

        if (url == null)
            return;


        Uri address = Uri.parse(url);
        Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);

        startActivity(Intent
                .createChooser(openlinkIntent, getString(R.string.select_app)));
    }


    private void validatePhone() {

        if (regEditPhone == null
                || mMask == null
                || regEditPhone.getText() == null
                || (!Countries.MASK_DEFAULT.equals(mMask)
                && !(regEditPhone.getText().length() >= mMask.length() - Countries.ADD_MASK.length()))) {
            DialogClient.alertInfo(getResources()
                    .getString(R.string.error_phone), getAWork());
            return;
        }

        phoneUser = regEditPhone.getText().toString().trim();

        getAWork().showWorkProgress();
        String phone = regPrefixPhone.getText() + phoneUser.replaceAll("\\D", "");
        final String value = phone.replace(" ", "");
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/

        SubmitRequest submitRequest = new SubmitRequest(value);
        String rfererrClient = Injector.getSettingsStore().getRefererrClient();
        Log.e("test_referal", "sms файрбах rfererrClient = " + rfererrClient);
        if (rfererrClient != null) {
            submitRequest.referralCode = rfererrClient;
        }

        restConnect.sendPhoneToServers(submitRequest, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                String error = apiResponse.error;
                if (error != null) {
                    DialogClient.alertInfo(getString(R.string.freg_error_phone_servers), getAWork());
                    return;
                }

                if (Constants.PATH_REG_PHONE.equals(apiResponse.path)) {
                    String phone = regPrefixPhone.getText() + phoneUser.replaceAll("\\D", "");
                    final String value = phone.replace(" ", "");
                    Submitted submitted = apiResponse.submitted;
                    long id = submitted.id;
                    getAWork().showV2FSmsCode(regPrefixPhone.getText() + phoneUser, id, value, isProfil, isRestart);
                }
            }
        });
    }


    private void setMaskedWatcher(String mask) {
        if (maskedWatcher != null)
            regEditPhone.removeTextChangedListener(maskedWatcher);
        maskedWatcher = new MaskedWatcher(mask);
        regEditPhone.addTextChangedListener(maskedWatcher);
    }


    @Override
    public void onDestroyView() {
        getAWork().hideWorkProgress();
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onDestroyView();
    }


    @OnClick(R.id.v2_bask)
    public void onv2Bask() {
        if (contReg.getVisibility() == View.GONE) {
            contReg.setVisibility(View.VISIBLE);
            v2Title.setText(titleDef);
        } else if (isProfil)
            getAWork().showFProfil();
        else
            getAWork().closeApp();
    }

    @Override
    public boolean onBackPressed() {
        onv2Bask();
        return true;
    }

    @OnClick(R.id.reg_send_phone)
    public void regSendPhone() {
        hideKeyboard(regEditPhone);
        validatePhone();
    }

    @OnClick(R.id.reg_del_phone)
    public void onDel() {
        regEditPhone.setText("");
    }

}
