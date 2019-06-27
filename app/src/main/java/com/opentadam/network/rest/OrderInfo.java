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

import java.util.ArrayList;

public class OrderInfo {
    @SerializedName("state")
    public int state = -1;
    @SerializedName("route")
    public ArrayList<ClientAddress> route;
    @SerializedName("assignee")
    public Assignee assignee;
    @SerializedName("options")
    public ArrayList<Option> options;
    @SerializedName("time")
    public String time;
    @SerializedName("comment")
    public String comment;
    @SerializedName("distance")
    public double distance = -1.0d;
    @SerializedName("cost")
    public Cost cost;
    @SerializedName("usedBonuses")
    public Float usedBonuses;
    @SerializedName("executionTime")
    public String executionTime;
    @SerializedName("paymentMethod")
    public PaymentMethod paymentMethod;
    @SerializedName("idRoute")
    public long idRoute;
    @SerializedName("costFixAllowed")
    public boolean costFixAllowed;

    public boolean isTaximetr = false;
    public Bonuses bonuses;
    @SerializedName("isComing")
    public boolean isComing;
    @SerializedName("needsProlongation")
    public boolean needsProlongation;

    public ShortOrderInfo mapperShortOrderInfo() {
        ShortOrderInfo shortOrderInfo = new ShortOrderInfo();
        shortOrderInfo.state = state;

        ArrayList<Address> routeAddress = new ArrayList<>();
        for (ClientAddress clientAddress : route) routeAddress.add(clientAddress.address);

        shortOrderInfo.route = routeAddress;
        shortOrderInfo.assignee = assignee;
        shortOrderInfo.id = idRoute;
        shortOrderInfo.time = time;
        shortOrderInfo.needsProlongation = needsProlongation;
        return shortOrderInfo;
    }

    public ArrayList<Address> getAddresses() {
        ArrayList<Address> addresses = new ArrayList<>();
        for (ClientAddress clientAddress : route)
            addresses.add(clientAddress.address);
        return addresses;
    }

    public boolean isContractor() {
        return paymentMethod != null && "contractor".equals(paymentMethod.kind);
    }

    public GpsPosition getGpsPositionStart() {
        return getAddresses().get(0).position;
    }

    public String getNameCost() {

        if (paymentMethod == null)
            return null;
        String name = paymentMethod.name;

        if ("credit_card".equals(paymentMethod.kind)) {

            int length = name.length();
            name = name.substring(length - 5, length);
            return Injector.getClientData().getResources().getString(R.string.value_num_card) + " " + name;
        }
        return name;
    }

    public ArrayList<CostItem> getOptions() {
        if (cost == null || cost.details == null || cost.details.size() == 0)
            return null;
        return cost.details;
    }


}
