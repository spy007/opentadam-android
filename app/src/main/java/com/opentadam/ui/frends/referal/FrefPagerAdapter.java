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

package com.opentadam.ui.frends.referal;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.opentadam.Injector;
import com.opentadam.R;

class FrefPagerAdapter extends FragmentPagerAdapter {

    private String lptype;
    private String[] mTabTitles = new String[]
            {Injector.getAppContext().getResources().getString(R.string.fref_item_bonus), Injector.getAppContext().getResources().getString(R.string.fref_item_history)};
    private String bonusTag;
    private String historyTag;

    public FrefPagerAdapter(FragmentManager fm, String lptype) {
        super(fm);
        this.lptype = lptype;
    }

    public String getBonusTag() {
        return bonusTag;
    }

    public String getHistoryTag() {
        return historyTag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Генерируем заголовки на основе позиции
        return mTabTitles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FReferalBonusProgramm.newInstance(lptype);

            case 1:
                return FReferalHistory.newInstance(lptype);
        }

        return FReferalBonusProgramm.newInstance(lptype);
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        switch (position) {
            case 0:
                bonusTag = fragment.getTag();
                break;
            case 1:
                historyTag = fragment.getTag();
                break;

        }

        return fragment;
    }

    public String getTagPositioh(int position) {
        return position == 0 ? bonusTag : historyTag;
    }
}
