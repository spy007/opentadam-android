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
import com.opentadam.utils.UtilitesDataClient;

import java.util.ArrayList;


public class TempObjectUIMRoute {
    @SerializedName("optionsClient")
    public ArrayList<Option> optionsClient;
    @SerializedName("textCostValue")
    public String textCostValue;
    @SerializedName("textDistanceValue")
    public String textDistanceValue;
    @SerializedName("textCostCalculation")
    public String textCostCalculation;
    @SerializedName("textCostType")
    public String textCostType;
    @SerializedName("textTariff")
    public String textTariff;
    @SerializedName("clientCorrection")
    public Float clientCorrection;
    @SerializedName("cost")
    public Cost cost;
    @SerializedName("clientCorrectionValue")
    public float clientCorrectionValue;
    @SerializedName("bunusClient")
    public String bunusClient;
    @SerializedName("routeComment")
    public String routeComment;
    @SerializedName("bonuses")
    public Bonuses bonuses;
    @SerializedName("isTaximetr")
    public boolean isTaximetr;
    public Float valueEditBonus;
    @SerializedName("timeOrder")
    private TimeOrder timeOrder;
    @SerializedName("valueAddCost")
    public float valueAddCost;
    @SerializedName("fixCost")
    public Float fixCost;


    public ArrayList<Option> getOptionsClient() {
        if (optionsClient == null)
            optionsClient = new ArrayList<>();
        return optionsClient;
    }

    public ArrayList<Long> getOptions() {

        optionsClient = getOptionsClient();

        ArrayList<Long> opt = new ArrayList<>();
        for (Option option : optionsClient) {
            opt.add(option.id);
        }
        return opt;
    }

    public TimeOrder getTimeOrder() {
        return timeOrder;
    }

    public void setTimeOrder(TimeOrder timeOrder) {
        this.timeOrder = timeOrder;
    }

    public String getTimeOrderIso() {
        if (timeOrder == null)
            return null;

        return timeOrder.time;

    }

    public static class TimeOrder {
        public int posTime;
        public String posDate;
        public String timeValue;
        public String dateValue;
        public String time;
        // 2017-09-01T14:00:00


        public TimeOrder(int posTime
                , String posDate
                , String timeValue
                , String time
                , String dateValue

        ) {
            this.posTime = posTime;
            this.posDate = posDate;
            this.timeValue = timeValue;
            this.dateValue = dateValue;
            this.time = time;
        }

        public String getTimeDMG() {

            return UtilitesDataClient.getStrigIsoDMG(time);

        }

    }
}
