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

package com.opentadam.start;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusHashBuildConfig;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.ui.AWork;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import static com.opentadam.Injector.alert;

public class StartActivity extends AppCompatActivity {

    private int defPositioUrl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.bus.register(this);
        Injector.restServer = App.app.hashBC.REST_SERVER[defPositioUrl];
        HashMap<String, String> nameMap = BuildConfig.localizationPush;
      //  Log.e("hjhjhjjh", "=" + nameMap.get("name"));
        getHashBuildConfig();
    }

    private void initActivity() {
        Injector.restServer = App.app.hashBC.REST_SERVER[defPositioUrl];
        com.opentadam.network.rest.Settings workSettings = Injector.getWorkSettings();
        if (workSettings != null) workSettings.setServiceId(null);

        Injector.getRC().getService(Injector.getClientData()
                .getPaymentMethodSelect(), App.app.mMyGoogleLocation.getLatLngMyLoc(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {
                    defPositioUrl++;
                    if (defPositioUrl <= App.app.hashBC.REST_SERVER.length - 1) {
                        Injector.restServer = App.app.hashBC.REST_SERVER[defPositioUrl];
                        initActivity();
                        return;
                    }
                    alert(getString(R.string.error_get_data_servers));
                    finish();
                    return;
                }
                getCountries();
            }
        });
    }

    private void getCountries() {
        Injector.getRC().getCountries(new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {

                    return;
                }
                Injector.setCountryList(apiResponse.countryList);
                initAWork();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (App.bus != null) App.bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onBusHashBuildConfig(BusHashBuildConfig event) {
        initActivity();
    }

    private void getHashBuildConfig() {
        if (BuildConfig.buildCFUrl != null)
            HashBC.initHashBuildConfig();
        else
            initActivity();
    }

    private void initAWork() {
        AWork.show(this);
        finish();
    }
}
