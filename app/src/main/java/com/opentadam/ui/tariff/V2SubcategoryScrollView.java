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

package com.opentadam.ui.tariff;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import java.lang.reflect.Field;


public class V2SubcategoryScrollView extends HorizontalScrollView {

    private OverScroller mScroller;
    private int displayWidth = 0;
    private IScroll iScroll;
    private int scrollX = 0;
    private int scrollXTemp = 0;
    private boolean isBBBBBBB = false;
    private boolean sw;

    private int scrollXStart;


    public V2SubcategoryScrollView(Context context) {
        super(context);
    }

    public V2SubcategoryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public V2SubcategoryScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init() {
        try {
            Class parent = this.getClass();
            do {
                parent = parent.getSuperclass();
            } while (!parent.getName().equals("android.widget.HorizontalScrollView"));

            Log.i("Scroller", "class: " + parent.getName());
            Field field = parent.getDeclaredField("mScroller");
            field.setAccessible(true);
            mScroller = (OverScroller) field.get(this);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void customSmoothScrollBy(int dx) {
        if (mScroller == null)
            init();

        if (getChildCount() == 0)
            return;

        final int width = getWidth() - getPaddingRight() - getPaddingLeft();
        final int right = getChildAt(0).getWidth();
        final int maxX = Math.max(0, right - width);
        final int scrollX = getScrollX();
        dx = Math.max(0, Math.min(scrollX + dx, maxX)) - scrollX;

        mScroller.startScroll(scrollX, getScrollY(), dx, 0, 800);
        invalidate();
    }

    private void customSmoothScrollTo(int x) {
        customSmoothScrollBy(x - getScrollX());
    }

    public void setIScroll(IScroll iScroll) {
        this.iScroll = iScroll;
    }


    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);

        this.scrollX = scrollX;
        sw = scrollXTemp < scrollX;
        scrollXTemp = scrollX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {

            ViewGroup parent = (ViewGroup) getChildAt(0);

            mScroller.forceFinished(true);
            mScroller.abortAnimation();


            if (Math.abs(scrollXStart - scrollX) < 20) {
                iScroll.closePopup();
                return true;
            }

            if (!isBBBBBBB) {
                isBBBBBBB = true;

                iScroll.onDrawFinish(getPos(parent));
            }

            return false;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            if (mScroller == null)
                init();
            isBBBBBBB = false;
            scrollXStart = scrollX;

        }
        return super.onTouchEvent(ev);
    }


    private int getPos(ViewGroup parent) {
        int pos = 0;
        boolean isPos = false;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int left = view.getLeft();


            if (sw) {
                if (scrollX <= left && !isPos) {
                    isPos = true;
                    pos = i;

                    customSmoothScrollTo(i * view.getWidth());

                }
            } else {
                if (scrollX <= left + displayWidth * 0.7 && !isPos) {
                    isPos = true;
                    pos = i;

                    customSmoothScrollTo(i * view.getWidth());

                }
            }
        }


        return pos;
    }

    public void setDisplayWidth(int display_width) {
        this.displayWidth = display_width;
    }

}
