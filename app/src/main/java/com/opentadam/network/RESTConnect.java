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

package com.opentadam.network;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.DefLocalePhone;
import com.opentadam.Injector;
import com.opentadam.bus.BusRestartAWork;
import com.opentadam.bus.BusUpdateDefMars;
import com.opentadam.bus.BusUpdateInterface;
import com.opentadam.network.model.ParamsRegRef;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.BaseHiveApi;
import com.opentadam.network.rest.Bonuses;
import com.opentadam.network.rest.CardAdditionRef;
import com.opentadam.network.rest.Confirmed;
import com.opentadam.network.rest.Country;
import com.opentadam.network.rest.DeleteCard;
import com.opentadam.network.rest.DeleteRoute;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.EditPaymentMethod;
import com.opentadam.network.rest.EmptyObject;
import com.opentadam.network.rest.Estimation;
import com.opentadam.network.rest.FsmInfo;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.LinesInfo;
import com.opentadam.network.rest.OptionsList;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.Params;
import com.opentadam.network.rest.ParamsComments;
import com.opentadam.network.rest.ParamsEditSubmissionDetails;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.Prolongation;
import com.opentadam.network.rest.ResultSendOrders;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.Settings;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.network.rest.SubmitRequest;
import com.opentadam.network.rest.Submitted;
import com.opentadam.ui.ErrorCodeServers;
import com.opentadam.ui.frends.referal.Result;
import com.opentadam.ui.frends.referal.ResultDayStat;
import com.opentadam.ui.frends.referal.Transaction;
import com.opentadam.ui.registration.api.RemoteService;
import com.opentadam.ui.registration.room.RoomDataSource;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

import static com.opentadam.Injector.getClientData;
import static com.opentadam.Injector.getSettingsStore;


public class RESTConnect {
    private IErrorShowBaze iErrorShowBaze;
    private Location mMyGoogleLocation;
    private String hiveProfile;
    private String restServer;
    private ErrorCodeServers errorCodeServers;
    private String tag;


    public RESTConnect(IErrorShowBaze iErrorShowBaze
            , Location mMyGoogleLocation
            , String hiveProfile
            , String restServer) {
        this.iErrorShowBaze = iErrorShowBaze;
        this.hiveProfile = hiveProfile;
        this.mMyGoogleLocation = mMyGoogleLocation;
        this.restServer = restServer;

        App.appComponent.inject(this);
    }

    @Inject
    RemoteService apiService;

    @Inject
    RoomDataSource roomDataSource;

    public BaseHiveApi getRESTService(LatLng latLng) {

        if (latLng == null && Injector.getClientData().getMarkerLocation() != null) {

            latLng = Injector.getClientData().getMarkerLocation();

        } else if (latLng == null && mMyGoogleLocation != null) {

            latLng = new LatLng(mMyGoogleLocation.getLatitude()
                    , mMyGoogleLocation.getLongitude());

        }

        DefLocalePhone defLocalePhone = App.app.getDefLocalePhone();
        String readString = getSettingsStore()
                .readString("getLocale", defLocalePhone.defaultCountryLocal);
        final LatLng finalLatLng = latLng;

        if ("sys".equals(readString))
            readString = defLocalePhone.defaultCountryLocal;
        final String finalReadString = readString;

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept-Language", finalReadString + ",en;q=0.8");
                request.addHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                                " Chrome/41.0.2272.76 Safari/537.36");
                request.addHeader("Connection", "keep-alive");
                request.addHeader("Hive-Profile", hiveProfile);

                if (finalLatLng != null) {
                    final double finalLatitude = finalLatLng.latitude;
                    final double finalLongitude = finalLatLng.longitude;

                    request.addHeader("X-Hive-GPS-Position", finalLatitude + " " + finalLongitude);
                }
            }
        };

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
        client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));
        client.cancel(null);
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");

            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            ctx.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            client.setSslSocketFactory(ctx.getSocketFactory());
        } catch (Exception e) {

        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(restServer)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        String tagLical = tag == null ?
                                "Retrofit:" : tag + " " + "Retrofit:";
                        Log.d(tagLical, msg);
                        if (msg.contains("\"code\":")) {
                            //   getClientData().setPaymentMethodSelect(new PaymentMethod("cash"));
                            ErrorCodeServers errorCS = new Gson()
                                    .fromJson(msg, ErrorCodeServers.class);
                            if (errorCS.code == -60009) {
                                Service service = Injector.getClientData().service;
                                service.isNoRegReferall = true;
                            } else if (errorCS.code != -10009 && errorCS.code != 0 && errorCS.message != null) {
                                errorCodeServers = errorCS;

                            } else if (errorCS.code == -10009) {
                                errorCodeServers = null;
                                iErrorShowBaze.showError(msg);
                            }


                        }
                    }
                })
                .setRequestInterceptor(requestInterceptor)
                .setClient(new OkClient(client))
                .build();

        return restAdapter.create(BaseHiveApi.class);

    }

    @NonNull
    private ApiResponse getErrprRest(String path, RetrofitError error, UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject) {

        ApiResponse apiResponse = new ApiResponse(path, true);
        apiResponse.retrofitError = error;
        apiResponse.errorCodeServers = errorCodeServers;
        errorCodeServers = null;
        apiResponse.error = error.getMessage();
        apiResponse.utilitesErrorIGetApiResponseObject = utilitesErrorIGetApiResponseObject;
        if (apiResponse.error.contains("401 Unauthorized")){
            Injector.getSettingsStore().writeString(Constants.REG_PHONE_CLIENT, null);
            Injector.getSettingsStore().writeLong(Constants.REG_ID_CLIENT, 0);
            Injector.getSettingsStore().writeString(Constants.REG_KEY_CLIENT, null);
            App.bus.post(new BusRestartAWork());
        }

//        String textErrorToTesting = "Retrofit:\nОшибка на запрос ::: " + path + "\nerror.getMessage ::: " + apiResponse.error;
//        Injector.alert(textErrorToTesting);
/*        if (!apiResponse.error.contains("400 Bad")) {
            String textErrorToTesting = "Retrofit:\nОшибка на запрос ::: " + path + "\nerror.getMessage ::: " + apiResponse.error;
            if (!App.app.hashBC.DEBUG) {
                Crashlytics
                        .logException(new RuntimeException(textErrorToTesting));
            }

            App.bus.post(new BusMessageError7220(textErrorToTesting));
        }*/

        return apiResponse;
    }


    // https://redmine.hivecompany.ru/issues/10259
    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности
    public void getReferralData(final LatLng markerLocation, final IGetApiResponse iGetApiResponse) {
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_FIND_REFERRAL_DATA);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(markerLocation);

        if (restService != null)
            restService.findReferralData(head.get("Date"), head.get("Authentication"),
                    new Callback<Result>() {
                        @Override
                        public void success(Result refferalState, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_REFERRAL_DATA);
                            apiResponse.refferalState = refferalState;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
/*
                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_REFERRAL_DATA)
                                    .setLatLong(markerLocation);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_REFERRAL_DATA
                                            , error
                                            , utilitesErrorIGetApiResponseObject));*/
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций-по-дням)

    public void getLoyaltyProgramByDay(final LatLng markerLocation, final IGetApiResponse iGetApiResponse) {
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_GET_LP_BY_DAY);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(markerLocation);

        if (restService != null)
            restService.findLoyaltyProgramByDay(head.get("Date"), head.get("Authentication"),
                    new Callback<ArrayList<ResultDayStat>>() {
                        @Override
                        public void success(ArrayList<ResultDayStat> loyaltyProgramByDay, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_GET_LP_BY_DAY);
                            apiResponse.loyaltyProgramByDay = loyaltyProgramByDay;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_GET_LP_BY_DAY)
                                    .setLatLong(markerLocation);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_GET_LP_BY_DAY
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций-по-дням)

    public void getLoyaltyProgramTransactions(final LatLng markerLocation, final Long oft, final IGetApiResponse iGetApiResponse) {
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_GET_LP_TRANSACTION);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(markerLocation);

        if (restService != null)
            restService.findLoyaltyProgramTransactions(head.get("Date"), head.get("Authentication"),
                    oft,
                    new Callback<ArrayList<Transaction>>() {
                        @Override
                        public void success(ArrayList<Transaction> transaction, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_GET_LP_TRANSACTION);
                            apiResponse.transaction = transaction;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_GET_LP_TRANSACTION)
                                    .setOft(oft)
                                    .setLatLong(markerLocation);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_GET_LP_TRANSACTION
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций,-детальный)

    public void getLoyaltyProgramByDayList(final LatLng markerLocation, final Long oft, final IGetApiResponse iGetApiResponse) {
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_GET_HISTORY_LIST);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(markerLocation);

        if (restService != null)
            restService.findLoyaltyProgramByDayLis(head.get("Date"), head.get("Authentication"),
                    oft,
                    new Callback<ArrayList<Transaction>>() {
                        @Override
                        public void success(ArrayList<Transaction> transaction, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_GET_HISTORY_LIST);
                            apiResponse.transaction = transaction;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_GET_HISTORY_LIST)
                                    .setOft(oft)
                                    .setLatLong(markerLocation);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_GET_HISTORY_LIST
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }


    //
    public void getCountries(final IGetApiResponse iGetApiResponse) {
        BaseHiveApi restService = getRESTService(null);
        if (restService != null)
            restService.findCountries(new Callback<List<Country>>() {
                @Override
                public void success(List<Country> countryList, Response response) {
                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_LIST_COUNTRY);
                    apiResponse.countryList = countryList;
                    iGetApiResponse.getApiResponse(apiResponse);
                }

                @Override
                public void failure(RetrofitError error) {
                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                            .newInstance(iGetApiResponse, Constants.PATH_LIST_COUNTRY);
                    iGetApiResponse
                            .getApiResponse(getErrprRest(Constants.PATH_LIST_COUNTRY
                                    , error
                                    , utilitesErrorIGetApiResponseObject));
                }
            });
    }

    //  *********** запросы Feature #9614  Реализовать возможность погашения долга ****

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Cостояние-клиентского-счета
    public void getAccount(final LatLng latLng, final IGetApiResponse iGetApiResponse) {
        if(!Injector.getWorkSettings().getCardPaymentAllowed())
            return;

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_FIND_GET_ACCOUNT);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(latLng);

        if (restService != null)
            restService.findGetAccount(head.get("Date"), head.get("Authentication"),
                    new Callback<AccountState>() {
                        @Override
                        public void success(AccountState accountState, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_GET_ACCOUNT);
                            apiResponse.accountState = accountState;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            getClientData().setPaymentMethodSelect(new PaymentMethod("cash"));
              /*              UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_GET_ACCOUNT)
                                    .setLatLong(latLng);*/

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_GET_ACCOUNT
                                            , error
                                            , null));
                        }
                    });
    }

    //  *********** запросы Feature #8623 Способ оплаты - контрагент ****

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Способы-оплаты
    public void getPaymentMethod(final LatLng ll, final IGetApiResponse iGetApiResponse) {
        if (!Injector.getClientData().isEnabledService())
            return;

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_PAYMENT_METHOD);
        final LatLng  latLng =  Injector.getClientData().getMarkerLocation();
        BaseHiveApi restService = getRESTService(latLng);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        Service service = Injector.getHashService();
        if (service != null && "stub".equals(service.getKind())){
            return;
        }

        if (restService != null)
            restService.findPaymentMethod(head.get("Date"), head.get("Authentication"),
                    new Callback<List<PaymentMethod>>() {
                        @Override
                        public void success(List<PaymentMethod> paymentMethods, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_PAYMENT_METHOD);
                            apiResponse.paymentMethods = paymentMethods;
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_PAYMENT_METHOD)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_PAYMENT_METHOD
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Предварительная-оценка-заказа
    public void getEstimation(
            final PaymentMethod paymentMethod
            , final long tariff
            , final List<Long> options
            , final List<GpsPosition> route
            , final String time
            , final IGetApiResponse iGetApiResponse) {
        if (route == null || route.size() == 0)
            return;
        if (!Injector.getClientData().isEnabledService()) {
            return;
        }
        Double lat = route.get(0).lat;
        Double lon = route.get(0).lon;

        String date = null;
        String authentication = null;

        if (getSettingsStore().isOnRegClient()) {
            final Map<String, String> head = HiveHmacSigner
                    .addRegAutor("POST", Constants.PATH_ESTIMATE);
            if (head == null) {
                // String error = "Пользователь не зарегистрирован";
                return;
            }
            date = head.get("Date");
            authentication = head.get("Authentication");
        }

        final Params params = new Params(paymentMethod
                , tariff
                , options
                , route
                , time);
        LatLng latLng = new LatLng(lat, lon);
        getEstimation(iGetApiResponse, latLng, date, authentication, params);
    }

    public void getEstimation(final IGetApiResponse iGetApiResponse,
                              final LatLng latLng,
                              final String date, final String authentication,
                              final Params params) {


        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null) {
            restService.findEstimation(date, authentication,
                    params, new Callback<Estimation>() {
                        @Override
                        public void success(Estimation estimation, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_ESTIMATE);
                            apiResponse.estimation = estimation;

                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_ESTIMATE)
                                    .setLatLong(latLng)
                                    .setDate(date)
                                    .setAuthentication(authentication)
                                    .setParams(params);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_ESTIMATE
                                            , error
                                            , utilitesErrorIGetApiResponseObject));

                        }
                    });
        }
    }

    public void timeSynchronizationServers(final LatLng latLng, final IGetApiResponse iGetApiResponse) {
        BaseHiveApi hiveApi = Injector.getRESTService(latLng);

        hiveApi.timeSynchronization(new Callback<String>() {
            @Override
            public void success(String timeSynchronizationServers, Response response) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_TIME_SYNCHRONIZATION);
                apiResponse.timeSynchronizationServers = timeSynchronizationServers;

                iGetApiResponse.getApiResponse(apiResponse);
            }

            @Override
            public void failure(RetrofitError error) {

                UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                        .newInstance(iGetApiResponse, Constants.PATH_TIME_SYNCHRONIZATION)
                        .setLatLong(latLng);

                iGetApiResponse
                        .getApiResponse(getErrprRest(Constants.PATH_TIME_SYNCHRONIZATION
                                , error
                                , utilitesErrorIGetApiResponseObject));
            }
        });
    }

    public void getCallDisp(final IGetApiResponse iGetApiResponse) {

        String phone = Injector.getClientData().getPhoneClient();
        if (phone == null)
            return;

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_FIND_DISPATCHER_CALL);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = Injector.getRESTService(Injector.getClientData().latLngTarifHTPSRequest);
/*        if (restService == null) {
            showErrorNetDialog();
            return;
        }*/
        restService
                .findDispatcherCall(head.get("Date")
                        , head.get("Authentication")
                        , phone
                        , new Callback<EmptyObject>() {
                            @Override
                            public void success(EmptyObject emptyObject, Response response) {
                                ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_DISPATCHER_CALL);
                                apiResponse.emptyObject = emptyObject;

                                iGetApiResponse.getApiResponse(apiResponse);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                        .newInstance(iGetApiResponse, Constants.PATH_FIND_DISPATCHER_CALL);


                                iGetApiResponse
                                        .getApiResponse(getErrprRest(Constants.PATH_FIND_DISPATCHER_CALL
                                                , error
                                                , utilitesErrorIGetApiResponseObject));
                            }
                        });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Информация-о-предоставляемом-сервисе
    public void getService(final PaymentMethod paymentMethod
            , final LatLng latLng, final IGetApiResponse iGetApiResponse) {

        String date = null;
        String authentication = null;

        if (getSettingsStore().isOnRegClient()) {
            final Map<String, String> head = HiveHmacSigner
                    .addRegAutor("POST", Constants.PATH_FIND_SERVICE);

            date = head.get("Date");
            authentication = head.get("Authentication");
        }

        final Params params = new Params(paymentMethod);
        Settings workSettings = Injector.getWorkSettings();
        if (workSettings != null && Injector.getHashService() != null){
            params.prevServiceId = "default".equals(workSettings.getServiceId()) ? null : workSettings.getServiceId();
        }

        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findService(
                    date
                    , authentication
                    , params, new Callback<Service>() {
                        @Override
                        public void success(Service service, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_SERVICE);
                            String kind = service.getKind();

                            if ("same".equals(kind)) {
                                apiResponse.service = Injector.getHashService();
                            } else {
                                Settings settings = service.getSettings();
                                if (settings == null) {
                                    Crashlytics
                                            .logException(new RuntimeException("ошибка service.settings: "
                                                    + "\nparams: " + new Gson().toJson(params)
                                                    + "\nservice: " + new Gson().toJson(service)));
                                    return;
                                }
                                apiResponse.service = service;
                                settings.setServiceId(service.getServiceId());

                                Injector.setWorkSettings(settings);
                                if(!Injector.getWorkSettings().getCardPaymentAllowed()) getClientData().setPaymentMethodSelect(new PaymentMethod("cash"));

                                if (Injector.getHashService() != null && Injector.getHashService().getSettings() != null) {
                                    Settings settingsApp = Injector
                                            .getHashService()
                                            .getSettings();
                                    String mainInterfacePrev = settingsApp.getMainInterface();
                                    if (!mainInterfacePrev.equals(settings.getMainInterface())) {
                                        // изменился интерфейс
                                        App.bus.post(new BusUpdateInterface(settings.getMainInterface(), settings.isEnabledRevers()));
                                    }

                                    String mainMaps = settingsApp.getMaps().get(0);
                                    if (!mainMaps.equals(settings.getMaps().get(0))) {
                                        // изменилась карта
                                        App.bus.post(new BusUpdateDefMars());
                                    }
                                }
                                Injector.setHashService(service);
                            }
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_SERVICE)
                                    .setPaymentMethod(paymentMethod)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_SERVICE
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Добавление-банковской-карты
    public void findAddCard(final LatLng latLng, final IGetApiResponse iGetApiResponse) {

        String date = null;
        String authentication = null;

        if (getSettingsStore().isOnRegClient()) {
            final Map<String, String> head = HiveHmacSigner
                    .addRegAutor("POST", Constants.PATH_ADD_CARD);
            if (head == null) {
                // String error = "Пользователь не зарегистрирован";
                return;
            }
            date = head.get("Date");
            authentication = head.get("Authentication");
        }


        BaseHiveApi restService = getRESTService(latLng);

        if (restService != null)
            restService.findAddCard(date, authentication, new EmptyObject(),
                    new Callback<CardAdditionRef>() {
                        @Override
                        public void success(CardAdditionRef cardAdditionRef, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_ADD_CARD + "_POST");
                            apiResponse.cardAdditionRef = cardAdditionRef;

                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_ADD_CARD + "_POST")
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_ADD_CARD + "_POST"
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // **** https://github.com/HIVETAXI/client-mobile-api/wiki/Создание-заказа
    public void sendOrders(final SendCreateRequest params, final IGetApiResponse iGetApiResponse) {
        final LatLng latLng = getSettingsStore().getLatLngTarifDef();
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST", Constants.PATH_FIND_ORDERS);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findOrders(head.get("Date"), head.get("Authentication"),
                    params, new Callback<ResultSendOrders>() {
                        @Override
                        public void success(ResultSendOrders resultSendOrders, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_ORDERS);
                            apiResponse.resultSendOrders = resultSendOrders;

                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_ORDERS)
                                    .setSendCreateRequest(params);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_ORDERS
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // *****https://github.com/HIVETAXI/client-mobile-api/wiki/Получение-подробностей-по-заказу

    public void getOrderInfo(final long idRoute, final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET"
                        , Constants.PATH_ORDER_INFO.replace("{id}", idRoute + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(null);

        if (restService != null)
            restService
                    .findGetOrderInfo(head.get("Date"), head.get("Authentication"), idRoute,
                            new Callback<OrderInfo>() {
                                @Override
                                public void success(OrderInfo orderInfo, Response response) {
                                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_ORDER_INFO);

                                    apiResponse.orderInfo = orderInfo;
                                    apiResponse.orderInfo.idRoute = idRoute;
                                    iGetApiResponse.getApiResponse(apiResponse);

                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                            .newInstance(iGetApiResponse, Constants.PATH_ORDER_INFO)
                                            .setId(idRoute);

                                    iGetApiResponse
                                            .getApiResponse(getErrprRest(Constants.PATH_ORDER_INFO
                                                    , error
                                                    , utilitesErrorIGetApiResponseObject));
                                }
                            });
    }

    //*******https://github.com/HIVETAXI/client-mobile-api/wiki/Получение-списка-заказов
    public void getOrderCurrent(final String method, final IGetApiResponse iGetApiResponse) {
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_ORDERS_CURRENT);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(null);
        if (restService != null)
            restService
                    .findGetShortOrderCurrent(head.get("Date"), head.get("Authentication"),
                            new Callback<ArrayList<ShortOrderInfo>>() {

                                @Override
                                public void success(ArrayList<ShortOrderInfo> orderInfos, Response response) {
                                    ApiResponse apiResponse
                                            = new ApiResponse(method + Constants.PATH_ORDERS_CURRENT);
                                    apiResponse.shortOrderInfos = orderInfos;

                                    iGetApiResponse.getApiResponse(apiResponse);

                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                            .newInstance(iGetApiResponse, method + Constants.PATH_ORDERS_CURRENT)
                                            .setMethod(method);

                                    iGetApiResponse
                                            .getApiResponse(getErrprRest(method + Constants.PATH_ORDERS_CURRENT
                                                    , error
                                                    , utilitesErrorIGetApiResponseObject));
                                }
                            });
    }

// *** https://github.com/HIVETAXI/client-mobile-api/wiki/Ближайшие-водители

    public void getDrivers(final LatLng latLng, final Params params, final IGetApiResponse iGetApiResponse) {
        if (!Injector.getClientData().isEnabledService()) {
            return;
        }
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST", Constants.PATH_DRIVERS);
        String date = null;
        String authentication = null;

        if (head != null) {
            date = head.get("Date");
            authentication = head.get("Authentication");
        }

        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null) {

            restService.findDrivers(date, authentication,
                    params, new Callback<List<Driver>>() {
                        @Override
                        public void success(List<Driver> driverList, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_DRIVERS);
                            apiResponse.driverList = driverList;

                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_DRIVERS)
                                    .setParams(params)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_DRIVERS
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
        }
    }

    // ************************************
    // *********** запросы ***********

    public void sendRegistrationToServer(final String token) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST", Constants.PATH_FCM);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        apiService.findToken(head.get("Date"), head.get("Authentication"),
                new FsmInfo(token)).enqueue(new retrofit2.Callback<EmptyObject>() {
            @Override
            public void onResponse(final retrofit2.Call<EmptyObject> confirmed, final retrofit2.Response<EmptyObject> response) {
                getSettingsStore().writeString(Constants.REG_TOKEN_CLIENT, token);
            }

            @Override
            public void onFailure(final retrofit2.Call<EmptyObject> call, final Throwable t) {

            }
        });
    }


    // бонусы получить
    public void getBonusClient(final LatLng latLng, final IGetApiResponse iGetApiResponse) {
        if (!Injector.getClientData().isEnabledService()) {
            return;
        }

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_BONUSES);

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
// "lat":55.044716,"lon":73.42157

        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)

            restService.findGetBonuses(head.get("Date"), head.get("Authentication"),
                    new Callback<Bonuses>() {


                        @Override
                        public void success(Bonuses bonuses, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_BONUSES);
                            apiResponse.bonuses = bonuses;

                            iGetApiResponse.getApiResponse(apiResponse);

                        }

                        @Override
                        public void failure(RetrofitError error) {
/*
                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_BONUSES)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_BONUSES
                                            , error
                                            , utilitesErrorIGetApiResponseObject));*/
                        }
                    });
    }

    // маршрут заказа
    public void getLinesInfo(final IGetApiResponse iGetApiResponse, final long idRoute) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET"
                        , Constants.PATH_LINES_INFO.replace("{id}", idRoute + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(null);
/*        if (restService == null)
            return;*/

        restService.findGetLinesInfo(head.get("Date"), head.get("Authentication"), idRoute,
                new Callback<LinesInfo>() {
                    @Override
                    public void success(LinesInfo linesInfo, Response response) {
                        ApiResponse apiResponse = new ApiResponse(Constants.PATH_LINES_INFO);

                        apiResponse.linesInfo = linesInfo;
                        iGetApiResponse.getApiResponse(apiResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                .newInstance(iGetApiResponse, Constants.PATH_LINES_INFO)
                                .setId(idRoute);

                        iGetApiResponse
                                .getApiResponse(getErrprRest(Constants.PATH_LINES_INFO
                                        , error
                                        , utilitesErrorIGetApiResponseObject));
                    }
                });


    }

    // История поездок:

    public void getHistoryOrders(final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET", Constants.PATH_HISTORY);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(null);
        if (restService != null)
            restService
                    .findGetShortOrderHistory(head.get("Date"), head.get("Authentication"), 0, 16,
                            new Callback<ArrayList<ShortOrderInfo>>() {

                                @Override
                                public void success(ArrayList<ShortOrderInfo> shortOrderInfos
                                        , Response response) {
                                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_HISTORY);

                                    apiResponse.shortOrderInfos = shortOrderInfos;
                                    iGetApiResponse.getApiResponse(apiResponse);
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                            .newInstance(iGetApiResponse, Constants.PATH_HISTORY);


                                    iGetApiResponse
                                            .getApiResponse(getErrprRest(Constants.PATH_HISTORY
                                                    , error
                                                    , utilitesErrorIGetApiResponseObject));
                                }
                            });
    }

    //

    public void getAdressGeocodeServers(final LatLng latLng, final IGetApiResponse iGetApiResponse, final Long timeRest) {

        if (latLng == null || latLng.longitude == 0)
            return;

        BaseHiveApi restService = getRESTService(latLng);

        if (restService != null)
            restService.findAddressGeocode(new Callback<List<Address>>() {
                @Override
                public void success(List<Address> addresses, Response response) {
                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_LIST_ADRESS_LAT_LON);
                    apiResponse.addresses = addresses;
                    apiResponse.timeRest = timeRest;
                    iGetApiResponse.getApiResponse(apiResponse);
/*                    Location mLocation = App.app.mMyGoogleLocation.getMLocation();
                    if(mLocation == null){
                        App.app.mMyGoogleLocation.setMemoryLocation(latLng);
                    }*/
                }

                @Override
                public void failure(RetrofitError error) {

                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                            .newInstance(iGetApiResponse, Constants.PATH_LIST_ADRESS_LAT_LON)
                            .setLatLong(latLng);

                    iGetApiResponse
                            .getApiResponse(getErrprRest(Constants.PATH_LIST_ADRESS_LAT_LON
                                    , error
                                    , utilitesErrorIGetApiResponseObject));
                }
            });
    }

    // полнотекстовый поиск
    public void getAdressPatern(
            final String path
            , final LatLng latLng
            , final IGetApiResponse iGetApiResponse
            , final long timeRest) {
        BaseHiveApi restServiceLatLng = getRESTService(latLng);
        if (restServiceLatLng == null || path == null)
            return;

        restServiceLatLng.findFFullTextSearch(path, new Callback<List<Address>>() {
            @Override
            public void success(List<Address> addresses, Response response) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_LIST_ADRESS_GEOCODING);
                apiResponse.addresses = addresses;
                apiResponse.timeRest = timeRest;
                iGetApiResponse.getApiResponse(apiResponse);
            }

            @Override
            public void failure(RetrofitError error) {

                UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                        .newInstance(iGetApiResponse, Constants.PATH_LIST_ADRESS_GEOCODING)
                        .setPath(path)
                        .settimeRest(timeRest)
                        .setLatLong(latLng);

                iGetApiResponse
                        .getApiResponse(getErrprRest(Constants.PATH_LIST_ADRESS_GEOCODING
                                , error
                                , utilitesErrorIGetApiResponseObject));
            }
        });
    }

    public void sendPhoneToServers(final SubmitRequest submitRequest, final IGetApiResponse iGetApiResponse) {
        apiService.findPhone(submitRequest).enqueue(new retrofit2.Callback<Submitted>() {
            @Override
            public void onResponse(final retrofit2.Call<Submitted> call, final retrofit2.Response<Submitted> response) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_REG_PHONE);
                apiResponse.submitted = response.body();
                iGetApiResponse.getApiResponse(apiResponse);
            }

            @Override
            public void onFailure(final retrofit2.Call<Submitted> call, final Throwable t) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_REG_PHONE);
                apiResponse.error = t.getMessage();
                iGetApiResponse.getApiResponse(apiResponse);
            }
        });

    }

    public void replaceCode(long mId, String type) {
        apiService.reSubmit(mId, type).enqueue(new retrofit2.Callback<EmptyObject>() {
            @Override
            public void onResponse(final retrofit2.Call<EmptyObject> call, final retrofit2.Response<EmptyObject> response) {
            }

            @Override
            public void onFailure(final retrofit2.Call<EmptyObject> call, final Throwable t) {
            }
        });
    }

    public void sendCallServers(final long idRoute, final IGetApiResponse iGetApiResponse) {


        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET"
                        , Constants.PATH_FIND_REQUEST_DRIVER_CALL
                                .replace("{id}", idRoute + ""));
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = Injector.getRESTService(null);
/*        if (restService == null) {
            showErrorNetDialog();
            return;
        }*/
        restService
                .findRequestDriverCall(head.get("Date"), head.get("Authentication"), idRoute,
                        new Callback<EmptyObject>() {

                            @Override
                            public void success(EmptyObject emptyObject, Response response) {
                                ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_REQUEST_DRIVER_CALL);
                                apiResponse.emptyObject = emptyObject;
                                iGetApiResponse.getApiResponse(apiResponse);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                        .newInstance(iGetApiResponse, Constants.PATH_FIND_REQUEST_DRIVER_CALL)
                                        .setId(idRoute);

                                iGetApiResponse
                                        .getApiResponse(getErrprRest(Constants.PATH_FIND_REQUEST_DRIVER_CALL
                                                , error
                                                , utilitesErrorIGetApiResponseObject));

                            }
                        });
    }


    public void sendProlongation(final int posProlongation, final long idRoute, final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET"
                        , Constants.PATH_FIND_SET_PROLONGATION.replace("{id}", idRoute + ""));
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi hiveApi = Injector.getRESTService(null);

        hiveApi.findSetProlongation(head.get("Date"), head.get("Authentication"),
                idRoute, posProlongation * 5, new Callback<Prolongation>() {
                    @Override
                    public void success(Prolongation prolongation, Response response) {
                        ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_SET_PROLONGATION);
                        apiResponse.emptyObject = new EmptyObject();
                        iGetApiResponse.getApiResponse(apiResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                .newInstance(iGetApiResponse, Constants.PATH_FIND_SET_PROLONGATION)
                                .setPosProlongation(posProlongation)
                                .setId(idRoute);

                        iGetApiResponse
                                .getApiResponse(getErrprRest(Constants.PATH_FIND_SET_PROLONGATION
                                        , error
                                        , utilitesErrorIGetApiResponseObject));

                    }
                });

    }

    public void sendToServers(final long mId, final String mCodeUser, final IGetApiResponse iGetApiResponse) {
        apiService.findConfirm(mId, mCodeUser).enqueue(new retrofit2.Callback<Confirmed>() {
            @Override
            public void onResponse(final retrofit2.Call<Confirmed> confirmed, final retrofit2.Response<Confirmed> response) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_REG_CODE);
                apiResponse.confirmed = response.body();
                iGetApiResponse.getApiResponse(apiResponse);
            }

            @Override
            public void onFailure(final retrofit2.Call<Confirmed> call, final Throwable t) {
                ApiResponse apiResponse = new ApiResponse(Constants.PATH_REG_CODE);
                apiResponse.error = t.getMessage();
                iGetApiResponse.getApiResponse(apiResponse);
            }
        });
    }

    public void deleteRoute(final long idRoute, final IGetApiResponse iGetApiResponse) {

        String uri = Constants.PATH_FIND_DELETE.replace("{id}", idRoute + "");
        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("DELETE", uri);

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = Injector.getRESTService(null);

        restService
                .findDelete(head.get("Date"), head.get("Authentication"),
                        idRoute,
                        new Callback<DeleteRoute>() {
                            @Override
                            public void success(DeleteRoute deleteRoute, Response response) {
                                ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_DELETE);
                                apiResponse.deleteRoute = deleteRoute;
                                iGetApiResponse.getApiResponse(apiResponse);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                        .newInstance(iGetApiResponse, Constants.PATH_FIND_DELETE)
                                        .setId(idRoute);

                                iGetApiResponse
                                        .getApiResponse(getErrprRest(Constants.PATH_FIND_DELETE
                                                , error
                                                , utilitesErrorIGetApiResponseObject));

                            }
                        });

    }

    public void debetCard(final LatLng latLngStartAdress, final long idCard,
                          final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET"
                        , Constants.PATH_DEBET_CARD);
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        String date = head.get("Date");
        String authentication = head.get("Authentication");

        BaseHiveApi restService = getRESTService(latLngStartAdress);

        if (restService != null)
            restService.debetCard(date, authentication, idCard,
                    new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject deleteCard, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_DEBET_CARD);
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_DEBET_CARD)
                                    .setId(idCard)
                                    .setLatLong(latLngStartAdress);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_DEBET_CARD
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    public void deleteCard(final LatLng latLngStartAdress, final long idCard,
                           final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("DELETE"
                        , Constants.PATH_DELETE_CARD
                                .replace("{card_id}", idCard + ""));
        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        String date = head.get("Date");
        String authentication = head.get("Authentication");

        BaseHiveApi restService = getRESTService(latLngStartAdress);

        if (restService != null)
            restService.deleteCard(date, authentication, idCard,
                    new Callback<DeleteCard>() {
                        @Override
                        public void success(DeleteCard deleteCard, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_DELETE_CARD_METOD);
                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_DELETE_CARD_METOD)
                                    .setId(idCard)
                                    .setLatLong(latLngStartAdress);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_DELETE_CARD_METOD
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-деталей-адреса-подачи-заказа
    public void findEditSubmissionDetails(final LatLng latLng
            , final long id
            , final ParamsEditSubmissionDetails params
            , final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST"
                        , Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS
                                .replace("{id}", id + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findEditSubmissionDetails(head.get("Date"), head.get("Authentication"),
                    id
                    , params
                    , new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject emptyObject, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS);


                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS)
                                    .setId(id)
                                    .setLatLong(latLng)
                                    .setParamsEditSubmissionDetails(params);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Фиксация-стоимости-заказа
    public void setFixCost(final long id,
                           final float amount,
                           final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET",
                        Constants.PATH_FIND_FIX_COST.replace("{id}", id + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(null);
        if (restService != null)
            restService
                    .findFixCost(
                            head.get("Date"),
                            head.get("Authentication"),
                            id,
                            amount,
                            new Callback<EmptyObject>() {

                                @Override
                                public void success(
                                        EmptyObject emptyObject,
                                        Response response) {
                                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_FIX_COST);
                                    iGetApiResponse.getApiResponse(apiResponse);
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                            .newInstance(iGetApiResponse, Constants.PATH_FIND_FIX_COST)
                                            .setId(id)
                                            .setAmount(amount);

                                    iGetApiResponse
                                            .getApiResponse(getErrprRest(Constants.PATH_FIND_FIX_COST
                                                    , error
                                                    , utilitesErrorIGetApiResponseObject));
                                }
                            });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-способа-оплаты-в-заказе
    public void editPaymentMethod(final LatLng latLng
            , final long id
            , final EditPaymentMethod params
            , final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST"
                        , Constants.PATH_FIND_EDIT_PAYMENT_METHOD
                                .replace("{id}", id + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findEditPaymentMethod(head.get("Date"), head.get("Authentication"),
                    id
                    , params
                    , new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject emptyObject, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_EDIT_PAYMENT_METHOD);


                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_EDIT_PAYMENT_METHOD)
                                    .setId(id)
                                    .setEditPaymentMethod(params)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_EDIT_PAYMENT_METHOD
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-опций-заказа

    public void editOptions(final LatLng latLng
            , final long id
            , final OptionsList params
            , final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST"
                        , Constants.PATH_FIND_EDIT_OPTIONS.replace("{id}", id + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findEditOptions(head.get("Date"), head.get("Authentication"),
                    id
                    , params
                    , new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject emptyObject, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_EDIT_OPTIONS);


                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_EDIT_OPTIONS)
                                    .setId(id)
                                    .setOptionsList(params)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_EDIT_OPTIONS
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

// https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Регистрация-в-программе-лояльности)

    public void findRegReferal(final LatLng latLng
            , final ParamsRegRef params
            , final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST"
                        , Constants.PATH_REG_REFERRAL);

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findRegReferal(head.get("Date"), head.get("Authentication"),
                    params
                    , new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject emptyObject, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_REG_REFERRAL);


                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {


                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_REG_REFERRAL
                                            , error
                                            , null));
                        }
                    });
    }

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-комментария-к-заказу

    public void editComments(final LatLng latLng
            , final long id
            , final ParamsComments params
            , final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("POST"
                        , Constants.PATH_FIND_EDIT_COMMENTS.replace("{id}", id + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }
        BaseHiveApi restService = getRESTService(latLng);
        if (restService != null)
            restService.findEditComments(head.get("Date"), head.get("Authentication"),
                    id
                    , params
                    , new Callback<EmptyObject>() {
                        @Override
                        public void success(EmptyObject emptyObject, Response response) {
                            ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_EDIT_COMMENTS);


                            iGetApiResponse.getApiResponse(apiResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                    .newInstance(iGetApiResponse, Constants.PATH_FIND_EDIT_COMMENTS)
                                    .setId(id)
                                    .setParamsComments(params)
                                    .setLatLong(latLng);

                            iGetApiResponse
                                    .getApiResponse(getErrprRest(Constants.PATH_FIND_EDIT_COMMENTS
                                            , error
                                            , utilitesErrorIGetApiResponseObject));
                        }
                    });
    }

    public void findOkStatusWait(final long idRoute, final IGetApiResponse iGetApiResponse) {

        final Map<String, String> head = HiveHmacSigner
                .addRegAutor("GET",
                        Constants.PATH_FIND_OK_STATUS_WAIT.replace("{id}", idRoute + ""));

        if (head == null) {
            // String error = "Пользователь не зарегистрирован";
            return;
        }

        BaseHiveApi restService = getRESTService(null);
        if (restService != null)
            restService
                    .findOkStatusWait(
                            head.get("Date"),
                            head.get("Authentication"),
                            idRoute,
                            new Callback<EmptyObject>() {

                                @Override
                                public void success(
                                        EmptyObject emptyObject,
                                        Response response) {
                                    ApiResponse apiResponse = new ApiResponse(Constants.PATH_FIND_OK_STATUS_WAIT);
                                    iGetApiResponse.getApiResponse(apiResponse);
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject = UtilitesErrorIGetApiResponseObject
                                            .newInstance(iGetApiResponse, Constants.PATH_FIND_OK_STATUS_WAIT)
                                            .setId(idRoute);

                                    iGetApiResponse
                                            .getApiResponse(getErrprRest(Constants.PATH_FIND_OK_STATUS_WAIT
                                                    , error
                                                    , utilitesErrorIGetApiResponseObject));
                                }
                            });
    }

    public RESTConnect setTAG(String name) {
        String[] split = name.split("\\.");
        tag = split[split.length - 1] + ".class";
        return this;
    }
}
