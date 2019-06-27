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

package com.opentadam.ui.order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusUpdMenu;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Bonuses;
import com.opentadam.network.rest.Capabilities;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.ResultSendOrders;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.creating_an_order.rest_froute.RestFroute;
import com.opentadam.ui.registration.V2KeyNumber;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;
import com.opentadam.utils.UtilitesDataClient;
import com.opentadam.view.V2FcostView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.opentadam.Injector.getClientData;


public class V2FBonuses extends BaseFr {

    @InjectView(R.id.clear_bonus)
    ImageView clearBonus;

    @InjectView(R.id.edit_bonus)
    TextView editBonus;
    @InjectView(R.id.min_bonus)
    TextView minBonus;

    @InjectView(R.id.max_bonus)
    TextView maxBonus;
    @InjectView(R.id.cont_bonus_period)
    LinearLayout contBonusPeriod;

    @InjectView(R.id.frame_blok_period)
    FrameLayout frameBlokPeriod;
    @InjectView(R.id.clear_bonuse_min_max)
    ImageView clearBonuseMinMax;
    @InjectView(R.id.frame_blok_min_max)
    FrameLayout frameBlokMinMax;
    @InjectView(R.id.edit_add_bonus)
    EditText editAddBonus;
    @InjectView(R.id.set_bonuses)
    TextView setBonuses;
    //

    @InjectView(R.id.minus_button)
    ImageView minusButton;
    @InjectView(R.id.plus_button)
    ImageView plusButton;
    private V2FcostView costView;
    private boolean mEditing = false;
    private boolean isRoute;
    private String balanceBString;

    private String bonusMinus = "0";
    private String balanceBonusesString = "0";
    private float optBonus = 0f;
    private float optionsCapabilityMin;
    private float optionsCapabilityMax;

    private float balanceBonuses;
    private boolean isBlockSendServers = false;
    private String currencyShort;

    public V2FBonuses() {
    }

    public static Fragment newInstance(long idRoute, boolean isRoute) {
        return new V2FBonuses().withViewId(R.layout.v2f_bonuses)
                .withArgument("idRoute", idRoute)
                .withArgument("isRoute", isRoute);
    }

    private void hideBlokStep() {
        frameBlokMinMax.setVisibility(View.VISIBLE);
        frameBlokPeriod.setVisibility(View.GONE);
        V2KeyNumber v2KeyNumber = (V2KeyNumber) getChildFragmentManager()
                .findFragmentById(R.id.keyboard_number);
        v2KeyNumber.initEdit(editAddBonus, false);
    }

    private void showBlokStep() {
        frameBlokMinMax.setVisibility(View.GONE);

        frameBlokPeriod.setVisibility(View.VISIBLE);
    }

    private void getBonusClient() {

        RESTConnect restConnect = Injector.getRC();


        // redmine.hivecompany.ru/issues/11663
        // LatLng latLng = Injector.getSettingsStore().getLatLngTarifDef();

        LatLng latLng = Injector.getClientData().getMarkerLocation();


        restConnect.getBonusClient(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
                //   Bonuses bonuses = apiResponse.bonuses;
                //    bonuses.balance = new BigDecimal("32.50");
                mRoute.bonuses = apiResponse.bonuses;

                App.bus.post(new BusUpdMenu());
                initBody();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)
            return;
        currencyShort = " " + Injector.getWorkSettings().getCurrencyShort();
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        isRoute = getArguments().getBoolean("isRoute");

        TintIcons.tintImageViewBrend(minusButton);
        TintIcons.tintImageViewBrend(plusButton);

        costView = (V2FcostView) getChildFragmentManager().findFragmentById(R.id.value_bonus);

        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getBonusClient();


    }

    @OnClick(R.id.edit_add_bonus)
    public void onEdit() {

        editAddBonus.setSelection(0, editAddBonus.getText().length() - currencyShort.length());
        editAddBonus.setCursorVisible(false);
    }


    private void getBonusesData(Bonuses bonusesClient) {
        if (bonusesClient == null)
            return;


        Capabilities capabilities = bonusesClient.capabilities;

        if (capabilities == null)
            return;

        balanceBonuses = bonusesClient.gefBalance();
        balanceBonusesString = bonusesClient.gefBalanceString();
        costView.initUI(balanceBonusesString);

        String typeCapabilities = capabilities.type;

        if ("min-max".equals(typeCapabilities)) {

            hideBlokStep();
            optionsCapabilityMin = capabilities.min;
            float maxCapabilities = capabilities.max;

            optBonus = maxCapabilities; //https://redmine.hivecompany.ru/issues/11656 Math.min(balanceBonuses, maxCapabilities);
            optionsCapabilityMax = optBonus;

            setValueButtonMinMax();

            preloadEditBonus();

            bonusMinus = BigDecimal.valueOf(balanceBonuses - optBonus).
                    setScale(UtilitesDataClient.getScale(balanceBonuses - optBonus)
                            , RoundingMode.HALF_UP).toString();

        } else if ("options".equals(typeCapabilities)) {
            showBlokStep();
            Float[] optionsCapabilities = capabilities.options;
            if (optionsCapabilities != null) {

                int max = optionsCapabilities.length;
                optBonus = optionsCapabilities[max - 1];
/*         https://redmine.hivecompany.ru/issues/11656


      optBonus = 0f;
                for (int i = 0; i < max; i++) {

                    optBonus = optionsCapabilities[i];

                    if (balanceBonuses < optBonus) {

                        optBonus = i == 0 ? 0f : optionsCapabilities[i - 1];
                        break;
                    }
                }*/
                //////
                preloadEditBonus();
                //////////////////

                bonusMinus = BigDecimal.valueOf(balanceBonuses - optBonus).
                        setScale(UtilitesDataClient.getScale(balanceBonuses - optBonus), RoundingMode.HALF_UP)
                        .toString();


                optionsCapabilityMin = optionsCapabilities[0];
                optionsCapabilityMax = optBonus;

                setValueButtonMinMax();
            }

        }
    }

    private void setValueButtonMinMax() {
        String minTxt = getStringFormatedFloatCost(optionsCapabilityMin, getResources().getString(R.string.min_bonus_format));


        minBonus.setText(minTxt);

        String maxTxt = getStringFormatedFloatCost(optBonus, getString(R.string.max_bonus_format));


        maxBonus.setText(maxTxt);
    }

    private String getStringFormatedFloatCost(float val, String javaMinFormatString) {

        String s = val + "";
        if (s.contains(".")) {
            String[] split = s.split("\\.");
            if (split[1].length() == 1) {

                s += "0";
            }
        }

        String minTxt = String.format(javaMinFormatString,
                s, currencyShort);
        return minTxt.replace(".0" + currencyShort, currencyShort)
                .replace(".00" + currencyShort, currencyShort);
    }

    private void preloadEditBonus() {
        if (isRoute) {
            TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
            if (mRoute != null && mRoute.bunusClient != null)
                balanceBString = mRoute.bunusClient;
        } else {
            Float usedBonuses = getAWork().getOrderInfo().usedBonuses;
            if (usedBonuses == null)
                usedBonuses = 0f;
            balanceBString = BigDecimal.valueOf(usedBonuses).
                    setScale(UtilitesDataClient.getScale(usedBonuses), RoundingMode.HALF_UP).toString();
        }

        clearBonuseMinMax.setVisibility("0".equals(balanceBString) ? View.INVISIBLE : View.VISIBLE);
        balanceBString = balanceBString == null ? "0" : balanceBString;

        String textEdit = balanceBString + currencyShort;
        editAddBonus.setText(Utilites.getModerateUICost(textEdit, editAddBonus));

        editBonus.setText(Utilites.getModerateUICost(textEdit, editBonus));
    }

    @OnClick(R.id.plus_button)
    public void onPlusButton() {
        float valueEditBonus = getFloatEditBonus();
        if (valueEditBonus < optionsCapabilityMax) {
            valueEditBonus += optionsCapabilityMin;
            setValueBonus(valueEditBonus);
        }

    }

    @OnClick(R.id.min_bonus)
    public void setMinBonusValue() {
        float val = 0;
        if (balanceBonuses >= optionsCapabilityMin) {
            val = optionsCapabilityMin;
        }

        setValueBonus(val);
    }

    @OnClick(R.id.max_bonus)
    public void setMaxBonusValue() {
        float val = 0;
        if (balanceBonuses >= optionsCapabilityMax) {
            val = optionsCapabilityMax;
        } else if (balanceBonuses >= optionsCapabilityMin) {
            val = balanceBonuses;
        }

        setValueBonus(val);

    }

    private void setValueBonus(float valueEditBonus) {
        if (!isVisible() || editBonus == null || editAddBonus == null)
            return;

        String valueEditBonusString = BigDecimal.valueOf(valueEditBonus).
                setScale(2, RoundingMode.HALF_UP).toString();

        String text = valueEditBonusString + currencyShort;
        SpannableString moderateUICost = Utilites.getModerateUICost(text, editBonus);
        SpannableString moderateUICost1 = Utilites.getModerateUICost(text, editAddBonus);
        if (moderateUICost == null || moderateUICost1 == null)
            return;


        editBonus.setText(moderateUICost);
        editAddBonus.setText(moderateUICost1);

        setGlobBonus(valueEditBonus);
        if (isRoute) {
            TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
            mRoute.bunusClient = valueEditBonusString;
        }
    }

    private void setGlobBonus(Float valueEditBonus) {
        if (valueEditBonus == null)
            return;
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        mRoute.valueEditBonus = valueEditBonus;

        String v = BigDecimal.valueOf(balanceBonuses - valueEditBonus).
                setScale(2, RoundingMode.HALF_UP).toString();


        //    costView.initUI(v);

    }

    private float getFloatEditBonus() {
        String s = editBonus.getText()
                .toString();

        String replace = s.replace(currencyShort, "");
        if ("".equals(replace))
            return 0;

        return Float.parseFloat(replace);
    }

    @OnClick(R.id.minus_button)
    public void onMinusButton() {

        float valueEditBonus = getFloatEditBonus();

        if (valueEditBonus > optionsCapabilityMin) {
            valueEditBonus -= optionsCapabilityMin;
            setValueBonus(valueEditBonus);
        } else if (valueEditBonus == optionsCapabilityMin) {
            onClearBonus();
        }
    }

    private void initBody() {
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();

        if (isRoute) {
            getBonusesData(mRoute.bonuses);
            if (mRoute.bunusClient != null) {
                onButtonBonus();
            }
        } else {

            Float usedBonuses = getAWork().getOrderInfo().usedBonuses;
            getBonusesData(mRoute.bonuses);
            if (usedBonuses != null) {
                onButtonBonus();
            }
        }


        String text = 0 + currencyShort;
        editBonus.setText(Utilites.getModerateUICost(text, editBonus));
        editAddBonus.setText(Utilites.getModerateUICost(text, editAddBonus));
        onButtonBonus();
    }

    @OnTextChanged(R.id.edit_bonus)
    public void editTextChangetBonus() {
        if (!isVisible() || setBonuses == null || setBonuses == null)
            return;

        float floatEditBonus = getFloatEditBonus();
        setGlobBonus(floatEditBonus);
        setBonuses
                .setText(getString(
                        floatEditBonus > 0
                                ? R.string.set_bonuses_to_route : R.string.value_def_no_bonuses));

    }

    @OnTextChanged(R.id.edit_add_bonus)
    public void editTextChangetAddBonus() {
        if (!isVisible() || editBonus == null || editAddBonus == null)
            return;

        // вычисляем число символов при изменении текста
        if (!mEditing) {
            mEditing = true;


            String text = editAddBonus.getText().
                    toString().trim();


            if (text.contains(currencyShort))
                text = text.replace(currencyShort, "");

            float v1 = "".equals(text) ? 0 : Float.parseFloat(text);
            setBonuses
                    .setText(getString(
                            v1 > 0
                                    ? R.string.set_bonuses_to_route : R.string.value_def_no_bonuses));
            if (text.contains(".")) {
                String[] arr = text.split("\\.");


                if (arr.length > 1 && arr[1].length() > 2) {

                    text = text.substring(0, text.length() - 1);
                }
            }

            if (text.length() == 0)
                text = "0";
            if (text.length() > 1 && text.startsWith("0", 0)
                    && !".".equals(Character.toString(text.charAt(1)))) {
                text = text.substring(1, text.length());
            }
            float v = 0f;
            if ("".equals(text))
                text = "0";
            try {
                v = Float.parseFloat(text);
            } catch (Exception e) {

            }

            clearBonuseMinMax.setVisibility("0".equals(text) ? View.INVISIBLE : View.VISIBLE);

/*
https://redmine.hivecompany.ru/issues/11656
            if (optionsCapabilityMax < v){
                setValueBonus(optionsCapabilityMax);
                editAddBonus.setSelection(editAddBonus.getText().length() - currencyShort.length());
                mEditing = false;
                return;
            }*/
            //  text = String.valueOf(optionsCapabilityMax);


            TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
            mRoute.bunusClient = "0".equals(text) ? null : text;
            String textEdit = text + currencyShort;

            setGlobBonus(Float.parseFloat(text));
            editAddBonus.setText(Utilites.getModerateUICost(textEdit, editAddBonus));
            editAddBonus.setSelection(editAddBonus.getText().length() - currencyShort.length());
            mEditing = false;
        }
    }

    private void onButtonBonus() {

        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();

        if (mRoute == null)
            return;

        if ("0".equals(mRoute.bunusClient) || mRoute.bunusClient == null) {

            bonusMinus = BigDecimal.valueOf(balanceBonuses).
                    setScale(UtilitesDataClient.getScale(balanceBonuses), RoundingMode.HALF_UP).toString();

        }


        //  costView.initUI(bonusMinus);

        mRoute.bunusClient = balanceBString;
        setGlobBonus(mRoute.valueEditBonus);

    }

    @OnClick({R.id.clear_bonus, R.id.clear_bonuse_min_max})
    public void onClearBonus() {
        costView.initUI(balanceBonusesString);

        editBonus.setText(Utilites.getModerateUICost(0 + currencyShort, editBonus));
        String text = 0 + currencyShort;
        editAddBonus.setText(Utilites.getModerateUICost(text, editAddBonus));
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        if (!isRoute) {

            mRoute.valueEditBonus = 0f;
        } else {

            mRoute.bunusClient = null;
            mRoute.valueEditBonus = null;
        }
    }


    @Override
    public void onDestroyView() {
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        if (isRoute) {
            if (mRoute.bunusClient == null && mRoute.valueEditBonus == null) {
                mRoute.bunusClient = "0.00";
                mRoute.valueEditBonus = 0.0f;
            }
            getAWork().showV3FRoute();
        }
        return true;
    }

    @OnClick(R.id.v2_bask)
    public void onv2Bask() {
        getAWork().showV3FRoute();
    }

    @OnClick(R.id.set_bonuses)
    public void sendServersBody() {
        if (getAWork() == null || isBlockSendServers)
            return;
        isBlockSendServers = true;
        final CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        final TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        if (editBonus.getText().length() == 0 || mRoute.valueEditBonus == null || mRoute.valueEditBonus == 0) {
            isBlockSendServers = false;
            float val = 0;
            setValueBonus(val);
            sendToservers(createRequest, mRoute);
            return;
        }

        if (mRoute.valueEditBonus < optionsCapabilityMin) {
            isBlockSendServers = false;
            float val = 0;
            if (balanceBonuses >= optionsCapabilityMin) {
                val = optionsCapabilityMin;
            }


            alert(String.format(getString(R.string.fb_error_min), getStringFormatedFloatCost(optionsCapabilityMin, "%1$s%2$s")));
            setValueBonus(val);
            return;
        }

        if (mRoute.valueEditBonus > optionsCapabilityMax) {
            isBlockSendServers = false;
            float val = 0;
            if (balanceBonuses >= optionsCapabilityMax) {
                val = optionsCapabilityMax;
            } else if (balanceBonuses >= optionsCapabilityMin) {
                val = balanceBonuses;
            }
            alert(String.format(getString(R.string.fb_error_max), getStringFormatedFloatCost(optionsCapabilityMax, "%1$s%2$s")));
            setValueBonus(val);
            return;
        }

        if (mRoute.valueEditBonus > optionsCapabilityMin
                &&  mRoute.valueEditBonus > balanceBonuses
                && mRoute.valueEditBonus < optionsCapabilityMax) {
            isBlockSendServers = false;
            alert(String.format(getString(R.string.fb_error_max_min), getStringFormatedFloatCost(balanceBonuses, "%1$s%2$s")));
            setValueBonus(balanceBonuses);
            return;
        }
        sendToservers(createRequest, mRoute);

    }

    private void sendToservers(final CreateRequest createRequest, final TempObjectUIMRoute mRoute) {
        if(!isVisible())
            return;

        if (getAWork().sizeCurrentOrders >= App.app.hashBC.limitNumberActiveOrders){
            alert(getString(R.string.rest_limit_order_info));
            getAWork().showV2FShortOrdersPrivate();
            return;
        }
        getAWork().showWorkProgress();
        setBonuses.setAlpha(0.7f);
        setBonuses.setClickable(false);
        setBonuses.setEnabled(false);

        RESTConnect restConnect = Injector.getRC();

        SendCreateRequest sendCreateRequest = RestFroute.newInstance()
                .getSendCreateRequest(createRequest, mRoute);

        restConnect.sendOrders(sendCreateRequest, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                mRoute.valueAddCost = 0;
                ResultSendOrders resultSendOrders = apiResponse.resultSendOrders;
                GpsPosition gpsPosition = createRequest.getRouteLocation().get(0);

                Injector.getClientData().initMemoryListNamesCost();

                long orderId = resultSendOrders.id;

                getAWork().showFSearchCar(gpsPosition, orderId);
            }
        });
    }
}
