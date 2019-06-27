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

package com.opentadam.ui;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Constants;
import com.opentadam.FetchAddressIntentService;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.HiveBus;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.AddressComponent;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OSMAddress;
import com.opentadam.network.rest.OSMApi;
import com.opentadam.network.rest.SubmitOSMReversGeocode;
import com.opentadam.network.rest.SubmitYaReversGeocode;
import com.opentadam.network.rest.YaAddress;
import com.opentadam.network.rest.YaApi;
import com.opentadam.network.rest.YaComponents;
import com.opentadam.network.rest.YaGeoObject;
import com.opentadam.network.rest.YaGeoObjectBase;
import com.opentadam.network.rest.YaGeoObjectCollection;
import com.opentadam.network.rest.YaGeocoderMetaData;
import com.opentadam.network.rest.YaMetaDataProperty;
import com.opentadam.network.rest.YaResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseFrReversGeocode extends BaseFr {

    protected int reverseGeocodingArrayPos;
    protected LatLng reverseGeocodingLatLng;


    public void startReverseGeocodingArray(final LatLng latLng) {
        if (latLng == null
                || !isVisible()
                || Injector.getWorkSettings() == null
                || getAWork() == null) {
            return;
        }


        String[] reverseGeocodingArray = Injector
                .getWorkSettings()
                .getReverseGeocodingArray();

        if (reverseGeocodingArray == null
                || reverseGeocodingArrayPos > reverseGeocodingArray.length - 1) {
            sendDefaultNullPoint(latLng);
            return;
        }


        String s = reverseGeocodingArray[reverseGeocodingArrayPos];


        switch (s) {
            case "google":
                reverseGeocodingGoogle(latLng);
                break;
            case "yandex":
                reverseGeocodingYandex(latLng);
                break;
            case "osm":
                reverseGeocodingOSM(latLng);
                break;
        }

        reverseGeocodingArrayPos++;
    }

    private void sendDefaultNullPoint(LatLng latLng) {
        Address adress = new Address();

        adress.position = new GpsPosition(latLng.latitude, latLng.longitude);
        adress.components = null;
        adress.name = getString(R.string.point_to_maps);
        HiveBus.postBusUpdUIAdressMapsYaGoogle(adress);
    }

    private void reverseGeocodingOSM(final LatLng latLng) {
        //    https://nominatim.openstreetmap.org/reverse?format=json&addressdetails=0&lat=54.975384&lon=73.335373
        // https://nominatim.openstreetmap.org/reverse?format=json&lat=55.04487629240654&lon=73.4218142554164
        // https://nominatim.openstreetmap.org/reverse?format=json&lat=55.045399737823836&lon=73.42705093324184 55.045399737823836,73.42705093324184

        OSMApi osmApi = Injector.getOSMApi();
        if (osmApi == null)
            return;

        osmApi.getReversGeocodingOSM(latLng.latitude, latLng.longitude, new Callback<SubmitOSMReversGeocode>() {
            @Override
            public void success(SubmitOSMReversGeocode submitOSMReversGeocode, Response response) {
                if (!isVisible() || getAWork() == null)
                    return;
                if (submitOSMReversGeocode == null) {
                    startReverseGeocodingArray(latLng);
                    return;
                }

                if (submitOSMReversGeocode.address == null || submitOSMReversGeocode.displayName == null) {
                    startReverseGeocodingArray(latLng);
                    return;
                }

                Address adress = new Address();

                adress.position = new GpsPosition(latLng.latitude, latLng.longitude);
                adress.components = getComponentsOSM(submitOSMReversGeocode.address);
                if (adress.components == null) {
                    startReverseGeocodingArray(latLng);
                    return;
                }
                adress.name = submitOSMReversGeocode.displayName;

                HiveBus.postBusUpdUIAdressMapsYaGoogle(adress);
            }

            @Override
            public void failure(RetrofitError error) {
                startReverseGeocodingArray(latLng);
            }
        });
    }

    private List<AddressComponent> getComponentsOSM(OSMAddress address) {
        List<AddressComponent> componentsOSM = new ArrayList<>();
        if (address.busStop != null) {
            address.busStop = getString(R.string.bus_stop) + " " + address.busStop;
        }
        String alias = address.alias == null ?
                address.busStop : address.alias;


        if (alias == null && address.house == null)
            return null;
        if (address.country != null)
            componentsOSM.add(new AddressComponent(0, address.country));// страна

        if (address.state != null)
            componentsOSM.add(new AddressComponent(1, address.state));// область

        if (address.city != null)
            componentsOSM.add(new AddressComponent(4, address.city));// населенный пункт

        String name1 = alias == null ?
                address.street : null;
        if (name1 != null)
            componentsOSM.add(new AddressComponent(7, name1));// улица


        String name = alias == null ?
                address.house : null;
        if (name != null)
            componentsOSM.add(new AddressComponent(8, name));// дом

        //   componentsOSM.add(new AddressComponent(9, alias));// alias


        return componentsOSM;
    }

    private void reverseGeocodingYandex(final LatLng latLng) {
        // 54.975384 73.335373
        //    geocode-maps.yandex.ru/1.x/?format=json&lang=ru&kind=house&geocode=73.335373,54.975384
        YaApi yaApi = Injector.getYaApi();
        if (yaApi == null)
            return;

        yaApi.getReversGeocodingYa(latLng.longitude + "," + latLng.latitude
                , new Callback<SubmitYaReversGeocode>() {
                    @Override
                    public void success(SubmitYaReversGeocode submitYaReversGeocode, Response response) {
                        if (!isVisible() || getAWork() == null)
                            return;

                        if (submitYaReversGeocode == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }

                        YaResponse yaResponse = submitYaReversGeocode.response;
                        if (yaResponse == null) {
                            startReverseGeocodingArray(latLng);
                            return;

                        }

                        YaGeoObjectCollection geoObjectCollection = yaResponse.geoObjectCollection;
                        if (geoObjectCollection == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        List<YaGeoObjectBase> featureMember = geoObjectCollection.featureMember;
                        if (featureMember == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        int size = featureMember.size();
                        if (size == 0) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }

                        YaGeoObjectBase yaGeoObjectBase = featureMember.get(0);
                        if (yaGeoObjectBase == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        YaGeoObject geoObject = yaGeoObjectBase.geoObject;
                        if (geoObject == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        YaMetaDataProperty metaDataProperty = geoObject.metaDataProperty;
                        if (metaDataProperty == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        YaGeocoderMetaData geocoderMetaData = metaDataProperty.geocoderMetaData;
                        if (geocoderMetaData == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        YaAddress yaAddress = geocoderMetaData.yaAddress;

                        if (yaAddress == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }

                        List<YaComponents> yaComponents = yaAddress.yaComponents;
                        if (yaComponents == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }

                        int size1 = yaComponents.size();
                        if (size1 == 0) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }

                        Address adress = new Address();

                        adress.position = new GpsPosition(latLng.latitude, latLng.longitude);
                        adress.components = getComponentsYa(yaComponents);
                        if (adress.components == null) {
                            startReverseGeocodingArray(latLng);
                            return;
                        }
                        adress.name = yaAddress.formatted;

                        HiveBus.postBusUpdUIAdressMapsYaGoogle(adress);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        startReverseGeocodingArray(latLng);
                    }
                });
    }

    private List<AddressComponent> getComponentsYa(List<YaComponents> yaComponents) {
        List<AddressComponent> components = new ArrayList<>();

        for (YaComponents yaComponent : yaComponents) {
            switch (yaComponent.kind) {
                case "country":
                    components.add(new AddressComponent(0, yaComponent.name));// страна
                    break;
                case "province":
                    components.add(new AddressComponent(1, yaComponent.name));// область
                    break;
                case "locality":

                    components.add(new AddressComponent(4, yaComponent.name));// населенный пункт
                    break;
                case "street":
                    components.add(new AddressComponent(7, yaComponent.name));// улица
                    break;
                case "house":
                    if (yaComponent.name == null) {
                        return null;
                    }
                    components.add(new AddressComponent(8, yaComponent.name));// дом
                    break;
            }
        }


        return components;
    }

    private void reverseGeocodingGoogle(LatLng latLng) {
        if (!isVisible() || getAWork() == null)
            return;

        Intent intent = new Intent(getAWork(), FetchAddressIntentService.class);
        intent.putExtra(Constants.LAT_GOOGLE, latLng.latitude);
        intent.putExtra(Constants.LON_GOOGLE, latLng.longitude);
        getAWork().startService(intent);
    }
}
