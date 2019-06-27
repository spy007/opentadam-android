/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentadam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.opentadam.bus.BusLocation;
import com.opentadam.bus.HiveBus;


public class BaseGoogleLocation implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    public Location mLastLocation;
    private final Context act;
    private GoogleApiClient mGoogleApiClient;

    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }

    private LocationRequest mLocationRequest;
    private boolean isStart;

    public BaseGoogleLocation(Context act) {
        this.act = act;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void init() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(act)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    public void deactivate() {
        stopLocationUpdates();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }


    public void startLocationUpdates() {
        if (mGoogleApiClient.isConnected() && !isStart) {
            if (ActivityCompat.checkSelfPermission(act
                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            isStart = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    public void stopLocationUpdates() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && isStart) {
            isStart = false;
            FusedLocationProviderApi fusedLocationApi = LocationServices.FusedLocationApi;
            fusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        }
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setSmallestDisplacement(0); // 0 meters
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        boolean b = act == null || location.getLongitude() != 0;

        if (b)

            updateUI(location);
    }

    private synchronized void updateUI(Location location) {

        //   location.setLatitude(58.5334209289421);
        //   location.setLongitude(31.3029624573941);
        location.setTime(System.currentTimeMillis());


        //     setMemoryLocation(location);


        mLastLocation = location;
        postLocation(location);
        App.bus.post(new BusLocation(location));
    }

    public void setMemoryLocation(LatLng latLng) {

        Injector.getSettingsStore().writeString("MarkerLocation", new Gson().toJson(latLng));
    }

    public LatLng getMemoryLocation() {
        String cl = Injector.getSettingsStore().readString("MarkerLocation", null);
        if (cl == null || "null".equals(cl))
            return null;
        return new Gson().fromJson(cl, LatLng.class);
    }


    public Location getStartLocation() {


        if (mLastLocation != null) {
            return mLastLocation;
        }

        if (Injector.getClientData().getMarkerLocation() != null) {
            LatLng markerLocation = Injector.getClientData().getMarkerLocation();
            if (!Injector.getClientData().isLatLngDisabled(markerLocation))
                return translateLatLng(markerLocation);
        }

        String cl = Injector.getSettingsStore().readString("MarkerLocation", null);
        if (cl != null && !"null".equals(cl)) {
            LatLng latLng = new Gson().fromJson(cl, LatLng.class);
            if (!Injector.getClientData().isLatLngDisabled(latLng))
                return translateLatLng(latLng);
        }

        Double[] defaultLatLon = App.app.hashBC.defaultLatLon;

        Location loo = new Location("GPS");
        loo.setLatitude(defaultLatLon[0]);
        loo.setLongitude(defaultLatLon[1]);
        loo.setTime(System.currentTimeMillis());
        loo.setAccuracy(10000);

        return loo;
    }


    private Location translateLatLng(LatLng latLng) {
        Location loo = new Location("GPS");
        loo.setLatitude(latLng.latitude);
        loo.setLongitude(latLng.longitude);
        loo.setTime(System.currentTimeMillis());
        loo.setAccuracy(10);
        return loo;
    }


    private void postLocation(Location location) {
        HiveBus.postBusGPSLocation(location);
        HiveBus.postBusEnabledUserLatLon();
    }


    public Location showCurrentLocation() {
/*        if (mLastLocation == null) {
            Tarif libDefTarif = getLibDefTarif();
            if (libDefTarif != null) {
                Location loo = new Location("GPS");
                loo.setLatitude(libDefTarif.lat);
                loo.setLongitude(libDefTarif.lon);
                return loo;
            } else if (App.app.hashBC.defaultLatLon != null) {
                Location loo = new Location("GPS");
                loo.setLatitude(App.app.hashBC.defaultLatLon[0]);
                loo.setLongitude(App.app.hashBC.defaultLatLon[1]);
                return loo;
            }
        }*/
        return getStartLocation();
    }

    public LatLng getLatLngMyLoc() {
        Location location = showCurrentLocation();

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public Location getMLocation() {
        return mLastLocation;
    }
}
