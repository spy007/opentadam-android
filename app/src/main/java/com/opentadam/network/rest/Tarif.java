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

import java.math.BigDecimal;
import java.util.ArrayList;

public class Tarif {
    @SerializedName("id")
    public Long id;
    @SerializedName("lat")
    public double lat = 0d;
    @SerializedName("lon")
    public double lon = 0d;
    @SerializedName("accur")
    public float accur = 10000f;
    @SerializedName("pos")
    public int pos = 0;
    @SerializedName("name")
    public String name;
    @SerializedName("options")
    public ArrayList<Option> options;
    @SerializedName("icon")
    public String icon = "carType_1";
    @SerializedName("description")
    public String description;
    @SerializedName("minCost")
    public float minCost;
    @SerializedName("costFixAllowed")
    public boolean costFixAllowed;
    @SerializedName("costChangeAllowed")
    public boolean costChangeAllowed;
    @SerializedName("costChangeStep")
    public BigDecimal costChangeStep;
    @SerializedName("showEstimation")
    public boolean showEstimation;
    @SerializedName("hint")
    public String hint;
}
