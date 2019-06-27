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

package com.opentadam.utils;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Cost;
import com.opentadam.network.rest.CostModifier;
import com.opentadam.network.rest.Estimation;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.TempObjectUIMRoute;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class UIOrder {
    private Float usedBonuses;
    private Cost costRoute;
    private double distance;

    private int length;
    private String valueType;
    private String textDec;
    private String summ;


    public UIOrder(OrderInfo orderInfo, int nameClass) {
        this.usedBonuses = orderInfo.usedBonuses;
        this.costRoute = orderInfo.cost;
        if (Constants.FFINISH_INFO == nameClass)
            initFinisHinfo();
    }

    private void initFinisHinfo() {
        TempObjectUIMRoute mRoute = Injector.getClientData().getTempObjectUIMRoute();
        float amount = (costRoute.fixed == null ?
                costRoute.amount : costRoute.fixed) + mRoute.valueAddCost;

        if (usedBonuses != null) {
            amount -= usedBonuses;
        }

        String type = costRoute.type;


        valueType = "minimum".equals(type) ? Injector.getClientData().getResources().getString(R.string.ot) : "";
        if ("approximate".equals(type))
            valueType = Injector.getClientData().getResources().getString(R.string.tilda);

        String textCostValue = BigDecimal.valueOf(amount).
                setScale(UtilitesDataClient.getScale(amount), RoundingMode.HALF_UP).toString();
        // textCostValue+=".52";
        String[] split = textCostValue.split("\\.");
        length = split.length;

        //  currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

        if (length > 1) {
            textDec = "." + split[1];
        }

        summ = split[0];
    }


    public UIOrder(Estimation estimation) {

        this.costRoute = estimation.cost;
        this.distance = estimation.distance;
        init();

    }

    public UIOrder(Cost costRoute) {
        this.costRoute = costRoute;
        init();

    }

    public String getValueType() {
        return valueType;
    }

    public String getTextDec() {
        return textDec;
    }

    public String getSumm() {
        return summ;
    }

    private void init() {

        TempObjectUIMRoute mRoute = Injector.getClientData().getTempObjectUIMRoute();
        mRoute.cost = costRoute;
        mRoute.textTariff = Injector.getClientData().getNameDefTarif();

        float amount = costRoute.amount + mRoute.valueAddCost;

        String calculation = costRoute.calculation;
        String type = costRoute.type;

        String textCostCalculation = "";
        if ("taximeter".equals(calculation))
            textCostCalculation = Injector.getClientData().getResources().getString(R.string.how_much);

        if ("fixed".equals(calculation))
            textCostCalculation = Injector.getClientData().getResources().getString(R.string.fix_cost);


        valueType = "minimum".equals(type) ? Injector.getClientData().getResources().getString(R.string.ot) : "";
        if ("approximate".equals(type))
            valueType = Injector.getClientData().getResources().getString(R.string.tilda);

        String textCostValue = BigDecimal.valueOf(amount).
                setScale(UtilitesDataClient.getScale(amount), RoundingMode.HALF_UP).toString();
        // textCostValue+=".52";
        String[] split = textCostValue.split("\\.");
        length = split.length;

        //  currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

        if (length > 1) {
            textDec = "." + split[1];
        }

        summ = split[0];

        mRoute.textCostCalculation = textCostCalculation;
        mRoute.textCostValue = textCostValue;
        mRoute.textCostType = type;

        mRoute.isTaximetr = "taximeter".equals(calculation);

        mRoute.textDistanceValue = distance == 0.0 ? ""
                : Utilites.getStringDistance(distance / 1000);

    }

    public CostModifier getCostModifier() {
        return costRoute.modifier;
    }

    public float getAmount() {

        return costRoute.amount;
    }
}
