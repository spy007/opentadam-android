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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusLocation;
import com.opentadam.bus.BusOkRem;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;
import com.squareup.otto.Subscribe;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.opentadam.Injector.getClientData;

public class FOsmMaps extends FBaseMaps implements
        IUPmaps {
    private final float[] alphaMarker = {0.3f};
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.map_osm_fragment)
    org.osmdroid.views.MapView mapView;
    @InjectView(R.id.map_osm_cont)
    FrameLayout mapOsmCont;
    ///
    @InjectView(R.id.controller_cont_disabled)
    View controllerDisabled;


    private String search;
    private int mapViewHeight = -1;
    private LatLng mLocation;
    private LatLng mLocationHash;
    private Runnable runnableOsmCameraIdle;
    private int posAnimate = 0;
    private org.osmdroid.views.overlay.Marker driverMarker;
    private org.osmdroid.views.overlay.Marker clientDinamiMarker;
    private Runnable runnableCameraIdle;
    private boolean isBlokUpdLoc;
    private ArrayList<Address> getRoute;
    private ArrayList<org.osmdroid.views.overlay.Marker> markerArrayList = new ArrayList<>();
    private DirectedLocationOverlay overlay;
    private boolean isDisabledCameraIdle;
    private boolean isErrorGeozoneMapReady;
    ////
    private boolean isEnabledStore;


    public FOsmMaps() {
        // Required empty public constructor
    }

    public static Fragment newInstance(boolean isPage0) {

        return new FOsmMaps()

                .withViewId(R.layout.map_osm_fragment);
    }

    public static Fragment newInstance(String search) {

        return new FOsmMaps()
                .withArgument("search", search)
                .withViewId(R.layout.map_osm_fragment);
    }

    public static Fragment newInstance() {

        return new FOsmMaps()
                .withViewId(R.layout.map_osm_fragment);
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) {
            org.osmdroid.views.overlay.Marker.cleanDefaults();
            InfoWindow.closeAllInfoWindowsOn(mapView);

            mapView.onDetach();
        }
        mapView = null;
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        setMemoryZoomMaps();
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && isEnabledStore != getEnabledStore()) {
            getIUtilsV3Route().initOsm();
            return;

        }
        if (mapView != null) {
            mapView.onResume();


        }


    }

    @OnTouch(R.id.map_tush)
    public boolean onToushMap(View v, MotionEvent event) {
        if (isDisabledWork())
            return false;
        isBlokUpdLoc = true;
        if (runnableCameraIdle != null) {
            mHandler.removeCallbacks(runnableCameraIdle);
        }
        runnableCameraIdle = new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;

                isBlokUpdLoc = false;
            }
        };
        mHandler.postDelayed(runnableCameraIdle, 5000);


        return false;
    }

    // TODO
    @OnClick(R.id.controller_cont_disabled)
    public void onControllerDisabled() {
        if (isDisabledWork())
            return;
        super.showDialogSetEnamledLocation();
        super.hideControllerDisabled(controllerDisabled, false);
    }

///////////

    @Subscribe
    public void onBusOkRem(BusOkRem e) {
        super.openApplicationSettingsLocation();
    }


    @Override
    void responseValidateMyLoc(Location location) {
        if (Injector.getClientData().isLatLngDisabled(location))
            return;
        if (isDisabledWork())
            return;
        IMapController mapController = mapView.getController();

        GeoPoint startPoint = new GeoPoint(location.getLatitude()
                , location.getLongitude());
        if (search == null) {
            LatLng markerLocation = new LatLng(location.getLatitude()
                    , location.getLongitude());
            getClientData().setMarkerLocation(markerLocation);
        }


        mapController.setCenter(startPoint);
        if (runnableOsmCameraIdle != null) {
            mHandler.removeCallbacks(runnableOsmCameraIdle);
        }
        if (getIUtilsV3Route() != null && !isDisabledCameraIdle)
            getIUtilsV3Route().onCameraIdle(getClientData().getMarkerLocation(), true);
        if (runnableOsmCameraIdle != null) {
            mHandler.removeCallbacks(runnableOsmCameraIdle);
        }
    }

    @Override
    void showDialogPermissions() {

    }

    @Override
    void geolocationDisabledAll(boolean b) {

// отключил геолокацию в телефоне или запретил приложению доступ к геолокации
        Log.e("jhjhjhjh", "отключил геолокацию в телефоне или запретил приложению доступ к геолокации " + b);
        if (isDisabledWork())
            return;
        if (search == null) {
            if (!b) {
                setEnabledULoc();
            } else {
                // App.app.mMyGoogleLocation.mLastLocation = null;
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
        if (isDisabledWork())
            return;
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
        Log.e("jhjhjhjh", "только на базе жпс " + b);
    }

    @Override
    void onlyNetwork(boolean b) {
// только на базе сети
        Log.e("jhjhjhjh", "только на базе сети " + b);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            isEnabledStore = getEnabledStore();
        }

        init();
    }

    private boolean getEnabledStore() {
        boolean isDisabledStorage = ActivityCompat.checkSelfPermission(getAWork()
                , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        return !isDisabledStorage;
    }

    public void init() {
        search = getArguments().getString("search", null);

        if ("errorNet".equals(search)) {
            initErrorGeozoneMapReady();
        } else if (!isErrorGeozoneMapReady) {
            setMarginMapsDefault();
        }

        if (getIUtilsV3Route() != null) {
            getIUtilsV3Route().onMapReady();
        }

        initMapOsm();

        if (isErrorGeozoneMapReady) {
            initErrorGeozoneMapReady();
        }
    }


    private void initMapOsm() {
        if (mapView != null) {


            CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getAWork());

            copyrightOverlay.setTextSize(10);
            copyrightOverlay.setOffset(getPX(4), getPX(10));
            mapView.getOverlays().add(copyrightOverlay);
            mapView.setMultiTouchControls(true);
            mapView.setBuiltInZoomControls(false);
            mapView.setUseDataConnection(true);
            IMapController mapController = mapView.getController();
            mapController.setZoom(Injector.getSettingsStore().getZoomMaps());


            mapView.addMapListener(new MapListener() {
                @Override
                public boolean onScroll(ScrollEvent event) {
                    if (isDisabledWork())
                        return true;
                    updateInfo();
                    return true;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    if (isDisabledWork())
                        return true;

                    updateInfo();
                    double zoomLevel = event.getZoomLevel();
                    if (zoomLevel >= mapView.getMinZoomLevel()
                            && zoomLevel <= mapView.getMaxZoomLevel()) {
                        Injector.getSettingsStore().setZoomMaps((float) zoomLevel);
                    }
                    return true;
                }
            });

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isVisible() || getIUtilsV3Route() == null)
                        return;

                    getIUtilsV3Route().allStopProgressPin();
                }
            }, 2000);

            if (search == null) {

                setEnabledULoc();
            }

            mapView.invalidate();
            //  super.getService();
        }

    }

    private void updateInfo() {
        IGeoPoint mapCenter = mapView.getMapCenter();
        double latitude = mapCenter.getLatitude();
        double longitude = mapCenter.getLongitude();
        if (Injector.getClientData().isLatLngDisabled(mapCenter))
            return;

        mLocation = new LatLng(latitude, longitude);
        onCameraIdle();
    }

    private void onCameraIdle() {
        if (isDisabledWork())
            return;
        if (!isVisible() || "searshCar".equals(search))
            return;

        if (mLocationHash == null) {
            mLocationHash = mLocation;
        } else {
            if (runnableOsmCameraIdle != null) {
                mHandler.removeCallbacks(runnableOsmCameraIdle);
            }
            runnableOsmCameraIdle = new Runnable() {
                @Override
                public void run() {
                    onCameraIdleRun();
                }
            };
            mHandler.postDelayed(runnableOsmCameraIdle, 300);
        }

    }

    private void onCameraIdleRun() {
        if (isDisabledWork())
            return;
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        if (!isVisible() || createRequest == null
                || "searshCar".equals(search))
            return;

        int distance = 1000;

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
            getClientData().setMarkerLocation(mLocation);
        }

        boolean isEnabledUpdTarif = distance > Constants.DISTANSE_BASE_DISABLED;

        if (getIUtilsV3Route() != null && !isDisabledCameraIdle) {
            clearMarkers();
            super.onCameraIdle(controllerDisabled);
            getIUtilsV3Route().onCameraIdle(mLocation, isEnabledUpdTarif);
        }
    }

    @Override
    public void setMarginMapsDefault() {

        if (isDisabledWork())
            return;


        if (mapViewHeight == -1) {
            mapViewHeight = mapView.getHeight();
        }
        Fragment f = getParentFragment();
        if (search == null && f instanceof V3FRoute) {
            V3FRoute v3FRoute = (V3FRoute) getParentFragment();
            mapOsmCont.setPadding(0, 0, 0, getPX(v3FRoute.getPageDP()));
        } else if ("searshCar".equals(search))
            mapOsmCont.setPadding(0, 0, 0, getPX(60));
        else if ("searsh".equals(search))
            mapOsmCont.setPadding(0, 0, 0, getPX(0));


        mapView.invalidate();
    }

    private Drawable converBitmap(Bitmap bitmap) {
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void initErrorGeozoneMapReady() {
        isErrorGeozoneMapReady = true;
        if (isDisabledWork())
            return;

        mapOsmCont.setPadding(getPX(0)
                , getPX(0)
                , getPX(0)
                , getPX(0));

    }

    @Override
    public void initMapReady(boolean isPage0) {
        if (isDisabledWork())
            return;


        setMarginMapsDefault();
        setCenterMap();
    }

    private boolean isDisabledWork() {
        return mapView == null || !isVisible() || mapView.getContext() == null;
    }

    @Override
    public void initMapReady(GoogleMap googleMap, boolean isPage0) {
        if (isDisabledWork())
            return;
        initMapReady(isPage0);
    }


    private void setMemoryZoomMaps() {
        if (isDisabledWork())
            return;

        float zoom = (float) mapView.getZoomLevelDouble();
        Injector.getSettingsStore().setZoomMaps(zoom);
    }

    @Override
    public void setMyLocation() {
        if (!isVisible() || mapView == null || isDisabledWork())
            return;

        // TODO
        Location location = App.app.mMyGoogleLocation.getMLocation();

        if (super.initSetMyLoation(location))
            return;

        responseValidateMyLoc(location);
    }

    @Override
    public void showDrivers(List<Driver> driverList) {
        if (isDisabledWork())
            return;

        if (getClientData().getMarkerLocation() == null
                || driverList == null || driverList.size() == 0) {
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

    private BoundingBox computeArea(List<Driver> driverList) {
        ArrayList<GeoPoint> points = new ArrayList<>();

        points.add(new GeoPoint(getClientData().getMarkerLocation().latitude
                , getClientData().getMarkerLocation().longitude));

        for (final Driver driver : driverList) {
            GpsPosition location = driver.location;
            points.add(new GeoPoint(location.lat, location.lon));
        }

        double nord = 0, sud = 0, ovest = 0, est = 0;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == null) continue;

            double lat = points.get(i).getLatitude();
            double lon = points.get(i).getLongitude();

            if ((i == 0) || (lat > nord)) nord = lat;
            if ((i == 0) || (lat < sud)) sud = lat;
            if ((i == 0) || (lon < ovest)) ovest = lon;
            if ((i == 0) || (lon > est)) est = lon;

        }

        return new BoundingBox(nord, est, sud, ovest);

    }

    private void zoomToBounds(List<Driver> driverList) {
        final BoundingBox box = computeArea(driverList);
        if (mapView.getHeight() > 0) {
            initZoomDrivers(box);
        } else {

            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    initZoomDrivers(box);
                    removeGOLListener(mapView.getViewTreeObserver(), this);
                }

            });
        }
    }


    private void zoomToBoundingBox(final BoundingBox boundingBox, final int borderSizeInPixels) {
        double nextZoom = TileSystem.getBoundingBoxZoom(boundingBox
                , mapView.getWidth() / 2 - 2 * borderSizeInPixels
                , mapView.getHeight() / 2 - 2 * borderSizeInPixels);
        if (nextZoom == Double.MIN_VALUE) {
            return;
        }
        double minZoomLevel = mapView.getMinZoomLevel();
        double max = Math.max(nextZoom, minZoomLevel);
        double maxZoomLevel = mapView.getMaxZoomLevel();
        nextZoom = Math.min(maxZoomLevel, max);
        Injector.getSettingsStore().setZoomMaps((float) nextZoom);
        //  final IGeoPoint center = boundingBox.getCenterWithDateLine();
        mapView.getController().setZoom(nextZoom);
        //  mapView.getController().setCenter(center);
        mapView.invalidate();
    }

    private void initZoomDrivers(BoundingBox box) {
        isDisabledCameraIdle = true;
        zoomToBoundingBox(box, getPX(24));


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isDisabledCameraIdle = false;
            }
        }, 1000);
    }

    private void forAnimate(final List<Driver> driverList) {
        bodyAnimDrivers(driverList);
    }

    private void bodyAnimDrivers(List<Driver> driverList) {

        for (final Driver driver : driverList) {
            postD(driver);
        }
        alphaMarker[0] += 0.1f;
        if (posAnimate < 6) {
            posAnimate++;
            forAnimate(driverList);
        }
    }

    private void postD(final Driver driver) {

        MapView privaieMapView = mapView;
        if (privaieMapView == null || !isVisible() || privaieMapView.getContext() == null)
            return;

        GpsPosition location = driver.location;
        GeoPoint startPoint = new GeoPoint(
                location.lat
                , location.lon);
        org.osmdroid.views.overlay.Marker marker = new org.osmdroid.views.overlay.Marker(privaieMapView);
        marker.setPosition(startPoint);
        marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        Bitmap iconDrawableToMaps = Utilites.createIconDrawableToMaps(34
                , 42
                , R.drawable.car_icon);
        marker.setAlpha(alphaMarker[0]);
        marker.setIcon(
                converBitmap(rotateBitmap(iconDrawableToMaps, driver.rotade))
        );

        marker.setOnMarkerClickListener(new org.osmdroid.views.overlay.Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(org.osmdroid.views.overlay.Marker marker, MapView mapView) {
                return true;
            }
        });

        privaieMapView.getOverlays().add(marker);
        markerArrayList.add(marker);
        privaieMapView.invalidate();
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

    }

    @Override
    public void clearMarkers() {
        if (isDisabledWork())
            return;

        if (markerArrayList == null
                || mapView.getOverlays() == null) {
            markerArrayList = new ArrayList<>();
            return;
        }
        mapView.getOverlays().removeAll(markerArrayList);
        markerArrayList.clear();
        markerArrayList.trimToSize();
        mapView.invalidate();
    }

    private void setMarkerCar(double latitude, double longitude) {

        if (driverMarker != null)
            return;

        GeoPoint startPoint = new GeoPoint(
                latitude
                , longitude);

        driverMarker = new org.osmdroid.views.overlay.Marker(mapView);
        driverMarker.setPosition(startPoint);
        driverMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        Bitmap iconDrawableToMaps = Utilites.createIconDrawableToMaps(34
                , 42
                , R.drawable.car_icon);
        driverMarker.setOnMarkerClickListener(new org.osmdroid.views.overlay.Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(org.osmdroid.views.overlay.Marker marker, MapView mapView) {
                return true;
            }
        });
        driverMarker.setIcon(
                converBitmap(iconDrawableToMaps)
        );
        mapView.getOverlays().add(driverMarker);
    }

    @Override
    public void showCarLocation(GpsPosition location) {
        if (isDisabledWork())
            return;

        if (driverMarker == null) {
            setCenterMaps(new LatLng(location.lat, location.lon));
            setMarkerCar(location.lat, location.lon);

        } else {

            GeoPoint position = driverMarker.getPosition();

            LatLng car = new LatLng(position.getLatitude(), position.getLongitude());
            LatLng client = new LatLng(location.lat, location.lon);
            double distanceBetween = SphericalUtil.computeDistanceBetween(car, client);

            if (distanceBetween > 10) {
                float rotade = getAzimut(car, client);
                driverMarker.setRotation(rotade);
            }

            driverMarker.setPosition(new GeoPoint(
                    location.lat
                    , location.lon));

        }
    }


    private float getAzimut(LatLng car, LatLng client) {

        return (float) SphericalUtil.computeHeading(car, client);
    }

    @Override
    public Marker addCarMarker(GpsPosition location) {
        return null;
    }

    @Override
    public void initializeMap(LatLng latLng) {

    }

    @Override
    public void setPaddingButtonMaps(int px, int px1, int px2, int px3) {
        if (isDisabledWork())
            return;

        mapView.setPadding(px, px1, px2, px3);
        mapView.invalidate();
    }

    @Override
    public void addMarkerAddress(ArrayList<Address> getRoute) {

        this.getRoute = getRoute;
        //bodyAddMarkerAdress(getRoute);
    }

    private void bodyAddMarkerAdress() {
        if (getRoute == null || getRoute.size() == 0)
            return;


        int i = 0;
        for (Address address : getRoute) {

            if (i == 5)
                return;
            if (address == null)
                return;
            GpsPosition position = address.position;
            if (position == null)
                return;

            double lat = position.lat;
            double lon = position.lon;
            GeoPoint startPoint = new GeoPoint(
                    lat
                    , lon);

            org.osmdroid.views.overlay.Marker point = new org.osmdroid.views.overlay.Marker(mapView);
            point.setPosition(startPoint);
            point.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
            Bitmap marker = Utilites.createMarker(
                    initColor(R.color.colorPrimary),
                    initColor(R.color.text_white),
                    (i + 1) + "",
                    initColor(R.color.text_white), 24, 24);

            point.setIcon(
                    converBitmap(marker)
            );
            mapView.getOverlays().add(point);
            i++;
        }
    }

    @Override
    public void animateCarForMap(double latitude, double longitude) {

    }

    @Override
    public void moveCameraZoomMinus() {
        if (isDisabledWork())
            return;

        double minZoom = mapView.getMinZoomLevel();
        double zoomCurrent = mapView.getZoomLevelDouble();
        zoomCurrent -= 1.0d;
        double zoom = zoomCurrent < minZoom ? minZoom : zoomCurrent;
        IMapController mapController = mapView.getController();
        mapController.setZoom(zoom);
        Injector.getSettingsStore().setZoomMaps((float) zoom);
    }

    @Override
    public void moveCameraZoomPlus() {
        if (isDisabledWork())
            return;

        double maxZoom = mapView.getMaxZoomLevel();
        double zoomCurrent = mapView.getZoomLevelDouble();
        zoomCurrent += 1.0f;
        double zoom = zoomCurrent > maxZoom ? maxZoom : zoomCurrent;

        IMapController mapController = mapView.getController();
        mapController.setZoom(zoom);
        Injector.getSettingsStore().setZoomMaps((float) zoom);
    }

    @Override
    public void addArrayPointToMap(List<Driver> drivers, double[] latLonOneList) {
        if (isDisabledWork())
            return;

        if (drivers == null || drivers.size() == 0)
            return;

        if (markerArrayList == null)
            markerArrayList = new ArrayList<>();
        // добавляем тех кого нет
        //  Log.e("jhjhjj", "addArrayPointToMap" + drivers.size());

        for (Driver driver : drivers) {
            boolean isExsist = false;
            for (org.osmdroid.views.overlay.Marker placemarkMapObject : markerArrayList) {
                String placemarkMapObjectId = placemarkMapObject.getId();
                String stringIdDriver = driver.id + "";
                if (stringIdDriver.equals(placemarkMapObjectId)) {
                    isExsist = true;

                    break;
                }
            }
            if (!isExsist) {

                GpsPosition location = driver.location;
                GeoPoint startPoint = new GeoPoint(
                        location.lat
                        , location.lon);

                org.osmdroid.views.overlay.Marker marker = new org.osmdroid.views.overlay.Marker(mapView);
                marker.setId("" + driver.id);
                marker.setPosition(startPoint);
                marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
                Bitmap iconDrawableToMaps = Utilites.createIconDrawableToMaps(34
                        , 42
                        , R.drawable.car_icon);
                marker.setIcon(
                        converBitmap(rotateBitmap(iconDrawableToMaps, driver.rotade))
                );

                marker.setOnMarkerClickListener(new org.osmdroid.views.overlay.Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(org.osmdroid.views.overlay.Marker marker, MapView mapView) {
                        return true;
                    }
                });
                mapView.getOverlays().add(marker);
                markerArrayList.add(marker);

            }
        }

        // удаляем тех кто отвалился

        ArrayList<org.osmdroid.views.overlay.Marker> temp = new ArrayList<>();
        for (org.osmdroid.views.overlay.Marker placemarkMapObject : markerArrayList) {
            String placemarkMapObjectId = placemarkMapObject.getId();
            boolean isExsist = false;
            for (Driver driver : drivers) {
                String stringIdDriver = driver.id + "";
                if (stringIdDriver.equals(placemarkMapObjectId)) {

                    isExsist = true;
                    GeoPoint position = placemarkMapObject.getPosition();
                    LatLng car = new LatLng(position.getLatitude(), position.getLongitude());
                    LatLng client = new LatLng(driver.location.lat, driver.location.lon);
                    double distanceBetween = SphericalUtil.computeDistanceBetween(car, client);
                    Log.e("jhjhjklkllh", "distanceBetween = " + distanceBetween);

                    if (distanceBetween > 10) {
                        // поворот
                        float rotade = getAzimut(car, client);
                        placemarkMapObject.setRotation(rotade);
                        placemarkMapObject.setPosition(new GeoPoint(driver.location.lat, driver.location.lon));

                    }

                    temp.add(placemarkMapObject);


                }
            }

            if (!isExsist) {
                mapView.getOverlays().remove(placemarkMapObject);
            }
        }

        markerArrayList.clear();
        markerArrayList.trimToSize();
        markerArrayList.addAll(temp);
        mapView.invalidate();
    }

    @Override
    public void setLatLngBounds(LatLng latLng, LatLng latLngClient, boolean isStateSet) {

    }

    @Override
    public void initPolyline(List<LatLng> mPoints) {
        if (isDisabledWork())
            return;

        setDisabledULoc();
        ArrayList<GeoPoint> list = new ArrayList<>();

        for (LatLng latLng : mPoints) {
            list.add(new GeoPoint(latLng.latitude, latLng.longitude));
        }

        org.osmdroid.views.overlay.Polyline mNorthPolyline = new org.osmdroid.views.overlay.Polyline();
        mNorthPolyline.setColor(Color.WHITE);
        mNorthPolyline.setWidth(getPX(6));

        mNorthPolyline.setPoints(list);
        mapView.getOverlays().add(mNorthPolyline);

        org.osmdroid.views.overlay.Polyline mSouthPolyline = new org.osmdroid.views.overlay.Polyline();
        mSouthPolyline.setPoints(list);
        mSouthPolyline.setColor(initColor(R.color.colorPrimary));
        mSouthPolyline.setWidth(getPX(3));
        mapView.getOverlays().add(mSouthPolyline);
        bodyAddMarkerAdress();
        if (driverMarker != null) {
            GeoPoint position = driverMarker.getPosition();
            mapView.getOverlays().remove(driverMarker);
            driverMarker = null;
            setMarkerCar(position.getLatitude(), position.getLongitude());
        }
        mapView.invalidate();
    }

    @Override
    public void exitWork() {

    }

    @Override
    public void initClientDinamiMarkerPos(LatLng position) {
        if (isDisabledWork())
            return;

        if (clientDinamiMarker == null) {
            Bitmap iconDrawableToMaps = TintIcons.getBitmap(R.drawable.ic_flag);

            clientDinamiMarker = new org.osmdroid.views.overlay.Marker(mapView);

            clientDinamiMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
            clientDinamiMarker.setPosition(new GeoPoint(position.latitude, position.longitude));
            clientDinamiMarker.setIcon(
                    converBitmap(iconDrawableToMaps)
            );


            mapView.getOverlays().add(clientDinamiMarker);
        }

        setEnabledULoc();

        if (App.app.mMyGoogleLocation.mLastLocation != null)
            onBusLocation(new BusLocation(App.app.mMyGoogleLocation.mLastLocation));
    }

    @Override
    public void setEnabledULoc() {
        if (isDisabledWork())
            return;


        if (overlay != null)
            return;

        overlay = new DirectedLocationOverlay(getAWork());
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.user_arrow);
        overlay.setDirectionArrow(drawable.getBitmap());
        overlay.setShowAccuracy(true);
        mapView.getOverlays().add(overlay);
    }

    @Override
    public void setDisabledULoc() {
        if (isDisabledWork())
            return;

        if (overlay == null)
            return;

        mapView.getOverlays().remove(overlay);
        mapView.invalidate();
        overlay = null;
    }

    @Subscribe
    public void onBusLocation(BusLocation e) {
        Location location = e.location;
        initOverlayLoc(location);
        if (isDisabledWork())
            return;

        if (isBlokUpdLoc || !"searshCar".equals(search) || getRoute == null)
            return;


        setCenterMaps(new LatLng(location.getLatitude(), location.getLongitude()));


    }

    private void initOverlayLoc(Location location) {
        if (overlay == null)
            return;

        overlay.setBearing(location.getBearing());
        overlay.setAccuracy((int) location.getAccuracy());
        overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        mapView.invalidate();
    }

    @Override
    public void removeClientDinamiMarker(LatLng position) {
        if (isDisabledWork())
            return;

        if (clientDinamiMarker != null) {
            mapView.getOverlays().remove(clientDinamiMarker);
            clientDinamiMarker = null;
        }
    }

    @Override
    public void removeCarMarker() {
        if (isDisabledWork())
            return;

        if (driverMarker == null) {

            return;
        }
        mapView.getOverlays().remove(driverMarker);
        mapView.invalidate();

    }

    @Override
    public void clearMarkerCar() {
        if (isDisabledWork())
            return;
        if (markerArrayList == null) {
            markerArrayList = new ArrayList<>();
            return;
        }
        mapView.getOverlays().removeAll(markerArrayList);
        markerArrayList.clear();
        mapView.invalidate();
    }


    @Override
    public void animateHidePinMaps() {

    }

    private void setCenterMap() {
        if (!getAWork().isInitMaps) {
            super.init();
            LatLng markerLocation = getLatLngStartLocation();
            if (markerLocation != null)
                setMyLocation();
            else {
                setCenterMaps(getLatLng());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LatLng markerLocation = getLatLngStartLocation();
                        if (markerLocation != null)
                            setMyLocation();
                    }
                }, 5000);
            }
        } else
            setCenterMaps(getLatLng());
    }

    @Override
    public void setCenterMaps(LatLng latLngClient) {
        if (Injector.getClientData().isLatLngDisabled(latLngClient))
            return;
        if (mapView == null || !isVisible())
            return;
        if (latLngClient == null || latLngClient.latitude == 0.0
                || latLngClient.longitude == 0.0 || isDisabledWork())
            return;

        IMapController mapController = mapView.getController();

        GeoPoint startPoint = new GeoPoint(latLngClient.latitude
                , latLngClient.longitude);
        mLocation = latLngClient;
        if (search == null) {
            LatLng markerLocation = new LatLng(startPoint.getLatitude()
                    , startPoint.getLongitude());
            getClientData().setMarkerLocation(markerLocation);
        }

        mapController.setCenter(startPoint);

        if (getIUtilsV3Route() != null && !isDisabledCameraIdle)
            getIUtilsV3Route().onCameraIdle(latLngClient, false);
    }
}
