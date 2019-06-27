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

package com.opentadam.ui.creating_an_order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.Option;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFr;
import com.opentadam.view.CostView;
import com.opentadam.view.DefSwitch;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

import static com.opentadam.Injector.getClientData;

public class FCommetAndOptions extends BaseFr {
    @InjectView(R.id.edit_comment)
    public EditText editComment;
    @InjectView(R.id.send_comment)
    public TextView sendComment;
    @InjectView(R.id.cont_option)
    public LinearLayout contOption;
    @InjectView(R.id.ic_clear_comment)
    View clearComment;

    public FCommetAndOptions() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new FCommetAndOptions().withViewId(R.layout.f_comment_and_options);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unitUI();
    }

    @OnTextChanged(R.id.edit_comment)
    public void afterTextChanged(final Editable s) {
        final String text = s.toString().trim();
        final int length = text.length();
        clearComment.setVisibility(length == 0 ? View.GONE : View.VISIBLE);
    }

    private void unitUI() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (createRequest.comment != null && !"".equals(createRequest.comment)) {
            editComment.setText(createRequest.comment);
            setActiveEdit(editComment);
            clearComment.setVisibility(View.VISIBLE);

        } else {
            setDefEdit(editComment);
            clearComment.setVisibility(View.GONE);
        }

        hideKeyboard(editComment);
        initOptionsView();
    }

    @OnFocusChange(R.id.edit_comment)
    public void onFocus(View v, boolean hasFocus) {
        if (editComment == null)
            return;
        if (editComment.getText() == null)
            return;
        if (hasFocus) {

            InputMethodManager imm = (InputMethodManager) getAWork()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        editComment.setHintTextColor(initColor(hasFocus ? R.color.colorTansparent
                : R.color.text_sec_time));

        editComment.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                , hasFocus ? 18 : 16);

    }

    private void setActiveEdit(EditText edit) {
        edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        edit.setSelection(edit.getText().length());

    }

    private void setDefEdit(EditText edit) {

        edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    }

    @OnClick(R.id.ic_clear_comment)
    public void onClearComment() {
        hideKeyboard(editComment);
        editComment.setText("");
        editComment.setHintTextColor(initColor(R.color.text_sec_time));

        editComment.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                , 16);

        sendComment.setFocusable(true);
        sendComment.setFocusableInTouchMode(true);
        sendComment.requestFocus();
    }

    @Override
    public boolean onBackPressed() {
        onBask();
        return true;
    }

    @OnClick(R.id.bask)
    public void onBask() {

        if (!isVisible())
            return;
        CreateRequest createRequest = getClientData().getCreateRequest();
        String s = editComment.getText().toString();
        createRequest.comment = "".equals(s) ? null : s;
        getAWork().showV3FRoute(false, false);
    }

    @Override
    public void onDestroyView() {
        getAWork().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getAWork().hideWorkProgress();
        hideKeyboard(editComment);

        super.onDestroyView();
    }

    /////////
    private void initOptionsView() {
        contOption.removeAllViews();
        Tarif defTariff = Injector.getClientData().getDefTarif();
        if (defTariff == null || defTariff.options == null) {
            onBask();
            return;
        }
        final ArrayList<Option> options = defTariff.options;

        final TempObjectUIMRoute mRoute = Injector.getClientData().getTempObjectUIMRoute();
        final ArrayList<Option> optionsClient = mRoute.getOptionsClient();

        int size = options.size();

        for (int i = 0; i < size; i++) {

            View view = LayoutInflater.from(contOption.getContext())
                    .inflate(R.layout.section_options, contOption, false);
            TextView nameOption = view.findViewById(R.id.name);
            nameOption.setText(options.get(i).name);
            CostView valueOption = view.findViewById(R.id.value);
            String type = options.get(i).type;
            valueOption.setText(String.format("%s%s", options.get(i).value, "percent".equals(type) ? "%"
                    : Injector.getWorkSettings().getCurrencyShort()));

            final DefSwitch switcher = view.findViewById(R.id.option_switcher);


            switcher.isActive = false;
            switcher.setEnabled(false);
            switcher.setActive(false);

            for (Option option : optionsClient) {
                if (option.name.equals(options.get(i).name)) {
                    switcher.isActive = true;
                    switcher.setEnabled(true);
                    switcher.setActive(true);
                }
            }

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switcher.setEnabled(!switcher.isActive);
                    switcher.setActive(!switcher.isActive);

                    if (switcher.isActive) {
                        optionsClient.add(options.get(finalI));
                    } else {
                        optionsClient.remove(options.get(finalI));
                        for (Option option : optionsClient) {
                            if (option.name.equals(options.get(finalI).name)) {
                                optionsClient.remove(option);
                                mRoute.optionsClient = optionsClient;
                                return;
                            }
                        }
                    }

                }
            });

            contOption.addView(view);
        }
    }
}
