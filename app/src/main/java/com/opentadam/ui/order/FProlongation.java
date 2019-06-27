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

package com.opentadam.ui.order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;

import butterknife.InjectView;
import butterknife.OnClick;

public class FProlongation extends BaseFr {

    private static final String ADRESS_ONE = "addressOne";
    private static final String ADRESS_FIN = "addressFin";
    private static final String ID_ROUTE_INDEX = "idRoute";
    @InjectView(R.id.disappointed_smile)
    ImageView disappointedSmile;
    @InjectView(R.id.adress_one)
    TextView adressOneView;
    @InjectView(R.id.adress_fin)
    TextView adressFinView;
    private FSetProlongation fSetProlongation;
    private long idRoute;
    private String addressFin;
    private String addressOne;

    public FProlongation() {
    }


    public static Fragment newInstance(String addressOne, String addressFin, long idRoute) {
        return new FProlongation().withViewId(R.layout.f_prolongation)
                .withArgument(ADRESS_ONE, addressOne).withArgument(ADRESS_FIN, addressFin)
                .withArgument(ID_ROUTE_INDEX, idRoute);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            addressOne = getArguments().getString(ADRESS_ONE);
            addressFin = getArguments().getString(ADRESS_FIN);
            idRoute = getArguments().getLong(ID_ROUTE_INDEX);
        }


        adressOneView.setText(addressOne);
        adressFinView.setText(addressFin);

        TintIcons.tintImageViewBrend(disappointedSmile);

        getAWork().setTitle(getString(R.string.title_FProlongation));
        fSetProlongation = (FSetProlongation) getChildFragmentManager()
                .findFragmentById(R.id.f_set_prolongation);
        fSetProlongation.setIdRoute(idRoute);
        fSetProlongation.initUi();
    }

    @OnClick(R.id.button_delete_route)
    public void deleteRoute() {
        RESTConnect restConnect = Injector.getRC();

        restConnect.deleteRoute(idRoute, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;
/*
                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }*/

                getAWork().restartAll();
            }
        });

    }

    @Override
    public boolean onBackPressed() {
        getAWork().sendProlongation(fSetProlongation.getPosProlongation(), idRoute);
        return true;
    }

}
