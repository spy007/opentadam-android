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

import java.util.ArrayList;

public class SendCreateRequest {
    @SerializedName("paymentMethod")
    public PaymentMethod paymentMethod;
    @SerializedName("tariff")
    public Long tariff;
    @SerializedName("options")
    public ArrayList<Long> options;
    @SerializedName("route")
    public ArrayList<ClientAddress> clientAddresses;
    @SerializedName("time")
    public String time;
    @SerializedName("comment")
    public String comment;
    @SerializedName("fixCost")
    public Float fixCost;
    @SerializedName("useBonuses")
    public Float useBonuses;

/*    public SendCreateRequest(PaymentMethod paymentMethod,
                             String time,
                             Float useBonuses,
                             ArrayList<Long> options,
                             ArrayList<ClientAddress> clientAddresses,
                             String phone,
                             String comment,
                             Long tariff,
                             Float costCorrection) {
        this.paymentMethod = paymentMethod;
        this.options = options;
        this.clientAddresses = clientAddresses;
        this.time = time;
        this.comment = comment;
        this.tariff = tariff;
        this.useBonuses = useBonuses;
    }*/

    private SendCreateRequest(Builder builder) {
        paymentMethod = builder.paymentMethod;
        options = builder.options;
        clientAddresses = builder.route;
        time = builder.time;
        comment = builder.comment;
        tariff = builder.tariff;
        fixCost = builder.fixCost;
        useBonuses = builder.useBonuses;
    }

    public static class Builder {

        private PaymentMethod paymentMethod;
        private Long tariff;
        private ArrayList<Long> options;
        private ArrayList<ClientAddress> route;
        private String time;
        private String comment;
        private Float fixCost;
        private Float useBonuses;


        public Builder() {

        }

        public Builder setPaymentMethod(PaymentMethod val) {
            paymentMethod = val;
            return this;
        }

        public Builder setTariff(long val) {
            tariff = val;
            return this;
        }

        public Builder setOptions(ArrayList<Long> val) {
            options = val;
            return this;
        }

        public Builder setRoute(ArrayList<ClientAddress> val) {
            route = val;
            return this;
        }


        public Builder setTime(String val) {
            time = val;
            return this;
        }

        public Builder setComment(String val) {
            comment = val;
            return this;
        }

        public Builder setFixCost(Float val) {
            fixCost = val;
            return this;
        }

        public Builder setUseBonuses(Float val) {
            useBonuses = val;
            return this;
        }

        public SendCreateRequest buidl() {
            return new SendCreateRequest(this);
        }

    }

}
