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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Option;
import com.opentadam.network.rest.OptionsList;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.ui.order.V2FOptions;
import com.opentadam.view.CostView;
import com.opentadam.view.DefSwitch;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class EV2FOptions extends V2FOptions {

    private long idOrder;
    private double[] latLon;
    private ArrayList<Option> optionsClient;

    public static Fragment newInstance(double[] latLonOneList, long idRoute) {

        return new EV2FOptions()
                .withViewId(R.layout.f_options)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute);
    }


    @Override
    public void initOptionsView() {
        if (getArguments() == null)
            return;

        idOrder = getArguments().getLong("id");
        double[] latLons = getArguments().getDoubleArray("latLon");
        if (latLons == null || latLons.length != 2) {
            latLons = new double[2];
            latLons[0] = getLatLngWorkOrder().latitude;
            latLons[1] = getLatLngWorkOrder().longitude;
        }
        latLon = latLons;


        contOption.removeAllViews();


        OrderInfo getOrderInfo = getAWork().getOrderInfo();
        if (getOrderInfo == null)
            return;
        optionsClient = getOrderInfo.options;
        if (optionsClient == null)
            optionsClient = new ArrayList<>();


        int size = optionsClient.size();
        if (size == 0) {
            alert(getString(R.string.empty_options));
            return;
        }


        for (int i = 0; i < size; i++) {

            View view = LayoutInflater.from(contOption.getContext())
                    .inflate(R.layout.section_options, contOption, false);
            TextView nameOption = view.findViewById(R.id.name);
            nameOption.setText(optionsClient.get(i).name);
            CostView valueOption = view.findViewById(R.id.value);
            String type = optionsClient.get(i).type;
            valueOption.setText(String.format("%s%s"
                    , optionsClient.get(i).value
                    , "percent".equals(type) ? "%"
                            : Injector.getWorkSettings().getCurrencyShort()));

            final DefSwitch switcher = view.findViewById(R.id.option_switcher);

            if (optionsClient.get(i).selected) {
                switcher.isActive = true;
                switcher.setEnabled(true);
                switcher.setActive(true);
            } else {
                switcher.isActive = false;
                switcher.setEnabled(false);
                switcher.setActive(false);
            }


            final int finalI = i;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switcher.setEnabled(!switcher.isActive);
                    switcher.setActive(!switcher.isActive);
                    optionsClient.get(finalI).selected = switcher.isActive;

                }
            });

            contOption.addView(view);
        }
    }

    @OnClick(R.id.bask)
    public void onBask() {

        List<Long> options = getOptionsSelect();
        RESTConnect rc = Injector.getRC();


        rc.editOptions(null
                , idOrder
                , new OptionsList(options)
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {

                        getAWork().showFSearchCar(new GpsPosition(latLon[0], latLon[1]), idOrder);
                    }
                });

    }

    private List<Long> getOptionsSelect() {
        ArrayList<Long> optionsSelect = new ArrayList<>();
        if (optionsClient == null || optionsClient.size() == 0)
            return optionsSelect;

        for (Option option : optionsClient) {
            if (option.selected)
                optionsSelect.add(option.id);
        }

        return optionsSelect;
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }
}
