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

package com.opentadam.yandex_google_maps;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Address;
import com.opentadam.utils.TintIcons;

import java.util.List;

public class UtilitesMaps {
    public static UtilitesMaps instanse() {
        return new UtilitesMaps();
    }

    public void setMarginButtonView(int dp, View[] marginButtonView) {
        for (View v : marginButtonView) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, getPX(dp));
        }
    }

    private int getPX(int DP) {
        final float scale = Injector.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (DP * scale + 0.5f);
    }

    public Address getAddressServers(List<Address> addresses) {
        for (Address address : addresses) {
            if (address == null)
                continue;
            if (address.components == null)
                continue;
            if (address.components.size() == 0)
                continue;

            return address;
        }

        return null;
    }

    public void initListTariffUI(int position, ViewGroup container) {

        if (container == null || container.getChildCount() - 1 < position)
            return;

        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = container.getChildAt(i);
            ImageView imageView = childAt.findViewById(R.id.fr_image_tarif);

            TextView name = childAt.findViewById(R.id.fr_name_tarif);
            name.setTextColor(TintIcons.initColor(R.color.fr_tarif_sec));

            if (App.app.hashBC.isChangeColorIPTariffs) {
                TintIcons.tintImageViewOther(imageView, R.color.fr_tarif_sec);
            }

        }
        View childAt = container.getChildAt(position);
        ImageView imageView = childAt.findViewById(R.id.fr_image_tarif);
        TextView name = childAt.findViewById(R.id.fr_name_tarif);

        name.setTextColor(TintIcons.initColor(R.color.text_default_color));

        if (App.app.hashBC.isChangeColorIPTariffs) {
            TintIcons.tintImageViewBrend(imageView);
        }
    }
}
