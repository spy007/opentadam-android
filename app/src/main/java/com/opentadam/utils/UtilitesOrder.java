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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.CostItem;
import com.opentadam.network.rest.OrderInfo;

import java.util.ArrayList;

public class UtilitesOrder {

    public static UtilitesOrder instance() {
        return new UtilitesOrder();
    }

    public void initDetalsOrder(OrderInfo orderInfo, LinearLayout contDetails) {
        ArrayList<CostItem> details = orderInfo.getOptions();
        if (details == null)
            return;

        contDetails.removeAllViews();
        View viewSeparator = LayoutInflater.from(contDetails.getContext())
                .inflate(R.layout.separator_layout
                        , contDetails
                        , false);
        contDetails.addView(viewSeparator);
        Float fixed = orderInfo.cost.fixed;

        Float usedBonuses = orderInfo.usedBonuses;


        if (fixed != null) {
            details.clear();
            details.add(new CostItem(Injector.getAppContext()
                    .getString(R.string.fix_cost_title_utilites_order), fixed));
        }


        if (usedBonuses != null) {

            if (!isB(details, R.string.order_bunus)) {
                details.add(new CostItem(Injector.getAppContext()
                        .getString(R.string.order_bunus), -1.0f * usedBonuses));
            }
        }


        for (CostItem costItem : details) {
            View view = LayoutInflater.from(contDetails.getContext())
                    .inflate(R.layout.f_s_c_option_layout
                            , contDetails
                            , false);
            TextView optionName = view.findViewById(R.id.option_name);
            TextView optionValue = view.findViewById(R.id.option_value);
            String title = costItem.title;
            if (title == null)
                continue;

            optionName.setText(title);

            optionValue.setText(String.format("%s%s",
                    costItem.getValue(),
                    Injector.getWorkSettings().getCurrencyShort()));
            contDetails.addView(view);

        }
    }

    private boolean isB(ArrayList<CostItem> details, int orderCompensation) {

        for (CostItem costItem : details) {
            if (costItem.title.equals(Injector.getAppContext()
                    .getString(orderCompensation))) return true;

        }
        return false;
    }
}
