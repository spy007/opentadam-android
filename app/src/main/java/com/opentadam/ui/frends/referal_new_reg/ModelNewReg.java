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

package com.opentadam.ui.frends.referal_new_reg;

import android.util.Log;

import com.opentadam.Injector;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.model.ParamsRegRef;

class ModelNewReg {
    private final PresenterNewReg mPresenterNewReg;

    ModelNewReg(PresenterNewReg presenterNewReg) {
        mPresenterNewReg = presenterNewReg;
    }

    void sendRefCode(String refcode) {
        RESTConnect rc = Injector.getRC();
        rc.findRegReferal(null
                , new ParamsRegRef(refcode)
                , new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        String error = apiResponse.error;
                        Log.e("jkhkjfdk", "hasFocus = " + error);

                        mPresenterNewReg.setIsSuccess(error == null);
                    }
                });
    }

}
