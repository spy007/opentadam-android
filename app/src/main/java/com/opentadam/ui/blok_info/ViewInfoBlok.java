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

package com.opentadam.ui.blok_info;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.utils.Utilites;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ViewInfoBlok extends FrameLayout {
    private final WeakHandler mHandler = new WeakHandler();
    @InjectView(R.id.ibl_top)
    View viewIblTop;
    @InjectView(R.id.ibl_left)
    View viewIblLeft;
    @InjectView(R.id.ibv_text_info)
    TextView textInfo;
    @InjectView(R.id.blok_transp)
    View blTransp;
    /*
        protected void onFinishInflate() {
            super.onFinishInflate();
            if (mInflated || isInEditMode())
                return;
            mInflated = true;
            final View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.info_blok_view, this, false);

            ButterKnife.inject(this, view);
            this.addView(view, 0);

        }*/
/*    @InjectView(R.id.ibv_v5)
    View ibvV5;*/
    @InjectView(R.id.ibv_ic_point)
    View ibvIcPoint;
    /*    @InjectView(R.id.view_anim)
        View viewAnim;*/
    private int countStep;
    private IGo iGo;
    private ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;
    private ArrayList<ODataInfoblok> oDataInfobloks;
    private int currentPos;
    private boolean isBlockAnime;


    public ViewInfoBlok(@NonNull Context context) {
        super(context);
    }


    public ViewInfoBlok(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewInfoBlok(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

/*    private void animView() {
        if (viewPropertyAnimatorCompat != null)
            viewPropertyAnimatorCompat.cancel();
        viewAnim.clearAnimation();
        viewAnim.setScaleX(1.0f);
        viewAnim.setScaleY(1.0f);
        viewAnim.setAlpha(1.0f);

        viewPropertyAnimatorCompat = ViewCompat.animate(viewAnim).alpha(0.0f)
                .scaleX(1.25f)
                .scaleY(1.25f)
                .setDuration(1000).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (!isBlockAnime)
                            animView();
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                });


    }*/

    public void deleteInfoBlok() {

        if (viewPropertyAnimatorCompat != null)
            viewPropertyAnimatorCompat.cancel();
        this.removeAllViews();
        this.setVisibility(GONE);
    }

    @OnClick(R.id.ibv_body)
    public void onBodyClick() {

        if (currentPos == oDataInfobloks.size() - 1) {

            ViewCompat.animate(this).alpha(0.0f)

                    .setDuration(600).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    Injector.getSettingsStore().setDefHashVerCode();
                    deleteInfoBlok();
                    iGo.showDefaultPage();
                            /*        if(iGo != null) {
            iGo.setReversButton();
        }*/
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();

        } else {
            currentPos++;
            initPlay(currentPos);
        }
    }

    public void play(final ArrayList<ODataInfoblok> val, final int pos, IGo iGo) {
        this.iGo = iGo;
        this.oDataInfobloks = val;
        deleteInfoBlok();


        if (oDataInfobloks == null)
            return;

        View inflate = LayoutInflater.from(getContext())
                .inflate(R.layout.info_blok_view, this, false);

        ButterKnife.inject(this, inflate);
        this.addView(inflate, 0);
        final ODataInfoblok oDataInfoblok = oDataInfobloks.get(0);
        final View view = oDataInfoblok.getView();
        view.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Ensure you call it only once :
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        for (ODataInfoblok dataInfoblok : oDataInfobloks) {
                            int[] loc = new int[2];
                            dataInfoblok.getView().getLocationOnScreen(loc);
                            dataInfoblok.leftView = loc[0];
                            dataInfoblok.topView = loc[1];
                        }
                        initPlay(pos);

                        //    animView();

                    }
                });


    }

    private void initPlay(int pos) {
        bodyChangePag(pos);

    }

    private void bodyChangePag(int pos) {
        currentPos = pos;

        final ODataInfoblok dataInfoblok = oDataInfobloks.get(pos);
        View view = dataInfoblok.getView();
        int hView = view.getLayoutParams().height;
        int wView = view.getLayoutParams().width;

        // по вертикали
        int hBlTransp = blTransp.getLayoutParams().height;
        int a = 24; // толбар
        int b = hBlTransp / 2;// половина дырки
        int c = hView / 2; // половина вьюхи

        countStep = 10;
        // по вертикали
        int height = dataInfoblok.getTopView()
                - Utilites.getDPtoPX(a) - b + c;
        if (pos == 1)
            height -= Utilites.getDPtoPX(4);


        // по горизонтали
        final int width = dataInfoblok.getLeftView()
                - blTransp.getLayoutParams().width / 2 + wView / 2;
        // для текста
        int widthInfoViewDP = dataInfoblok.getWidthInfoViewDP();
        final int dPtoPX = Utilites.getDPtoPX(widthInfoViewDP);

        if (pos != 0) {

            int heightTop = viewIblTop.getLayoutParams().height;
            int widthLeft = viewIblLeft.getLayoutParams().width;
            int deltaH = height - heightTop;
            int deltaW = width - widthLeft;
            deltaH = (deltaH < 0 ? -1 : 1) * deltaH / countStep;
            deltaW = (deltaW > 0 ? -1 : 1) * deltaW / countStep;


            handlerAnim(deltaH, deltaW, height, width);

            textInfo.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setParametrTextInfo(dataInfoblok, dPtoPX);
                            textInfo.requestLayout();
                            textInfo.animate()
                                    .alpha(1.0f)
                                    .setDuration(300)
                                    .setInterpolator(new FastOutSlowInInterpolator());
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
            //    viewIblTop.requestLayout();
            //    viewIblLeft.requestLayout();
            //    textInfo.requestLayout();

            return;
        }

        viewIblTop.getLayoutParams().height = height;
        viewIblLeft.getLayoutParams().width = width;

        setParametrTextInfo(dataInfoblok, dPtoPX);
        ViewInfoBlok.this.setVisibility(VISIBLE);
    }

    private void setParametrTextInfo(ODataInfoblok dataInfoblok, int dPtoPX) {
        textInfo.getLayoutParams().width = dPtoPX;
        textInfo.setText(dataInfoblok.getTextInfo());
    }

    private void handlerAnim(final int finalDeltaH, final int deltaW,
                             final int height, final int width) {

        int delHand = 10;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (countStep == 0) {
                    viewIblTop.getLayoutParams().height
                            = height;// - Utilites.getDPtoPX(4);
                    viewIblLeft.getLayoutParams().width =
                            width;


                } else {

                    viewIblTop.getLayoutParams().height
                            = viewIblTop.getLayoutParams().height + finalDeltaH;

                    viewIblLeft.getLayoutParams().width =
                            viewIblLeft.getLayoutParams().width + deltaW;
                }

                viewIblTop.requestLayout();
                viewIblLeft.requestLayout();

                if (countStep > 0) {
                    handlerAnim(finalDeltaH, deltaW, height, width);
                }
                countStep--;
            }
        }, delHand);
    }
}

