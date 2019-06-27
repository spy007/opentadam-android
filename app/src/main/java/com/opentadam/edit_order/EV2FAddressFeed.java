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
package com.opentadam.edit_order;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.ParamsEditSubmissionDetails;
import com.opentadam.ui.order.V2FAddressFeed;

import butterknife.OnClick;

public class EV2FAddressFeed extends V2FAddressFeed {


    private long idOrder;
    private double[] latLon;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public static Fragment newInstance(double[] latLonOneList, long idRoute) {

        return new EV2FAddressFeed()
                .withViewId(R.layout.f_set_cash_order)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute);
    }

    @Override
    @OnClick(R.id.private_redactor_done_button)
    public void onRedactorDoneButton() {
        String flat = editFlat.getText().toString();
        String poscht = editPorcht.getText().toString();
        String comment = redactorDopInfo.getText().toString();
        ParamsEditSubmissionDetails paramsEditSubmissionDetails =
                new ParamsEditSubmissionDetails(
                        "".equals(poscht) ? null : poscht,
                        "".equals(flat) ? null : flat,
                        "".equals(comment) ? null : comment);
        RESTConnect rc = Injector.getRC();
        rc.findEditSubmissionDetails(null
                , idOrder
                , paramsEditSubmissionDetails
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        onBask();
                    }
                });

    }

    private void onBask() {
        if(Injector.getClientData().isLatLngDisabled(latLon)){
            getAWork().restartAll();
        }

        getAWork().showFSearchCar(new GpsPosition(latLon[0], latLon[1]), idOrder);
    }

    @Override
    public void unitUIFA() {
        if (getArguments() != null) {
            idOrder = getArguments().getLong("id");
            latLon = getArguments().getDoubleArray("latLon");
        }
        icSearshAdress.setVisibility(View.GONE);
        buttonAddPrivateAdress.setVisibility(View.GONE);
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        OrderInfo getOrderInfo = getAWork().getOrderInfo();
        if (getOrderInfo == null)
            return;


        setDefEdit(editFlat);
        setDefEdit(editPorcht);
        setDefEdit(redactorDopInfo);


        preLoadAnimation();
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (parentView == null)
                    return;
                Rect r = new Rect();
                //создаём прямоугольник r с координатами видимого пространства

                parentView.getWindowVisibleDisplayFrame(r);
                //Вычисляем разницу между высотой нашего View и высотой видимого пространства
                final int height = parentView.getRootView().getHeight();

                int heightDiff = height - (r.bottom - r.top);


                if (bazeDiff == -1)
                    bazeDiff = heightDiff + 10;

                //    isShowKeyBoord = heightDiff > bazeDiff * 4;
                boolean b = heightDiff > bazeDiff * 4;
                if (!b) {
                    // закрыта клавиатура
                    if (isShowKeyBoord) {
                        isShowKeyBoord = false;
                        hideCursor();
                        initTextSizeEdit();
                    }
                } else {
                    // открыта
                    isShowKeyBoord = true;
                    initTextSizeEdit();
                }

            }

        };

        parentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        if (getOrderInfo.route == null || getOrderInfo.route.size() == 0)
            return;

        ClientAddress clientAddress = getOrderInfo.route.get(0);
        initUi(clientAddress);
    }

    @Override
    public void onDestroyView() {
        removeGOLListener(parentView.getViewTreeObserver(), onGlobalLayoutListener);
        super.onDestroyView();
    }

    @Override
    @OnClick({R.id.adress_value, R.id.ic_clear_edit})
    public void onAdressValue() {

    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }

    @Override
    @OnClick(R.id.ic_bask)
    public void onIcBask() {
        onBask();
    }
}
