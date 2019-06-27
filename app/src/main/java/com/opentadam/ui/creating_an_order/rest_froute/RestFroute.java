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

package com.opentadam.ui.creating_an_order.rest_froute;

import android.annotation.SuppressLint;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Params;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.ui_payemnts_metods.ProgressView;
import com.opentadam.yandex_google_maps.UtilitesMaps;

import java.util.ArrayList;
import java.util.List;

import static com.opentadam.Injector.getClientData;

public class RestFroute {

    private final V3FRoute v3FRoute;

    private RestFroute(V3FRoute val) {
        v3FRoute = val;
    }

    private RestFroute() {
        v3FRoute = null;
    }

    public static RestFroute newInstance(V3FRoute val) {
        return new RestFroute(val);
    }

    public static RestFroute newInstance() {
        return new RestFroute();
    }

    public void getAdressGeocodeServers(final LatLng latLng) {

        if (v3FRoute == null ||
                !v3FRoute.isVisible()
                || Injector.getClientData().isLatLngDisabled(latLng)) {
            return;
        }

        RESTConnect restConnect = Injector.getRC();

        restConnect.getAdressGeocodeServers(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!v3FRoute.isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    // v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (v3FRoute.isBlockSendServers)
                    return;


                if (Constants.PATH_LIST_ADRESS_LAT_LON.equals(apiResponse.path)) {

                    List<Address> addresses = apiResponse.addresses;

                    Address geo = UtilitesMaps.instanse()
                            .getAddressServers(addresses);

                    if (geo == null || addresses.size() == 0) {


                        v3FRoute.startReverseGeocodingArray(latLng);
                        return;
                    }

                    geo.position = new GpsPosition(latLng);
                    v3FRoute.updUIAdressMaps(geo);
                }
            }
        }, null);
    }

    public void getDrivers(final LatLng latLng) {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;

        String name = getClass().getName();

        RESTConnect restConnect = Injector.getRC()
                .setTAG(name);

        Long tarif = getClientData().getIdDefTarif();

        if (tarif == null) {
            v3FRoute.errorGetDrivers();
            return;
        }


        restConnect.getDrivers(
                latLng,
                new Params(Injector.getClientData().getPaymentMethodSelect(), tarif),
                new IGetApiResponse() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!v3FRoute.isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            v3FRoute.errorGetDrivers();
                            //   v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        if (apiResponse.driverList.size() == 0) {
                            v3FRoute.errorGetDrivers();
                            return;
                        }

                        v3FRoute.bodyGetDrivers(apiResponse, latLng);


                    }
                });
    }

    public void getCostRest() {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;

        if (!Injector.getClientData().isEnabledService()) {
            return;
        }
        ArrayList<Long> options = getClientData().getTempObjectUIMRoute().getOptions();
        String timeValue = getClientData().getTempObjectUIMRoute().getTimeOrderIso();
        final Long idTariff = getClientData().getIdDefTarif();

        if (idTariff == null)
            return;

        CreateRequest createRequest = getClientData().getCreateRequest();
        if (createRequest == null)
            return;

        ArrayList<GpsPosition> routeLocation = createRequest.getRouteLocation();

        PaymentMethod paymentMethodSelect = getClientData().getPaymentMethodSelect();


        if (routeLocation != null && routeLocation.size() != 0) {
            LatLng latLng = routeLocation.get(0).getLatLng();
            v3FRoute.getAWork().timeSynchronizationServers(latLng);
        }


        RESTConnect restConnect = Injector.getRC();

        restConnect.getEstimation(paymentMethodSelect
                , idTariff, options, routeLocation, timeValue, new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {

                        if (!v3FRoute.isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            // v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        v3FRoute.bodyGetCost(apiResponse);


                    }

                });
    }

    public void getTarif(final ProgressView progressView, final boolean isRecreate) {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;
        RESTConnect restConnect = Injector.getRC();
        Injector.getWorkSettings().setServiceId(null);
        restConnect.getService(getClientData().getPaymentMethodSelect(),
                Injector.getClientData().latLngTarifHTPSRequest, new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {

                        if (!v3FRoute.isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        progressView.setVisibility(View.GONE);
                        v3FRoute.getAWork().bodyGetTarif(apiResponse);
                        if (isRecreate) {
                            return;
                        }
                        v3FRoute.getAWork().showV3RestoryFRoute();
                    }
                });

    }

    public void getRestInit() {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;

        RESTConnect restConnect = Injector.getRC();
        restConnect.getPaymentMethod(v3FRoute.getLatLng(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!v3FRoute.isVisible())
                    return;

                if (apiResponse != null
                        && apiResponse.error != null
                        && Injector.isOfflineStatus(apiResponse.retrofitError)) {
                    v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                getClientData().paymentMethods = apiResponse.paymentMethods;

                getAccount();
            }
        });


    }

    private void getAccount() {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;

        if (getClientData().isEnabledCard()) {
            RESTConnect restConnect = Injector.getRC();
            restConnect.getAccount(v3FRoute.getLatLng(), new IGetApiResponse() {
                @Override
                public void getApiResponse(ApiResponse apiResponse) {
                    if (!v3FRoute.isVisible())
                        return;

                    if (apiResponse == null || apiResponse.error != null) {
                      //  v3FRoute.getAWork().showErrorIGetApiResponse(apiResponse);
                        return;
                    }
                    getClientData().accountState = apiResponse.accountState;
                }
            });
        }
    }

    public void sendServersBody() {
        if (v3FRoute == null || !v3FRoute.isVisible())
            return;

        final CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        final TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();


        SendCreateRequest sendCreateRequest = getSendCreateRequest(createRequest, mRoute);

        RESTConnect restConnect = Injector.getRC();


        restConnect.sendOrders(sendCreateRequest, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                v3FRoute.getApiResponseSendOrders(mRoute, createRequest, apiResponse);
            }
        });

    }

    public SendCreateRequest getSendCreateRequest(CreateRequest createRequest, TempObjectUIMRoute mRoute) {
        String time = mRoute.getTimeOrderIso();
        String bunusClient = mRoute.bunusClient;
        Float bunusClientFloat = bunusClient == null ? null : Float.parseFloat(bunusClient);


        ArrayList<ClientAddress> routeTemp = createRequest.getRoute();
        final ArrayList<ClientAddress> route = new ArrayList<>();
        for (ClientAddress clientAddress : routeTemp) {
            if (clientAddress != null && clientAddress.address != null) {
                route.add(clientAddress);
            }
        }

        int size = route.size();
        if (size > 1 && route.get(1) == null)
            route.remove(1);

        String routeComment = null;
        if (createRequest.comment != null)
            routeComment = createRequest.comment.trim().replace("\n", " ");


        return new SendCreateRequest.Builder()
                .setPaymentMethod(getClientData().getPaymentMethodSelect())
                .setTariff(getClientData().getIdDefTarif())
                .setOptions(mRoute.getOptions())
                .setRoute(route)
                .setTime(time)
                .setComment(routeComment)
                .setFixCost(mRoute.fixCost)
                .setUseBonuses(bunusClientFloat).buidl();
    }
}
