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
package com.opentadam;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.opentadam.data.ObjectCountryList;
import com.opentadam.data.OblectListPrivatePoint;
import com.opentadam.network.model.DisabledArrayListShortOrderInfo;
import com.opentadam.network.model.LocalizationPushHashMap;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.Country;
import com.opentadam.network.rest.Settings;
import com.opentadam.start.HashBC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsStore {
    public static final int MAPS_GOOGLE = 0;
    public static final int MAPS_OSM = 1;
    public static final String KEY_DEFAULT_MAPS = "def_maps_disabled";
    public static final int MAPS_YANDEX = 2;
    private final SharedPreferences sharedPreferences;

    public SettingsStore(Context context) {

        this.sharedPreferences = context.
                getSharedPreferences("com.hivetaxi.client", Context.MODE_PRIVATE);
    }

    public boolean clearSettings() {
        return sharedPreferences
                .edit()
                .clear()
                .commit();
    }

    public HashBC getHashBuildConfig() {
        String value = sharedPreferences.getString("HashBC", null);
        return value == null ? new HashBC() : new Gson().fromJson(value, HashBC.class);
    }

    public void setHashBuildConfig(HashBC hashBC) {
        sharedPreferences.edit().putString("HashBC", new Gson().toJson(hashBC)).apply();
    }

    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void writeString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Boolean readBoolean(String key, Boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void writeBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int readInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void writeInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public float readFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }


    private void writeFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }


    public long readLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void writeLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public int getDefMars() {

        String[] mapTitleSourceArray = Injector
                .getWorkSettings()
                .getMapTitleSourceArray();


        if (mapTitleSourceArray == null || mapTitleSourceArray.length == 0) {
            return MAPS_OSM;
        }


        switch (mapTitleSourceArray[0]) {
            case "google":
                return MAPS_GOOGLE;


            case "yandex":
                return MAPS_YANDEX;

            default:
                return MAPS_OSM;
        }

    }


    public LatLng getLatLngTarifDef() {
        String ll = sharedPreferences.getString("latLngTarifDef", null);
        if (ll == null)
            return null;
        return new Gson().fromJson(ll, LatLng.class);
    }

    public void setLatLngTarifDef(LatLng latLngTarifDef) {
        String ll = new Gson().toJson(latLngTarifDef);
        writeString("latLngTarifDef", ll);
    }

    public void addOrderAddressToListPrivatePoint(ClientAddress clientAddress) {

        OblectListPrivatePoint oblectListPrivatePoint = getOrderAddressToListPrivatePoint();
        oblectListPrivatePoint.clientAddressesPrivatePoint.add(clientAddress);
        setMemoryOrderAddressListPrivatePoint(oblectListPrivatePoint);

    }

    public List<ClientAddress> getListPrivatePoint() {
        return getOrderAddressToListPrivatePoint().clientAddressesPrivatePoint;

    }

    private void setMemoryOrderAddressListPrivatePoint(OblectListPrivatePoint oblectListPrivatePoint) {

        writeString("listPrivatePoint", new Gson().toJson(oblectListPrivatePoint));
    }

    private OblectListPrivatePoint getOrderAddressToListPrivatePoint() {
        String ll = sharedPreferences.getString("listPrivatePoint", null);

        return ll == null ? new OblectListPrivatePoint() :
                new Gson().fromJson(ll, OblectListPrivatePoint.class);

    }

    public int getCountPrivatePoint() {
        return getOrderAddressToListPrivatePoint().clientAddressesPrivatePoint.size();
    }

    public void removePrivateOrderAddress(ClientAddress clientAddress) {
        OblectListPrivatePoint orderAddressToListPrivatePoint = getOrderAddressToListPrivatePoint();

        List<ClientAddress> clientAddressesPrivatePoint =
                orderAddressToListPrivatePoint.clientAddressesPrivatePoint;

        for (ClientAddress clientAddress1 : clientAddressesPrivatePoint) {
            if (clientAddress1.id.equals(clientAddress.id)) {
                clientAddressesPrivatePoint.remove(clientAddress1);
                setMemoryOrderAddressListPrivatePoint(orderAddressToListPrivatePoint);
                return;
            }
        }

    }

    public void removePrivateOrderAddress(long id) {
        OblectListPrivatePoint orderAddressToListPrivatePoint = getOrderAddressToListPrivatePoint();

        List<ClientAddress> clientAddressesPrivatePoint =
                orderAddressToListPrivatePoint.clientAddressesPrivatePoint;

        for (ClientAddress clientAddress1 : clientAddressesPrivatePoint) {
            if (clientAddress1.id.equals(id)) {
                clientAddressesPrivatePoint.remove(clientAddress1);
                setMemoryOrderAddressListPrivatePoint(orderAddressToListPrivatePoint);
                return;
            }
        }

    }

    public ClientAddress getPrivateOrderAddress(long id) {
        OblectListPrivatePoint orderAddressToListPrivatePoint = getOrderAddressToListPrivatePoint();
        List<ClientAddress> clientAddressesPrivatePoint =
                orderAddressToListPrivatePoint.clientAddressesPrivatePoint;
        for (ClientAddress clientAddress : clientAddressesPrivatePoint) {
            if (id == clientAddress.id)
                return clientAddress;
        }

        return null;
    }

    public void replOrderAddress(long id, ClientAddress clientAddressRepl) {
        OblectListPrivatePoint orderAddressToListPrivatePoint = getOrderAddressToListPrivatePoint();
        List<ClientAddress> clientAddressesPrivatePoint =
                orderAddressToListPrivatePoint.clientAddressesPrivatePoint;
        int i = 0;
        for (ClientAddress clientAddress : clientAddressesPrivatePoint) {
            if (id == clientAddress.id) {
                clientAddressesPrivatePoint.set(i, clientAddressRepl);
                setMemoryOrderAddressListPrivatePoint(orderAddressToListPrivatePoint);
                return;
            }
            i++;
        }

    }

    public boolean isOnRegClient() {

        return readString(Constants.REG_KEY_CLIENT, null) != null;
    }

    public ArrayList<Long> getDisabledArrayListShortOrderInfo() {
        String readString = readString("DisabledArrayListShortOrderInfo", null);
        if (readString == null)
            return new ArrayList<>();
        else {
            DisabledArrayListShortOrderInfo disabledArrayListShortOrderInfo
                    = new Gson().fromJson(readString, DisabledArrayListShortOrderInfo.class);
            return disabledArrayListShortOrderInfo.longIdDisabled;
        }
    }

    public void setDisabledArrayListShortOrderInfo(long id) {
        ArrayList<Long> longs = getDisabledArrayListShortOrderInfo();
        longs.add(id);
        DisabledArrayListShortOrderInfo info
                = new DisabledArrayListShortOrderInfo(longs);
        writeString("DisabledArrayListShortOrderInfo", new Gson().toJson(info));

    }

    public float getZoomMaps() {
        return sharedPreferences.getFloat("zoomMaps", Constants.DEF_ZOOM_MAP);
    }

    public void setZoomMaps(float zoom) {
        writeFloat("zoomMaps", zoom);
    }

    public int getDefHashVerCode() {
        return sharedPreferences.getInt("getDefHashVerCode", 0);
    }

    public void setDefHashVerCode(int id) {
        writeInt("getDefHashVerCode", id);
    }

    public void setDefHashVerCode() {
        writeInt("getDefHashVerCode", App.app.hashBC.VERSION_CODE);
    }


    public String getRefererrClient() {
        return readString(Constants.REFERRER_CLIENT, null);
    }

    public void setRefererrClient(String refererrClient) {
        writeString(Constants.REFERRER_CLIENT, refererrClient);
    }


    public List<Country> getCountryList() {
        String countryList = sharedPreferences.getString("CountryList", null);
        ObjectCountryList objectCountryList = new Gson().fromJson(countryList, ObjectCountryList.class);
        return objectCountryList.cList;
    }

    public void setCountryList(List<Country> cList) {
        writeString("CountryList", new Gson().toJson(new ObjectCountryList(cList)));
    }


    public long getTimeDialogUpdApp() {
        return sharedPreferences.getLong("getTimeDialogUpdApp", 0);
    }

    public void setTimeDialogUpdApp(long val) {
        writeLong("getTimeDialogUpdApp", val);
    }

    public Settings getHashSettings() {
      String hashSettings =  sharedPreferences.getString("hashSettings", null);
      if(hashSettings == null)
          return null;

     return new Gson().fromJson(hashSettings, Settings.class);

    }

    public HashMap<String, String> getLocalizationPushHashMap() {
        String hashSettings =  sharedPreferences.getString("getLocalizationPushHashMap", null);
        if(hashSettings == null)
            return null;

        return new Gson().fromJson(hashSettings, LocalizationPushHashMap.class).localizationPushHashMap;
    }

    public void setLocalizationPushHashMap(HashMap<String, String> localizationPushHashMap) {
        writeString("getLocalizationPushHashMap", new Gson().toJson(new LocalizationPushHashMap(localizationPushHashMap)));
    }
}
