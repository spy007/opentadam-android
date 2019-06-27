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
package com.opentadam.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Assignee;
import com.opentadam.network.rest.Car;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.UtilitesDataClient;

import java.util.ArrayList;

import static com.opentadam.R.id.orders_cont;

public class OrdersAdapter extends BaseAdapter {

    private ArrayList<ShortOrderInfo> shortOrderInfos;


    public OrdersAdapter(ArrayList<ShortOrderInfo> shortOrderInfos) {
        this.shortOrderInfos = shortOrderInfos;

    }

    @Override
    public int getCount() {
        return shortOrderInfos.size();
    }

    @Override
    public ShortOrderInfo getItem(int position) {
        return shortOrderInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(parent.getContext(), R.layout.row_short, null);
        }

        ShortOrderInfo shortOrderInfos = getOrders(position);
        TextView ordersTime = view.findViewById(R.id.orders_time);
        TextView ordersTimeDelta = view.findViewById(R.id.orders_time_delta);
        TextView ordersOneAddress = view.findViewById(R.id.orders_one_address);
        LinearLayout ordersCont = view.findViewById(orders_cont);
        LinearLayout searchCarCont = view.findViewById(R.id.search_car_cont);
        TextView ordersStateTxt = view.findViewById(R.id.orders_state_txt);
        ImageView ordersIcon = view.findViewById(R.id.orders_icon);

        initShowDataCar(view, shortOrderInfos, searchCarCont);


        String txt = shortOrderInfos.time;
        ArrayList<Address> route = shortOrderInfos.route;
        Address routeOne = route.get(0);
        ordersCont.removeAllViews();

        int max = route.size();

        if (max > 1) {
            for (int i = 1; i < max; i++) {
                View viewAdr = LayoutInflater.from(ordersCont.getContext())
                        .inflate(R.layout.orders_item_addresses, ordersCont, false);
                TextView ordersAddressList = viewAdr.findViewById(R.id.orders_address_list);
                Address route_ = route.get(i);
                String addressString_ = route_.getMapsValueStringAdress();

                ordersAddressList.setText(addressString_);
                ordersCont.addView(viewAdr);

            }
        }


        String addressString = routeOne.getMapsValueStringAdress();
        String textState = "";
        ordersStateTxt.setVisibility(View.VISIBLE);
        ordersTimeDelta.setVisibility(View.VISIBLE);
        ordersTimeDelta.setTextColor(Injector.getAppContext().getResources().getColor(R.color.colorPrimary));
        boolean history = false;
        switch (shortOrderInfos.state) {
            case Constants.STATE_CREATE:
                ordersIcon.setImageResource(R.drawable.ic_status_search);
                textState = Injector.getAppContext().getResources().getString(R.string.search_car_caps);
                TintIcons.tintImageViewBrend(ordersIcon);

                break;
            case Constants.STATE_SET:
                textState = Injector.getAppContext().getResources().getString(R.string.car_assigned_caps);
                ordersIcon.setImageResource(R.drawable.status_assigned);
                TintIcons.tintImageViewBrend(ordersIcon);
                break;
            case Constants.STATE_WAIT:
                textState = Injector.getAppContext().getResources().getString(R.string.car_wait_caps);
                ordersIcon.setImageResource(R.drawable.status_arrived);
                TintIcons.tintImageViewBrend(ordersIcon);
                break;
            case Constants.STATE_WORK:
                textState = Injector.getAppContext().getResources().getString(R.string.order_work_caps);
                ordersIcon.setImageResource(R.drawable.status_inprogress);
                TintIcons.tintImageViewBrend(ordersIcon);
                break;
            case Constants.STATE_DONE:
                textState = "";//Injector.getAppContext().getResources().getString(R.string.order_finish_caps);
                ordersIcon.setImageResource(R.drawable.history_status_finished);
                ordersStateTxt.setVisibility(View.GONE);
                TintIcons.tintImageViewBrend(ordersIcon);
                history = true;
                ordersTimeDelta.setTextColor(Injector.getAppContext().getResources().getColor(R.color.text_color));
                break;
            default:
                history = true;
                ordersIcon.setImageResource(R.drawable.ic_taxi_cancel);
                TintIcons.tintImageViewOther(ordersIcon, R.color.history_status_delete);
                ordersStateTxt.setVisibility(View.GONE);

                ordersTimeDelta.setTextColor(Injector.getAppContext().getResources().getColor(R.color.text_color));

        }

        ordersOneAddress.setText(addressString);


        if (txt != null) {
            ordersTime.setVisibility(View.VISIBLE);
            ordersTimeDelta.setVisibility(View.VISIBLE);
            String deltaTime = UtilitesDataClient.getDeltaTimePreorder(txt);

            ordersTimeDelta.setText(history ? "" : deltaTime);
            txt = UtilitesDataClient.getStrigIso(txt, true);

            ordersTime.setText(txt);

        } else {
            ordersTime.setVisibility(View.GONE);
            ordersTimeDelta.setVisibility(View.GONE);
        }
        ordersStateTxt.setText(Constants.STATE_DONE == shortOrderInfos.state ?
                Html.fromHtml("<font color=\"green\">" + textState + "</font>") : textState);

        return view;
    }

    private void initShowDataCar(View view, ShortOrderInfo shortOrderInfos, LinearLayout searchCarCont) {
        Assignee assignee = shortOrderInfos.assignee;
        if (assignee != null) {
            searchCarCont.setVisibility(View.VISIBLE);
            TextView searchRegNumInfo = view.findViewById(R.id.search_reg_num_info);
            TextView searchBrand = view.findViewById(R.id.search_brand);
            TextView searchColor = view.findViewById(R.id.search_color);

            Car car = assignee.car;
            if (car != null) {

                String regNum = car.regNum;
                // String brand = car.brand;
                String color = car.color;
                //  String model = car.model;
                String alias = car.alias;


                searchRegNumInfo.setText(UtilitesDataClient.formatRegNum(regNum));


                if (alias != null)
                    searchBrand.setText(alias);
                if (color != null)
                    searchColor.setText(color);


            }
        } else
            searchCarCont.setVisibility(View.GONE);
    }

    private ShortOrderInfo getOrders(int position) {
        return getItem(position);
    }


}
