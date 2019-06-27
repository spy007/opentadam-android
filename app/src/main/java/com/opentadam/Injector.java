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

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.opentadam.bus.BusErrorCodeServers;
import com.opentadam.data.ClientData;
import com.opentadam.data.DeviceScreen;
import com.opentadam.data.IErrorShow;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.UtilitesErrorIGetApiResponseObject;
import com.opentadam.network.rest.BaseHiveApi;
import com.opentadam.network.rest.Country;
import com.opentadam.network.rest.GoogleRouteApi;
import com.opentadam.network.rest.OSMApi;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.Settings;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.VersionApi;
import com.opentadam.network.rest.YaApi;
import com.opentadam.utils.SoundPoolClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class Injector {
    public static boolean isDisabledPingUpdateApp;
    private static ClientData clientData;
    private static SoundPoolClient soundPoolClient;
    private static VersionApi versionApi;
    private static GoogleRouteApi googleRouteApi;
    public static DeviceScreen deviceScreen;
    private static YaApi yaApi;
    private static OSMApi osmApi;
    private static List<Country> countryList;
    private static final String TAG = Injector.class.getName();
    public static String deltaTimezone;
    public static long deltaCorrectTimeDefault;
    public static long deltaCorrectTime;
    private static Context appContext;
    private static SettingsStore settingsStore;
    public static String restServer = null;
    private static Tarif tarif;

    public static Tarif getLibDefTarif() {
        return tarif;
    }

    public static void setLibDefTarif(Tarif t) {
        tarif = t;
    }


    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context context) {
        appContext = context;
    }

    public static SettingsStore getSettingsStore() {
        if (settingsStore == null)
            settingsStore = new SettingsStore(getAppContext());
        return settingsStore;
    }

    public static void alert(String text) {
        Toast.makeText(getAppContext(), text, Toast.LENGTH_LONG).show();
    }


    public static long getCurrentTimeServers() {
        return System.currentTimeMillis() + deltaCorrectTime;
    }

    public static boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) Injector.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {

            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    public static boolean isOfflineStatus(RetrofitError retrofitError) {

        return !isOnline() || getStatus(retrofitError) == null;

    }

    public static Integer getStatus(RetrofitError retrofitError) {
        //    RetrofitError retrofitError = apiResponse.retrofitError;
        if (retrofitError.getResponse() == null) {
            return null;
        }

        Response response = retrofitError.getResponse();
        return response.getStatus();
    }

    public static ClientData getClientData() {
        if (clientData == null) {
            clientData = new ClientData();
            clientData.setArrayMap(new ArrayMap<String, UtilitesErrorIGetApiResponseObject>());
        }

        return clientData;
    }

    public static RESTConnect getRC() {
        Location mMyGoogleLocation = getLocation();
        return new RESTConnect(new IErrorShow() {
            @Override
            public void showError(String gsonError) {
                App.bus.post(new BusErrorCodeServers(gsonError));
            }
        }
                , mMyGoogleLocation
                , App.app.hashBC.HiveProfile,
                restServer);
    }

    public static Location getLocation() {
        Location mMyGoogleLocation = null;
        if (getClientData().getMarkerLocation() != null && getClientData().getMarkerLocation().longitude != 0) {
            mMyGoogleLocation = new Location("GPS");
            mMyGoogleLocation.setLatitude(getClientData().getMarkerLocation().latitude);
            mMyGoogleLocation.setLongitude(getClientData().getMarkerLocation().longitude);

        } else if (App.app.mMyGoogleLocation != null && App.app.mMyGoogleLocation.showCurrentLocation() != null) {

            if (App.app.mMyGoogleLocation.showCurrentLocation().getLongitude() != 0)
                mMyGoogleLocation = App.app.mMyGoogleLocation.showCurrentLocation();

            else {
                mMyGoogleLocation = new Location("GPS");
                mMyGoogleLocation.setLatitude(App.app.hashBC.defaultLatLon[0]);
                mMyGoogleLocation.setLongitude(App.app.hashBC.defaultLatLon[1]);
            }
        }
        return mMyGoogleLocation;
    }

    public static SoundPoolClient getSoundPoolClient() {

        if (soundPoolClient == null) {
            soundPoolClient = new SoundPoolClient();
            soundPoolClient.init(getAppContext());
        }
        return soundPoolClient;
    }
/*
    public static void setSoundPoolClient(SoundPoolClient soundPoolClient) {
        Injector.soundPoolClient = soundPoolClient;
    }*/
/*

    // главный метод для проверки подключения
    private static boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)
                getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // проверка подключения
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                // тест доступности внешнего ресурса
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // Timeout в секундах
                urlc.connect();
                // статус ресурса OK
                return urlc.getResponseCode() == 200;
                // иначе проверка провалилась

            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }
*/


    public static BaseHiveApi getRESTService(LatLng latLng) {

        RESTConnect rc = getRC();
/*        if (rc == null) {
            return null;
        }*/
        return rc.getRESTService(latLng);
    }

    private static Locale getAppLocale() {
        return Locale.getDefault();
    }

    public static GoogleRouteApi getGoogleApiRoute() {

        if (googleRouteApi == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept-Language", getAppLocale().toString() + ",en;q=0.8");
                    request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36");
                    request.addHeader("Connection", "keep-alive");
                }
            };
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
            client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
            client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://maps.googleapis.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String msg) {
                            Log.d(TAG + "::RouteApi", msg);
                        }
                    })
                    .setRequestInterceptor(requestInterceptor)
                    .setClient(new OkClient(client))
                    .build();

            googleRouteApi = restAdapter.create(GoogleRouteApi.class);

        }
        return googleRouteApi;
    }

    public static VersionApi getVersionApi() {

        if (versionApi == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept-Language", getAppLocale().toString() + ",en;q=0.8");
                    request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36");
                    request.addHeader("Connection", "keep-alive");

                }
            };

            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
            client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
            client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://mversion.hivecompany.ru:3443")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String msg) {
                            Log.d(TAG + "::getVersionApi", msg);
                        }
                    })
                    .setRequestInterceptor(requestInterceptor)
                    .setClient(new OkClient(client))
                    .build();

            versionApi = restAdapter.create(VersionApi.class);

        }
        return versionApi;
    }

    public static YaApi getYaApi() {

        if (yaApi == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept-Language", getAppLocale().toString() + ",en;q=0.8");
                    request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36");
                    request.addHeader("Connection", "keep-alive");

                }
            };

            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
            client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
            client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://geocode-maps.yandex.ru")
                    .setRequestInterceptor(requestInterceptor)
                    .setClient(new OkClient(client))
                    .build();

            yaApi = restAdapter.create(YaApi.class);

        }
        return yaApi;
    }

    public static OSMApi getOSMApi() {

        if (osmApi == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept-Language", getAppLocale().toString() + ",en;q=0.8");
                    request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.76 Safari/537.36");
                    request.addHeader("Connection", "keep-alive");

                }
            };

            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS); // connect timeout
            client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
            client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://nominatim.openstreetmap.org")
                    .setRequestInterceptor(requestInterceptor)
                    .setClient(new OkClient(client))
                    .build();

            osmApi = restAdapter.create(OSMApi.class);

        }
        return osmApi;
    }

    public static void setCountryList(List<Country> cList) {
        for (Country country : cList) {
            String isoCode = country.isoCode;
            country.isoCode = isoCode.toLowerCase(Locale.ENGLISH);
        }

        getSettingsStore().setCountryList(cList);
        countryList = cList;
    }

    public static List<Country> getCountryList() {

        if (countryList == null)
            return getSettingsStore().getCountryList();

        return countryList;

    }

    public synchronized static void setWorkSettings(Settings settings) {
        Settings hashSettings = getSettingsStore().getHashSettings();
        if (settings == null && hashSettings == null)
            return;

        initSettings(settings == null ? hashSettings : settings);
    }

    private static void initSettings(Settings settings) {
        final Settings workSettings = getWorkSettings();


        workSettings.setServiceId(settings.getServiceId())
                .setCardPaymentAllowed(settings.getCardPaymentAllowed())
                .setDispatcherCall(settings.getDispatcherCall())
                .setMainInterface(settings.getMainInterface())
                .setGeocoding(settings.getGeocoding())
                .setCurrency(settings.getCurrency())
                .setMaps(settings.getMaps())
                .setAverageSpeed(settings.getAverageSpeed());


        getSettingsStore().writeString("hashSettings"
                , new Gson().toJson(Settings.invoke(workSettings)));
    }

    public Settings getHashSettings() {
        return Injector.getSettingsStore().getHashSettings();
    }

    public synchronized static Settings getWorkSettings() {
        return App.app.getWorkSettings();
    }

    public synchronized static void setHashService(Service service) {
        if (service == null)
            return;
        final Service hashService = getHashService();
        hashService.setKind(service.getKind())
                .setServiceId(service.getServiceId())
                .setSettings(service.getSettings())
                .setLocation(service.getLocation())
                .setTariffs(service.getTariffs())
                .setMessage(service.getMessage())
                .setAddress(service.getAddress())
                .setLptype(service.getLptype());
    }

    public synchronized static Service getHashService() {
        return App.app.getHashService();
    }


}
