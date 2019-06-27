/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentadam.data;

import android.support.v4.util.ArrayMap;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Countries {

    public static final String MASK_DEFAULT = "####################";
    public static final String ADD_MASK = " ##########";
    private static ArrayList<String> enabledLocale;
    private static ArrayMap<String, String> countryLocale;
    private static ArrayMap<String, Integer> countryFlags;

    static {
        countryLocale = new ArrayMap<>();

        countryLocale.put("ru", "Русский");
        countryLocale.put("pl", "Poland");
        countryLocale.put("kk", "Қазақша");
        countryLocale.put("en", "English");
        countryLocale.put("tr", "Türkçe");
        countryLocale.put("az", "Azərbaycanca");
        countryLocale.put("ka", "ქართული");
        countryLocale.put("ro", "Română");
        countryLocale.put("ca", "English");

        enabledLocale = new ArrayList<>();
        enabledLocale.add("ru");
        enabledLocale.add("az");
        enabledLocale.add("be");
        enabledLocale.add("en");
        enabledLocale.add("uk");
        enabledLocale.add("uz");
        enabledLocale.add("tr");
        enabledLocale.add("kk");
        enabledLocale.add("md");
        enabledLocale.add("pl");
        enabledLocale.add("am");
        enabledLocale.add("iq");
        enabledLocale.add("ka");
        enabledLocale.add("ro");
        enabledLocale.add("sys");


        // для стран и для локалей флаги
        countryFlags = new ArrayMap<>();
        countryFlags.put("ru", R.drawable.ru);
        countryFlags.put("ua", R.drawable.ua);
        countryFlags.put("by", R.drawable.by);
        countryFlags.put("be", R.drawable.by);
        countryFlags.put("kz", R.drawable.kz);
        countryFlags.put("kk", R.drawable.kz);
        countryFlags.put("us", R.drawable.us);
        countryFlags.put("en", R.drawable.us);
        countryFlags.put("tr", R.drawable.tr);
        countryFlags.put("az", R.drawable.az);
        countryFlags.put("uz", R.drawable.uz);
        countryFlags.put("md", R.drawable.md);
        countryFlags.put("am", R.drawable.am);
        countryFlags.put("iq", R.drawable.iq);
        countryFlags.put("ge", R.drawable.ge);
        countryFlags.put("ka", R.drawable.ge);
        countryFlags.put("ro", R.drawable.ro);


        countryFlags.put("bg", R.drawable.bg);
        countryFlags.put("ee", R.drawable.ee);
        countryFlags.put("de", R.drawable.de);
        countryFlags.put("kg", R.drawable.kg);
        countryFlags.put("lt", R.drawable.lt);
        countryFlags.put("lv", R.drawable.lv);
        countryFlags.put("my", R.drawable.my);
        countryFlags.put("pl", R.drawable.pl);
        countryFlags.put("tj", R.drawable.tj);
        countryFlags.put("ca", R.drawable.ca);
    }

    private static ArrayMap<String, String> sortValueArrayMap(ArrayMap<String, String> arrayMap) {
        ArrayMap<String, String> revers = new ArrayMap<>();
        ArrayMap<String, String> res = new ArrayMap<>();
        for (int i = 0; i < arrayMap.size(); i++) {
            String key = arrayMap.keyAt(i);
            String value = arrayMap.valueAt(i);
            revers.put(value, key);
        }

        Collection<String> values = arrayMap.values();
        Object[] array = values.toArray();
        Arrays.sort(array);
        for (Object s : array) {
            String string = (String) s;
            res.put(revers.get(string), string);
        }

        return res;
    }

    static ArrayMap<String, String> getCountryLocale() {

        return sortValueArrayMap(countryLocale);
    }

    public static String getCountryName(String acronim) {
        ArrayMap<String, String> countryNames = new ArrayMap<>();

        List<Country> countryList = Injector.getCountryList();
        for (Country country : countryList) {
            String isoCode = country.isoCode;
            String name = country.name;
            countryNames.put(isoCode, name);
        }

        String name = countryNames.get(acronim);
        return name == null ? "" : name;
    }

    public static String getLocaleName(String acronim) {
        if ("sys".equals(acronim)) {
            return App.app.getDefLocalePhone().defaultSystemLocal;
        }

        String defKey = countryLocale.containsKey(acronim) ? acronim : "en";
        return countryLocale.get(defKey);
    }

    public static String getValidateLocaleName(String acronim) {
        if ("sys".equals(acronim)) {
            return App.app.getDefLocalePhone().defaultSystemLocal;
        }
        return countryLocale.containsKey(acronim) ? acronim : "ru";
    }

    public static String getCountryPhonePrefix(String acronim) {
        ArrayMap<String, String> countryPhonePrefixes = new ArrayMap<>();
        //  countryPhonePrefixes.put("ru", "+7");
        List<Country> countryList = Injector.getCountryList();
        if (countryList == null) {
            return "+";
        }
        for (Country country : countryList) {
            String isoCode = country.isoCode;
            String phoneCode = "+" + country.phoneCode;
            countryPhonePrefixes.put(isoCode, phoneCode);
        }


        return countryPhonePrefixes.get(acronim);
    }

    public static Integer getCountryFlag(String acronim) {


        return countryFlags.get(acronim);
    }

    public static String getCountryPhoneMask(String acronim) {
        ArrayMap<String, String> countryPhoneMask = new ArrayMap<>();
        //  countryPhonePrefixes.put("ru", "+7");
        List<Country> countryList = Injector.getCountryList();
        if (countryList == null)
            return MASK_DEFAULT;

        for (Country country : countryList) {
            String isoCode = country.isoCode;
            String phoneMask =
                    country.phoneMask;
            if (phoneMask != null) {

                countryPhoneMask.put(isoCode, phoneMask.replace("x", "#"));
            }
        }


        String s = countryPhoneMask.get(acronim);
        return s == null ? MASK_DEFAULT : s
                + ADD_MASK; // ru null
    }

    public static String getLocale(String[] contr) {
        String readString = Injector.getSettingsStore()
                .readString("getLocale", null);

        String s = "ic".equals(contr[0]) ? "en" : contr[0];

        return readString == null ?
                s : readString;
    }


    public static String getRegCountries(String[] contr) {
        String readString = Injector.getSettingsStore()
                .readString("countries", null);
        String val = contr == null || contr.length == 0 ? "en" : contr[0];

        return readString == null ?
                val : readString;
    }

    public static boolean getEnabledLocale(String l) {
        return enabledLocale.contains(l);
    }

    public static String getCountry(String[] countriesList) {
        String readString = Injector.getSettingsStore()
                .readString("getCountry", null);

        String s = countriesList[0];
        return readString == null ?
                s : readString;
    }
}
