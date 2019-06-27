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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.GpsPosition;

import java.util.ArrayList;
import java.util.List;

public interface IUPmaps {
    void setEnabledULoc();

    void setDisabledULoc();

    void initErrorGeozoneMapReady();

    void initMapReady(boolean b);

    void initMapReady(GoogleMap googleMap, boolean isPage0);

    void setMyLocation();

    void showDrivers(List<Driver> driverList);

    void clearMarkers();

    void showCarLocation(GpsPosition location);

    Marker addCarMarker(GpsPosition location);

    void initializeMap(LatLng latLng);

    void setPaddingButtonMaps(int px, int px1, int px2, int px3);

    void addMarkerAddress(ArrayList<Address> getRoute);

    void animateCarForMap(double latitude, double longitude);

    void moveCameraZoomMinus();

    void moveCameraZoomPlus();

    void addArrayPointToMap(List<Driver> driverList, double[] latLonOneList);

    void setLatLngBounds(LatLng latLng, LatLng latLngClient, boolean isStateSet);

    void initPolyline(List<LatLng> mPoints);

    void exitWork();

    void initClientDinamiMarkerPos(LatLng position);

    void removeClientDinamiMarker(LatLng position);

    void removeCarMarker();

    void clearMarkerCar();

    void animateHidePinMaps();

    void setCenterMaps(LatLng latLngClient);

    void setMarginMapsDefault();
}
