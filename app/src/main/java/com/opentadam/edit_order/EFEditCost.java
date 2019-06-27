/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentadam.edit_order;

import android.support.v4.app.Fragment;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Cost;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.ui.FEditCost;

import butterknife.OnClick;

public class EFEditCost extends FEditCost {


    private long idOrder;
    private double[] latLon;
    private float amountSetFixSumm;

    public static Fragment newInstance(double[] latLonOneList, long idRoute) {

        return new EFEditCost()
                .withViewId(R.layout.v3f_edit_cost)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute);
    }

    @Override
    public void preloadUI() {
        if (getArguments() != null) {
            idOrder = getArguments().getLong("id");
            latLon = getArguments().getDoubleArray("latLon");
        }
        OrderInfo getOrderInfo = getAWork().getOrderInfo();
        if (getOrderInfo == null || getOrderInfo.cost == null)
            return;

        valueAddCost = 0;
        initUI();
    }

    @Override
    protected void initUI() {
        OrderInfo getOrderInfo = getAWork().getOrderInfo();
        Cost cost = getOrderInfo.cost;
        Float usedBonuses = getOrderInfo.usedBonuses;

        float baseAmount = cost.amount +
                (usedBonuses != null && usedBonuses != 0 ? usedBonuses : 0);

        float fixed = cost.fixed == null ? 0 : cost.fixed;

        float summCost = Math.max(baseAmount, fixed);
        getBaseSumm(summCost);

        valueCostPrefix.setText("");


        String stepValue = getStepValue();
        minusStep.setText(stepValue);
        plusStep.setText(stepValue);


        if (valueAddCost == 0) {
            setDisabledView();
        } else {
            setEnabledView();
        }


        amountSetFixSumm = summCost + valueAddCost;
        getArrSumm(amountSetFixSumm);
    }

    @OnClick(R.id.button_def_cost)
    public void onButtonDefCost() {
        valueAddCost = 0;
        initUI();
    }

    @OnClick(R.id.button_add_cost)
    public void onButtonAddCost() {

        RESTConnect rc = Injector.getRC();

        rc.setFixCost(idOrder
                , amountSetFixSumm
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        onBask();
                    }
                });

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

    @Override
    @OnClick(R.id.bask)
    public void onBask() {
        getAWork().showFSearchCar(new GpsPosition(latLon[0], latLon[1]), idOrder);
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }

}
