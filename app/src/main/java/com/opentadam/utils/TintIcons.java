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

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;


public class TintIcons {


    public static ArrayMap<String, Integer> getArrCarIconFull() {
        ArrayMap<String, Integer> arrCarIcon = new ArrayMap<>();
        arrCarIcon.put("carType_1", R.drawable.ic_tariff_auto_01);
        arrCarIcon.put("carType_2", R.drawable.ic_tariff_auto_02);
        arrCarIcon.put("carType_3", R.drawable.ic_tariff_auto_03);
        arrCarIcon.put("carType_4", R.drawable.ic_tariff_auto_04);
        arrCarIcon.put("carType_5", R.drawable.ic_tariff_auto_05);
        arrCarIcon.put("carType_6", R.drawable.ic_tariff_auto_06);
        arrCarIcon.put("carType_7", R.drawable.ic_tariff_auto_07);
        arrCarIcon.put("carType_8", R.drawable.ic_tariff_auto_08);
        arrCarIcon.put("carType_9", R.drawable.ic_tariff_auto_09);
        arrCarIcon.put("carType_10", R.drawable.ic_tariff_auto_10);
        arrCarIcon.put("carType_11", R.drawable.ic_tariff_auto_11);
        arrCarIcon.put("carType_12", R.drawable.ic_tariff_auto_12);
        arrCarIcon.put("counteragent_tariff", R.drawable.ic_tarif_counteragent);

        return arrCarIcon;
    }

    public static ArrayMap<String, Integer> getArrCarIconSmall() {
        ArrayMap<String, Integer> arrCarIconSmall = new ArrayMap<>();
        arrCarIconSmall.put("carType_1", R.drawable.ic_tariff_auto_01_small);
        arrCarIconSmall.put("carType_2", R.drawable.ic_tariff_auto_02_small);
        arrCarIconSmall.put("carType_3", R.drawable.ic_tariff_auto_03_small);
        arrCarIconSmall.put("carType_4", R.drawable.ic_tariff_auto_04_small);
        arrCarIconSmall.put("carType_5", R.drawable.ic_tariff_auto_05_small);
        arrCarIconSmall.put("carType_6", R.drawable.ic_tariff_auto_06_small);
        arrCarIconSmall.put("carType_7", R.drawable.ic_tariff_auto_07_small);
        arrCarIconSmall.put("carType_8", R.drawable.ic_tariff_auto_08_small);
        arrCarIconSmall.put("carType_9", R.drawable.ic_tariff_auto_09_small);
        arrCarIconSmall.put("carType_10", R.drawable.ic_tariff_auto_10_small);
        arrCarIconSmall.put("carType_11", R.drawable.ic_tariff_auto_11_small);
        arrCarIconSmall.put("carType_12", R.drawable.ic_tariff_auto_12_small);
        arrCarIconSmall.put("counteragent_tariff", R.drawable.ic_tarif_counteragent_small);

        return arrCarIconSmall;
    }


/*
    public static Drawable tintIconBrend(Drawable icon) {
        ColorStateList list = ContextCompat.getColorStateList(Injector.getAppContext(), R.color.colorPrimary);
        if (icon != null) {
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, list);
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        }
        return icon;
    }
*/


    private static int getQuickIcon(int pos) {
//
        if (pos > 26)
            return R.drawable.ic_location;
        int[] idResQuickIcon = new int[]{
                R.drawable.ic_location,
                R.drawable.quick_ic_01_bus_stop, //
                R.drawable.quick_ic_02_crossroads, //
                R.drawable.quick_ic_03_supermarket, //
                R.drawable.quick_ic_04_bar, //
                R.drawable.quick_ic_05_barbershop, //
                R.drawable.quick_ic_06_organization, //
                R.drawable.quick_ic_07_medical, //
                R.drawable.quick_ic_08_sauna, //
                R.drawable.quick_ic_09_hotel, //
                R.drawable.quick_ic_10_railroad, //
                R.drawable.quick_ic_11_other, //
                R.drawable.quick_ic_12_airport,
                R.drawable.quick_ic_13_education,
                R.drawable.quick_ic_14_gas_station,
                R.drawable.quick_ic_15_sport,
                R.drawable.quick_ic_16_tourism,
                R.drawable.quick_ic_17_government,
                R.drawable.quick_ic_18_art,
                R.drawable.quick_ic_19_pets,
                R.drawable.quick_ic_20_plants,
                R.drawable.quick_ic_21_settlement,
                R.drawable.quick_ic_22_villas,
                R.drawable.quick_ic_23_subway,
                R.drawable.quick_ic_24_bank,
                R.drawable.quick_ic_25_religion,
                R.drawable.quick_ic_26_cemetery
        };

        return idResQuickIcon[pos];
    }

    private static Drawable tintIcon(Drawable icon, ColorStateList colorStateList) {

        if (icon != null) {
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, colorStateList);
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        }
        return icon;
    }

    public static int initColor(int id) {
        return ContextCompat.getColor(Injector.getAppContext(), id);
    }

/*    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }*/

/*
    public static Drawable getDrawable(int id) {

        return ContextCompat.getDrawable(Injector.getAppContext(), id);
    }
*/

    /*
        public static Drawable tintIconOther(Drawable icon, int color) {
            ColorStateList list =
                    ContextCompat.getColorStateList(Injector.getAppContext(), R.color.colorPrimary);
            if (icon != null) {

                icon = DrawableCompat.wrap(icon).mutate();
                DrawableCompat.setTintList(icon, list);
                DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);

            }
            return icon;
        }
    */
    public static void setStuleReg(String text, TextView textView, String path, int sizeDp) {

        Typeface font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                , path);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sizeDp);
        SpannableString redName = new SpannableString(text);
        redName.setSpan(new CustomTypefaceSpan(font)
                , 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        textView.setText(redName);
    }

    public static VectorDrawableCompat getVectorDrawableCompatIcon(int quickIcon) {

        return VectorDrawableCompat
                .create(Injector.getAppContext().getResources()
                        , quickIcon, null);
    }

    public static VectorDrawableCompat getVectorDrawableCompat(int pos) {
        return VectorDrawableCompat
                .create(Injector.getAppContext().getResources()
                        , TintIcons.getQuickIcon(pos), null);
    }

    public static Bitmap getBitmap(int id) {
        VectorDrawableCompat vectorDrawable = getVectorDrawableRes(id);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
/*
    public static Bitmap createVectorIconDrawableToMaps(int widthDP, int heightDP, int idDrawableImg) {

        int widthPX = Utilites.getDPtoPX(widthDP);
        int heightPX = Utilites.getDPtoPX(heightDP);

        Bitmap bmOriginal = getBitmap(idDrawableImg);

        return Bitmap.createScaledBitmap(bmOriginal, widthPX,
                heightPX, false);
    }*/
/*
    public static Bitmap createVectorMarkerV2(String text, int colorText,
                                              int widthDP, int heightDP, int idDrawableImg) {
        int widthPX = Utilites.getDPtoPX(widthDP);
        int heightPX = Utilites.getDPtoPX(heightDP);
        float centerX = widthPX / 2;
        float centerY = heightPX / 2;

        Bitmap newImage = createVectorIconDrawableToMaps(widthDP, heightDP, idDrawableImg);

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
    }*/

    public static VectorDrawableCompat getVectorDrawableRes(int id) {
        return VectorDrawableCompat
                .create(Injector.getAppContext().getResources()
                        , id, null);
    }

    public static void tintImageViewBrend(ImageView imageView) {
        ColorStateList list =
                ContextCompat.getColorStateList(imageView.getContext(), R.color.colorPrimary);
        if (list != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(list);
            } else {
                Drawable drawable = imageView.getDrawable();
                imageView.setImageDrawable(tintIcon(drawable, list));
            }
        }
    }

    public static void tintImageViewOther(ImageView imageView, int color) {
        ColorStateList list = ContextCompat.getColorStateList(imageView.getContext(), color);
        if (list != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(list);
            } else {
                Drawable drawable = imageView.getDrawable();
                imageView.setImageDrawable(tintIcon(drawable, list));
            }
        }
    }

}
