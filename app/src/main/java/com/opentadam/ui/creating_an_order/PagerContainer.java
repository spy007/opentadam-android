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

package com.opentadam.ui.creating_an_order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.opentadam.ui.creating_an_order.adapter.SmallMyPagerAdapter;
import com.opentadam.yandex_google_maps.UtilitesMaps;


public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {

    private final Point mCenter = new Point();
    private final Point mInitialTouch = new Point();
    private boolean mNeedsRedraw = false;
    private boolean isOverlapEnabled = false;
    private ViewPager mPager;


    public PagerContainer(Context context) {
        super(context);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        try {
            mPager = (ViewPager) getChildAt(0);
            mPager.addOnPageChangeListener(this);

        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public void setOverlapEnabled(boolean overlapEnabled) {
        isOverlapEnabled = overlapEnabled;
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenter.x = w / 2;
        mCenter.y = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int) ev.getX();
                mInitialTouch.y = (int) ev.getY();

                break;
            case MotionEvent.ACTION_UP:
                PagerAdapter adapter = mPager.getAdapter();
                if (mPager == null || !(adapter instanceof SmallMyPagerAdapter)) {
                    break;
                }

                SmallMyPagerAdapter smallMyPagerAdapter
                        = (SmallMyPagerAdapter) mPager.getAdapter();
                int delta = isInNonTappableRegion(getWidth()
                        , mPager.getWidth(), mInitialTouch.x, ev.getX());

                if (delta != 0) {
                    int preItem = mPager.getCurrentItem();

                    int currentItem = preItem + delta;

                    if (currentItem < 0
                            || currentItem > smallMyPagerAdapter.getCount() - 1) {
                        return mPager.dispatchTouchEvent(ev);
                    }
                    mPager.setCurrentItem(currentItem);

                    smallMyPagerAdapter.iSelectItem.onClickPosition(currentItem);
                    ViewGroup viewGroup = smallMyPagerAdapter.getViewGroup();
                    UtilitesMaps
                            .instanse()
                            .initListTariffUI(currentItem, viewGroup);

                }

                break;
        }

        return mPager.dispatchTouchEvent(ev);
    }

    private int isInNonTappableRegion(int containerWidth, int pagerWidth, float oldX, float newX) {
        int nonTappableWidth = (containerWidth - pagerWidth) / 2;
        if (oldX < nonTappableWidth && newX < nonTappableWidth) {
            return -(int) Math.ceil((nonTappableWidth - newX) / (float) pagerWidth);
        }
        nonTappableWidth = (containerWidth + pagerWidth) / 2;
        if (oldX > nonTappableWidth && newX > nonTappableWidth) {
            return (int) Math.ceil((newX - nonTappableWidth) / (float) pagerWidth);
        }
        return 0;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        if (mNeedsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(final int position) {
        if (isOverlapEnabled) {
            //Counter for loop
            int loopCounter = 0;
            int PAGER_LOOP_THRESHOLD = 2;

            //SET THE START POINT back 2 views
            if (position >= PAGER_LOOP_THRESHOLD) {
                loopCounter = position - PAGER_LOOP_THRESHOLD;
            }
            PagerAdapter adapter = mPager.getAdapter();
            if (adapter == null)
                return;

            do {
                loopCounter++;
            } while (loopCounter < position + PAGER_LOOP_THRESHOLD);
        } else if (mPager.getAdapter() != null && mPager.getAdapter() instanceof SmallMyPagerAdapter) {
            SmallMyPagerAdapter smallMyPagerAdapter
                    = (SmallMyPagerAdapter) mPager.getAdapter();
            smallMyPagerAdapter.iSelectItem.onClickPosition(position);
            ViewGroup viewGroup = smallMyPagerAdapter.getViewGroup();
            UtilitesMaps
                    .instanse()
                    .initListTariffUI(position, viewGroup);

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }


}
