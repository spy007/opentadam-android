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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.adapter.OrdersAdapter;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.ui.BaseFr;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;


public class V2FShortOrdersPrivate extends BaseFr {

    @InjectView(R.id.list_view)
    ListView listView;
    @InjectView(R.id.v2_title)
    TextView v2Title;

    public V2FShortOrdersPrivate() {
        // Required empty public constructor
    }


    public static Fragment newInstance() {
        return new V2FShortOrdersPrivate().withViewId(R.layout.v2f_short_orders);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAWork().showWorkCont();

        ArrayList<ShortOrderInfo> shortOrderInfos = getAWork().shortOrderInfos;
        if (shortOrderInfos == null) {
            getAWork().restartAll();
            return;
        }


        v2Title.setText(R.string.private_order);


        showListAdress(shortOrderInfos);
        getAWork().setBGWork();
    }


    private void showListAdress(final ArrayList<ShortOrderInfo> shortOrderInfos) {

        OrdersAdapter adapter = new OrdersAdapter(shortOrderInfos);
        Parcelable state = listView.onSaveInstanceState();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShortOrderInfo shortOrderInfo = shortOrderInfos.get(position);
                ArrayList<Address> route = shortOrderInfo.route;
                Address oneAdress = route.get(0);
                GpsPosition gpsPosition = oneAdress.position;

                if (gpsPosition != null) {
                    getAWork().showFSearchCar(gpsPosition, shortOrderInfo.id);
                }
            }
            // }
        });
        listView.setAdapter(adapter);
        listView.onRestoreInstanceState(state);
    }


    @Override
    public void onDestroyView() {
        getAWork().hideWorkProgress();
        super.onDestroyView();
    }

    @OnClick(R.id.v2_bask)
    public void onv2Bask() {

        getAWork().showMenu();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showMenu();
        return true;
    }

}


