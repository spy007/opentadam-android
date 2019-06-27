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

package com.opentadam.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import com.opentadam.Injector;


public class CostView extends android.support.v7.widget.AppCompatTextView {


    public CostView(Context context) {
        super(context);
        formatRouble();
    }

    private void formatRouble() {


        setText(formatRouble(Injector.getWorkSettings().getCurrencyShort()));

    }

    public CostView(Context context, AttributeSet attrs) {
        super(context, attrs);

        formatRouble();
    }

    public CostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        formatRouble();
    }

    @Override
    public void setText(CharSequence string, BufferType type) {

        super.setText(formatRouble(string.toString()), BufferType.SPANNABLE);
    }

    private SpannableStringBuilder formatRouble(String string) {
        if(string == null || string.length() == 0){
            return new SpannableStringBuilder("");
        }
        SpannableStringBuilder resultSpan = new SpannableStringBuilder(string);
        String loc = Injector.getWorkSettings().getCurrencyShort();
        if (loc.length() == 0)
            return resultSpan;

        for (int i = 0; i < resultSpan.length(); i++) {
            if (resultSpan.charAt(i) == getChLocale()) {
                resultSpan.setSpan(new TypefaceSpan2(getRoubleSupportedTypeface()), i, i + 1, 0);
            }
        }
        return resultSpan;
    }

    private char getChLocale() {
        String loc = Injector.getWorkSettings().getCurrencyShort();


        return loc.charAt(0);
    }

    private Typeface getRoubleSupportedTypeface() {

        return Typeface.createFromAsset(Injector.getAppContext().getAssets(),
                "fonts/Roboto/Roboto-Medium.ttf");

    }


}
