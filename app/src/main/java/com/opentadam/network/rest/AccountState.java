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

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccountState {
    @SerializedName("balance")
    public String balance;
    @SerializedName("currency")
    public int currency;

    public boolean isDebet() {
        if (balance == null)
            balance = "0";
        return balance.contains("-");
    }

    public String getSummValue() {
        if (balance == null)
            balance = "0";
        BigDecimal bigDecimal = new BigDecimal(balance);
        String trim = balance.trim();
        bigDecimal = bigDecimal
                .setScale(trim.endsWith(".0")
                        || trim.endsWith(".00")
                        || trim.endsWith(".000")
                        || trim.endsWith(".")
                        ? 0 : 2, RoundingMode.HALF_UP);
        return String.valueOf(bigDecimal);

    }
}
