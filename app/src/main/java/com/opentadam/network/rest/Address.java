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
import com.opentadam.Injector;
import com.opentadam.R;


import java.util.List;

public class Address {
    @SerializedName("name")
    public String name;
    @SerializedName("components")
    public List<AddressComponent> components;
    @SerializedName("types")
    public AddressTypes types;
    @SerializedName("position")
    public GpsPosition position;
    @SerializedName("sityLevel")
    private Integer sityLevel;
    @SerializedName("valueStreet")
    public String valueStreet;
    @SerializedName("idParent")
    public Long idParent;

    public Address(String name) {
        this.name = name;
    }

    public Address(String addressName, String lat, String lon) {
        this.name = addressName;
        this.position = new GpsPosition(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    public Address(String addressName, double lat, double lon) {
        this.name = addressName;
        this.position = new GpsPosition(lat, lon);
    }

    public Address() {

    }


    public String getTempAdress() {
        String alias = getNameAliasSearsh();
        if (alias != null)
            return alias;

        return getNameAdressSearsh();
    }


    public String getNameAdressSearsh() {
        String nameAdressSearsh;
        String nameStreetSearsh = null;
        String nameHouseSearsh = null;
        if (components == null || components.size() == 0)
            return name;
        //Injector.getAppContext().getString(R.string.point_to_maps);

        for (AddressComponent addressComponent : components) {
            if (addressComponent.level == 6)
                nameStreetSearsh = addressComponent.name;
            if (addressComponent.level == 7)
                nameStreetSearsh = addressComponent.name;
            if (addressComponent.level == 8)
                nameHouseSearsh = addressComponent.name;
        }

        nameAdressSearsh = (nameStreetSearsh == null ? "" : nameStreetSearsh)
                + (nameStreetSearsh != null && nameHouseSearsh != null ? ", " : "")
                + (nameHouseSearsh == null ? "" : nameHouseSearsh);
        if ("".equals(nameAdressSearsh)) {
            return getNameSitySearsh();
        }
        return nameAdressSearsh;
    }

    public String getNameSitySearsh() {
        if (components == null)
            return null;

        String temp = null;
        String parentNameSity = null;
        for (AddressComponent addressComponent : components) {
            if (addressComponent.level == 4) {
                temp = addressComponent.name;
            }
            if (addressComponent.level == 7) {
                return parentNameSity;
            } else {
                sityLevel = addressComponent.level;
                parentNameSity = addressComponent.name;
            }

        }
        if (temp != null)
            return temp;

        return parentNameSity;
    }


    public String getMapsValueStringAdress() {

        String alias = getNameAliasSearsh();
        if (alias != null)
            return alias;

        return getNameAdressSearsh();

    }

    public String getNameHouseSearsh() {
        if (components == null)
            return null;
        boolean b = false;
        for (AddressComponent addressComponent : components) {
            if (addressComponent.level == 8)
                b = true;
        }

        if (!b && getNameAliasSearsh() == null)
            return getNameAdressSearsh();

        return null;
    }

    public String getNameAliasSearsh() {
        if (components == null)
            return null;
        if (components.size() == 0)
            return name;

        for (AddressComponent addressComponent : components) {
            if (addressComponent != null && addressComponent.level == 9)
                return addressComponent.name;
        }

        if (getNameAdressSearsh() == null && getNameSitySearsh() == null)
            return name;

        return null;
    }

    public String getParentNameSitySearsh() {
        for (AddressComponent addressComponent : components) {
            if (addressComponent.level == sityLevel - 1)
                return addressComponent.name;
            if (addressComponent.level == sityLevel - 2)
                return addressComponent.name;
            if (addressComponent.level == sityLevel - 3)
                return addressComponent.name;

        }
        return null;

    }

    public String getStringNameAdress() {
        String alias = getNameAliasSearsh();
        if (alias != null)
            return alias;

        String nameAdressSearsh = getNameAdressSearsh();

        return nameAdressSearsh == null ? Injector.getClientData().getResources().getString(R.string.point_to_maps) : nameAdressSearsh;
    }
}
