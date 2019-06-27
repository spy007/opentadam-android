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
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.utils.TintIcons;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class V2FEditPrivateAdress extends V2FAddressFeed {

    @InjectView(R.id.title_template)
    TextView titleTemplate;
    @InjectView(R.id.button_add_private_adress)
    TextView buttonAddPrivateAdress;
    @InjectView(R.id.private_redactor_done_button)
    TextView privateRedactorDoneButton;

    private long id;
    private boolean enabledChanged = true;
    private boolean isAdd;

    public static Fragment newInstance(long id, boolean isAdd) {
        return new V2FEditPrivateAdress().withArgument("isAdd", isAdd)
                .withArgument("id", id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong("id");
            isAdd = getArguments().getBoolean("isAdd", false);
        }
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        redactorDopInfo.setText("");
        textDopInfo.setVisibility(View.INVISIBLE);

        titleTemplate.setText(R.string.my_adress_edit_title);
        TintIcons.tintImageViewBrend(icMaps);

        setDefEdit(privateRedactorName);
        setDefEdit(editFlat);
        setDefEdit(editPorcht);
        setDefEdit(redactorDopInfo);
        ClientAddress clientAddress = Injector.getSettingsStore().getPrivateOrderAddress(id);
        contPrivate.setVisibility(View.VISIBLE);
        buttonAddPrivateAdress.setVisibility(View.GONE);

        privateRedactorDoneButton.setVisibility(View.VISIBLE);

        privateRedactorName.setText(clientAddress.getName());
        clientAddress.comment = null;
        initUi(clientAddress);

    }

    @OnClick(R.id.private_redactor_done_button)
    public void onpDoneButton() {
        ClientAddress clientAddress = Injector.getSettingsStore().getPrivateOrderAddress(id);

        String flat = editFlat.getText().toString();
        String poscht = editPorcht.getText().toString();
        String comment = redactorDopInfo.getText().toString();
        String name = privateRedactorName.getText().toString();

        if ("".equals(name))
            name = null; //getString(R.string.def_name_add_private_adress);

        clientAddress.flat = flat;
        clientAddress.entrance = poscht;
        clientAddress.comment = comment;
        clientAddress.namePrivate = name;

        Injector.getSettingsStore().replOrderAddress(id, clientAddress);
        getAWork().showV2FMypoint(id);
    }

    @OnClick({R.id.adress_value, R.id.ic_clear_edit})
    public void onAdressValue() {
        getAWork().showV2FPrivateFullTextSearch(id);
    }

    @OnTextChanged(R.id.private_redactor_name)
    public void afterTextChangedName(Editable s) {
        // текст только что изменили
        if (enabledChanged) {
            enabledChanged = false;
            String s1 = s.toString();
            int length = s1.length();
            if (length == 1) {
                s1 = s1.toUpperCase();
                privateRedactorName.setText(s1);
                privateRedactorName.setSelection(1);
            }


            if (s.length() == 0)
                setDefEdit(privateRedactorName);
            else
                setActiveEdit(privateRedactorName, privateRedactorNameText);
        }

        enabledChanged = true;

    }

    @Override
    public boolean onBackPressed() {
        bask();
        return true;
    }

    private void bask() {
        if (isAdd) {
            Injector.getSettingsStore().removePrivateOrderAddress(id);
            id = 0;
        }

        getAWork().showV2FMypoint(id);
    }

    @OnClick(R.id.ic_bask)
    public void onIcBask() {
        bask();
    }
}
