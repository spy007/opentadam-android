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


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.Cost;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.UtilitesDataClient;
import com.opentadam.utils.UtilitesOrder;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;


public class V2FOrderHistoryInfo extends BaseFr {

    @InjectView(R.id.order_history_time_prelum)
    TextView timePrelum;

    @InjectView(R.id.order_history_cont)
    LinearLayout orderHistoryCont;

    @InjectView(R.id.button_revers)
    TextView buttonRevers;

    @InjectView(R.id.v2_title)
    TextView v2Title;
    @InjectView(R.id.cont_details)
    LinearLayout contDetails;
    @InjectView(R.id.value_currency_short)
    TextView currencyShort;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;
    @InjectView(R.id.finish_cost)
    TextView finishCost;
    private ArrayList<Address> addresses;
    private long orderId;

    public V2FOrderHistoryInfo() {
        // Required empty public constructor
    }

    public static Fragment newInstance(long orderId, String timeCreate) {
        return new V2FOrderHistoryInfo()
                .withArgument("timeCreate", timeCreate)
                .withArgument("orderId", orderId)
                .withViewId(R.layout.f_order_history_info);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)
            return;

        orderId = getArguments().getLong("orderId");
        String timeCreate = getArguments().getString("timeCreate");

        timePrelum.setVisibility(timeCreate != null ?
                View.VISIBLE : View.GONE);
        if (timeCreate != null) {
            timePrelum.setText(String.format(getString(R.string.f_h_i_order_create_time)
                    , UtilitesDataClient.getStrigIso(timeCreate, true)));
        }
        getAWork().showWorkProgress();
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null){
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.getOrderInfo(orderId, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                getAWork().setOrderInfo(apiResponse.orderInfo);
                showBodyInfo(apiResponse);
            }
        });
        v2Title.setText(R.string.order_details);
    }

    @OnClick(R.id.button_revers)
    public void onButtonRevers() {
        getMSearchAdressList(false);
    }

    @OnClick(R.id.button_double)
    public void onButtonDouble() {
        getMSearchAdressList(true);
    }

    private void getMSearchAdressList(boolean doubleRoute) {
        //   route;
        ArrayList<ClientAddress> clientAddresses = new ArrayList<>();
        if (addresses == null) {
            alert(getString(R.string.error_create_route));
            return;
        }

        int max = addresses.size();

        if (doubleRoute) {

            for (int i = 0; i < max; i++) {
                Address geo = addresses.get(i);
                if (geo == null) {
                    alert(getString(R.string.error_create_route));
                    return;
                }

                clientAddresses.add(new ClientAddress(geo));
            }
        } else {
            for (int i = max - 1; i >= 0; i--) {
                Address geo = addresses.get(i);
                if (geo == null) {
                    alert(getString(R.string.error_create_route));
                    return;
                }


                clientAddresses.add(new ClientAddress(geo));
            }
        }
        CreateRequest createRequest = getClientData().getCreateRequest();
        createRequest.setRoute(clientAddresses);

        getAWork().isManualAdress = true;
        GpsPosition g = createRequest.getRouteLocation().get(0);
        LatLng latLng = g.getLatLng();
        getClientData().setMarkerLocation(latLng);
        getTarif(new LatLng(g.lat, g.lon));
    }

    private void getTarif(LatLng markerLocation) {

        getClientData().latLngTarifHTPSRequest = markerLocation;
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null){
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.getService(getClientData().getPaymentMethodSelect(), markerLocation, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                getAWork().bodyGetTarif(apiResponse);
            }
        });

    }

    @OnClick({R.id.v2_bask, R.id.f_o_h_i_delete_button})
    public void onv2Bask(View v) {
        if (v.getId() == R.id.f_o_h_i_delete_button)
            Injector.getSettingsStore().setDisabledArrayListShortOrderInfo(orderId);

        getAWork().showV2FShortOrdersHistory();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV2FShortOrdersHistory();
        return true;
    }

    private void initBody(OrderInfo orderInfo) {

        if (orderInfo == null) {
            return;
        }


        Cost cost = orderInfo.cost;
        UIOrder uiOrder = new UIOrder(orderInfo, Constants.FFINISH_INFO);
        String textDec = uiOrder.getTextDec();
        String summ = uiOrder.getSumm();


        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

        finishCost.setText(summ);
        contDetails.removeAllViews();
        UtilitesOrder.instance()
                .initDetalsOrder(orderInfo, contDetails);

    }


    @SuppressLint("DefaultLocale")
    private void showBodyInfo(ApiResponse apiResponse) {
        OrderInfo orderInfo = apiResponse.orderInfo;
        initBody(orderInfo);
        getAWork().hideWorkProgress();
        addresses = orderInfo.getAddresses();

        orderHistoryCont.removeAllViews();
        int max = addresses.size();
        if (max == 1)
            buttonRevers.setVisibility(View.GONE);
        for (int i = 0; i < max; i++) {
            Address address = addresses.get(i);
            View view = LayoutInflater.from(orderHistoryCont.getContext())
                    .inflate(R.layout.section_order_history, orderHistoryCont, false);
            TextView posAdr = view.findViewById(R.id.pos_adr);
            TextView valAadr = view.findViewById(R.id.val_adr);
            TextView commentAdr = view.findViewById(R.id.comment_adr);
            posAdr.setText(String.format("%d", i + 1));

            String addressRoute = address.getMapsValueStringAdress();
            commentAdr.setVisibility(View.GONE);
            valAadr.setText(addressRoute);

            orderHistoryCont.addView(view);
        }
    }
}
