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

package com.opentadam.network.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Settings {
    // айди родительского сервиса
    @SerializedName("serviceId")
    private String serviceId;
    // Доступна ли оплата по банковской карте
    @SerializedName("cardPaymentAllowed")
    private boolean cardPaymentAllowed;
    // Параметры для связи с диспетчерской
    @SerializedName("dispatcherCall")
    private DispatcherCall dispatcherCall;
    // Режим отображения главного интерфейса

    /*
    simple - старт с первой страницы,переворот на вторую запрещен
    advanced - старт с второй, поворот на первую запрещен
    simple-advanced Старт с первой поворот на вторую разрешен
    advanced-simple Старт с второй поворот на первую страницу разрешен
    */


    @SerializedName("mainInterface")
    private String mainInterface;

    // Сервисы геокодинга
     /*
     google	Google
    yandex	Yandex
    osm	OpenStreetMap
    */

    @SerializedName("geocoding")
    private List<String> geocoding;

    // Валюта
    @SerializedName("currency")
    private Currency currency;
    // Типы отображаемой карты
    /*
     google	Google
    yandex	Yandex
    osm	OpenStreetMap
    */
    @SerializedName("maps")
    private List<String> maps;
    // Средняя скорость
    @SerializedName("averageSpeed")
    private int averageSpeed;

    public Settings(Settings workSettings) {
        serviceId = null;
        cardPaymentAllowed = workSettings.getCardPaymentAllowed();
        dispatcherCall = workSettings.getDispatcherCall();
        mainInterface = workSettings.getMainInterface();
        geocoding = workSettings.getGeocoding();
        currency = workSettings.getCurrency();
        maps = workSettings.getMaps();
        averageSpeed = workSettings.getAverageSpeed();
    }

    public Settings() {

    }


    // Конечный адрес обязателен
/*    @SerializedName("destinationRequired")
    public boolean destinationRequired;*/

    public static Settings invoke(Settings workSettings) {
        return new Settings(workSettings);
    }

    public boolean isEnabledRevers() {

        return "simple-advanced".equals(mainInterface) || "advanced-simple".equals(mainInterface);

    }

    public boolean isSkipFirstScreen() {

        return "advanced".equals(mainInterface) || "advanced-simple".equals(mainInterface);

    }

    public String getCurrencyShort() {
        return currency.sign;
    }

    public float getFloatAverageSpeed() {
        return averageSpeed * 3.6f;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public Settings setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
        return this;
    }

    public String[] getReverseGeocodingArray() {
        if (geocoding != null && geocoding.size() != 0) {
            String[] strings = new String[geocoding.size()];
            for (int i = 0; i < geocoding.size(); i++) {
                strings[i] = geocoding.get(i);
            }


            return strings;

        } else {
            return new String[]{"google"};
        }

    }

    public String[] getMapTitleSourceArray() {
        if (maps != null && maps.size() != 0) {
            int size = maps.size();
            String[] strings = new String[size];
            for (int i = 0; i < size; i++) {
                strings[i] = maps.get(i);
            }


            return strings;

        } else {
            return new String[]{"google"};
        }

    }

    public String getServiceId() {
        return serviceId;
    }

    public Settings setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public boolean getCardPaymentAllowed() {
        return cardPaymentAllowed;
    }

    public Settings setCardPaymentAllowed(boolean cardPaymentAllowed) {
        this.cardPaymentAllowed = cardPaymentAllowed;
        return this;
    }

    public DispatcherCall getDispatcherCall() {
        return dispatcherCall;
    }

    public Settings setDispatcherCall(DispatcherCall dispatcherCall) {
        this.dispatcherCall = dispatcherCall;
        return this;
    }

    public String getMainInterface() {
        return mainInterface;
    }

    public Settings setMainInterface(String mainInterface) {
        this.mainInterface = mainInterface;
        return this;
    }

    public List<String> getGeocoding() {
        return geocoding;
    }

    public Settings setGeocoding(List<String> geocoding) {
        this.geocoding = geocoding;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Settings setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public List<String> getMaps() {
        return maps;
    }

    public Settings setMaps(List<String> maps) {
        this.maps = maps;
        return this;
    }
}
