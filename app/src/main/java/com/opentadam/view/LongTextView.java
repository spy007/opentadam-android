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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusCountLines2;
import com.opentadam.model.CreateRequest;

import static com.opentadam.Injector.getClientData;


public class LongTextView {
    private static boolean isEnabledUpd = false;
    // private static TextView textViewGl;


    @NonNull
    public static ViewTreeObserver.OnGlobalLayoutListener getOnGlobalLayoutListener(
            final TextView textView, final Context context, final boolean isWhile) {

        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = textView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                updateEllipsizedText(textView, context, isWhile);
            }
        };
    }

    public static ViewTreeObserver.OnGlobalLayoutListener setStartAdressCountOnGLListener(
            final TextView textViewGl) {

        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = textViewGl.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                bodyGlob(textViewGl);
            }

        };
    }

    private static void bodyGlob(TextView textViewGl) {
        if (!isEnabledUpd) {
            isEnabledUpd = true;
            App.bus.post(new BusCountLines2(false));
            //   textViewGl.setPadding(0, Utilites.getDPtoPX(1), 0, 0);
            textViewGl.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

            TextPaint textpaint = textViewGl.getPaint();
            // кол-во символов и их ширина
            float[] measuredWidth = new float[1];
            String text = textViewGl.getText().toString();
            int cnt = textpaint.breakText(text, true
                    , textViewGl.getWidth(), measuredWidth);

            final int length = text.length();

            if (cnt == 0)
                return;

            boolean aBoolean = Injector.getAppContext()
                    .getResources().getBoolean(R.bool.isPhoneSmall);

            if (cnt - (aBoolean ? 0 : 0) < length) {
                CreateRequest createRequest = getClientData().getCreateRequest();
                String textStartAdressDopInfo = createRequest.getTextStartAdressDopInfo();

                if (textStartAdressDopInfo != null) {
                    String trim = textStartAdressDopInfo.trim();
                    String textViewText = textViewGl.getText().toString();

                    if (textViewGl.getTag() != null && !textViewText.endsWith(", " + trim))
                        textViewGl.setText(String.format("%s, %s", textViewText, trim));
                }

                App.bus.post(new BusCountLines2(true));
                textViewGl.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                //   textViewGl.setPadding(0, Utilites.getDPtoPX(3), 0, 0);
            } else {
                App.bus.post(new BusCountLines2(false));
                //    textViewGl.setPadding(0, Utilites.getDPtoPX(1), 0, 0);
                textViewGl.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

            }

            isEnabledUpd = false;
        }
    }

    public static ViewTreeObserver.OnGlobalLayoutListener getSetCountOnGlobalLayoutListener(
            final TextView textViewGl, final int count) {

        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = textViewGl.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                TextPaint textpaint = textViewGl.getPaint();

                // кол-во символов и их ширина
                float[] measuredWidth = new float[1];
                String text = textViewGl.getText().toString();
                int cnt = textpaint.breakText(text, true
                        , textViewGl.getWidth(), measuredWidth);

                final int length = text.length();

                if (cnt == 0)
                    return;

                if (cnt * 2 - 4 < length) {
                    textViewGl.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    String[] txt = {"адрес", "адреса", "адреса", "адреса", "адресов"};

                    String s = count > 5 ? "адресов" : txt[count - 1];
                    textViewGl.setText(String.format("%d %s", count, s));
                }
            }
        };
    }

    private static void updateEllipsizedText(TextView tv, Context context, boolean isWhile) {
        TextPaint textpaint = tv.getPaint();
        // кол-во символов и их ширина
        float[] measuredWidth = new float[1];
        String text = tv.getText().toString();
        int cnt = textpaint.breakText(text, true, tv.getWidth(), measuredWidth);

        int col80 = ContextCompat.getColor(context
                , isWhile ? R.color.text_white80 : R.color.text_default_color80);
        int col60 = ContextCompat.getColor(context
                , isWhile ? R.color.text_white60 : R.color.text_default_color60);
        int col30 = ContextCompat.getColor(context
                , isWhile ? R.color.text_white30 : R.color.text_default_color30);

        if (cnt != 0 && cnt < text.length()) {
            String text1 = text.substring(0, cnt);

            Spannable textSpan = new SpannableString(text1);
            int len = text1.length();
            if (len >= 3) {
                textSpan.setSpan(new ForegroundColorSpan(col80),
                        len - 3, len - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textSpan.setSpan(new ForegroundColorSpan(col60),
                        len - 2, len - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textSpan.setSpan(new ForegroundColorSpan(col30),
                        len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            tv.setText(textSpan);
        }
    }

}
