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

package com.opentadam.ui.registration;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CountryViewHolder {
    @InjectView(R.id.i_login_country_flag)
    ImageView iLoginCountryFlag;
    @InjectView(R.id.i_login_country_prefix)
    TextView iLoginCountryPrefix;


    @InjectView(R.id.i_login_country_name)
    TextView iLoginCountryName;
    @InjectView(R.id.i_login_country_line)
    LinearLayout iLoginCountryLine;


    CountryViewHolder(View view) {
        ButterKnife.inject(this, view);
    }
}

