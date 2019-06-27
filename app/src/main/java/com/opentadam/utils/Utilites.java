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

package com.opentadam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Utilites {

    public static void showSoftKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm == null)
            return;

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) Injector.getAppContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null || imm == null)
            return;

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null || imm == null)
            return;

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static SpannableString getModerateUICost(String textEdit
            , View v
            , double ratio
            , int procentDelta) {
        float textSize = 24f;
        String currencyShort = " " + Injector.getWorkSettings().getCurrencyShort();

        if (v instanceof TextView) {
            TextView view = (TextView) v;
            textSize = view.getTextSize();
        }
        if (v instanceof EditText) {
            EditText view = (EditText) v;
            textSize = view.getTextSize();
        }

        int size = (int) textSize / 2;
        float v21 = textSize / 100;
        int delta = (int) (procentDelta * v21);
        size += delta;
        Typeface font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                , "fonts/Roboto/Roboto-Light.ttf");

        if (textEdit.trim().contains(".00")) {
            textEdit = textEdit.replace(".00", "");
        }

        SpannableString ss = new SpannableString(textEdit);
        int length = textEdit.length();
        int start = length - currencyShort.length();

        String[] split = textEdit.trim().split("\\.");
        if (split.length > 1) {
            String s = split[1];

             start  = length - s.length() -1;
        }


        ss.setSpan(new CustomTSpan("normal", font)
                , start, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        ss.setSpan(new AbsoluteSizeSpan(size),
                start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new SuperscriptSpanAdjuster(ratio),
                start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    public static SpannableString getModerateUICost(String textEdit, View v) {

        return getModerateUICost(textEdit, v, 0.65, 5);
    }

    public static void getStrigIso(String txt) {

        try {
            boolean isISO = txt.endsWith("Z");
            int length = txt.length();
            long del = 0;
            Injector.deltaTimezone = null;
            if (!isISO) {
                String substring1 = txt.substring(length - 6, length);
                boolean plus = substring1.startsWith("+");

                // +06:00
                String[] substring2 = substring1.substring(1, 5).split(":");
                int hyr = Integer.parseInt(substring2[0]);
                int min = Integer.parseInt(substring2[1]);
                del = (hyr * 60 * 60 + min * 60) * 1000 * (plus ? 1 : -1);
                Injector.deltaTimezone = substring1.replace(":", "");
            }

            DateTime dateTime = new DateTime(txt
                    , DateTimeZone.UTC);


            DateTime dateTime1 = new DateTime(System.currentTimeMillis()
                    , DateTimeZone.UTC);


            Injector.deltaCorrectTimeDefault = dateTime1.getMillis() - dateTime.getMillis();
            Injector.deltaCorrectTime = del - Injector.deltaCorrectTimeDefault;


        } catch (Exception e) {
            Injector.deltaCorrectTime = 0;
        }

    }

    public static String getStringDistance(double distanse) {
        DecimalFormat df = new DecimalFormat(Injector.getAppContext()
                .getString(R.string.distance_formated_value));
        return df.format(distanse);
    }

    public static Bitmap createIconDrawableToMaps(int widthDP, int heightDP, int idDrawableImg) {


        int widthPX = getDPtoPX(widthDP);
        int heightPX = getDPtoPX(heightDP);

        Bitmap bmOriginal = BitmapFactory.decodeResource(Injector.getAppContext().getResources(),
                idDrawableImg);

        return Bitmap.createScaledBitmap(bmOriginal, widthPX,
                heightPX, false);
    }

    public static Bitmap createScaleIconDrawableToMaps(int widthDP, int heightDP, int idDrawableImg, int i11) {
        float procent = (30 + i11 * 10);
        float k = procent / 100f;
        int widthPX = (int) (k * getDPtoPX(widthDP));
        int heightPX = (int) (k * getDPtoPX(heightDP));

        Bitmap bmOriginal = BitmapFactory.decodeResource(Injector.getAppContext().getResources(),
                idDrawableImg);

        return Bitmap.createScaledBitmap(bmOriginal, widthPX,
                heightPX, false);
    }

    public static int getDPtoPX(int widthDP) {
        final float scale = Injector.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (widthDP * scale + 0.5f);
    }

    public static float getPX(int DP) {

        final float scale = Injector.getAppContext().getResources().getDisplayMetrics().density;
        return DP * scale + 0.5f;
    }
/*

    public static Bitmap createMarkerV2(String text, int colorText,
                                        int widthDP, int heightDP, int idDrawableImg) {
        int widthPX = getDPtoPX(widthDP);
        int heightPX = getDPtoPX(heightDP);
        float centerX = widthPX / 2;
        float centerY = heightPX / 2;

        Bitmap newImage = createIconDrawableToMaps(widthDP, heightDP, idDrawableImg);

        Canvas c = new Canvas(newImage);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(initColor(colorText));
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(widthPX * 0.4f);
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        Rect mTextBoundRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), mTextBoundRect);
        float mTextWidth = paint.measureText(text);
        float mTextHeight = mTextBoundRect.height();

        c.drawText(text,
                centerX - (mTextWidth / 2f),
                centerY + (mTextHeight / 2f) - (mTextHeight * 0.25f),
                paint
        );

        return newImage;
    }
*/

    public static String getDefCostBigDec(float cost) {
        float v1 = cost - ((int) cost);
        return BigDecimal.valueOf(cost).
                setScale(v1 == 0 ? 0 : 2, RoundingMode.HALF_UP).toString();

    }

    public static int initColor(int id) {
        return ContextCompat.getColor(Injector.getAppContext(), id);
    }

    public static Bitmap createMarker(int colorBg, int bgCrug, String text,
                                      int colorText, int widthDP, int heightDP) {
        //  int idIcon = R.drawable.code_login_disabled;


        int widthPX = getDPtoPX(widthDP);
        int heightPX = getDPtoPX(heightDP);
        float centerX = widthPX / 2;
        float centerY = heightPX / 2;
        Bitmap newImage = Bitmap.createBitmap(widthPX, heightPX, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(newImage);

        Paint paint = new Paint();


        // Рисуем белый круг
        paint.setAntiAlias(true); // задаем режим сглаживания
        paint.setColor(bgCrug); // выбираем белый цвет
        c.drawCircle(centerY, centerX, widthPX / 2, paint); // рисуем круг

        paint.setAntiAlias(true); // задаем режим сглаживания
        paint.setColor(colorBg); //
        c.drawCircle(centerY, centerX, widthPX / 2.7f, paint); // рисуем круг

        paint.setColor(colorText);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(widthPX / 1.7f);
        paint.setFakeBoldText(true);

        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        Rect mTextBoundRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), mTextBoundRect);
        float mTextWidth = paint.measureText(text);
        float mTextHeight = mTextBoundRect.height();

        c.drawText(text,
                centerX - (mTextWidth / 2f),
                centerY + (mTextHeight / 2f),
                paint
        );

        return newImage;
    }

    public static String capitalize(String str) {
        return capitalize(str, (char[]) null);
    }

    private static String capitalize(String str, char... delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (!TextUtils.isEmpty(str) && delimLen != 0) {
            char[] buffer = str.toCharArray();
            boolean capitalizeNext = true;
            for (int i = 0; i < buffer.length; ++i) {
                char ch = buffer[i];
                if (isDelimiter(ch, delimiters)) {
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer[i] = Character.toTitleCase(ch);
                    capitalizeNext = false;
                }
            }
            return new String(buffer);
        } else {
            return str;
        }
    }

    private static boolean isDelimiter(char ch, char[] arr$) {
        if (arr$ == null) {
            return Character.isWhitespace(ch);
        } else {

            for (char delimiter : arr$) {
                if (ch == delimiter) {
                    return true;
                }
            }
            return false;
        }
    }
}


