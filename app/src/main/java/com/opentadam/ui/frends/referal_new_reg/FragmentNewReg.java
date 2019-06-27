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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.R;
import com.opentadam.ui.BaseFr;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class FragmentNewReg extends BaseFr implements InterfaceFragmentNewReg {

    private final PresenterNewReg presenterNewReg;
    @InjectView(R.id.editTextViewSend)
    EditText editTextViewSend;
    @InjectView(R.id.fglobCont)
    LinearLayout globCont;
     @InjectView(R.id.fnlInfoRegReferalText)
     TextView infoRegReferalText;


    public FragmentNewReg() {
        presenterNewReg = new PresenterNewReg(this);
    }

    public static Fragment newInstance() {
        return new FragmentNewReg().withViewId(R.layout.fragment_new_reg);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenterNewReg.onActivityCreated(savedInstanceState == null);
        editTextViewSend.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if(App.app.hashBC.infoRegReferalText != null)
            infoRegReferalText.setText(App.app.hashBC.infoRegReferalText);

    }


    @OnClick(R.id.showScannerQRCode)
    public void onShowScannerQRCode(){
        getAWork().showGRScaner();
    }

    @Override
    public void onDestroyView() {
        hideKeyboard(editTextViewSend);
        presenterNewReg.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {

        return presenterNewReg.onBackPressed();
    }

    @OnClick(R.id.icBack)
    public void onIcBack() {
        presenterNewReg.onBackPressed();
    }

    @OnClick(R.id.textViewSend)
    public void onTextViewSend() {
        String refcode = editTextViewSend.getText().toString();
        presenterNewReg.onTextViewSend(refcode);
    }

    @OnTextChanged(R.id.editTextViewSend)
    public void onTextChanget(CharSequence s, int start, int before,
                              int coun) {
        presenterNewReg.onTextChanged(s.toString(), start,   before, coun);
    }

    @Override
    public void showV3FRoute() {
        getAWork().showV3FRoute();
    }

    @Override
    public void setDefaultStyleText(String redName) {
        editTextViewSend.setText(redName);
    }

    @Override
    public void showError() {
        alert(getString(R.string.error_validate_code));

    }

    @Override
    public void showSuccess(String lptype) {
        getAWork().showFReferral(lptype);

    }

    @Override
    public void hideProgress() {
        getAWork().hideProgressDevault();
    }

    @Override
    public void showProgress() {
        getAWork().showProgressDevault();
    }

    @Override
    public void setTextSize(int dp) {
        editTextViewSend.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setLetterSpacing(float v) {
        editTextViewSend.setLetterSpacing(v);
    }

    @Override
    public void setSelection(int coun) {
        editTextViewSend.setSelection(coun);
    }

    public void initRegToScannerQR(String refCode) {
        presenterNewReg.onTextViewSend(refCode);
    }
}
