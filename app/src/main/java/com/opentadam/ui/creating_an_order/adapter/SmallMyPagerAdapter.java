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

package com.opentadam.ui.creating_an_order.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.creating_an_order.IIsClickDefault;
import com.opentadam.ui.creating_an_order.ISelectItem;
import com.opentadam.utils.TintIcons;
import com.opentadam.yandex_google_maps.UtilitesMaps;

import java.util.List;


public class SmallMyPagerAdapter extends PagerAdapter {
    private final List<Tarif> arrTarif;
    private final int currentItem;
    public final ISelectItem iSelectItem;
    private final IIsClickDefault iIsClickDefault;

    private ViewGroup container;


    public SmallMyPagerAdapter(List<Tarif> arrTarif
            , int currentItem
            , ISelectItem iSelectItem
            , IIsClickDefault iIsClickDefault) {
        this.currentItem = currentItem;
        this.iSelectItem = iSelectItem;
        this.arrTarif = arrTarif;
        this.iIsClickDefault = iIsClickDefault;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup con, int pos) {
        container = con;
        int fr_item_opt_small = R.layout.fr_item_opt_small;
        View view = LayoutInflater
                .from(container.getContext()).inflate(fr_item_opt_small, null);
        final int position = pos;
        ImageView imageView = view.findViewById(R.id.fr_image_tarif);
        TextView name = view.findViewById(R.id.fr_name_tarif);

        Tarif tarif = arrTarif.get(position);
        String nameIcon = tarif.icon;

        Integer resId = TintIcons.getArrCarIconSmall()
                .get(nameIcon);


        imageView.setImageResource(resId == null
                ? R.drawable.ic_tariff_auto_01_small : resId);

        name.setText("" + tarif.name);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iIsClickDefault.onClickPosition(position);

            }
        });
        container.addView(view);
        if (position == getCount() - 1) {

            UtilitesMaps
                    .instanse()
                    .initListTariffUI(currentItem, container);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    public ViewGroup getViewGroup() {
        return container;
    }

    @Override
    public int getCount() {
        return arrTarif.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }
}
