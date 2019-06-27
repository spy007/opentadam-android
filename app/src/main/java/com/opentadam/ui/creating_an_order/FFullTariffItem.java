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

package com.opentadam.ui.creating_an_order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusSetFullTariff;
import com.opentadam.data.ClientData;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;


public class FFullTariffItem extends BaseFr {
    @InjectView(R.id.cardview)
    LinearLayout cardView;
    @InjectView(R.id.fr_image_tarif)
    ImageView imageView;
    @InjectView(R.id.fr_name_tarif)
    TextView name;
    @InjectView(R.id.fr_text_info)
    TextView textInfo;
    @InjectView(R.id.fr_prefix_tarif)
    TextView prefixTarif;
    @InjectView(R.id.fr_route_cost_value)
    TextView costTarif;
    @InjectView(R.id.fr_currency_short)
    TextView currencyShort;
    private int resourceId;


    public FFullTariffItem() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int resourceId) {
        return new FFullTariffItem().withViewId(R.layout.fr_item_opt_full)
                .withArgument("resourceId", resourceId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)
            return;

        resourceId = getArguments().getInt("resourceId");
        ClientData clientData = Injector.getClientData();
        List<Tarif> arrTarif = clientData.arrTarif;
        if (arrTarif == null || arrTarif.size() == 0) {
            return;
        }
        Tarif tarif = arrTarif.get(resourceId);
        name.setText("" + tarif.name);
        String description = tarif.description;
        if (description != null) {
            textInfo.setText(description.trim());
            textInfo.setVisibility(View.VISIBLE);
        }

        Integer resId = TintIcons.getArrCarIconFull()
                .get(tarif.icon);
        imageView.setImageResource(resId == null ? R.drawable.ic_tariff_auto_01 : resId);

        prefixTarif.setText(R.string.prefix_popup_tarif_min_cost);

        costTarif.setText(Utilites.getDefCostBigDec(tarif.minCost));
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

    }


    @OnClick(R.id.fr_select_tarif)
    public void setTarif() {
        App.bus.post(new BusSetFullTariff(resourceId));

    }
}
