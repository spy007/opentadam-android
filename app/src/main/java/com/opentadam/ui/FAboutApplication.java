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

package com.opentadam.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.R;
import com.opentadam.menu.UtilitesMenu;

import butterknife.InjectView;
import butterknife.OnClick;

public class FAboutApplication extends BaseFr {
    @InjectView(R.id.v2_title)
    TextView v2Title;

    @InjectView(R.id.faa_version_name)
    TextView versionName;

    @InjectView(R.id.faa_user_agreement)
    TextView userAgreement;

    public static Fragment newInstance() {
        return new FAboutApplication().withViewId(R.layout.v3_f_a_aplication);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        v2Title.setText(R.string.faa_title);

        initUI();

    }

    private void initUI() {
        String stringVersionName = UtilitesMenu.getStringVersionName(getAWork());
        versionName.setText(stringVersionName);

        if (App.app.hashBC.URL_USER_AGREEMENT == null)
            userAgreement.setVisibility(View.GONE);
    }

    @OnClick(R.id.faa_user_agreement)
    public void onvUserAgreement() {

        String urlUserAgreement = App.app.hashBC.URL_USER_AGREEMENT;
        getUrl(urlUserAgreement);
    }

    private void getUrl(String url) {

        if (url == null)
            return;


        Uri address = Uri.parse(url);
        Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);

        startActivity(Intent
                .createChooser(openlinkIntent, getString(R.string.select_app)));
    }

    @OnClick(R.id.faa_privacy_policy)
    public void onPrivacyPolicy() {

        Uri address = Uri.parse(App.app.hashBC.URL_POLICY_PRIVACY);
        Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);

        startActivity(Intent
                .createChooser(openlinkIntent, getString(R.string.select_app)));
    }

    @OnClick(R.id.v2_bask)
    public void onv2Bask() {

        getAWork().toglLeftMenu();
    }

    @Override
    public boolean onBackPressed() {

        getAWork().showV3FRoute();
        return true;
    }
}
