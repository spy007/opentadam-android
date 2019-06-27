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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.Locale;

public class ResultDayStat {

    // YYYY-MM-DD
    @SerializedName("date")
    public String date;
    @SerializedName("plusBalance")
    public BigDecimal plusBalance;
    @SerializedName("plusBalanceRef")
    public BigDecimal plusBalanceRef;
    @SerializedName("minusBalance")
    public BigDecimal minusBalance;
    @SerializedName("plusFriends")
    public int plusFriends;
    @SerializedName("dateValuePopup")
    public String dateValuePopup;
    @SerializedName("dayBalance")
    public BigDecimal dayBalance;
    @SerializedName("plusBalanceAll")
    public BigDecimal plusBalanceAll;

    public String getDatePoup() {
        DateTimeFormatter formatterTransaction =
                DateTimeFormat.forPattern("d MMMM")
                        .withLocale(Locale.getDefault());
        DateTime dateTime = new DateTime(date);

        return formatterTransaction.print(dateTime).toUpperCase(Locale.getDefault());
    }

    public long getDateLong() {

        DateTime dateTime = new DateTime(date);
        return dateTime.getMillis();
    }

}
