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


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.model.CreateRequest;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import static com.opentadam.Injector.getClientData;

public class V2FCommentOrder extends BaseFr {

    @InjectView(R.id.ic_clear_comment)
    public ImageView clearComment;
    @InjectView(R.id.edit_comment)
    public EditText editComment;
    @InjectView(R.id.send_comment)
    public TextView sendComment;

    public V2FCommentOrder() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new V2FCommentOrder().withViewId(R.layout.f_v2_comment_order);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unitUI();
    }

    protected void unitUI() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TintIcons.tintImageViewOther(clearComment, R.color.colorPrimary);

        if (createRequest.comment != null && !"".equals(createRequest.comment)) {
            editComment.setText(createRequest.comment);
            setActiveEdit(editComment);

        } else {
            setDefEdit(editComment);
        }

        hideKeyboard(editComment);
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

    protected void setActiveEdit(EditText edit) {
        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        edit.setSelection(edit.getText().length());

    }

    protected void setDefEdit(EditText edit) {

        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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

    @OnClick(R.id.send_comment)
    public void onSendComment() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        String s = editComment.getText().toString();
        createRequest.comment = "".equals(s) ? null : s;
        onBask();
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

        getAWork().showV3FRoute(true);
    }

    @Override
    public void onDestroyView() {
        getAWork().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getAWork().hideWorkProgress();
        hideKeyboard(editComment);

        super.onDestroyView();
    }
}