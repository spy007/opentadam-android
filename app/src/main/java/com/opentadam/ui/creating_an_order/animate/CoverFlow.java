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

import android.support.v4.view.ViewPager;

public class CoverFlow {

    //  public static final float DEFAULT_SCALE_VALUE  = 0.3f;

    //  public static final float DEFAULT_PAGER_MARGIN = 0f;

    //   public static final float DEFAULT_SPACE_SIZE   = 0f;

    private CoverFlow(CoverFlow.Builder builder) {

        if (null == builder) {
            throw new IllegalArgumentException("A non-null CoverFlow.Builde must be provided");
        }

        ViewPager viewPager = builder.viewPager;

        float scaleValue = builder.scaleValue;
        float pagerMargin = builder.pagerMargin;
        float spaceSize = builder.spaceSize;
        float rotationY = builder.rotationY;

        if (viewPager != null) {
            viewPager.setPageTransformer(false,
                    new CoverTransformer(scaleValue, pagerMargin, spaceSize, rotationY));
        }


    }

    public static class Builder {
        private ViewPager viewPager;

        private float scaleValue;
        private float pagerMargin;
        private float spaceSize;
        private float rotationY;

        public CoverFlow.Builder with(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }


        public CoverFlow.Builder scale(float scaleValue) {
            this.scaleValue = scaleValue;
            return this;
        }

        public CoverFlow.Builder pagerMargin(float pagerMargin) {
            this.pagerMargin = pagerMargin;
            return this;
        }

        public CoverFlow.Builder spaceSize(float spaceSize) {
            this.spaceSize = spaceSize;
            return this;
        }

        public CoverFlow.Builder rotationY(float rotationY) {
            this.rotationY = rotationY;
            return this;
        }

        public CoverFlow build() {
            return new CoverFlow(this);

        }
    }
}
