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

package com.opentadam.ui.frends.referal;

import com.google.gson.annotations.SerializedName;
import com.opentadam.Injector;
import com.opentadam.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;

public class Transaction {
    // YYYY-MM-DD
    @SerializedName("date")
    public String date;
    @SerializedName("amount")
    public BigDecimal amount;
    @SerializedName("balance")
    public BigDecimal balance;
    @SerializedName("typ")
    public Integer typ;
    @SerializedName("oft")
    public Long oft;
    @SerializedName("millis")
    public long millis;


    public long getDateLong() {

        DateTime dateTime = new DateTime(date);
        return dateTime.getMillis();
    }

    public String getValStringTyp() {
        switch (typ) {
            case 11:
                return Injector.getAppContext().getString(R.string.fbp_item_11);
            case 12:
                return Injector.getAppContext().getString(R.string.fbp_item_12);
            case 13:
                return Injector.getAppContext().getString(R.string.fbp_item_13);
            case 14:
                return Injector.getAppContext().getString(R.string.fbp_item_14);
            case 23:
                return Injector.getAppContext().getString(R.string.fbp_item_23);
            default:
                return "";
        }
    }

    public Transaction(Transaction transaction) {
        this.date = transaction.date;
        this.amount = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.millis = transaction.millis - 60 * 60 * 1000;
        this.oft = transaction.millis - 60 * 60 * 1000;
        this.typ = -1;
    }

    public Transaction(long millis) {
        this.date = null;
        this.amount = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.millis = millis; //System.currentTimeMillis() - 60*60*1000;
        this.oft = millis;
        this.typ = -1;
    }

    public String getDatePoup() {
        DateTimeFormatter formatterTransaction = DateTimeFormat.forPattern("d MMMM HH:mm:ss");
        DateTime dateTime = new DateTime(date);
        return formatterTransaction.print(dateTime);
    }
}
