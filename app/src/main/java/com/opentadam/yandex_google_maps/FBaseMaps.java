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

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusStartOrStopPinAnimate;
import com.opentadam.data.ClientData;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.ui.creating_an_order.interface_class.IUtilsV3Route;

import java.util.ArrayList;

import static com.opentadam.Injector.getClientData;

public abstract class FBaseMaps extends BaseFr {


    private final ControllerMaps controllerMaps = ControllerMaps.invoke(this);
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    public boolean isActivityCreated;
    private boolean isDisabledTimer;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated = false;
        controllerMaps.requestMultiplePermissions();
    }

    public void init() {
        getAWork().isInitMaps = true;
    }

    @Override
    public void onDestroyView() {
        App.bus.post(new BusStartOrStopPinAnimate(false));
        super.onDestroyView();
        getAWork().nameParentFragmentMaps = getStringParentClass();
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivityCreated = true;
        controllerMaps.onRequestLoc();
        getPaymentMethod();
    }

    @Override
    public void onPause() {

        hideDialogDisablegGlobalLocation();
        super.onPause();
    }

    abstract void responseValidateMyLoc(Location location);

    abstract void geolocationDisabledAll(boolean b);

    abstract void disabledStorage(boolean b);

    abstract void onlyOtherMetodsLocation(boolean b);

    abstract void geolocationDisabled(boolean b);

    abstract void unknownMethod(boolean b);

    abstract void onlyGPS(boolean b);

    abstract void onlyNetwork(boolean b);

    abstract void showDialogPermissions();

    void disablegGlobalLocation(boolean b) {
// запретил приложению доступ к геолокации
        Log.e("jhjhjhjh", "запретил приложению доступ к геолокаци " + b);
        if (b) {
            showDialogDisablegGlobalLocation();

        } else {
            hideDialogDisablegGlobalLocation();
        }

    }

    public void controllerDisabled() {

        ControllerNetwork controllerNetwork = getControllerNetwork();
        ModelLocationProviders modelLocationProviders = getModelLocationProviders();

        boolean disablegGlobalLocation = modelLocationProviders
                .isDisablegGlobalLocation();

        if (disablegGlobalLocation) {
            // на настройки ремишена
            showDialogPermissions();

        } else {
            // на настройки телефона
            controllerNetwork.showDialogSetEnamledLocation();
        }
    }

    synchronized boolean initSetMyLoation(Location loc) {
        if (getModelLocationProviders() != null && getModelLocationProviders().isDisablegGlobalLocation()) {
            App.bus.post(new BusStartOrStopPinAnimate(false));
            initResponseGD();
            getAWork().showPrivateDialog((BaseFr) FDialogRemisshen.newInstance());
            return true;

        }
        if (getModelLocationProviders() != null && getModelLocationProviders().isGeolocationDisabled()) {
            App.bus.post(new BusStartOrStopPinAnimate(false));
            initResponseGD();
            geolocationDisabled(true);
            return true;

        }
        if (loc == null) {
            //  TODO: обработать
            App.bus.post(new BusStartOrStopPinAnimate(true));
            final long timeMillis = System.currentTimeMillis();
            isDisabledTimer = false;


            continueTimer(timeMillis);

            return true;

        }
        return false;
    }

    private void initResponseGD() {

        responseValidateMyLoc(getStartLocation());
    }

    private void continueTimer(final long timeMillis) {
        if (!isVisible()) {
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVisible()) {
                    return;
                }

                boolean b = System.currentTimeMillis() - timeMillis < 10000;
                if (!b) {
                    App.bus.post(new BusStartOrStopPinAnimate(false));
                    initResponseGD();
/*                    if (!getModelLocationProviders().isEnabledNetwork()
                            || !getModelLocationProviders().isEnabledGPS()) {
                        geolocationDisabled(true);
                        Log.e("jhjhjhjh", "geolocationDisabled ");
                    }*/
                }

                if (isVisible() &&
                        !isDisabledTimer
                        && b) {
                    Location location = App.app.mMyGoogleLocation.getMLocation();
                    if (location == null) {
                        continueTimer(timeMillis);
                    } else {
                        App.bus.post(new BusStartOrStopPinAnimate(false));
                        isDisabledTimer = true;
                        responseValidateMyLoc(location);
                    }

                }
            }
        }, 100);
    }

    public Location getStartLocation() {
        return App.app.mMyGoogleLocation.getStartLocation();
    }

    public LatLng getLatLngStartLocation() {
        Location startLocation = getStartLocation();
        return new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
    }

    IUtilsV3Route getIUtilsV3Route() {
        return (IUtilsV3Route) getParentFragment();
    }

    public ModelLocationProviders getModelLocationProviders() {
        return controllerMaps
                .getModelLocationProviders();
    }

    public ControllerNetwork getControllerNetwork() {
        return controllerMaps.getControllerNetwork();
    }

    public ControllerMaps getControllerMaps() {

        return controllerMaps;
    }

    public LatLng getLatLng() {

        if (getClientData().getMarkerLocation() != null) {
            return getClientData().getMarkerLocation();
        }

        ArrayList<GpsPosition> routeLoc = getClientData()
                .getCreateRequest().getRouteLocation();
        if (routeLoc.size() != 0) {
            GpsPosition rl = routeLoc.get(0);
            return new LatLng(rl.lat, rl.lon);
        }

        Location location = App.app.mMyGoogleLocation.showCurrentLocation();
        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());

        }


        String latString = Injector.getSettingsStore().readString("LAT_MEMORY", null);
        String lonString = Injector.getSettingsStore().readString("LON_MEMORY", null);

        if (latString != null) {
            return new LatLng(Double.parseDouble(latString), Double.parseDouble(lonString));
        }

        ClientData clientData = getClientData();
        if (clientData.service != null) {

            return clientData.getLatLngCenterAdress();

        }
        Double[] defaultLatLon = App.app.hashBC.defaultLatLon;
        if (defaultLatLon != null) {
            return new LatLng(defaultLatLon[0], defaultLatLon[1]);
        }
        return new LatLng(0, 0);

    }

    void onRequestLoc() {
        controllerMaps.onRequestLoc();
    }


    void busEnabledStorageFotoPermission(boolean isEnabled) {
        controllerMaps.changeEnabledStorag(isEnabled);
    }

    private void getPaymentMethod() {

        RESTConnect restConnect = Injector.getRC();
        /*        if (restConnect == null)
                    return;*/
        restConnect.getPaymentMethod(getLatLng(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {

                    return;
                }

                getClientData().paymentMethods = apiResponse.paymentMethods;

            }
        });
    }

    void showDialogSetEnamledLocation() {
        getControllerNetwork().showDialogSetEnamledLocation();
    }

    void openApplicationSettingsLocation() {
        getControllerNetwork().openApplicationSettingsLocation();
    }

    void showDialogDisablegGlobalLocation() {

        if (isEnabledShowDialogRemiss()) {
            getAWork().showPrivateDialog((BaseFr) FDialogRemisshen.newInstance());
        }
    }

    boolean isEnabledShowDialogRemiss() {
        String nameParentFragmentMapsHash = getAWork().nameParentFragmentMaps;
        Fragment f = getParentFragment();
        return f instanceof V3FRoute
                && nameParentFragmentMapsHash == null;
    }

    private String getStringParentClass() {
        if (getParentFragment() == null)
            return null;
        return getParentFragment().getClass().getName();
    }

    void hideDialogDisablegGlobalLocation() {

        getAWork().hidePrivateDialog();
    }

    void hideControllerDisabled(View controllerDisabled, boolean setVisible) {
        controllerDisabled.setVisibility(setVisible && isEnabledShowDialogRemiss() ? View.VISIBLE : View.GONE);
    }

    public void onCameraIdle(View controllerDisabled) {
        isDisabledTimer = true;
        hideControllerDisabled(controllerDisabled, false);
    }
}
