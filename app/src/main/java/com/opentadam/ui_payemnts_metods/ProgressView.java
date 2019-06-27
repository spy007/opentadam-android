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

package com.opentadam.ui_payemnts_metods;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.R;
import com.opentadam.utils.TintIcons;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProgressView extends FrameLayout {
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.image_progress_full)
    ImageView imageProgressFull;

    public ProgressView(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.progress_app, this);
        ButterKnife.inject(this);
        this.setVisibility(GONE);
        TintIcons.tintImageViewBrend(imageProgressFull);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initAnimateProgress();
            }
        });
    }

    private void initAnimateProgress() {
        Animation animProgress = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_center);
        animProgress.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_center);
                anim.setAnimationListener(this);
                imageProgressFull.startAnimation(anim);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        imageProgressFull.startAnimation(animProgress);
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

}
