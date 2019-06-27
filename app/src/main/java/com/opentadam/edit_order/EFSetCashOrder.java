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
import android.view.View;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.ClientData;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.EditPaymentMethod;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.ui_payemnts_metods.V2FSetCashOrder;

import butterknife.OnClick;

public class EFSetCashOrder extends V2FSetCashOrder {
    private long idOrder;
    private double[] latLon;


    public static Fragment newInstance(double[] latLonOneList, long idRoute) {

        return new EFSetCashOrder()
                .withViewId(R.layout.f_set_cash_order)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute);
    }

    public static Fragment newInstance(double[] latLonOneList
            , long idRoute
            , boolean isContractor) {
        return new EFSetCashOrder()
                .withViewId(R.layout.f_set_cash_order)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute)
                .withArgument("isContractor", !isContractor);
    }

    @Override
    public void unitUI() {
        if (getArguments() != null) {
            idOrder = getArguments().getLong("id");
            latLon = getArguments().getDoubleArray("latLon");
            isHideContractor = getArguments().getBoolean("isContractor", false);
        }

        //   title.setText(R.string.e_f_s_change_payment_method);
        super.unitUI();
    }

    @OnClick(R.id.bask)
    public void onBask() {
        GpsPosition gpsPosition = null;

        if (latLon != null)
            gpsPosition = new GpsPosition(latLon[0], latLon[1]);

        getAWork().showFSearchCar(gpsPosition, idOrder);
    }


    @Override
    public void onClickPosPayment(ClientData clientData, int finalI, View view) {

        PaymentMethod paymentMethodSelect;
        Object tag = view.getTag();
        if (tag instanceof PaymentMethod) {
            paymentMethodSelect = (PaymentMethod) tag;
        } else {
            paymentMethodSelect = new PaymentMethod("cash");
        }

        RESTConnect rc = Injector.getRC();

        rc.editPaymentMethod(null
                , idOrder
                , new EditPaymentMethod(paymentMethodSelect)
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        onBask();
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }

}
