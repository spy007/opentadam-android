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

public class PaymentMethod {
    @SerializedName("kind")
    public String kind;
    @SerializedName("id")
    public Long id;
    @SerializedName("name")
    public String name;
    @SerializedName("enoughMoney")
    public Boolean enoughMoney;

    public PaymentMethod(String kind, Long id, String name) {
        this.kind = kind;
        this.id = id;
        this.name = name;
    }

    public PaymentMethod(String cash) {
        this.kind = cash;
    }

    public String getName() {
        return name;
    }
}
