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

public class Params {
    @SerializedName("paymentMethod")
    public PaymentMethod paymentMethod;
    @SerializedName("prevServiceId")
    public String prevServiceId;
    @SerializedName("tariff")
    public Long tariff;
    @SerializedName("options")
    public List<Long> options;
    @SerializedName("route")
    public List<GpsPosition> route;
    @SerializedName("time")
    public String time;
    @SerializedName("comment")
    public String comment;
    @SerializedName("costCorrection")
    public Float costCorrection;
    @SerializedName("useBonuses")
    public Float useBonuses;


    public Params(PaymentMethod paymentMethod
            , long tariff
            , List<Long> options
            , List<GpsPosition> route
            , String time) {
        this.paymentMethod = paymentMethod;
        this.tariff = tariff;
        this.options = options;
        this.route = route;
        this.time = time;
    }

    public Params(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Params(PaymentMethod paymentMethodSelect, Long tarif) {
        this.paymentMethod = paymentMethodSelect;
        this.tariff = tarif;
    }
}
