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
package com.opentadam.data;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;

import butterknife.InjectView;
import butterknife.OnClick;

public class ErrorNetDialog extends BaseFr {


    @InjectView(R.id.ic_call_48)
    ImageView icCall;
    @InjectView(R.id.fen_error_text)
    TextView errorText;
    private boolean isOnBut = false;

    public ErrorNetDialog() {

    }

    public static Fragment newInstance() {
        return new ErrorNetDialog().withViewId(R.layout.f_error_net_dialog);
    }

    public static Fragment newInstance(String errText) {
        return new ErrorNetDialog()
                .withArgument("errText", errText)
                .withViewId(R.layout.f_error_net_dialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TintIcons.tintImageViewBrend(icCall);

        if (getArguments() != null && getArguments().getString("errText", null) != null) {

            errorText.setText(String.format(getString(R.string.err_net_message)
                    , getArguments().getString("errText", null)));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isOnBut)
            return;
        getAWork().hidePrivateDialog();
        getAWork().closeApp();
    }

    @OnClick(R.id.button_ok)
    public void onButtonOk() {
        isOnBut = true;
        getAWork().hidePrivateDialog();
        getAWork().closeApp();
    }


}
