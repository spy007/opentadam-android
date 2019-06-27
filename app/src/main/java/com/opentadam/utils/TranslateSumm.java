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

package com.opentadam.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TranslateSumm {
    private final String rub;
    private final String kop;

    public TranslateSumm(String r, String k) {
        rub = r;
        kop = k;
    }

    public static TranslateSumm invoke(String balance) {
        if (balance == null || "".equals(balance)) {
            return new TranslateSumm("0", "");
        }
        String[] split = balance.split("\\.");
        if (split.length == 0) {
            return new TranslateSumm("0", "");
        }
        if (split.length == 1) {
            return new TranslateSumm(split[0], "");
        } else {
            return new TranslateSumm(split[0], split[1]);
        }
    }

    public static TranslateSumm invoke(String balance, boolean isShowZPT) {
/*
        translateSumm.rub = split[0];
        translateSumm.kop = (isShowZPT ? "," : "") + split[1];
        return translateSumm;*/

        if (balance == null || "".equals(balance)) {
            return new TranslateSumm("0", "");
        }
        String[] split = balance.split("\\.");
        if (split.length == 0) {
            return new TranslateSumm("0", "");
        }
        if (split.length == 1) {
            return new TranslateSumm(split[0], "");
        } else {
            String val = "," + split[1];
            if("".equals(split[1]) || "0".equals(split[1]) || "00".equals(split[1])){
                isShowZPT = false;
            }

            return new TranslateSumm(split[0], isShowZPT ? val : "");
        }
    }

    public static String getDateUTCToDevaice(String date, String pattern) {
        DateTimeFormatter formatterTransaction = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = new DateTime(date);
        return formatterTransaction.print(dateTime);
    }

    public String getRub() {
        return rub;
    }

    public String getKop() {
        return kop;
    }
}
