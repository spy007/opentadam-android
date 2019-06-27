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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class APinFroute extends FrameLayout {
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    private final int idView[] = {R.id.fr_error_ic_servis, R.id.fr_error_ic_net, R.id.v3_marker_center_passender};
    @InjectView(R.id.v3_profress_pin)
    View profressPin;
    @InjectView(R.id.v3_profress_pin_double)
    View profressPinDouable;
    private boolean mInflated;
    private Animator animatorLoopProgressPin;
    private Animator animatorLoopProgressPinDouble;
    private boolean isVisible;
    private View passender;
    private View views[];

    public APinFroute(@NonNull Context context) {
        super(context);
    }

    public APinFroute(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public APinFroute(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mInflated || isInEditMode())
            return;
        mInflated = true;
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.apin_froute, this, false);

        ButterKnife.inject(this, view);
        this.addView(view, 0);
        isVisible = true;
    }

    private void animateLoopProgressPin() {
        if (profressPin == null || profressPinDouable == null)
            return;


        if (animatorLoopProgressPin != null) {
            animatorLoopProgressPin.cancel();
        }


        if (animatorLoopProgressPinDouble != null) {
            animatorLoopProgressPinDouble.cancel();
        }

        animatorLoopProgressPin = AnimatorInflater.loadAnimator(getContext()
                , R.animator.fr_progress_ping);

        animatorLoopProgressPin.setTarget(profressPin);
        animatorLoopProgressPinDouble = AnimatorInflater.loadAnimator(getContext()
                , R.animator.fr_progress_ping_double);


        animatorLoopProgressPin.start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVisible || animatorLoopProgressPinDouble == null)
                    return;

                animatorLoopProgressPinDouble.setTarget(profressPinDouable);
                animatorLoopProgressPinDouble.start();
            }
        }, 250);

    }

    private void showPassender() {
        if (passender == null) {
            return;
        }
/*
         if (views == null) {
            views = getViews();
             for (View v : views) {
                 if(passender.getId() != v.getId())
                 v.setVisibility(GONE);
             }
         }
*/


        passender.setVisibility(View.VISIBLE);
    }

    private View[] getViews() {
        int length = idView.length;
        View[] view = new View[length];
        for (int i = 0; i < length; i++) {
            view[i] = getRootView().findViewById(idView[i]);
        }

        return view;
    }

    private void hidePassender() {
        if (passender == null) {
            return;
        }
/*        if (views == null) {
            views = getViews();
            for (View v : views) {
                if(passender.getId() != v.getId())
                    v.setVisibility(GONE);
            }
        }*/
        passender.setVisibility(GONE);
    }

    public void animateShowProgressPin(View val) {
        if (passender == null) {
            passender = val;
        }

        hidePassender();

        if (profressPin == null)
            return;

        if (animatorLoopProgressPin == null)
            animateLoopProgressPin();


        profressPin.setVisibility(View.VISIBLE);
        profressPinDouable.setVisibility(View.VISIBLE);


    }

    public void animateStopProgressPin() {
        showPassender();
        if (profressPin == null || profressPinDouable == null)
            return;

        profressPinDouable.setVisibility(View.GONE);
        profressPin.setVisibility(View.GONE);


    }


    public void finish() {
        if (animatorLoopProgressPin != null) {
            animatorLoopProgressPin.cancel();
        }

        if (animatorLoopProgressPinDouble != null) {
            animatorLoopProgressPinDouble.cancel();
        }
        isVisible = false;
        ButterKnife.reset(this);
    }
}
