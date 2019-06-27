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

import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.opentadam.bus.HiveBus;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.AddressComponent;
import com.opentadam.network.rest.GpsPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {


    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            double mLatitude = intent.getDoubleExtra(Constants.LAT_GOOGLE, 0);
            double mLongitude = intent.getDoubleExtra(Constants.LON_GOOGLE, 0);
            //     Locale aDefault = Locale.ENGLISH;
            Locale aDefault = Locale.getDefault();
            Geocoder geocoder = new Geocoder(this, aDefault);

            try {

                List<android.location.Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

                if (addresses != null && addresses.size() != 0) {

                    android.location.Address returnedAddress = addresses.get(0);
/*
                    StringBuilder addressBuilder = new StringBuilder(
                            "Адрес:\n");
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        addressBuilder
                                .append(returnedAddress.getAddressLine(i)).append(
                                "\n");
                    }*/
/*
                    Log.d(TAG,
                            "getAddressLine: " + addressBuilder.toString()
                                    + " getAdminArea: " + returnedAddress.getAdminArea() + "\n"
                                    + " getCountryCode: " + returnedAddress.getCountryCode() + "\n"
                                    + " getCountryName: " + returnedAddress.getCountryName() + "\n"
                                    + " getFeatureName: " + returnedAddress.getFeatureName() + "\n"
                                    + " getLocality: " + returnedAddress.getLocality() + "\n"
                                    + " getPhone: " + returnedAddress.getPhone() + "\n"
                                    + " getPostalCode: " + returnedAddress.getPostalCode() + "\n"
                                    + " getPremises: " + returnedAddress.getPremises() + "\n"
                                    + " getSubAdminArea: " + returnedAddress.getSubAdminArea() + "\n"
                                    + " getSubLocality: " + returnedAddress.getSubLocality() + "\n"
                                    + " getThoroughfare: " + returnedAddress.getThoroughfare() + "\n"
                                    + " getSubThoroughfare: " + returnedAddress.getSubThoroughfare() + "\n"
                                    + " getLocale: " + returnedAddress.getLocale() + "\n"
                    );*/

                    Address adress = new Address();

                    adress.position = new GpsPosition(mLatitude, mLongitude);
                    adress.components = getComponents(returnedAddress);
                    adress.name = getName(adress);

                    if (adress.name == null || adress.components == null) {
                        HiveBus.postBusUpdUIAdressMapsYaGoogle(
                                getNullAdress(mLatitude, mLongitude));
                        return;
                    }
                    //    HiveBus.postBusUpdUIAdressMapsYaGoogle(null);
                    //    Log.d("uuiuiiu", "FetchAddressIntentService");
                    HiveBus.postBusUpdUIAdressMapsYaGoogle(adress);

                } else {
                    HiveBus.postBusUpdUIAdressMapsYaGoogle(getNullAdress(mLatitude, mLongitude));
                }
            } catch (IOException e) {
                e.printStackTrace();
                HiveBus.postBusUpdUIAdressMapsYaGoogle(getNullAdress(mLatitude, mLongitude));
            }
        }
    }

    @NonNull
    private Address getNullAdress(double mLatitude, double mLongitude) {
        return new Address(Injector
                .getAppContext()
                .getResources()
                .getString(R.string.point_to_maps)
                ,mLatitude, mLongitude);
    }

    private String getName(Address adress) {
        StringBuilder name = new StringBuilder();
        List<AddressComponent> components = adress.components;
        if (components == null)
            return null;
        //  return Injector.getAppContext().getString(R.string.point_to_maps);

        for (AddressComponent addressComponent : components) {
            name.append(addressComponent.name);
        }
        int max = components.size();
        for (int i = 0; i < max; i++) {
            name.append(components.get(i).name).append(i != max - 1 ? ", " : "");
        }
        return "".equals(name.toString()) ? Injector
                .getAppContext()
                .getResources()
                .getString(R.string.point_to_maps) : name.toString();
    }

    private List<AddressComponent> getComponents(android.location.Address returnedAddress) {
        List<AddressComponent> components = new ArrayList<>();
        if (returnedAddress == null)
            return null;

        String countryName = returnedAddress.getCountryName();
        if (countryName != null)
            components.add(new AddressComponent(0, countryName));// страна

        String administrativeAreaName = returnedAddress.getAdminArea();
        if (administrativeAreaName != null)
            components.add(new AddressComponent(1, administrativeAreaName));// область

        String localityName = returnedAddress.getLocality();
        if (localityName != null)
            components.add(new AddressComponent(4, localityName));// населенный пункт

        String thoroughfareName = returnedAddress.getThoroughfare();
        if (thoroughfareName == null || "Unnamed Road".equals(thoroughfareName))
            return null;

        String tfareName = thoroughfareName.replace("улица ", "");
        components.add(new AddressComponent(7, tfareName));// улица

        String subThoroughfare = returnedAddress.getSubThoroughfare();
        if (subThoroughfare == null)
            return null;
        components.add(new AddressComponent(8, subThoroughfare));// дом


        return components.size() == 0 ? null : components;

    }

}
