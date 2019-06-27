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

package com.opentadam.network;

import com.google.gson.annotations.SerializedName;
import com.opentadam.App;
import com.opentadam.bus.BusApiResponseSuccess;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Bonuses;
import com.opentadam.network.rest.CardAdditionRef;
import com.opentadam.network.rest.Confirmed;
import com.opentadam.network.rest.Country;
import com.opentadam.network.rest.DeleteRoute;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.EmptyObject;
import com.opentadam.network.rest.Estimation;
import com.opentadam.network.rest.LinesInfo;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.ResultSendOrders;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.network.rest.Submitted;
import com.opentadam.ui.ErrorCodeServers;
import com.opentadam.ui.frends.referal.Result;
import com.opentadam.ui.frends.referal.ResultDayStat;
import com.opentadam.ui.frends.referal.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

public class ApiResponse {
    @SerializedName("timeRest")
    public Long timeRest;
    @SerializedName("path")
    public String path;
    @SerializedName("error")
    public String error;
    @SerializedName("shortOrderInfos")
    public ArrayList<ShortOrderInfo> shortOrderInfos;
    @SerializedName("addresses")
    public List<Address> addresses;
    @SerializedName("linesInfo")
    public LinesInfo linesInfo;
    @SerializedName("orderInfo")
    public OrderInfo orderInfo;
    @SerializedName("bonuses")
    public Bonuses bonuses;
    @SerializedName("estimation")
    public Estimation estimation;
    @SerializedName("submitted")
    public Submitted submitted;
    @SerializedName("confirmed")
    public Confirmed confirmed;
    @SerializedName("paymentMethods")
    public List<PaymentMethod> paymentMethods;
    @SerializedName("service")
    public Service service;
    @SerializedName("token")
    public String token;
    @SerializedName("resultSendOrders")
    public ResultSendOrders resultSendOrders;
    @SerializedName("driverList")
    public List<Driver> driverList;
    @SerializedName("CardAdditionRef")
    public CardAdditionRef cardAdditionRef;
    @SerializedName("accountState")
    public AccountState accountState;

    @SerializedName("refferalState")
    public Result refferalState;

    @SerializedName("countryList")
    public List<Country> countryList;
    @SerializedName("loyaltyProgramByDay")
    public ArrayList<ResultDayStat> loyaltyProgramByDay;
    @SerializedName("transaction")
    public ArrayList<Transaction> transaction;
    @SerializedName("deleteRoute")
    public DeleteRoute deleteRoute;
    @SerializedName("emptyObject")
    public EmptyObject emptyObject;
    @SerializedName("timeSynchronizationServers")
    public String timeSynchronizationServers;
    @SerializedName("utilitesErrorIGetApiResponseObject")
    public UtilitesErrorIGetApiResponseObject utilitesErrorIGetApiResponseObject;
    @SerializedName("isError")
    public boolean isError;
    @SerializedName("retrofitError")
    public RetrofitError retrofitError;
    @SerializedName("retrofitError")
    public ErrorCodeServers errorCodeServers;

    public ApiResponse(String path) {
        this.path = path;
        App.bus.post(new BusApiResponseSuccess(path));
    }

    public ApiResponse(String path, boolean isError) {
        this.path = path;
        this.isError = isError;
    }
}
