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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Option;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFr;
import com.opentadam.view.CostView;
import com.opentadam.view.DefSwitch;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class V2FOptions extends BaseFr {
    @InjectView(R.id.cont_option)
    public LinearLayout contOption;

    public V2FOptions() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new V2FOptions().withViewId(R.layout.f_options);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initOptionsView();
    }

    protected void initOptionsView() {
        contOption.removeAllViews();
        Tarif defTariff = Injector.getClientData().getDefTarif();
        if (defTariff == null || defTariff.options == null) {
            onBask();
            return;
        }
        final ArrayList<Option> options = defTariff.options;

        final TempObjectUIMRoute mRoute = Injector.getClientData().getTempObjectUIMRoute();
        final ArrayList<Option> optionsClient = mRoute.getOptionsClient();

        int size = options.size();

        for (int i = 0; i < size; i++) {

            View view = LayoutInflater.from(contOption.getContext())
                    .inflate(R.layout.section_options, contOption, false);
            TextView nameOption = view.findViewById(R.id.name);
            nameOption.setText(options.get(i).name);
            CostView valueOption = view.findViewById(R.id.value);
            String type = options.get(i).type;
            valueOption.setText(String.format("%s%s", options.get(i).value, "percent".equals(type) ? "%"
                    : Injector.getWorkSettings().getCurrencyShort()));

            final DefSwitch switcher = view.findViewById(R.id.option_switcher);


            switcher.isActive = false;
            switcher.setEnabled(false);
            switcher.setActive(false);

            for (Option option : optionsClient) {
                if (option.name.equals(options.get(i).name)) {
                    switcher.isActive = true;
                    switcher.setEnabled(true);
                    switcher.setActive(true);
                }
            }

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switcher.setEnabled(!switcher.isActive);
                    switcher.setActive(!switcher.isActive);

                    if (switcher.isActive) {
                        optionsClient.add(options.get(finalI));
                    } else {
                        optionsClient.remove(options.get(finalI));
                        for (Option option : optionsClient) {
                            if (option.name.equals(options.get(finalI).name)) {
                                optionsClient.remove(option);
                                mRoute.optionsClient = optionsClient;
                                return;
                            }
                        }
                    }

                }
            });

            contOption.addView(view);
        }
    }

    @OnClick(R.id.bask)
    public void onBask() {

        if (!isVisible())
            return;

        getAWork().showV3FRoute(true);
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }
}
