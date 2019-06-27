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

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.opentadam.App;
import com.opentadam.R;
import com.opentadam.ui.common.CommonWebViewFragment;

import butterknife.OnClick;

public class FShowSupportUrl extends BaseFr {

    public static Fragment newInstance() {
        return new FShowSupportUrl().withViewId(R.layout.f_show_support_url);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showUrl(App.app.hashBC.supportButtonURL);
    }

    public void showUrl(String url) {
        CommonWebViewFragment supportCommonWebViewFragment = (CommonWebViewFragment) getChildFragmentManager()
                .findFragmentById(R.id.supportCommonWebViewFragment);
        supportCommonWebViewFragment.setWebView(url);
    }

    @OnClick(R.id.supportCloseVebWiew)
    public void onSupportCloseVebWiew() {
        getAWork().showMenu();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV3FRoute();
        return true;
    }
}
