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

package com.opentadam.ui.frends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusSetNameLeftMenu;
import com.opentadam.data.DialogClient;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.Utilites;

import butterknife.InjectView;
import butterknife.OnClick;

public class FSetNameAndMail extends BaseFr {
    @InjectView(R.id.fnm_title)
    TextView fnmTitle;

    @InjectView(R.id.fnm_sub_title)
    TextView fnmSubTitle;
    @InjectView(R.id.fnm_edit)
    EditText editText;
    private int type;


    public static Fragment newInstance(int type) {
        return new FSetNameAndMail()
                .withArgument("type", type)
                .withViewId(R.layout.f_name_mail);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getInt("type");
        initUI();
    }

    private void initUI() {
        switch (type) {
            case Constants.TYPE_NAME:
                fnmTitle.setText(R.string.fnm_title_name);
                fnmSubTitle.setText(R.string.fnm_sub_title_name);

                String name = Injector.getSettingsStore().readString(Constants.PROFIL_NAME, null);
                if (name != null) {
                    editText.setText(name);

                }

                break;
            case Constants.TYPE_MAIL:
                fnmTitle.setText(R.string.fnm_title__mail);
                fnmSubTitle.setText(R.string.fnm_sub_title__mail);

                String mail = Injector.getSettingsStore().readString(Constants.REG_USER_MAIL, null);
                if (mail != null) {
                    editText.setText(mail);
                }
                break;
        }
        editText.setSelection(editText.getText().length());
        Utilites.showSoftKeyboard(getAWork());
    }

    private boolean isValid(String value) {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    @OnClick(R.id.fnm_edit_close)
    public void onClose() {
        editText.setText("");
    }

    @OnClick(R.id.fnm_set_memory)
    public void setProfilName() {
        String name = editText.getText().toString();
        if ("".equals(name))
            name = null;

        switch (type) {
            case Constants.TYPE_NAME:
                Injector.getSettingsStore()
                        .writeString(Constants.PROFIL_NAME, name);

                App.bus.post(new BusSetNameLeftMenu());
                fnmBask();
                break;
            case Constants.TYPE_MAIL:

                if (name != null && !isValid(name)) {

                    DialogClient.showOneButtonDialog("Ошибка"
                            , "Неправильный формат адреса электронной почты"
                            , this
                    );
                    return;
                }
                Injector.getSettingsStore()
                        .writeString(Constants.REG_USER_MAIL, name);
                fnmBask();
                break;
        }

    }

    @OnClick(R.id.fnm_bask)
    public void fnmBask() {
        getAWork().showFProfil();
    }

    @Override
    public boolean onBackPressed() {
        fnmBask();
        return true;
    }

    @Override
    public void onDestroyView() {
        Utilites.hideSoftKeyboard(getAWork(), editText);
        super.onDestroyView();
    }
}
