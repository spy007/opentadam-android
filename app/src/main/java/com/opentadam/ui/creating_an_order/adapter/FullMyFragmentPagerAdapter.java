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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.creating_an_order.FFullTariffItem;

import java.util.List;


public class FullMyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final List<Tarif> arrTarif;

    public FullMyFragmentPagerAdapter(FragmentManager fm, List<Tarif> arrTarif) {
        super(fm);
        this.arrTarif = arrTarif;
    }

    @Override
    public Fragment getItem(int position) {
        return FFullTariffItem.newInstance(position);
    }

    @Override
    public int getCount() {
        return arrTarif.size();
    }
}
