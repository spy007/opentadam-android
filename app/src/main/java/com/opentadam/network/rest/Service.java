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

public class Service {
    public boolean isNoRegReferall;
    @SerializedName("kind")
    private String kind;
    @SerializedName("serviceId")
    private String serviceId;
    @SerializedName("settings")
    private Settings settings;
    @SerializedName("location")
    private GpsPosition location;
    @SerializedName("tariffs")
    private List<Tarif> tariffs;
    @SerializedName("message")
    private String message;
    @SerializedName("address")
    private Address address;
    @SerializedName("lptype")
    private String lptype;

    public String getKind() {
        return kind;
    }

    public Service setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Service setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public Settings getSettings() {
        return settings;
    }

    public Service setSettings(Settings settings) {
        this.settings = settings;
        return this;
    }

    public GpsPosition getLocation() {
        return location;
    }

    public Service setLocation(GpsPosition location) {
        this.location = location;
        return this;
    }

    public List<Tarif> getTariffs() {
        return tariffs;
    }

    public Service setTariffs(List<Tarif> tariffs) {
        this.tariffs = tariffs;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Service setMessage(String message) {
        this.message = message;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public Service setAddress(Address address) {
        this.address = address;
        return this;
    }

    public String getLptype() {
        return lptype;
    }

    public Service setLptype(String lptype) {
        this.lptype = lptype;
        return this;
    }


}
