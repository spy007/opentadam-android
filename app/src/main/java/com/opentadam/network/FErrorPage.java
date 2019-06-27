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

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusApiResponseSuccess;
import com.opentadam.network.rest.Settings;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.squareup.otto.Subscribe;

public class FErrorPage extends V3FRoute {
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    private UtilitesErrorIGetApiResponse utilitesErrorIGetApiResponse;
    private Runnable runnableCameraIdle;

    public static Fragment newInstance() {

        return new FErrorPage().withViewId(R.layout.v3_froute);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setModeErrorNet();
        super.onActivityCreated(savedInstanceState);


        utilitesErrorIGetApiResponse = getAWork().getUtilitesErrorIGetApiResponse();
        if (utilitesErrorIGetApiResponse == null)
            return;

        ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap =
                Injector.getClientData()
                        .getArrayMap();
        int size = arrayMap.size();
        if (size == 0) {
            // не пингуем инет
            showPageError();
        } else {
            // startPingNet();
            showPageError();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        goBakgraund();

    }


    @Override
    public synchronized void onCameraIdle(final LatLng latLng, final boolean isEnabledUpdTarif) {
        Injector.getClientData().setMarkerLocation(latLng);
        goBakgraund();
    }

    private synchronized void goBakgraund() {
        if (runnableCameraIdle != null)
            mHandler.removeCallbacks(runnableCameraIdle);

        runnableCameraIdle = new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;
                repeatNet();
                aPinFrouteEServis.animateShowProgressPin(errorIcNet);

            }
        };

        mHandler.postDelayed(runnableCameraIdle, 500);
    }

    @Subscribe
    public void onBusApiResponseSuccess(BusApiResponseSuccess e) {

        ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap =
                Injector.getClientData()
                        .getArrayMap();
        arrayMap.remove(e.path);
        if (arrayMap.size() != 0)
            goBakgraund();
        else {

            if (utilitesErrorIGetApiResponse == null)
                return;

            utilitesErrorIGetApiResponse.hideErrorPage();
        }
    }

    @Override
    public void onDestroyView() {
        hidePageError();
        Injector.getClientData().setArrayMap(new ArrayMap<String, UtilitesErrorIGetApiResponseObject>());
        if (utilitesErrorIGetApiResponse != null) {
            utilitesErrorIGetApiResponse.finish();
        }

        utilitesErrorIGetApiResponse = null;
        getAWork().setUtilitesErrorIGetApiResponse(null);
        getAWork().isDisabledPing = false;
        super.onDestroyView();
    }


    private void repeatNet() {
        if (!isVisible())
            return;

        ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap =
                Injector.getClientData()
                        .getArrayMap();
        int size = arrayMap.size();
        final RESTConnect restConnect = Injector.getRC();
        for (int i = 0; i < size; i++) {
            String path = arrayMap.keyAt(i);
            UtilitesErrorIGetApiResponseObject val = arrayMap.valueAt(i);

            switch (path) {
/*                case Constants.PATH_FIND_REFERRAL_DATA:
                    restConnect.getReferralData(val.getLatLng(), val.getiGetApiResponse());
                    break;*/
                case Constants.PATH_GET_LP_BY_DAY:
                    restConnect.getLoyaltyProgramByDay(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_GET_LP_TRANSACTION:
                    restConnect.getLoyaltyProgramTransactions(val.getLatLng(), val.getOft(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_GET_HISTORY_LIST:
                    restConnect.getLoyaltyProgramByDayList(val.getLatLng(), val.getOft(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_LIST_COUNTRY:
                    restConnect.getCountries(val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_GET_ACCOUNT:
                 //   restConnect.getAccount(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_PAYMENT_METHOD:
                    restConnect.getPaymentMethod(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_ESTIMATE:
                    restConnect.getEstimation(
                            val.getiGetApiResponse()
                            , val.getLatLng()
                            , val.getFinalDate()
                            , val.getFinalAuthentication()
                            , val.getParams());
                    break;
                case Constants.PATH_TIME_SYNCHRONIZATION:
                    restConnect.timeSynchronizationServers(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_DISPATCHER_CALL:
                    restConnect.getCallDisp(val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_SERVICE:
                    restConnect.getService(val.getPaymentMethod(), val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_ADD_CARD + "_POST":
                    restConnect.findAddCard(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_ORDERS:
                 //   restConnect.sendOrders(val.getSendCreateRequest(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_ORDER_INFO:
                    restConnect.getOrderInfo(val.getId(), val.getiGetApiResponse());
                    break;
                case "getCurrentOrders" + Constants.PATH_ORDERS_CURRENT:
                    restConnect.getOrderCurrent("getCurrentOrders", val.getiGetApiResponse());
                    break;
                case "getPingOrders" + Constants.PATH_ORDERS_CURRENT:
                    restConnect.getOrderCurrent("getPingOrders", val.getiGetApiResponse());
                    break;
                case Constants.PATH_DRIVERS:
                    restConnect.getDrivers(val.getLatLng(), val.getParams(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_BONUSES:
                    restConnect.getBonusClient(val.getLatLng(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_LINES_INFO:
                    restConnect.getLinesInfo(val.getiGetApiResponse(), val.getId());
                    break;
                case Constants.PATH_HISTORY:
                    restConnect.getHistoryOrders(val.getiGetApiResponse());
                    break;
                case Constants.PATH_LIST_ADRESS_LAT_LON:
                    restConnect.getAdressGeocodeServers(val.getLatLng(), val.getiGetApiResponse(), System.currentTimeMillis());
                    break;
                case Constants.PATH_LIST_ADRESS_GEOCODING:
                    restConnect.getAdressPatern(val.getPath(), val.getLatLng()
                            , val.getiGetApiResponse(), val.getTimeRest());
                    break;
                case Constants.PATH_REG_PHONE:
                    restConnect.sendPhoneToServers(val.getSubmitRequest(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_REQUEST_DRIVER_CALL:
                    restConnect.sendCallServers(val.getId(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_SET_PROLONGATION:
                    restConnect.sendProlongation(val.getPosProlongation(), val.getId(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_REG_CODE:
                    restConnect.sendToServers(val.getId(), val.getmCodeUser(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_DELETE:
                    restConnect.deleteRoute(val.getId(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_DEBET_CARD:
                    restConnect.debetCard(val.getLatLng(), val.getId(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_DELETE_CARD:
                    restConnect.deleteCard(val.getLatLng(), val.getId(), val.getiGetApiResponse());
                    break;

                case Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS:
                    restConnect.findEditSubmissionDetails(val.getLatLng(), val.getId()
                            , val.getParamsEditSubmissionDetails(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_FIX_COST:
                    restConnect.setFixCost(val.getId(), val.getAmount(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_EDIT_PAYMENT_METHOD:
                    restConnect.editPaymentMethod(val.getLatLng(), val.getId()
                            , val.getEditPaymentMethod(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_EDIT_OPTIONS:
                    restConnect.editOptions(val.getLatLng(), val.getId()
                            , val.getOptionsList(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_EDIT_COMMENTS:
                    restConnect.editComments(val.getLatLng(), val.getId()
                            , val.getParamsComments(), val.getiGetApiResponse());
                    break;
                case Constants.PATH_FIND_OK_STATUS_WAIT:
                    restConnect.findOkStatusWait(val.getId(), val.getiGetApiResponse());
                    break;
            }
        }
    }

    @Override
    public int getPageDP() {
        return 0;
    }

    private void showPageError() {
        if (utilitesErrorIGetApiResponse == null) {
            return;
        }
        utilitesErrorIGetApiResponse.showMessage();
        utilitesErrorIGetApiResponse.showErrorWorkContainer();
    }

    private void hidePageError() {
        if (utilitesErrorIGetApiResponse == null) {
            return;
        }
        utilitesErrorIGetApiResponse.hideMessage();
        utilitesErrorIGetApiResponse.hideErrorWorkContainer();
    }

    private void startPingNet() {
        Settings workSettings = Injector.getWorkSettings();
        if (workSettings == null) {
            getAWork().hideBody();
        }
    }

    public void hideAnimePin() {
        if(!isVisible() || aPinFrouteEServis == null){
            return;
        }
        aPinFrouteEServis.animateStopProgressPin();
    }
}
