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

package com.opentadam.ui.execution_of_orders;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Cost;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.UtilitesOrder;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;


public class FFinishInfo extends BaseFr {

    @InjectView(R.id.ratingbar)
    RatingBar ratingBar;
    @InjectView(R.id.finish_cost)
    TextView finishCost;
    @InjectView(R.id.search_adress_cont)
    LinearLayout searchAdressCont;
    @InjectView(R.id.cont_details)
    LinearLayout contDetails;
    @InjectView(R.id.value_currency_short)
    TextView currencyShort;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;

    public FFinishInfo() {

    }

    public static Fragment newInstance() {
        return new FFinishInfo().withViewId(R.layout.f_finish_info);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        initBody();

    }

    private void initBody() {
        OrderInfo orderInfo = getAWork().getOrderInfo();
        if (orderInfo == null) {
            getAWork().delFragment(this);
            getAWork().restartAll();
            return;
        }
        showRoute(orderInfo.getAddresses(), searchAdressCont);

        Cost cost = orderInfo.cost;
        cost.amount = cost.fixed == null ? cost.amount : cost.fixed;


        Float usedBonuses = orderInfo.usedBonuses;
        if (usedBonuses != null && usedBonuses != 0) {
            cost.amount -= usedBonuses;

        }

        UIOrder uiOrder = new UIOrder(cost);
        String textDec = uiOrder.getTextDec();

        String summ = uiOrder.getSumm();


        if (summ != null)
            finishCost.setText(summ);

        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

        contDetails.removeAllViews();

        UtilitesOrder.instance()
                .initDetalsOrder(orderInfo, contDetails);
    }

    private void showRoute(ArrayList<Address> route, LinearLayout searchAC) {
        searchAC.removeAllViews();
        if (route == null)
            return;

        int size = route.size();
        if (size == 0)
            return;

        for (int i = 0; i < size; i++) {
            View view = LayoutInflater.from(searchAC.getContext())
                    .inflate(R.layout.row_adress_info, searchAC, false);
            TextView number = view.findViewById(R.id.number);
            number.setText(String.valueOf(i + 1));
            TextView adressContent = view.findViewById(R.id.adress_content);
            Address geo = route.get(i);
            String text = geo.getMapsValueStringAdress();

            adressContent.setText(text);

            searchAC.addView(view);
        }
    }

    @OnClick(R.id.bask)
    public void onHome() {
        getAWork().toglLeftMenu();
    }

    @OnClick(R.id.dialog_yes)
    public void onClickButton() {
        String set_rating = getString(R.string.set_rating);
        String substitutedString = String.format(set_rating, String.valueOf(ratingBar.getRating()));
        getAWork().alert(substitutedString);
        getAWork().restartAll();
    }

    @Override
    public void onDestroyView() {
        getAWork().setOrderInfo(null);
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().restartAll();
        return true;
    }

}
