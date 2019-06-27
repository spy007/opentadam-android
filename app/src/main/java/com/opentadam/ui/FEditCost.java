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

package com.opentadam.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.UtilitesDataClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;


public class FEditCost extends BaseFr {
    @InjectView(R.id.route_cost_value_prefix)
    public
    TextView valueCostPrefix;
    @InjectView(R.id.route_cost_value)
    TextView routeCostValue;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;
    @InjectView(R.id.currency_short)
    TextView currencyShort;
    @InjectViews({R.id.minus_step
            , R.id.button_add_cost
            , R.id.button_def_cost
            , R.id.ic_minus})
    View viewUI[];
    @InjectViews({R.id.plus_step
            , R.id.ic_plus})
    View viewGoneUI[];
    @InjectView(R.id.minus_step)
    public
    TextView minusStep;
    @InjectView(R.id.button_add_cost)
    TextView buttonAddCost;
    @InjectView(R.id.plus_step)
    public
    TextView plusStep;
    @InjectView(R.id.add_route_cost_value)
    TextView addRouteCostValue;
    @InjectView(R.id.add_route_cost_value_dec)
    TextView addRouteCostValueDec;
    @InjectView(R.id.add_currency_short)
    TextView addCurrencyShort;

    private UIOrder uiOrder;


    public FEditCost() {

    }

    protected float valueAddCost = 0;


    public static Fragment newInstance(UIOrder uiOrder) {
        String uiOrderString = new Gson().toJson(uiOrder);
        return new FEditCost().withViewId(R.layout.v3f_edit_cost)
                .withArgument("uiOrderString", uiOrderString);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preloadUI();
    }

    protected void preloadUI() {
        if (getArguments() == null)

            return;
        String uiOrderString = getArguments().getString("uiOrderString");
        uiOrder = new Gson().fromJson(
                uiOrderString, UIOrder.class);
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();

        this.valueAddCost = mRoute.valueAddCost;

        initUI();
    }

    protected void initUI() {
        float baseAmount = uiOrder.getAmount();

        getBaseSumm(baseAmount);

        String valueType = uiOrder.getValueType();


        if (valueType != null) {
            if (valueType.equals(getString(R.string.tilda))) {
                valueCostPrefix.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
                final LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) valueCostPrefix.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
            }

            valueCostPrefix.setText(valueType);
        }

        float amount = uiOrder.getAmount();
        String stepValue = getStepValue();
        minusStep.setText(stepValue);
        plusStep.setText(stepValue);


        if (this.valueAddCost == 0) {
            TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
            mRoute.fixCost = null;
            setDisabledView();
        } else {
            setEnabledView();
        }


        getArrSumm(amount + this.valueAddCost);
    }


    protected void getBaseSumm(float amount) {

        setVisibleView();
        String textDec = null;
        String textCostValue = BigDecimal.valueOf(amount).
                setScale(UtilitesDataClient.getScale(amount), RoundingMode.HALF_UP).toString();
        // textCostValue+=".52";
        String[] split = textCostValue.split("\\.");
        int length = split.length;

        if (length > 1) {
            textDec = "." + split[1];
        }

        String summ = split[0];
        if (summ != null)
            routeCostValue.setText(summ);

        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());
    }


    protected void getArrSumm(float amount) {
        setVisibleView();
        String textDec = null;
        String textCostValue = BigDecimal.valueOf(amount).
                setScale(UtilitesDataClient.getScale(amount), RoundingMode.HALF_UP).toString();
        // textCostValue+=".52";
        String[] split = textCostValue.split("\\.");
        int length = split.length;

        if (length > 1) {
            textDec = "." + split[1];
        }

        String summ = split[0];
        if (summ != null)
            addRouteCostValue.setText(summ);
        if (textDec != null) {
            addRouteCostValueDec.setText(textDec);
            addRouteCostValueDec.setVisibility(View.VISIBLE);
        } else
            addRouteCostValueDec.setVisibility(View.GONE);

        addCurrencyShort.setText(Injector.getWorkSettings().getCurrencyShort());
    }

    protected void setEnabledView() {
        for (View v : viewUI) {
            v.setEnabled(true);
            v.setAlpha(1.0f);
        }
    }

    protected void setDisabledView() {
        for (View v : viewUI) {
            v.setEnabled(false);
            v.setAlpha(0.5f);
        }
    }

    private void setVisibleView() {
        for (View v : viewGoneUI) {
            v.setEnabled(true);
            v.setAlpha(1.0f);
            buttonAddCost.setText(R.string.edit_cost);
        }
    }

    @OnClick(R.id.button_def_cost)
    public void onButtonDefCost() {
        this.valueAddCost = 0;
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        mRoute.valueAddCost = 0;
        mRoute.fixCost = null;
        initUI();
    }

    @OnClick(R.id.button_add_cost)
    public void onButtonAddCost() {
        TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
        mRoute.valueAddCost = this.valueAddCost;
        mRoute.fixCost = this.valueAddCost == 0 ? null : uiOrder.getAmount() + this.valueAddCost;
        Log.e("ggghg", "mRoute = " + mRoute);
        onBask();
    }

    @OnClick(R.id.ic_minus)
    public void onMinusStep() {

        valueAddCost -= Injector.getClientData().getCostChangeStep();
        initUI();
    }

    @OnClick(R.id.ic_plus)
    public void onPlusStep() {
        valueAddCost += Injector.getClientData().getCostChangeStep();
        initUI();
    }


    protected String getStepValue() {
        String s = Injector.getClientData().getCostChangeStep() + Injector.getWorkSettings().getCurrencyShort();
        String patern = ".0";
        if (s.contains(patern))
            s = s.replace(patern, "");
        return s;
    }


    @OnClick(R.id.bask)
    public void onBask() {

        if (!isVisible())
            return;
        getAWork().showV3RestoryFRoute();
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }
}
