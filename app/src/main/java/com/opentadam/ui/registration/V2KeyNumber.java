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

package com.opentadam.ui.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;



public class V2KeyNumber extends Fragment {

    @InjectView(R.id.key_point)
    TextView keyPoint;
    @InjectView(R.id.body_cont)
    LinearLayout bodyCont;


    private EditText regEditPhone;


    public V2KeyNumber() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.key_board_layout, container, false);
        ButterKnife.inject(this, v);

        return v;
    }


    public void initEdit(View editText, boolean enabledChanger) {
        setVisibilityKeyboord(View.VISIBLE);
        keyPoint.setVisibility(enabledChanger ? View.INVISIBLE : View.VISIBLE);
        regEditPhone = (EditText) editText;
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // хешим тип входных данных
                edittext.setInputType(InputType.TYPE_NULL); // дисаблим стандартную клаву
                edittext.onTouchEvent(event);               // устанавливаем слушатель
                edittext.setInputType(inType);              // восстанавливаем
                return true;
            }
        });

        if (enabledChanger)
            regEditPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    regEditPhone.setSelection(count);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
    }

    public void setVisibilityKeyboord(int visibilityKeyboord) {
        bodyCont.setVisibility(visibilityKeyboord);
    }


    @OnClick({R.id.key_point, R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4,
            R.id.key_5, R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_bask})
    public void onKeyEventReg(View v) {
        int selectionStart = regEditPhone.getSelectionStart();
        int selectionEnd = regEditPhone.getSelectionEnd();
        String textEdit = regEditPhone.getText().toString();
        if(selectionStart != selectionEnd){
            textEdit = "";
        }
        if (v.getTag() == null) {
            if("".equals(textEdit) ){
                regEditPhone.setText("");
                return;
            }

            String target = " " + Injector.getWorkSettings().getCurrencyShort();
            textEdit = textEdit.replace(target, "");

            if (textEdit.length() == 0)
                return;
            if (textEdit.length() == 1) {
                regEditPhone.setText("");
            }

            String substring = textEdit.substring(0, textEdit.length() - 1);

            regEditPhone.setText(substring);

            return;
        }
        String tag = (String) v.getTag();

        if (".".equals(tag) && textEdit.contains("."))
            return;
        String text = textEdit + tag;

        regEditPhone.setText(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
