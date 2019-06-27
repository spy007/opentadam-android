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

import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.ParamsComments;
import com.opentadam.ui.order.V2FCommentOrder;
import com.opentadam.utils.TintIcons;

import butterknife.OnClick;

public class EV2FCommentOrder extends V2FCommentOrder {


    private long idOrder;
    private double[] latLon;

    public static Fragment newInstance(double[] latLonOneList, long idRoute) {

        return new EV2FCommentOrder()
                .withViewId(R.layout.f_v2_comment_order)
                .withArgument("latLon", latLonOneList)
                .withArgument("id", idRoute);
    }

    @Override
    @OnClick(R.id.send_comment)
    public void onSendComment() {

        String s = editComment.getText().toString();
        String comment = "".equals(s) ? null : s;

        RESTConnect rc = Injector.getRC();
        if (rc == null)
            return;

        rc.editComments(null
                , idOrder
                , new ParamsComments(comment)
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        onBask();
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }

    @Override
    @OnClick(R.id.bask)
    public void onBask() {

        if (!isVisible())
            return;

        getAWork().showFSearchCar(new GpsPosition(latLon[0], latLon[1]), idOrder);
    }

    @Override
    public void unitUI() {
        if (getArguments() != null) {
            idOrder = getArguments().getLong("id");
            latLon = getArguments().getDoubleArray("latLon");
        }


        OrderInfo getOrderInfo = getAWork().getOrderInfo();
        if (getOrderInfo == null)
            return;


        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TintIcons.tintImageViewOther(clearComment, R.color.colorPrimary);

        if (getOrderInfo.comment != null && !"".equals(getOrderInfo.comment)) {
            editComment.setText(getOrderInfo.comment);
            setActiveEdit(editComment);

        } else {
            setDefEdit(editComment);
        }

        hideKeyboard(editComment);
    }
}
