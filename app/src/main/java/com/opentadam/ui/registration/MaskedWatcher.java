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

import android.text.Editable;
import android.text.TextWatcher;

import java.text.ParseException;


public class MaskedWatcher implements TextWatcher {

    private String mResult = "";
    private String mMask;

    public MaskedWatcher(String mask) {
        mMask = mask;
    }

    private static String removeCharAt(String s, int pos) {

        return s.substring(0, pos) + s.substring(pos + 1);

    }

    @Override
    public void afterTextChanged(Editable s) {

        String mask = mMask;
        String value = s.toString();

        if (value.equals(mResult))
            return;

        try {

            // prepare the formatter
            MaskedFormatter formatter = new MaskedFormatter(mask);
            formatter.setValueContainsLiteralCharacters(false);
            formatter.setPlaceholderCharacter((char) 1);

            // get a string with applied mask and placeholder chars
            value = formatter.valueToString(value);

            try {

                // find first placeholder
                value = value.substring(0, value.indexOf((char) 1));

                //process a mask char
                if (value.charAt(value.length() - 1) ==
                        mask.charAt(value.length() - 1)) {
                    value = value.substring(0, value.length() - 1);
                }

            } catch (Exception e) {

            }

            mResult = value;

            s.replace(0, s.length(), value);


        } catch (ParseException e) {

            //the entered value does not match a mask
            int offset = e.getErrorOffset();
            value = removeCharAt(value, offset);
            s.replace(0, s.length(), value);

        }


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {
    }
}