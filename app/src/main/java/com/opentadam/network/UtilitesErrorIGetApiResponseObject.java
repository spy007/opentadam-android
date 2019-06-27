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

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Injector;
import com.opentadam.network.rest.EditPaymentMethod;
import com.opentadam.network.rest.OptionsList;
import com.opentadam.network.rest.Params;
import com.opentadam.network.rest.ParamsComments;
import com.opentadam.network.rest.ParamsEditSubmissionDetails;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.SubmitRequest;

public class UtilitesErrorIGetApiResponseObject {
    private final IGetApiResponse iGetApiResponse;
    private final String pathFindReferralData;
    private Long oft;
    private String finalDate;
    private String finalAuthentication;
    private Params params;
    private PaymentMethod paymentMethod;
    private SendCreateRequest sendCreateRequest;
    private String method;
    private SubmitRequest submitRequest;
    private int posProlongation;
    private long id;
    private ParamsEditSubmissionDetails paramsEditSubmissionDetails;
    private String mCodeUser;
    private float amount;
    private EditPaymentMethod editPaymentMethod;
    private OptionsList optionsList;
    private ParamsComments paramsComments;

    public long getTimeRest() {
        return timeRest;
    }

    private long timeRest;

    public String getPath() {
        return path;
    }

    private String path;

    private UtilitesErrorIGetApiResponseObject(IGetApiResponse iGetApiResponse, String pathFindReferralData) {
        this.iGetApiResponse = iGetApiResponse;
        this.pathFindReferralData = pathFindReferralData;
    }

    public static UtilitesErrorIGetApiResponseObject newInstance(IGetApiResponse iGetApiResponse, String pathFindReferralData) {


        return new UtilitesErrorIGetApiResponseObject(iGetApiResponse, pathFindReferralData);
    }

    public IGetApiResponse getiGetApiResponse() {
        return iGetApiResponse;
    }

    public String getPathFindReferralData() {
        return pathFindReferralData;
    }

    public LatLng getLatLng() {
        return Injector.getClientData().getMarkerLocation();

                //latLng;
    }

    public Long getOft() {
        return oft;
    }

    public UtilitesErrorIGetApiResponseObject setOft(Long val) {
        oft = val;
        return this;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public String getFinalAuthentication() {
        return finalAuthentication;
    }

    public Params getParams() {
        return params;
    }

    public UtilitesErrorIGetApiResponseObject setParams(Params val) {
        params = val;
        return this;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public UtilitesErrorIGetApiResponseObject setPaymentMethod(PaymentMethod val) {
        paymentMethod = val;
        return this;
    }

    public SendCreateRequest getSendCreateRequest() {
        return sendCreateRequest;
    }

    public UtilitesErrorIGetApiResponseObject setSendCreateRequest(SendCreateRequest val) {
        sendCreateRequest = val;

        return this;
    }

    public String getMethod() {
        return method;
    }

    public UtilitesErrorIGetApiResponseObject setMethod(String val) {
        method = val;
        return this;
    }

    public SubmitRequest getSubmitRequest() {
        return submitRequest;
    }

    public UtilitesErrorIGetApiResponseObject setSubmitRequest(SubmitRequest val) {
        submitRequest = val;
        return this;
    }

    public int getPosProlongation() {
        return posProlongation;
    }

    public UtilitesErrorIGetApiResponseObject setPosProlongation(int val) {
        posProlongation = val;
        return this;
    }

    public long getId() {
        return id;
    }

    public UtilitesErrorIGetApiResponseObject setId(long val) {
        id = val;
        return this;
    }

    public ParamsEditSubmissionDetails getParamsEditSubmissionDetails() {
        return paramsEditSubmissionDetails;
    }

    public UtilitesErrorIGetApiResponseObject setParamsEditSubmissionDetails(ParamsEditSubmissionDetails val) {
        paramsEditSubmissionDetails = val;
        return this;
    }

    public String getmCodeUser() {
        return mCodeUser;
    }

    public float getAmount() {
        return amount;
    }

    public UtilitesErrorIGetApiResponseObject setAmount(float val) {
        amount = val;
        return this;
    }

    public EditPaymentMethod getEditPaymentMethod() {
        return editPaymentMethod;
    }

    public UtilitesErrorIGetApiResponseObject setEditPaymentMethod(EditPaymentMethod val) {
        editPaymentMethod = val;
        return this;
    }

    public OptionsList getOptionsList() {
        return optionsList;
    }

    public UtilitesErrorIGetApiResponseObject setOptionsList(OptionsList val) {
        optionsList = val;
        return this;
    }

    public ParamsComments getParamsComments() {
        return paramsComments;
    }

    public UtilitesErrorIGetApiResponseObject setParamsComments(ParamsComments val) {
        paramsComments = val;
        return this;
    }

    public UtilitesErrorIGetApiResponseObject setLatLong(LatLng val) {

        return this;
    }

    public UtilitesErrorIGetApiResponseObject setDate(String val) {
        finalDate = val;
        return this;
    }

    public UtilitesErrorIGetApiResponseObject setAuthentication(String val) {
        finalAuthentication = val;
        return this;
    }

    public UtilitesErrorIGetApiResponseObject setCodeUser(String val) {
        mCodeUser = val;
        return this;
    }

    public UtilitesErrorIGetApiResponseObject setPath(String val) {
        path = val;
        return this;
    }

    public UtilitesErrorIGetApiResponseObject settimeRest(long val) {
        timeRest = val;
        return this;
    }
}
