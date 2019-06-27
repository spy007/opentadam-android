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

package com.opentadam.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.ui.BaseFr;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class V2FcostView extends BaseFr {

    @InjectView(R.id.route_cost_value)
    TextView routeCostValue;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;
    @InjectView(R.id.currency_short)
    TextView currencyShort;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.v2f_cost_view, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    public void initUI(String textCostValue) {


        String[] split = textCostValue.split("\\.");

        String textDec = null;


        if (split.length > 1) {
            textDec = "." + split[1];
        }

        String summ = split[0];
        if (summ != null)
            routeCostValue.setText(summ);

        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());
    }

/*    private void castomize(int textSize, float cof, int delta) {
        routeCostValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        costValueDec.setTextSize(TypedValue.COMPLEX_UNIT_DIP,  textSize/cof);
        currencyShort.setTextSize(TypedValue.COMPLEX_UNIT_DIP,   textSize/cof);

        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) costValueDec.getLayoutParams();

        layoutParams.setMargins(0, getPX(delta), 0, 0);

        layoutParams =
                (ViewGroup.MarginLayoutParams) currencyShort.getLayoutParams();

        layoutParams.setMargins(0, getPX(delta), 0, 0);
    }*/


}
