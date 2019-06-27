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

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.adapter.OrdersAdapter;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.ui.BaseFr;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class V2FShortOrdersHistory extends BaseFr {
    @InjectView(R.id.list_view)
    ListView listView;
    @InjectView(R.id.order_history_empty)
    TextView orderHistoryEmpty;
    @InjectView(R.id.v2_title)
    TextView v2Title;

    public static Fragment newInstance() {
        return new V2FShortOrdersHistory().withViewId(R.layout.v2f_short_orders);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getHistoryOrders();
        v2Title.setText(R.string.list_orders_nistory);
    }

    private void getHistoryOrders() {
        getAWork().showWorkProgress();
        getAWork().setTitle(getResources().getString(R.string.list_orders_nistory));
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.getHistoryOrders(new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                ArrayList<ShortOrderInfo> shortOrderInfos = apiResponse.shortOrderInfos;
                getAWork().hideWorkProgress();
                if (shortOrderInfos.size() > 0) {


                    initListOrderHistory(shortOrderInfos);
                } else
                    orderHistoryEmpty.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initListOrderHistory(ArrayList<ShortOrderInfo> shortOrderInfos) {
        ArrayList<Long> disabledArrayListShortOrderInfo =
                Injector.getSettingsStore().getDisabledArrayListShortOrderInfo();
        ArrayList<ShortOrderInfo> tempShortOrderInfos = new ArrayList<>();
        for (ShortOrderInfo sh : shortOrderInfos) {

            boolean contains = disabledArrayListShortOrderInfo.contains(sh.id);

            if (!contains) {
                tempShortOrderInfos.add(sh);

            }
        }


        bodyILOH(tempShortOrderInfos);
    }

    private void bodyILOH(ArrayList<ShortOrderInfo> shortOrderInfos) {
        showListAdress(shortOrderInfos);
        orderHistoryEmpty.setVisibility(View.GONE);
    }

    private void showListAdress(final ArrayList<ShortOrderInfo> shortOrderInfos) {

        OrdersAdapter adapter = new OrdersAdapter(shortOrderInfos);
        Parcelable state = listView.onSaveInstanceState();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShortOrderInfo shortOrderInfo = shortOrderInfos.get(position);
                long orderId = shortOrderInfo.id;
                getAWork().showV2FOrderHistoryInfo(orderId, shortOrderInfo.time);

            }
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
