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

package com.opentadam.ui.frends.referal;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.bus.BusBlockSwipe;
import com.opentadam.bus.BusEnabledSwipe;

public class DeactivatableViewPager extends ViewPager {


    public DeactivatableViewPager(Context context) {
        super(context);
    }

    public DeactivatableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float startX;
    private float startY;
    private boolean isPost;

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = Injector.getAppContext().getResources().getDisplayMetrics();

        return (int) ((px / displayMetrics.density) + 0.5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                isPost = false;
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE: // движение
                if (Math.abs(startX - event.getX()) > pxToDp(50)
                        && Math.abs(startY - event.getY()) < pxToDp(50) && !isPost) {
                    isPost = true;
                    App.bus.post(new BusBlockSwipe());
                }

                break;
            case MotionEvent.ACTION_UP: // отпускание
                App.bus.post(new BusEnabledSwipe());

                break;
        }


        return !isEnabled() || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
/*        if(event.getAction() == MotionEvent.ACTION_UP){
            // отпускание
            App.bus.post(new BusEnabledSwipe());
        }

        boolean b = isEnabled() && super.onInterceptTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN && b){
            App.bus.post(new BusBlockSwipe());
        }*/
        return isEnabled() && super.onInterceptTouchEvent(event);
    }

}
