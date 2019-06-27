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

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class GpsPosition {
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lon")
    public Double lon;

    public GpsPosition(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public GpsPosition() {

    }

    public GpsPosition(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

    public LatLng getLatLng() {


        return new LatLng(this.lat, this.lon);
    }
}
