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

package com.opentadam.yandex_google_maps;

import java.util.ArrayList;

public class ModelLocationProviders {
    private int countProvider;
    private boolean isEnabledGPS;
    private String[] listProvider;
    private boolean IsEnabledNetwork;
    private boolean isUnknownMethod;
    private boolean isOnlyNetwork;
    private boolean isOnlyGPS;
    private boolean isGeolocationDisabled;

    public boolean isDisabledStorage() {
        return isDisabledStorage;
    }

    private boolean isDisabledStorage;

    public boolean isDisablegGlobalLocation() {
        return isDisablegGlobalLocation;
    }

    private boolean isDisablegGlobalLocation;

    public ArrayList<String> getNameSUnknownMethods() {
        return nameSUnknownMethods;
    }

    private ArrayList<String> nameSUnknownMethods;

    public static ModelLocationProviders invoke() {
        return new ModelLocationProviders();
    }

    public boolean isGeolocationDisabled() {
        return isGeolocationDisabled;
    }

    public boolean isOnlyNetwork() {
        return isOnlyNetwork;
    }

    public boolean isOnlyGPS() {
        return isOnlyGPS;
    }

    public void setIsEnabledGPS(boolean val) {
        isEnabledGPS = val;
    }

    public void setIsEnabledNetwork(boolean val) {
        IsEnabledNetwork = val;
    }

    public int getCountProvider() {
        return countProvider;
    }

    public ModelLocationProviders setCountProvider(int val) {
        countProvider = val;
        return this;
    }

    public boolean isEnabledGPS() {
        return isEnabledGPS;
    }

    public String[] getListProvider() {
        return listProvider;
    }

    public ModelLocationProviders setListProvider(String[] val) {
        listProvider = val;
        return this;
    }

    public boolean isEnabledNetwork() {
        return IsEnabledNetwork;
    }

    public boolean isUnknownMethod() {
        return isUnknownMethod;
    }

    public void setUnknownMethod(boolean val) {
        isUnknownMethod = val;
    }

    public void setIsOnlyGPS(boolean b) {
        isOnlyGPS = b;
    }

    public void setIsOnlyNetwork(boolean b) {
        isOnlyNetwork = b;
    }

    public void setIsGeolocationDisabled(boolean b) {
        isGeolocationDisabled = b;
    }

    public void setListNameUnknownMethod(ArrayList<String> list) {
        nameSUnknownMethods = list;
    }

    public void setDisablegGlobalLocation(boolean b) {
        isDisablegGlobalLocation = b;
    }

    public void setIsDisabledStorage(boolean b) {
        isDisabledStorage = b;
    }
}
