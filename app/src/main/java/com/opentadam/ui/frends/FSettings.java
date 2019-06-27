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

package com.opentadam.ui.frends;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.SettingsStore;
import com.opentadam.data.Countries;
import com.opentadam.data.DialogClient;
import com.opentadam.data.IResponseDialog;
import com.opentadam.data.IResponseDialogMap;
import com.opentadam.data.IV3ResponseDialog;
import com.opentadam.ui.AWork;
import com.opentadam.ui.BaseFr;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class FSettings extends BaseFr implements IResponseDialog, IResponseDialogMap {
    @InjectView(R.id.block_sms)
    com.opentadam.view.DefSwitch blockSms;
    @InjectView(R.id.block_call)
    com.opentadam.view.DefSwitch blockCall;
    @InjectView(R.id.value_type_maps)
    TextView typeMaps;
    @InjectView(R.id.value_locale_app)
    TextView valueLocale;

    public FSettings() {
    }

    public static Fragment newInstance() {
        return new FSettings().withViewId(R.layout.f_settings);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.f_settings, container, false);
        ButterKnife.inject(this, inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Injector.getClientData().isShowSettings = false;
        disabledSMS();
        disabledCALL();
        updateLocleUI();
        int defMars = Injector.getSettingsStore().getDefMars();
        String val = "Google Maps";
        switch (defMars) {
            case SettingsStore.MAPS_GOOGLE:
                val = "Google Maps";
                break;
            case SettingsStore.MAPS_OSM:
                val = "osm";
                break;
            case SettingsStore.MAPS_YANDEX:
                val = "Yandex Maps";
                break;
        }

        typeMaps.setText(val);
    }

    private void updateLocleUI() {
        String locale = Countries.getLocale(App.app.hashBC.availableLanguages);
        String localeName = Countries.getLocaleName(locale);
        valueLocale.setText(localeName);
    }

    @OnClick(R.id.cont_maps_setting_profile)
    public void onMapsSettings() {

        int defMars = Injector.getSettingsStore().getDefMars();
        DialogClient.showProfilMapsDialog(this, defMars);
    }

    @OnClick(R.id.cont_locale_setting_profile)
    public void onLocaleSettings() {
        String locale = Countries.getLocale(App.app.hashBC.availableLanguages);

        DialogClient.showProfilLocaleDialog(this, locale);
    }

    @OnClick(R.id.cont_exit_setting_profile)
    public void onExitSettings() {
        DialogClient.showV3TwoButtonDialog(getString(R.string.fs_exit_title)
                , getString(R.string.fs_exit_message)
                , getString(R.string.fs_exit_button_no)
                , getString(R.string.fs_exit_button_yes)
                , new IV3ResponseDialog() {
                    @Override
                    public AWork getAWork() {
                        return FSettings.this.getAWork();
                    }

                    @Override
                    public void responseAction() {

                        if (Injector.getSettingsStore().clearSettings())
                            getAWork().restartAll();

                    }
                });
    }


    private void disabledCALL() {
        blockCall.isActive = false;
        blockCall.setEnabled(true);
        blockCall.setActive(false);
    }


    private void disabledSMS() {
        blockSms.isActive = false;
        blockSms.setEnabled(true);
        blockSms.setActive(false);
    }


    @Override
    public boolean onBackPressed() {
        getAWork().showV3FRoute();
        return true;
    }

    @Override
    public void setMaps(int type) {
        if (!isVisible() || typeMaps == null)
            return;
        String valueTypeMaps = "Google Maps";
        switch (type) {
            case SettingsStore.MAPS_GOOGLE:
                valueTypeMaps = "Google Maps";
                break;
            case SettingsStore.MAPS_OSM:
                valueTypeMaps = "osm";
                break;
            case SettingsStore.MAPS_YANDEX:
                valueTypeMaps = "Yandex Maps";
                break;
        }

        typeMaps.setText(valueTypeMaps);
    }

    @Override
    public void addPfotoCamera(boolean b) {

    }

    @Override
    public Context getContectApp() {
        return null;
    }

    @Override
    public void responseAction(String name) {

    }
}
