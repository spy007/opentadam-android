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

package com.opentadam.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.google.gson.Gson;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.AccountState;

import butterknife.InjectView;
import butterknife.OnClick;


public class FDebetInfo extends BaseFr {
    @InjectView(R.id.value_debet)
    TextView valueDebet;
    private AccountState accountState;

    public FDebetInfo() {
    }

    public static Fragment newInstance(AccountState a) {
        String accountState = new Gson().toJson(a);
        return new FDebetInfo().withViewId(R.layout.f_debet_info)
                .withArgument("accountState", accountState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            String a = getArguments().getString("accountState");
            accountState = new Gson().fromJson(a, AccountState.class);
            valueDebet.setText(String.format("%s%s",
                    accountState.getSummValue(),
                    Injector.getWorkSettings().getCurrencyShort()));
        }
    }

    @OnClick(R.id.show_debet_card)
    public void showDebetCard() {
        getAWork().showFCardSettings(accountState, -1);
    }

    @OnClick(R.id.bask)
    public void onBask() {

        getAWork().showV2FSetCashOrder();
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }
}
