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

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusOnNewversionOpenmarketClicked;

import butterknife.OnClick;

public class FUpdateApp extends BaseFr {

    public static BaseFr instance() {

        return new FUpdateApp().withViewId(R.layout.f_update_app);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.getSettingsStore().setTimeDialogUpdApp(System.currentTimeMillis());


    }

    @Override
    public boolean onBackPressed(){
        getAWork().hideUpdateApp();
        return true;
    }

    @OnClick(R.id.fupd_button_no)
    public void onButtonNO() {
        getAWork().hideUpdateApp();

    }

    @OnClick(R.id.fupd_button_ok)
    public void onButtonOk() {
        App.bus.post(new BusOnNewversionOpenmarketClicked());
        getAWork().hideUpdateApp();

    }
}
