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

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

import com.opentadam.Injector;
import com.opentadam.network.rest.Service;
import com.opentadam.utils.CustomTypefaceSpan;

public class PresenterNewReg {
    private final InterfaceFragmentNewReg mInterfaceFragmentNewReg;

    private final ModelNewReg mModelNewReg;
    private boolean isDisableTextChanged;

    PresenterNewReg(InterfaceFragmentNewReg interfaceFragmentNewReg) {
        mInterfaceFragmentNewReg = interfaceFragmentNewReg;
        mModelNewReg = new ModelNewReg(this);
    }

    void onActivityCreated(boolean isPrimaryStart) {


    }


    void onDestroyView() {

    }

    public boolean onBackPressed() {
        mInterfaceFragmentNewReg.showV3FRoute();

        return true;
    }

    void onTextViewSend(String refcode) {
        mInterfaceFragmentNewReg.showProgress();
        mModelNewReg.sendRefCode(refcode);
    }

    public void onTextChanged(String s, int start, int before, int coun) {
        if (isDisableTextChanged)
            return;
        isDisableTextChanged = true;

        int delta = start - before;


        if (s.length() == 0) {
            // очистка долгим нажатием на клаве
            mInterfaceFragmentNewReg.setLetterSpacing(0);
            mInterfaceFragmentNewReg.setTextSize(16);
            isDisableTextChanged = false;
            return;
        }

        if (before != 0) {
            isDisableTextChanged = false;
            return;
        }


        if (coun != 0) {
            char charAt = s.charAt(delta);

            if (!Character.isLetter(charAt)) {
                mInterfaceFragmentNewReg.setTextSize(24);
                mInterfaceFragmentNewReg.setLetterSpacing(.15f);
                isDisableTextChanged = false;
                return;
            }
            Log.e("nmmn", "charAt = " + charAt);
        }


        SpannableString redName = new SpannableString(s.toUpperCase());
        Typeface font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                , "fonts/Roboto/Roboto-Regular.ttf");

        if (s.length() != 0) {
            font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                    , "fonts/Roboto/Roboto-Bold.ttf");
            mInterfaceFragmentNewReg.setTextSize(24);
            mInterfaceFragmentNewReg.setLetterSpacing(.15f);
        } else {
            mInterfaceFragmentNewReg.setLetterSpacing(0);
            mInterfaceFragmentNewReg.setTextSize(16);
        }

        redName.setSpan(new CustomTypefaceSpan(font)
                , 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


        mInterfaceFragmentNewReg.setDefaultStyleText(s.toUpperCase());
        mInterfaceFragmentNewReg.setSelection(delta + 1);

        isDisableTextChanged = false;
    }

    void setIsSuccess(boolean b) {
        mInterfaceFragmentNewReg.hideProgress();
        if (b) {
            Service service = Injector.getClientData().service;
            service.isNoRegReferall = false;
            mInterfaceFragmentNewReg.showSuccess(service.getLptype());

        } else {
            mInterfaceFragmentNewReg.showError();
        }
    }
}
