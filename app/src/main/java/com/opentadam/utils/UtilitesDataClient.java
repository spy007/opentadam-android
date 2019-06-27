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

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.Countries;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class UtilitesDataClient {

    public static int getScale(float amount) {
        return amount - (int) amount == 0 ? 0 : 2;
    }

    public static boolean isEnabledDataPreorder(String str) {
        DateTime dt = new DateTime(str);
        DateTime now = new DateTime();

        Interval interval = new Interval(now.minusWeeks(2), now.plusHours(12));
        return !interval.contains(dt.getMillis());
    }

    public static String getStrigIso(String txt, boolean full) {
        if (txt == null)
            return "";

        Locale locale = new Locale(Countries.getLocale(App.app.hashBC.availableLanguages));
        boolean isISO = txt.endsWith("Z");
        int hyr = 0;
        int min = 0;
        if (!isISO) {
            int length = txt.length();
            String substring = txt.substring(length - 5, length - 3);
            hyr = Integer.parseInt(substring);

            String substring1 = txt.substring(length - 2, length);

            min = Integer.parseInt(substring1);
        }

        DateTime dt = new DateTime(txt);
        DateTimeFormatter formatter = DateTimeFormat
                .forPattern(full ? "HH:mm  dd MMMM yyyy" : "HH:mm")
                .withZone(DateTimeZone.forOffsetHoursMinutes(hyr, min))
                .withLocale(locale);

        return formatter.print(dt);


    }

    public static String getStrigIsoDMG(String txt) {
        if (txt == null)
            return "";

        Locale locale = new Locale(Countries.getLocale(App.app.hashBC.availableLanguages));
        DateTime dt = new DateTime(txt);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy")
                .withLocale(locale);
        return formatter.print(dt);

    }


    private static long getLongIso(String txt) {
        long longIso = -1L;
        if (txt == null)
            return longIso;
        return new DateTime(txt).getMillis();
    }

    public static boolean isShowTextPreorder(String txt) {
        long delta = getDeltaLongTimePreorder(txt);
        long period30min = 30 * 60 * 1000;

        return delta > period30min;
    }

    public static String getDeltaTimePreorder(String txt) {
        long delta = getDeltaLongTimePreorder(txt);
        if (delta > 0) {
            DateTime d1 = new DateTime();
            DateTime d2 = new DateTime(getLongIso(txt));


            Duration duration = new Duration(d1, d2);

            int hours = (int) duration.getStandardHours();
            int minutes = (int) duration.getStandardMinutes();

            int dMin = minutes - hours * 60;

            return (hours > 9 ? hours : "0" + hours) + Injector.getClientData().getResources().getString(R.string.hour_reduced)
                    + (dMin > 9 ? dMin : "0" + dMin) + Injector.getClientData().getResources().getString(R.string.minutes_reduced);
        }

        return null;
    }

    private static long getDeltaLongTimePreorder(String txt) {
        return getLongIso(txt) - System.currentTimeMillis();
    }

    public static String formatRegNum(String regNum) {
        //   regNum = "b079ХО55";
        char[] s = regNum.toCharArray();

        StringBuilder res = new StringBuilder("" + s[0]);
        int i0 = Character.isDigit(s[0]) ? 1 : -1;
        for (int i = 1; i < s.length; i++) {
            int i1 = Character.isDigit(s[i]) ? 1 : -1;

            if (i0 * i1 < 0) {
                res.append(" ");
            }

            res.append(s[i]);
            i0 = i1;
        }

        return res.toString();
    }


}
