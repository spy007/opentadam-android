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
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class SubcategoryScrollView extends HorizontalScrollView {

    private int leftDelta = 0;

    public SubcategoryScrollView(Context context) {
        super(context);
    }

    public SubcategoryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubcategoryScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getLeftDelta() {
        return leftDelta;
    }

    private void setLeftDelta(int l) {
        this.leftDelta = l;
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        setLeftDelta(l);
        super.onScrollChanged(l, t, oldl, oldt);
    }


}
