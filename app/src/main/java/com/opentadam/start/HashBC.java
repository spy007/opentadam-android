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

package com.opentadam.start;

import android.os.AsyncTask;

import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Injector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class HashBC {
    public boolean DEBUG;
    public String APPLICATION_ID;
    public String BUILD_TYPE;
    public String FLAVOR;
    public int VERSION_CODE;
    public String VERSION_NAME;
    public String HiveProfile;
    public String[] REST_SERVER;
    public String URL_POLICY_PRIVACY;
    public String URL_USER_AGREEMENT;
    public Double[] defaultLatLon;
    public String downloadApkUrl;
    public boolean isChangeColorIPTariffs;
    public int limitNumberActiveOrders;
    public String[] availableLanguages;
    public String buildCFUrl;
    public int countHourPeriodUpdateVersionApp;
    public String dynamicLinkDomain;
    public String dynamicQueryParameter;
    public String infoRegReferalText;
    public String iosAppleId;
    public String iosBundleId;
    public String menuBottomBannerLink;
    public boolean menuBottomBannerState;
    public String supportButtonURL;
    public String test;
    public String textInfoAddPrice;
    public Float zoomMaxMapsCarList;
    public Float zoomMinMapsCarList;
    public int minimalCodeGoogleVersion;
    public int currentCodeGoogleVersion;
    public String keyAppMetrica;
    public String urlPushStringsDefault;
    public HashMap<String, String> localizationPushHashMap;

    public HashBC() {
        DEBUG = BuildConfig.DEBUG;
        APPLICATION_ID = BuildConfig.APPLICATION_ID;
        BUILD_TYPE = BuildConfig.BUILD_TYPE;
        FLAVOR = BuildConfig.FLAVOR;
        VERSION_CODE = BuildConfig.VERSION_CODE;
        VERSION_NAME = BuildConfig.VERSION_NAME;
        HiveProfile = BuildConfig.HiveProfile;
        REST_SERVER = BuildConfig.REST_SERVER;
        URL_POLICY_PRIVACY = BuildConfig.URL_POLICY_PRIVACY;
        URL_USER_AGREEMENT = BuildConfig.URL_USER_AGREEMENT;
        defaultLatLon = BuildConfig.defaultLatLon;
        downloadApkUrl = BuildConfig.downloadApkUrl;
        isChangeColorIPTariffs = BuildConfig.isChangeColorIPTariffs;
        limitNumberActiveOrders = BuildConfig.limitNumberActiveOrders;
        availableLanguages = BuildConfig.availableLanguages;
        buildCFUrl = BuildConfig.buildCFUrl;
        countHourPeriodUpdateVersionApp = BuildConfig.countHourPeriodUpdateVersionApp;
        dynamicLinkDomain = BuildConfig.dynamicLinkDomain;
        dynamicQueryParameter = BuildConfig.dynamicQueryParameter;
        infoRegReferalText = BuildConfig.infoRegReferalText;
        iosAppleId = BuildConfig.iosAppleId;
        iosBundleId = BuildConfig.iosBundleId;
        menuBottomBannerLink = BuildConfig.menuBottomBannerLink;
        menuBottomBannerState = BuildConfig.menuBottomBannerState;
        supportButtonURL = BuildConfig.supportButtonURL;
        test = BuildConfig.test;
        textInfoAddPrice = BuildConfig.textInfoAddPrice;
        zoomMaxMapsCarList = BuildConfig.zoomMaxMapsCarList;
        zoomMinMapsCarList = BuildConfig.zoomMinMapsCarList;
        minimalCodeGoogleVersion = BuildConfig.minimalCodeGoogleVersion;
        currentCodeGoogleVersion = BuildConfig.currentCodeGoogleVersion;
        keyAppMetrica = BuildConfig.keyAppMetrica;
        urlPushStringsDefault = BuildConfig.urlPushStringsDefault;
    }

    static void initHashBuildConfig() {
        new DownloadPageTask().execute(BuildConfig.buildCFUrl);
    }

    private static HashBC parserData(String response) {
        HashBC hashBC = new HashBC();
        String[] split = response.split("\\n");

        for (String result : split) {
            if (result.startsWith("buildConfigField")) {
                String res = parserResponseBuildConfig(result);
                String[] resultParserArgVal = res.split(":::");
                String arg = resultParserArgVal[0];
                String val = resultParserArgVal[1];

                switch (arg) {
                    case "REST_SERVER":
                        hashBC.REST_SERVER = parserArrayString(val);
                        break;
                    case "downloadApkUrl":
                        hashBC.downloadApkUrl = val;
                        break;
                    case "URL_POLICY_PRIVACY":
                        hashBC.URL_POLICY_PRIVACY = val;
                        break;
                    case "URL_USER_AGREEMENT":
                        hashBC.URL_USER_AGREEMENT = val;
                        break;
                    case "availableLanguages":
                        hashBC.availableLanguages = parserArrayString(val);
                        break;
                    case "HiveProfile":
                        hashBC.HiveProfile = val;
                        break;
                    case "textInfoAddPrice":
                        hashBC.textInfoAddPrice = val;
                        break;
                    case "dynamicLinkDomain":
                        hashBC.dynamicLinkDomain = val;
                        break;
                    case "dynamicQueryParameter":
                        hashBC.dynamicQueryParameter = val;
                        break;
                    case "iosBundleId":
                        hashBC.iosBundleId = val;
                        break;
                    case "iosAppleId":
                        hashBC.iosAppleId = val;
                        break;
                    case "defaultLatLon":
                        hashBC.defaultLatLon = parserArrayDouble(val);
                        break;
                    case "tinkoffPublicKey":
                        hashBC.isChangeColorIPTariffs = parserBoolean(val);
                        break;
                    case "zoomMinMapsCarList":
                        hashBC.zoomMinMapsCarList = parserFloat(val);
                        break;
                    case "zoomMaxMapsCarList":
                        hashBC.zoomMaxMapsCarList = parserFloat(val);
                        break;
                    case "menuBottomBannerState":
                        hashBC.menuBottomBannerState = parserBoolean(val);
                        break;
                    case "menuBottomBannerLink":
                        hashBC.menuBottomBannerLink = val;
                        break;
                    case "infoRegReferalText":
                        hashBC.infoRegReferalText = val;
                        break;
                    case "countHourPeriodUpdateVersionApp":
                        hashBC.countHourPeriodUpdateVersionApp = parserInt(val);
                        break;
                    case "limitNumberActiveOrders":
                        hashBC.limitNumberActiveOrders = parserInt(val);
                        break;
                    case "supportButtonURL":
                        hashBC.supportButtonURL = val;
                        break;
                    case "test":
                        hashBC.test = val;
                        break;
                    case "minimalCodeGoogleVersion":
                        hashBC.minimalCodeGoogleVersion = parserInt(val);
                        break;
                    case "currentCodeGoogleVersion":
                        hashBC.currentCodeGoogleVersion = parserInt(val);
                        break;
                    case "keyAppMetrica":
                        hashBC.keyAppMetrica = val;
                        break;
                    case "urlPushStringsDefault":
                        hashBC.urlPushStringsDefault = val;
                        break;
                }
            }
        }

        Injector.getSettingsStore().setHashBuildConfig(hashBC);
        return hashBC;
    }

    private static boolean parserBoolean(String val) {
        return "true".equals(val);
    }

    private static Float parserFloat(String val) {
        return Float.parseFloat(val);
    }

    private static int parserInt(String val) {
        return Integer.parseInt(val);
    }

    private static String[] parserArrayString(String val) {
        return val.split("####");
    }

    private static Double[] parserArrayDouble(String val) {
        String[] split = val.split("####");
        Double[] splitDouble = new Double[2];
        splitDouble[0] = Double.parseDouble(split[0]);
        splitDouble[1] = Double.parseDouble(split[1]);
        return splitDouble;
    }

    private static long parserLong(String val) {
        return Long.parseLong(val.replace("L", ""));
    }

    private static String parserResponseBuildConfig(String s) {
        return s.replace("buildConfigField", "")
                .replace("\"String\",", "")
                .replace("\"String[]\",", "")
                .replace("\"boolean\",", "")
                .replace("\"Long\",", "")

                .replace("\", \"", ":::")
                .replace("\\\", \\\"", "####")
                .replace("\\\"", "")
                .replace("new String[]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .trim();
    }

    static String downloadOneUrl(String address) throws IOException {
        InputStream inputStream = null;
        String data = null;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int read;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();
                data = new String(result);
            }

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
        }
        return data;
    }

    private static class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadOneUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            App.app.hashBC = result == null ?
                    Injector.getSettingsStore().getHashBuildConfig() :
                    parserData(result);
            new HashPushLocalise().initHashBuildConfig();
            super.onPostExecute(result);
        }
    }
}

