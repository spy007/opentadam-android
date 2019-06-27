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

package com.opentadam.yandex_google_maps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.opentadam.App;
import com.opentadam.R;
import com.opentadam.bus.BusOkRem;
import com.opentadam.ui.BaseFr;

import butterknife.InjectView;
import butterknife.OnClick;

public class FDialogRemisshen extends BaseFr {
    @InjectView(R.id.ok_rem)
    View v;

    public static Fragment newInstance() {
        return new FDialogRemisshen().withViewId(R.layout.f_dialog_remission);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnClick(R.id.ok_rem)
    public void okRem() {
        App.bus.post(new BusOkRem());
        getAWork().hidePrivateDialog();
    }

    @OnClick(R.id.close_rem)
    public void closeRem() {
        getAWork().hidePrivateDialog();
    }
}
