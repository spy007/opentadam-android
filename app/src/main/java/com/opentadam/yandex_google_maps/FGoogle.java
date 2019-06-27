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

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusOkRem;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.utils.CarIconAnimate;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;

public class FGoogle extends FBaseMaps implements GoogleMap.OnMarkerClickListener
        , OnMapReadyCallback
        , GoogleMap.OnMapClickListener
        , GoogleMap.OnCameraMoveStartedListener
        , GoogleMap.OnCameraIdleListener
        , IUPmaps {


    private final float[] alphaMarker = {0.3f};
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    //////////
    @InjectView(R.id.controller_cont_disabled)
    View controllerDisabled;
    private GoogleMap mMap;
    private ArrayList<Marker> markers;
    private int posAnimate = 0;
    private SupportMapFragment mapFragment;
    private Marker carMarker;
    private CameraUpdate cameraUpdate;
    private ArrayList<Marker> arrMarker;
    private BitmapDescriptor iconCarLocation;
    private boolean isInitZoom;
    private double[] latLonOneList;
    private ArrayList<Marker> arrMarkerCar;
    private Marker clientDinamiMarker;
    private String search;
    private boolean isDisabledCameraIdle;
    private boolean isPage0;


    //////////
    public FGoogle() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {

        return new FGoogle().withViewId(R.layout.fg_maps);
    }

    public static Fragment newInstance(String search) {


        return new FGoogle()
                .withArgument("search", search)
                .withViewId(R.layout.fg_maps);

    }

    @Subscribe
    public void onBusOkRem(BusOkRem e) {
        super.openApplicationSettingsLocation();
    }

    // TODO
    @OnClick(R.id.controller_cont_disabled)
    public void onControllerDisabled() {
        if (isDisabledWork()) return;
        super.showDialogSetEnamledLocation();
        super.hideControllerDisabled(controllerDisabled, false);
    }


    @Override
    void responseValidateMyLoc(Location location) {
        if (isDisabledWork()) return;

        if (Injector.getClientData().isLatLngDisabled(location))
            return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(Injector.getSettingsStore().getZoomMaps())
                .build();
        CameraUpdate cUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        mMap.moveCamera(cUpdate);


        if (getIUtilsV3Route() != null && !isDisabledCameraIdle)
            getIUtilsV3Route().onCameraIdle(latLng, true);
    }

    @Override
    void showDialogPermissions() {

    }

    @Override
    void geolocationDisabledAll(boolean b) {
        if (isDisabledWork()) return;
// отключил геолокацию в телефоне или запретил приложению доступ к геолокации
        Log.e("jhjhjhjh", "отключил геолокацию в телефоне или запретил приложению доступ к геолокации " + b);

        if (search == null) {
            if (!b) {
                setEnabledULoc();
            } else {
                //    App.app.mMyGoogleLocation.mLastLocation = null;
                setDisabledULoc();
            }
        }
    }

    @Override
    void disabledStorage(boolean b) {

// запретил приложению доступ к карте памяти
        Log.e("jhjhjhjh", "запретил приложению доступ к карте памятии " + b);
    }

    @Override
    void geolocationDisabled(boolean b) {
// отключил геолокацию в телефоне
        Log.e("jhjhjhjh", "отключил геолокацию в телефоне " + b);
        super.hideControllerDisabled(controllerDisabled, b);

    }

    @Override
    void onlyOtherMetodsLocation(boolean b) {
// хороший вариант как минимум жпс + сеть
        Log.e("jhjhjhjh", "хороший вариант как минимум жпс + сеть " + b);
    }

    @Override
    void unknownMethod(boolean b) {
// неизвестный метод
        Log.e("jhjhjhjh", "неизвестный метод " + b);
    }

    @Override
    void onlyGPS(boolean b) {
// только на базе жпс
        Log.e("jhjhjhjh", "только на базе жпс");
    }

    @Override
    void onlyNetwork(boolean b) {
// только на базе сети
        Log.e("jhjhjhjh", "только на базе сети");
    }

    @Override
    public void onPause() {
        super.onPause();
        setMemoryZoomMaps();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init() {
        search = getArguments().getString("search", null);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_google_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void initErrorGeozoneMapReady() {
        if (isDisabledWork()) return;

        mMap.setPadding(getPX(0)
                , getPX(0)
                , getPX(0)
                , getPX(0));
        if (getClientData().getMarkerLocation() == null)
            return;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getClientData().getMarkerLocation()));
    }

    public void initMapReady(boolean isPage0) {
        this.isPage0 = isPage0;
        setDefMarginAll(isPage0);
        if (!getAWork().isInitMaps) {
            // TODO redmine.7220.by/issues/28
            super.init();
            LatLng markerLocation = getLatLngStartLocation();
            if (markerLocation != null)
                setMyLocation();
            else {
                setCenterMap();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LatLng markerLocation = getLatLngStartLocation();
                        if (markerLocation != null)
                            setMyLocation();
                    }
                }, 5000);
            }
        } else setCenterMap();
    }

    @Override
    public void setMarginMapsDefault() {

        if (isDisabledWork()) return;

        Fragment f = getParentFragment();
        if (search == null && f instanceof V3FRoute) {
            V3FRoute v3FRoute = (V3FRoute) getParentFragment();
            mMap.setPadding(getPX(10)
                    , getPX(10)
                    , getPX(10)
                    , getPX(v3FRoute.getPageDP()));
        }


    }

    private void setDefMarginAll(boolean isPage0) {

        if (isDisabledWork()) return;
        this.isPage0 = isPage0;
        if (search == null) {
            setMarginMapsDefault();
            return;
        }

        if ("searshCar".equals(search)) {
            mMap.setPadding(getPX(10)
                    , getPX(10)
                    , getPX(10)
                    , getPX(64));
            return;
        }

        if ("searsh".equals(search)) {
            mMap.setPadding(getPX(10)
                    , getPX(10)
                    , getPX(10)
                    , getPX(10));

        }

    }

    private boolean isDisabledWork() {
        return mMap == null || !isVisible();
    }

    @Override
    public void setEnabledULoc() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext()
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (isDisabledWork()) return;


        mMap.setMyLocationEnabled(true); //определение местоположения
    }

    @Override
    public void setDisabledULoc() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext()
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (isDisabledWork()) return;
        mMap.setMyLocationEnabled(false); //определение местоположения
    }


    @Override
    public void setPaddingButtonMaps(int px, int px1, int px2, int px3) {
        if (isDisabledWork()) return;
        if ("searshCar".equals(search)) {
            mMap.setPadding(getPX(10)
                    , getPX(10)
                    , getPX(10)
                    , getPX(64));
            return;
        }
        mMap.setPadding(px, px1, px2, px3);
    }

    @Override
    public void initMapReady(GoogleMap googleMap, boolean isPage0) {
        if (!isVisible())
            return;

        if (googleMap != null)
            mMap = googleMap;
        if (isDisabledWork()) return;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setBuildingsEnabled(true);
        mMap.setOnCameraMoveStartedListener(this);
        if ("searshCar".equals(search)) {
            mMap.setPadding(getPX(10)
                    , getPX(10)
                    , getPX(10)
                    , getPX(64));
        }

        CreateRequest createRequest = getClientData().getCreateRequest();
        if (createRequest == null)
            return;

        this.isPage0 = isPage0;
        initializeMap(isPage0);
    }

    @Override
    public void initializeMap(LatLng latLng) {
        if (isDisabledWork()) return;
        UiSettings uiSettings = mMap.getUiSettings();


        uiSettings.setZoomControlsEnabled(false); //кнопки зума
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomGesturesEnabled(true);


        uiSettings.setMyLocationButtonEnabled(false); //кнопка определения местоположения

        if (search == null) {
            setEnabledULoc();
        }


        cameraUpdate = latLng == null ? CameraUpdateFactory.zoomTo(Injector.getSettingsStore().getZoomMaps())
                : CameraUpdateFactory.newLatLngZoom(latLng, Injector.getSettingsStore().getZoomMaps());

        mMap.moveCamera(cameraUpdate);
    }


    @Override
    public void addMarkerAddress(ArrayList<Address> route) {
        if (isDisabledWork()) return;
        if (route == null || route.size() == 0)
            return;

        arrMarker = new ArrayList<>();


        int i = 0;
        for (Address address : route) {

            if (i == 5)
                return;
            if (address == null)
                return;
            GpsPosition position = address.position;
            if (position == null)
                return;

            double lat = position.lat;
            double lon = position.lon;

            String title = address.getNameAdressSearsh();
            if (title == null)
                title = getString(R.string.point_to_maps);

            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromBitmap(Utilites.createMarker(
                            initColor(R.color.colorPrimary),
                            initColor(R.color.text_white),
                            (i + 1) + "",
                            initColor(R.color.text_white), 24, 24));

            Marker mark = mMap.addMarker(
                    new MarkerOptions().icon(icon)
                            .position(new LatLng(lat, lon))
                            .anchor(0.5f, 0.5f)
                            .title(title)
            );
            arrMarker.add(mark);
            i++;
        }
    }

    @Override
    public void animateCarForMap(double latitude, double longitude) {
        if (isDisabledWork()) return;
        LatLng latLng = new LatLng(latitude, longitude);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, Injector.getSettingsStore().getZoomMaps());
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void moveCameraZoomMinus() {
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    @Override
    public void moveCameraZoomPlus() {
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    @Override
    public void addArrayPointToMap(List<Driver> drivers, double[] latLonOneList) {
        if (isDisabledWork()) return;
        this.latLonOneList = latLonOneList;
        if (drivers == null || drivers.size() == 0)
            return;

        if (arrMarkerCar == null)
            arrMarkerCar = new ArrayList<>();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        // добавляем тех кого нет
        for (Driver driver : drivers) {
            boolean isExsist = false;
            for (Marker marker : arrMarkerCar) {
                Driver driverMarker = (Driver) marker.getTag();
                if ((driverMarker != null ? driverMarker.id : 0) == driver.id)
                    isExsist = true;
            }

            if (!isExsist) {
                GpsPosition location = driver.location;

                Marker marker = addMarkerCar(location);
                marker.setTag(driver);
                arrMarkerCar.add(marker);
            }
        }


        // удаляем тех кто отвалился

        ArrayList<Marker> temp = new ArrayList<>();
        for (Marker marker : arrMarkerCar) {
            Driver driverMarker = (Driver) marker.getTag();
            boolean isExsist = false;
            for (Driver driver : drivers) {
                if ((driverMarker != null ? driverMarker.id : 0) == driver.id) {
                    isExsist = true;
                    marker.setTag(driver);
                    temp.add(marker);
                    latLngBuilder.include(driver.location.getLatLng());
                    new CarIconAnimate().initCarMarker(marker, 5000);
                }
            }

            if (!isExsist) {
                marker.remove();
            }
        }

        arrMarkerCar = temp;


        if (!isInitZoom) {
            isInitZoom = true;
            initZoomAvto(latLngBuilder);
        }
    }

    @Override
    public void setLatLngBounds(LatLng latLngCar, LatLng latLngClient, boolean isStateSet) {
        if (isDisabledWork()) return;
        if (isStateSet) {
            cameraUpdate = CameraUpdateFactory.newLatLng(latLngCar);
        }
        int sizeW = getResources().getDisplayMetrics().widthPixels;
        int sizeH = getResources().getDisplayMetrics().heightPixels;
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        latLngBuilder.include(latLngCar);
        latLngBuilder.include(latLngClient);
        LatLngBounds latLngBounds = latLngBuilder.build();

        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, sizeW, sizeH,
                isStateSet ? sizeW / 4 : getPX(50));
        mMap.moveCamera(track);
        mMap.moveCamera(cameraUpdate);
    }

    @Override
    public void initPolyline(List<LatLng> mPoints) {
        if (isDisabledWork() || mPoints == null || mPoints.size() == 0) {
            return;
        }

        PolylineOptions line = new PolylineOptions();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        line.width(getPX(9)).color(initColor(R.color.bg_while));


        int size = mPoints.size();
        for (int i = 0; i < size; i++) {

            line.add(mPoints.get(i));
            latLngBuilder.include(mPoints.get(i));
        }

        if (size == 0) {
            return;
        }

        line.zIndex(1000f);

        mMap.addPolyline(line);

        line.width(getPX(5)).color(initColor(R.color.colorPrimary));

        mMap.addPolyline(line);
    }

    @Override
    public void exitWork() {
        if (isDisabledWork()) return;


        if (arrMarker != null) {
            for (Marker m : arrMarker) {
                if (m != null)
                    m.remove();
            }
        }
        mMap.stopAnimation();
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

    }

    @Override
    public void initClientDinamiMarkerPos(LatLng position) {

        if (isDisabledWork()) return;

        if (clientDinamiMarker == null) {

            Bitmap iconDrawableToMaps = TintIcons.getBitmap(R.drawable.ic_flag);

            clientDinamiMarker = mMap.addMarker(
                    new MarkerOptions().icon(BitmapDescriptorFactory
                            .fromBitmap(iconDrawableToMaps))
                            .position(position));
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false); //кнопка определения местоположения
        if (ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true); //определение местоположения

    }

    @Override
    public void removeClientDinamiMarker(LatLng position) {
        if (isDisabledWork()) return;

        if (clientDinamiMarker != null)
            clientDinamiMarker.remove();

        mMap.getUiSettings().setMyLocationButtonEnabled(false); //кнопка определения местоположения
        if (ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getAWork(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(false); //определение местоположения
    }

    @Override
    public void removeCarMarker() {
        if (isDisabledWork()) return;
        if (carMarker != null) {
            carMarker.remove();
            carMarker = null;
        }
    }

    @Override
    public void clearMarkerCar() {
        if (isDisabledWork()) return;
        if (arrMarkerCar == null || arrMarkerCar.size() == 0)
            return;
        for (Marker marker : arrMarkerCar) {
            marker.remove();
        }

        arrMarkerCar.clear();
    }

    private void initZoomAvto(LatLngBounds.Builder latLngBuilder) {
        if (latLonOneList == null)
            return;
        if (!isVisible())
            return;
        double lat = latLonOneList[0];
        double lon = latLonOneList[1];
        LatLng latLng = new LatLng(lat, lon);
        latLngBuilder.include(latLng);

        int sizeW = getResources().getDisplayMetrics().widthPixels;
        int sizeH = getResources().getDisplayMetrics().heightPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory
                .newLatLngBounds(latLngBounds, sizeW, sizeH, getPX(5));

        mMap.moveCamera(track);

        cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(cameraUpdate);
    }

    private Marker addMarkerCar(GpsPosition location) {

        if (iconCarLocation == null)

            iconCarLocation = BitmapDescriptorFactory.fromResource(R.drawable.car_icon);

        return mMap.addMarker(
                new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(iconCarLocation)
                        .position(new LatLng(location.lat, location.lon))

        );
    }

    private void initializeMap(boolean isPage0) {

        if (isDisabledWork()) return;

        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(false); //кнопки зума
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomGesturesEnabled(true);


        uiSettings.setMyLocationButtonEnabled(false); //кнопка определения местоположения

        if (search == null) {

            setEnabledULoc();
        }

        this.isPage0 = isPage0;
        setDefMarginAll(isPage0);

        if (!Injector.getClientData().isNotNullmarkerLocation) {
            CameraUpdate cUpdate = CameraUpdateFactory
                    .newLatLngZoom(mMap.getCameraPosition().target, Injector.getSettingsStore().getZoomMaps());
            mMap.moveCamera(cUpdate);
            setMyLocation();
            return;
        }

        LatLng latLng = Injector.getClientData().getMarkerLocation();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(Injector.getSettingsStore().getZoomMaps())
                .build();
        CameraUpdate cUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        mMap.moveCamera(cUpdate);
    }

    private void setCenterMap() {
        if (isDisabledWork()) return;
        if (getClientData().getMarkerLocation() != null) {
            // TODO redmine.7220.by/issues/28
            mMap.moveCamera(CameraUpdateFactory.newLatLng(getClientData().getMarkerLocation()));
            return;
        }

        Location location = App.app.mMyGoogleLocation.showCurrentLocation();

        if (location != null) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (search == null) {
                getClientData().setMarkerLocation(latLng);
            }
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLng(latLng));
            return;
        }

        Double[] defaultLatLon = App.app.hashBC.defaultLatLon;
        if (defaultLatLon == null)
            return;
        LatLng latLng = new LatLng(defaultLatLon[0], defaultLatLon[1]);
        if (search == null) {
            getClientData().setMarkerLocation(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory
                .newLatLng(latLng));
    }


    @Override
    public void onCameraIdle() {
        if (isDisabledWork()) return;
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();

        if (!isVisible() || mMap == null || createRequest == null)

            return;

        int distance = 1000;
        CameraPosition cameraPosition = mMap.getCameraPosition();
        LatLng mLocation = cameraPosition.target;

        if (Injector.getClientData().isLatLngDisabled(mLocation))
            return;

        if (mLocation.latitude == 0 || mLocation.longitude == 0)
            return;

        if (getClientData().getDefTarif() != null) {
            Tarif tarif = Injector.getClientData().getDefTarif();
            float[] results = new float[1];
            android.location.Location.distanceBetween(tarif.lat,
                    tarif.lon,
                    mLocation.latitude,
                    mLocation.longitude, results);

            distance = (int) results[0];
            tarif.lat = mLocation.latitude;
            tarif.lon = mLocation.longitude;
            LatLng latLngTarifDef = new LatLng(tarif.lat, tarif.lon);
            Injector.getSettingsStore().setLatLngTarifDef(latLngTarifDef);
            Injector.getClientData().setDefTarif(tarif);
        }


        if (search == null) {
            getClientData().setMarkerLocation(cameraPosition.target);
        }
        boolean isEnabledUpdTarif = distance > Constants.DISTANSE_BASE_DISABLED;

        if (getIUtilsV3Route() != null && !isDisabledCameraIdle)
            getIUtilsV3Route().onCameraIdle(mLocation, isEnabledUpdTarif);
    }

    @Override
    public void animateHidePinMaps() {

        if (!isVisible())
            return;
        View mapFragmentView = mapFragment.getView();

        View googleWatermark = mapFragmentView.findViewWithTag("GoogleWatermark");

        googleWatermark.animate()
                .alpha(0)
                .setDuration(250);


    }

    @Override
    public void setCenterMaps(LatLng latLngClient) {

    }

    @Override
    public void showCarLocation(GpsPosition location) {

        if (isDisabledWork()) return;
        carMarker = addCarMarker(location);
        new CarIconAnimate().initCarMarker(carMarker, 5000);

    }

    @Override
    public Marker addCarMarker(GpsPosition location) {
        double lat = location.lat;
        double lon = location.lon;
        LatLng latLngCar = new LatLng(lat, lon);
        if (carMarker == null) {
            Bitmap iconDrawableToMaps = Utilites.createIconDrawableToMaps(34, 42, R.drawable.car_icon);

            carMarker = mMap.addMarker(
                    new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(iconDrawableToMaps))
                            .anchor(0.5f, 0.5f)
                            .position(latLngCar));
        }
        carMarker.setTag(location);
        return carMarker;
    }

    @Override
    public void setMyLocation() {

        if (isDisabledWork()) return;

/*        Location location = App.app.mMyGoogleLocation.showCurrentLocation();

        if (location == null || mMap == null)
            return;*/
        // TODO
        Location location = App.app.mMyGoogleLocation.getMLocation();

        if (super.initSetMyLoation(location))
            return;

        responseValidateMyLoc(location);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isDisabledWork()) return;
        if (latLng != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    public void setMemoryZoomMaps() {
        if (isDisabledWork() || search != null) return;
        if (mMap != null && mMap.getCameraPosition() != null) {
            float zoom = mMap.getCameraPosition().zoom;
            Injector.getSettingsStore().setZoomMaps(zoom);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {

        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        if (!isVisible() || mMap == null || createRequest == null)

            return;

        getIUtilsV3Route().onCameraMoveStarted(i);
    }


    public void showDrivers(final List<Driver> driverList) {
        if (isDisabledWork()) return;
        if (getClientData().getMarkerLocation() == null
                || mMap == null || driverList == null || driverList.size() == 0) {
            return;
        }

        if (search == null && App.app.hashBC.zoomMaxMapsCarList != null
                && App.app.hashBC.zoomMinMapsCarList != null) {
            zoomToBounds(driverList);
        }
        posAnimate = 0;
        alphaMarker[0] = 0.3f;
        clearMarkers();
        forAnimate(driverList);


    }

    private void zoomToBounds(List<Driver> driverList) {
        isDisabledCameraIdle = true;
        int px = 60; //getPX(!isPage0 ? 60 : 60);
        int sizeW = getResources().getDisplayMetrics().widthPixels;
        int sizeH = getResources().getDisplayMetrics().heightPixels - px;

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        latLngBuilder.include(getClientData().getMarkerLocation());
        for (final Driver driver : driverList) {
            GpsPosition location = driver.location;
            double vLat = getClientData().getMarkerLocation().latitude - location.lat;
            double vLon = getClientData().getMarkerLocation().longitude - location.lon;
            latLngBuilder.include(new LatLng(getClientData().getMarkerLocation().latitude + vLat
                    , getClientData().getMarkerLocation().longitude + vLon));
            latLngBuilder.include(new LatLng(location.lat, location.lon));
        }
        LatLngBounds latLngBounds = latLngBuilder.build();


        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, sizeW
                , sizeH, getPX(24));

        mMap.moveCamera(track);

        float zoom = mMap.getCameraPosition().zoom;
        if (zoom > App.app.hashBC.zoomMaxMapsCarList) {
            zoom = App.app.hashBC.zoomMaxMapsCarList;
        }


        CameraUpdate cUpdate = CameraUpdateFactory.zoomTo(zoom);

        mMap.moveCamera(cUpdate);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isDisabledCameraIdle = false;
            }
        }, 500);
    }

    private void bodyAnimDrivers(List<Driver> driverList) {
        if (isDisabledWork()) return;
        for (final Driver driver : driverList) {
            postD(driver);

        }
        alphaMarker[0] += 0.1f;
        if (posAnimate < 6) {
            posAnimate++;
            forAnimate(driverList);
        }
    }

    private void forAnimate(final List<Driver> driverList) {
        if (isDisabledWork()) return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                bodyAnimDrivers(driverList);
            }

        }, 28);
    }

    private void postD(final Driver driver) {
        if (isDisabledWork()) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                LatLng latLngCar = driver.location.getLatLng();


                Bitmap iconDrawableToMaps = Utilites.createScaleIconDrawableToMaps(34
                        , 42
                        , R.drawable.car_icon
                        , posAnimate);


                Marker carMarker = mMap.addMarker(
                        new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(iconDrawableToMaps))
                                .anchor(0.5f, 0.5f)
                                .rotation(driver.rotade)
                                .alpha(alphaMarker[0])
                                .position(latLngCar));

                markers.add(carMarker);

            }

        });
    }


    public void clearMarkers() {
        if (isDisabledWork()) return;
        if (!isVisible() || mMap == null || markers == null) {
            markers = new ArrayList<>();
        } else {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!isVisible())
            return;

        mMap = googleMap;
        if (getIUtilsV3Route() != null) {
            getIUtilsV3Route().onMapReady();
        }
    }

}
