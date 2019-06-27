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

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.ui.AWork;

class ControllerNetwork {

    private final FBaseMaps fBaseMaps;


    private ControllerNetwork(FBaseMaps obj1) {

        fBaseMaps = obj1;
    }

    static ControllerNetwork invoke(FBaseMaps fBaseMaps) {
        return new ControllerNetwork(fBaseMaps);
    }

    void openApplicationSettingsLocation() {
        final AWork aWork = fBaseMaps.getAWork();
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + aWork.getPackageName()));
        aWork.startActivityForResult(appSettingsIntent
                , Constants.PERMISSION_REQUEST_LOCATION);
    }

    public void showDialogSetEnamledLocation() {

        final AWork aWork = fBaseMaps.getAWork();

 /*       if (isDisabledGloballocation) {
            openApplicationSettingsLocation();
            return;
        }
*/
        LocationRequest locationRequest = App.app.mMyGoogleLocation.getmLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> resultPendingResult = LocationServices
                .SettingsApi
                .checkLocationSettings(App.app.mMyGoogleLocation.getmGoogleApiClient(), builder.build());

        resultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.e("jhjk", "SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Log.e("jhjk", "RESOLUTION_REQUIRED");

                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    aWork,
                                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.


                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Log.e("jhjk", "SETTINGS_CHANGE_UNAVAILABLE");

                        break;
                }

            }
        });

    }


}
