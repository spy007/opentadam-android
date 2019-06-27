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

package com.opentadam.ui.execution_of_orders;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.TipeSound;

import butterknife.InjectView;
import butterknife.OnClick;


public class FStatusInfo extends BaseFr {

    private static final String ARG_STATUS = "status";
    private static final String ARG_COLOR = "color";
    private static final String ARG_BRAND_MODEL = "brandModel";
    private static final String ARG_INFO_TEXT = "regNumInfoText";
    private static final String ARG_INFO_REGION_TEXT = "regNumInfoRegionText";
    @InjectView(R.id.dialog_title)
    TextView dialogTitle;
    @InjectView(R.id.dialog_data)
    TextView dialogData;
    @InjectView(R.id.dialog_reg_num_info)
    TextView dialogRegNum;
    @InjectView(R.id.dialog_reg_num_info_region)
    TextView dialogRegNumRegion;
    @InjectView(R.id.dialog_yes)
    TextView dialoYes;
    @InjectView(R.id.ic_wait_gross)
    ImageView icWaitGross;

    private int status;
    private String color;
    private String brandModel;
    private String regNumInfoText;
    private String title;
    private String textButton;
    private long idRoute;

    public FStatusInfo() {

    }

    public static Fragment newInstance(long idRoute, int status, String color, String brandModel,
                                       String regNumInfoText) {
        return new FStatusInfo().withViewId(R.layout.f_status_info)
                .withArgument("idRoute", idRoute)
                .withArgument(ARG_STATUS, status)
                .withArgument(ARG_COLOR, color)
                .withArgument(ARG_BRAND_MODEL, brandModel)
                .withArgument(ARG_INFO_TEXT, regNumInfoText);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idRoute = getArguments().getLong("idRoute");
            status = getArguments().getInt(ARG_STATUS);
            color = getArguments().getString(ARG_COLOR);
            brandModel = getArguments().getString(ARG_BRAND_MODEL);
            regNumInfoText = getArguments().getString(ARG_INFO_TEXT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (status) {
            case Constants.STATE_SET:

                Injector.getSoundPoolClient()
                        .playSoundWav(TipeSound.STATE_SET.nameFailSound);
                title = getResources().getString(R.string.car_assigned);
                textButton = getResources().getString(R.string.ok);

                break;
            case Constants.STATE_WAIT:

                Injector.getSoundPoolClient()
                        .playSoundWav(TipeSound.STATE_WAIT.nameFailSound);

                icWaitGross.setVisibility(View.VISIBLE);
                TintIcons.tintImageViewOther(icWaitGross, R.color.colorPrimary);

                title = getString(R.string.car_wait_info);
                textButton = getResources().getString(R.string.go_to_car);

                break;

        }

        dialogTitle.setText(title);
        dialogData.setText(String.format("%s\n%s", color, brandModel));
        dialogRegNum.setText(regNumInfoText);

        dialoYes.setText(textButton);

    }

    @OnClick(R.id.dialog_yes)
    public void onClickButton() {
        if (status == Constants.STATE_WAIT) {
            RESTConnect rc = Injector.getRC();


            rc.findOkStatusWait(idRoute, new IGetApiResponse() {
                @Override
                public void getApiResponse(ApiResponse apiResponse) {

                }
            });

        }
        getAWork().removeFStatusInfo(this);
    }


    @Override
    public boolean onBackPressed() {
        getAWork().removeFStatusInfo(this);
        return true;
    }
}
