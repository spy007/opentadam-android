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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.opentadam.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DefSwitch extends FrameLayout {
    public boolean isActive;
    @InjectView(R.id.switcher_background)
    ImageView switcherBackground;
    @InjectView(R.id.switcher_foreground_off)
    ImageView switcherForegroundOff;
    @InjectView(R.id.switcher_foreground_on)
    ImageView switcherForegroundOn;
    private boolean mInflated = false;


    public DefSwitch(Context context) {
        super(context);
    }

    public DefSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mInflated || isInEditMode())
            return;
        mInflated = true;
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.radar_big_switcher, this, false);

        ButterKnife.inject(this, view);
        this.addView(view, 0);

    }

    public void setActive(boolean a) {
        isActive = a;
        updateView();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateView();
    }

    private void updateView() {
        switcherForegroundOff.setVisibility(isActive ? GONE : VISIBLE);
        switcherForegroundOn.setVisibility(!isActive ? GONE : VISIBLE);


        int bgRadarAreaDisabled = R.drawable.bg_radar_area_disabled;
        if (!isActive)
            switcherBackground.setImageResource(bgRadarAreaDisabled);
        else {

            int bgRadarAreaEnabledDay = R.drawable.bg_radar_area_enabled_day;
            switcherBackground.setImageResource(isEnabled() ? bgRadarAreaEnabledDay
                    : bgRadarAreaDisabled);
        }
    }
}



