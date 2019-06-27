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

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.ui.AWork;
import com.opentadam.ui.ErrorCodeServers;

import static com.opentadam.utils.Utilites.getDPtoPX;

public class UtilitesErrorIGetApiResponse {
    private AWork aWork;

    public ApiResponse getApiResponse() {
        return apiResponse;
    }

    private ApiResponse apiResponse;
    private View errorWorkContainer;
    private Snackbar errorSnackbar;
    private View bodyApp;


    public static UtilitesErrorIGetApiResponse newInstance(AWork aWork, ApiResponse apiResponse) {
        UtilitesErrorIGetApiResponse utilitesErrorIGetApiResponse = new UtilitesErrorIGetApiResponse();
        utilitesErrorIGetApiResponse.aWork = aWork;
        utilitesErrorIGetApiResponse.apiResponse = apiResponse;
        return utilitesErrorIGetApiResponse;
    }

    private String getMessageError() {
        Integer status = Injector.getStatus(apiResponse.retrofitError);
        if (!Injector.isOnline() && status == null) {
            return aWork.getString(R.string.error_network);
        }

        if (status == null) {
            return String.format(aWork.getString(R.string.unknown_error_servers)
                    , getTranslateRerrorTimeout());
        }

        switch (status) {
            case 400:
             //   finish();
                ErrorCodeServers errorCodeServers = apiResponse.errorCodeServers;
                return getMessageVIKI() + "\n" +
                         (errorCodeServers == null ? "" :
                                 errorCodeServers.code + " " +  errorCodeServers.message);


            default:
              //  finish();
                return getMessageVIKI() + "\n" +
                        apiResponse.error;

        }

    }

    private String getTranslateRerrorTimeout() {
        return apiResponse.error.contains("SSL handshake timed out") ?
                aWork.getString(R.string.err_timeout_server) : aWork.getString(R.string.err_timeout_net);
    }

    private String getMessageVIKI() {

        switch (apiResponse.path) {
            case Constants.PATH_FIND_REFERRAL_DATA:
                return aWork.getString(R.string.path_find_refrerral_data);
            case Constants.PATH_GET_LP_BY_DAY:
                return aWork.getString(R.string.path_get_lp_by_day);
            case Constants.PATH_GET_LP_TRANSACTION:
                return aWork.getString(R.string.path_get_lpo_trasaction);
            case Constants.PATH_GET_HISTORY_LIST:
                return aWork.getString(R.string.path_get_history_list);
            case Constants.PATH_LIST_COUNTRY:
                return aWork.getString(R.string.path_list_contry);
            case Constants.PATH_FIND_GET_ACCOUNT:
                return aWork.getString(R.string.path_find_get_account);
            case Constants.PATH_PAYMENT_METHOD:
                return aWork.getString(R.string.path_payment_method);
            case Constants.PATH_ESTIMATE:
                return aWork.getString(R.string.path_estimate);
            case Constants.PATH_TIME_SYNCHRONIZATION:
                return aWork.getString(R.string.path_time_syn);
            case Constants.PATH_FIND_DISPATCHER_CALL:
                return aWork.getString(R.string.path_dispatcher_call);
            case Constants.PATH_FIND_SERVICE:
                return aWork.getString(R.string.path_find_service);
            case Constants.PATH_ADD_CARD + "_POST":
                return aWork.getString(R.string.path_add_card);
            case Constants.PATH_FIND_ORDERS:
                return aWork.getString(R.string.path_find_order);
            case Constants.PATH_ORDER_INFO:
                return aWork.getString(R.string.path_cyrrent_order);
            case "getCurrentOrders" + Constants.PATH_ORDERS_CURRENT:
                return aWork.getString(R.string.path_order);
            case "getPingOrders" + Constants.PATH_ORDERS_CURRENT:
                return aWork.getString(R.string.err_send_value_400);
            case Constants.PATH_DRIVERS:
                return aWork.getString(R.string.path_drivers);
            case Constants.PATH_BONUSES:
                return aWork.getString(R.string.path_bonuses);
            case Constants.PATH_LINES_INFO:
                return aWork.getString(R.string.path_lines_info);
            case Constants.PATH_HISTORY:
                return aWork.getString(R.string.path_history);
            case Constants.PATH_LIST_ADRESS_LAT_LON:
                return aWork.getString(R.string.path_lis_lat_lon);
            case Constants.PATH_LIST_ADRESS_GEOCODING:
                return aWork.getString(R.string.path_geocoding);
            case Constants.PATH_REG_PHONE:
                return aWork.getString(R.string.path_reg_phone);
            case Constants.PATH_FIND_REQUEST_DRIVER_CALL:
                return aWork.getString(R.string.path_driver_call);
            case Constants.PATH_FIND_SET_PROLONGATION:
                return aWork.getString(R.string.path_find_prolong);
            case Constants.PATH_REG_CODE:
                return aWork.getString(R.string.path_reg_code);
            case Constants.PATH_FIND_DELETE:
                return aWork.getString(R.string.path_find_delete);
            case Constants.PATH_DEBET_CARD:
                return aWork.getString(R.string.path_debet_card);
            case Constants.PATH_DELETE_CARD:
                return aWork.getString(R.string.path_delete_card);

            case Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS:
                return aWork.getString(R.string.path_edit_submission_detalies);
            case Constants.PATH_FIND_FIX_COST:
                return aWork.getString(R.string.path_fix_cost);
            case Constants.PATH_FIND_EDIT_PAYMENT_METHOD:
                return aWork.getString(R.string.path_find_edit_payment_method);
            case Constants.PATH_FIND_EDIT_OPTIONS:
                return aWork.getString(R.string.path_edit_options);
            case Constants.PATH_FIND_EDIT_COMMENTS:
                return aWork.getString(R.string.path_edit_comment);
            case Constants.PATH_FIND_OK_STATUS_WAIT:
                return aWork.getString(R.string.path_ok_status_wait);
        }
        return aWork.getString(R.string.err_send_value_400);
    }

    private void showErrorPage() {
        aWork.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.error_work_container, FErrorPage.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();

    }

    public void hideErrorPage() {
        Fragment fragmentById = aWork.getSupportFragmentManager().findFragmentById(R.id.error_work_container);
        aWork.getSupportFragmentManager()
                .beginTransaction()
                .remove(fragmentById)
                .commitAllowingStateLoss();

    }

    public void showErrorWorkContainer() {
        errorWorkContainer.setVisibility(View.VISIBLE);
    }

    public void hideErrorWorkContainer() {
        errorWorkContainer.setVisibility(View.GONE);
    }

    public UtilitesErrorIGetApiResponse setErrorWorkContainer(View val) {
        errorWorkContainer = val;
        return this;
    }


    public void showMessage() {
        String messagError = getMessageError();
        if (messagError != null) {
            errorSnackbar = showErrorSnackbar(messagError);
        }
    }

    public void hideMessage() {
        if (errorSnackbar != null) {
            errorSnackbar.dismiss();
        }
    }

    private Snackbar showErrorSnackbar(String message) {

        final Snackbar finalGrant = Snackbar
                .make(bodyApp, message, Snackbar.LENGTH_INDEFINITE);

        View snackbarView = finalGrant.getView();
        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalGrant.dismiss();
            }
        });
        snackbarView.setBackgroundResource(R.color.bg_snackbar_error);
        TextView snackTextView = snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);

        snackTextView
                .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white, 0);


        snackTextView.setCompoundDrawablePadding(getDPtoPX(8));

        snackTextView.setTextColor(Color.WHITE);
        snackTextView.setMaxLines(2);
        snackTextView.setIncludeFontPadding(false);

        snackTextView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
        finalGrant.show();
        return finalGrant;
    }

    public UtilitesErrorIGetApiResponse setBodyApp(View val) {
        bodyApp = val;
        return this;
    }

    public void build() {
        showErrorPage();
    }

    public void finish() {
        Injector.getClientData()
                .setArrayMap(new ArrayMap<String, UtilitesErrorIGetApiResponseObject>());
    }

    public boolean isSnackbarHide() {
        if(errorSnackbar == null){
            return false;
        }
        return !errorSnackbar.isShown();
    }

    public void hideAnimePin() {
        FErrorPage fErrorPage = (FErrorPage) aWork.getSupportFragmentManager()
                .findFragmentById(R.id.error_work_container);
        if(fErrorPage != null){
            fErrorPage.hideAnimePin();
        }
    }
}
