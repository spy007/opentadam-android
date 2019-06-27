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
import com.opentadam.Injector;
import com.opentadam.bus.BusHashBuildConfig;

import java.io.IOException;
import java.util.HashMap;

public class HashPushLocalise {

    void initHashBuildConfig() {
        new DownloadPageTask().execute(App.app.hashBC.urlPushStringsDefault);
    }

    private static class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HashBC.downloadOneUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            App.app.hashBC.localizationPushHashMap = result == null ?
                    Injector.getSettingsStore().getLocalizationPushHashMap() :
                    parserData(result);

            App.bus.post(new BusHashBuildConfig());
            super.onPostExecute(result);
        }

        private HashMap<String, String> parserData(String result) {
            if (result == null) return null;
            String[] split = result.split("\\n");
            HashMap<String, String> localizationPushHashMap = null;
            for (String value : split) {
                value = value.trim();
                if (value.isEmpty() || value.startsWith("#")) continue;

                value = value.replace("push.action.name.", "");
                String[] strings = value.split("=");
                if (localizationPushHashMap == null) localizationPushHashMap = new HashMap<>();
                localizationPushHashMap.put(strings[0].trim(), strings[1].trim());
            }
            Injector.getSettingsStore().setLocalizationPushHashMap(localizationPushHashMap);
            return localizationPushHashMap;
        }
    }
}
