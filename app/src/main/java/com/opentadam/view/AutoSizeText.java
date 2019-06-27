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
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.opentadam.R;


public class AutoSizeText extends android.support.v7.widget.AppCompatTextView {
    private float baze = 80;
    private int widthTextView = 200;

    public AutoSizeText(Context context) {
        super(context);

    }

    private int getPX(int DP) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (DP * scale + 0.5f);
    }

    public AutoSizeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWith(attrs);

    }

    private void initWith(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoSizeText);
        int count = typedArray.getInt(R.styleable.AutoSizeText_max_lendch_DP, 0);
        if (count != 0)
            widthTextView = getPX(count);
        typedArray.recycle();
    }

    public AutoSizeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWith(attrs);

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        baze = getTextSize();

        init();
    }

    private void init() {

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setTextSize(baze);
        // кол-во символов и их ширина
        float[] measuredWidth = new float[1];
        String text = getText().toString();
        int cnt = p.breakText(text, true, widthTextView, measuredWidth);
        float realWidth = measuredWidth[0];

        String info = "baze=" + baze +
                "width=" + widthTextView + "text=" + text
                + "cnt = " + cnt + ", realWidth = " + realWidth
                + ", maxWidth = " + getWidth();

        if (cnt == 0)
            return;
        if (cnt < text.length()) {

            setTextSize(TypedValue.COMPLEX_UNIT_PX, baze -= 1f);
            init();
        }
    }

}
