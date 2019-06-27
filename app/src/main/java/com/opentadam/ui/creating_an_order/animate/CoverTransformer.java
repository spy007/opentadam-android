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

package com.opentadam.ui.creating_an_order.animate;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

class CoverTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "CoverTransformer";

    private static final float SCALE_MIN = 0.3f;
    private static final float SCALE_MAX = 1f;
    private static final float MARGIN_MIN = 0f;
    private static final float MARGIN_MAX = 50f;
    private float scale;

    private float pagerMargin;
    private float spaceValue;
    private float rotationY;

    public CoverTransformer(float scale, float pagerMargin, float spaceValue, float rotationY) {
        this.scale = scale;
        this.pagerMargin = pagerMargin;
        this.spaceValue = spaceValue;
        this.rotationY = rotationY;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {


        if (rotationY != 0) {
            float realRotationY = Math.min(rotationY, Math.abs(position * rotationY));
            page.setRotationY(position < 0f ? realRotationY : -realRotationY);
        }

        if (scale != 0f) {
            float realScale = getFloat(1 - Math.abs(position * scale), SCALE_MIN, SCALE_MAX);
            page.setScaleX(realScale);
            page.setScaleY(realScale);
        }

        if (pagerMargin != 0) {

            float realPagerMargin = position * (pagerMargin);

            if (spaceValue != 0) {
                float realSpaceValue = getFloat(Math.abs(position * spaceValue), MARGIN_MIN, MARGIN_MAX);
                realPagerMargin += (position > 0) ? realSpaceValue : -realSpaceValue;
            }

            page.setTranslationX(realPagerMargin);
        }

    }

    private float getFloat(float value, float minValue, float maxValue) {
        return Math.min(maxValue, Math.max(minValue, value));
    }
}
