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

package com.opentadam.ui.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.ui.BaseFr;
import com.opentadam.view.SubcategoryScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTouch;

public class FSetProlongation extends BaseFr {
    private final int COUNT = 14;
    @InjectView(R.id.cont_sections)
    LinearLayout contSections;
    @InjectView(R.id.hscroll)
    SubcategoryScrollView hscroll;
    private int pos = 2;
    private boolean activateUnit = false;
    private long idRoute;

    public FSetProlongation() {

    }

    public void setIdRoute(long idRoute) {
        this.idRoute = idRoute;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_set_prolongation, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @OnTouch(R.id.hscroll)
    public boolean onTouchScroll(View v, MotionEvent event) {
        return bodyOnToush(event);
    }


    private boolean bodyOnToush(MotionEvent event) {
        ViewGroup parent = (ViewGroup) hscroll.getChildAt(0);

        int screenWidth = getAWork().getWindowManager()
                .getDefaultDisplay().getWidth();

        if (event.getAction() == MotionEvent.ACTION_UP) {

            int leftDelta = hscroll.getLeftDelta();

            int scrollX = 0;
            boolean isSelected = false;
            for (int i = 0; i < COUNT; i++) {
                if (i == 0 || i == COUNT - 1)
                    continue;
                View view = parent.getChildAt(i);
                TextView prolongationValue = view.findViewById(R.id.f_set_prolongation_value);
                TextView prolongationMinute = view.findViewById(R.id.f_set_prolongation_minute);
                TextView prolongationValueAdd = view.findViewById(R.id.f_set_prolongation_value_add);
                LinearLayout prolongationCont = view.findViewById(R.id.f_set_prolongation_cont);

                if (leftDelta <= view.getLeft() && !isSelected) {
                    //       getAWork().setTariffClient(i - 1);
                    isSelected = true;
                    pos = i;
                    // разрешаем клик только по красной
                    onClickPos(view);

                    ativateMenuTariff(prolongationValue, prolongationMinute,
                            prolongationValueAdd, prolongationCont);

                    scrollX = (view.getLeft() - (screenWidth / 2))
                            + (view.getWidth() / 2);


                } else {
                    deactivateMenuTariff(prolongationValue, prolongationMinute,
                            prolongationValueAdd, prolongationCont);
                }

            }

            if (!isSelected) {
                View view = parent.getChildAt(COUNT - 2);
                TextView prolongationValue = view.findViewById(R.id.f_set_prolongation_value);
                TextView prolongationMinute = view.findViewById(R.id.f_set_prolongation_minute);
                TextView prolongationValueAdd = view.findViewById(R.id.f_set_prolongation_value_add);
                LinearLayout prolongationCont = view.findViewById(R.id.f_set_prolongation_cont);
                pos = COUNT - 2;


                ativateMenuTariff(prolongationValue, prolongationMinute,
                        prolongationValueAdd, prolongationCont);

                scrollX = (view.getLeft() - (screenWidth / 2))
                        + (view.getWidth() / 2);

            }

            hscroll.smoothScrollTo(scrollX, 0);


            return true;
        }

        return false;
    }

    private void onClickPos(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAWork().sendProlongation(pos, idRoute);
            }
        });
    }


    @SuppressLint("DefaultLocale")
    @SuppressWarnings("deprecation")
    public void initUi() {

        contSections.removeAllViews();
        for (int i = 0; i < COUNT; i++) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(contSections.getContext())
                    .inflate(R.layout.section_prolongation, contSections, false);

            TextView prolongationValue = view.findViewById(R.id.f_set_prolongation_value);
            TextView prolongationMinute = view.findViewById(R.id.f_set_prolongation_minute);
            TextView prolongationValueAdd = view.findViewById(R.id.f_set_prolongation_value_add);
            LinearLayout prolongationCont = view.findViewById(R.id.f_set_prolongation_cont);

            if (i == 0 || i == COUNT - 1)
                view.setVisibility(View.INVISIBLE);
            if (i == pos) {
                onClickPos(view);
                ativateMenuTariff(prolongationValue, prolongationMinute,
                        prolongationValueAdd, prolongationCont);
            } else {
                deactivateMenuTariff(prolongationValue, prolongationMinute,
                        prolongationValueAdd, prolongationCont);

            }

            prolongationValue.setText(String.format("%d", 5 * i));

            contSections.addView(view);
        }
        setCenterInit(pos);

    }

    private void deactivateMenuTariff(TextView prolongationValue, TextView prolongationMinute,
                                      TextView prolongationValueAdd, LinearLayout prolongationCont) {
        prolongationCont.setBackgroundResource(R.drawable.bg_f_prolongation);
        prolongationMinute.setTextColor(initColor(R.color.text_color));
        prolongationValue.setTextColor(initColor(R.color.text_color));
        prolongationValueAdd.setTextColor(initColor(R.color.text_color));
    }

    private void ativateMenuTariff(TextView prolongationValue, TextView prolongationMinute,
                                   TextView prolongationValueAdd, LinearLayout prolongationCont) {
        prolongationCont.setBackgroundResource(R.drawable.bg_f_prolongation_primary);
        prolongationMinute.setTextColor(initColor(R.color.colorPrimary));
        prolongationValue.setTextColor(initColor(R.color.colorPrimary));
        prolongationValueAdd.setTextColor(initColor(R.color.colorPrimary));

    }

    private void setCenterInit(final int index) {

        final View content = getAWork().findViewById(Window.ID_ANDROID_CONTENT);
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeGOLListener(content.getViewTreeObserver(), this);
                if (activateUnit)
                    return;
                activateUnit = true;
                setCenter(index);

            }
        });


    }

    @SuppressWarnings("deprecation")
    private void setCenter(int index) {
        if (!isVisible())
            return;
        ViewGroup parent = (ViewGroup) hscroll.getChildAt(0);

        final View view = parent.getChildAt(index);
        int screenWidth = getAWork().getWindowManager()
                .getDefaultDisplay().getWidth();

        int scrollX = (view.getLeft() - (screenWidth / 2))
                + (view.getWidth() / 2);
        hscroll.smoothScrollTo(scrollX, 0);

    }


    public int getPosProlongation() {

        return pos;
    }
}
