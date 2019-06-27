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

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusBlockSwipe;
import com.opentadam.bus.BusEnabledSwipe;
import com.opentadam.bus.BusTouchChartDown;
import com.opentadam.bus.BusTouchChartUp;
import com.opentadam.network.rest.Service;
import com.opentadam.ui.BaseFr;
import com.squareup.otto.Subscribe;

import java.lang.reflect.Field;

import butterknife.InjectView;
import butterknife.OnClick;

public class V2FReferral extends BaseFr {
    public static final int POS_BONUS = 0;
    public static final int POS_HISTORY = 1;
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.fr_heder)
    View frHeder;
    @InjectView(R.id.fref_tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.fref_swipe_container)
    SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.fref_view_pager)
    com.opentadam.ui.frends.referal.DeactivatableViewPager viewPager;
    @InjectView(R.id.fref_title)
    TextView frefTitle;
    @InjectView(R.id.fref_bonus_info)
    View frefBonusInfo;
    @InjectView(R.id.fref_cont_ui_none)
    View contUiNone;
    private Runnable runnable;
    private String lptype;

    public static Fragment newInstance(String lptype) {
        return new V2FReferral()
                .withArgument("lptype", lptype)
                .withViewId(R.layout.v2_freferral);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lptype = getArguments().getString("lptype", "none");

        if ("ext-bonus-referral-vip".equals(lptype)) {
            if (!isNewUser()) {
                initExtBonusReferalVip();

            } else {
                return;
            }

        } else {
            frHeder.getLayoutParams().height = getPX(57);
            tabLayout.setVisibility(View.GONE);
            contUiNone.setVisibility(View.VISIBLE);
            frefTitle.setText(getString(R.string.invite_friends));
            frefBonusInfo.setVisibility(View.GONE);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fref_cont_ui_none, FReferalBonusProgramm.newInstance(lptype))
                    .commitAllowingStateLoss();
        }
        if ("basic-bonus".equals(lptype)) {
            frefTitle.setText(R.string.title_basic_bonus);
            //  initSwipeContainer();
            swipeContainer.setEnabled(false);
        }

        if ("none".equals(lptype)) {
            swipeContainer.setEnabled(false);
        }
    }

    private boolean isNewUser() {
        Service service = Injector.getClientData().service;

        boolean isNoRegReferall = service.isNoRegReferall;
        if (isNoRegReferall) {
            getAWork().showFragmentNewReg();
        }


        return isNoRegReferall;
    }

    private void initExtBonusReferalVip() {
        initSwipeContainer();
        viewPager.setVisibility(View.VISIBLE);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                updCurrentUI(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        contUiNone.setVisibility(View.GONE);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fref_item_bonus));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fref_item_history));

        FrefPagerAdapter adapter = new FrefPagerAdapter(getChildFragmentManager(), lptype);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);


        setCurrentFragment(POS_BONUS);
        reflex();
    }

    private void updCurrentUI(int position, boolean isEnabledProgress) {
        FrefPagerAdapter adapter = (FrefPagerAdapter) viewPager.getAdapter();
        if (adapter != null) {
            String tag = adapter.getTagPositioh(position);
            Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(tag);
            if (fragmentByTag instanceof IPageSelected) {
                IPageSelected iPageSelected = (IPageSelected) fragmentByTag;
                iPageSelected.updateUI(isEnabledProgress);
            }


        }
    }

    private void setCurrentFragment(int pos) {
        viewPager.setCurrentItem(0);
    }

    public IPageSelected getIPageSelected(int position) {
        FrefPagerAdapter adapter = (FrefPagerAdapter) viewPager.getAdapter();
        if (adapter != null) {
            String tag = adapter.getTagPositioh(position);
            Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(tag);
            if (fragmentByTag instanceof IPageSelected) {
                return (IPageSelected) fragmentByTag;

            }


        }
        return null;
    }

    private void initSwipeContainer() {
        int end = getPX(150);
        //getAWork().getDisplayInfo() == null ? 400 : getAWork().getDisplayInfo().heightPixels / 2;
        swipeContainer.setProgressViewEndTarget(true,
                end);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if ("basic-bonus".equals(lptype)) {
                    FReferalBonusProgramm fragmentByTag = (FReferalBonusProgramm) getChildFragmentManager().findFragmentById(R.id.fref_cont_ui_none);
                    fragmentByTag.updateUI(false);
                } else {
                    updCurrentUI(viewPager.getCurrentItem(), false);
                }

            }
        });
    }

    @Subscribe
    public void onBusEnabledSwipe(BusEnabledSwipe e) {

        setEnabledSC();
    }

    private void setEnabledSC() {
        setEnabledSC(true);
    }

    @Subscribe
    public void onBusBlockSwipe(BusBlockSwipe e) {

        setEnabledSC(false);
    }

    @Subscribe
    public void onBusTouchChartDown(BusTouchChartDown e) {
        viewPager.setEnabled(false);
        setEnabledSC(true);
    }

    @Subscribe
    public void onBusTouchChartUp(BusTouchChartUp e) {

        viewPager.setEnabled(true);
        setEnabledSC(true);
    }

    public void setEnabledSC(boolean enabled) {

        swipeContainer.setEnabled(enabled);
    }

    private void reflex() {

        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        mTextView.setTypeface(Typeface.createFromAsset(tabView.getContext().getAssets()
                                , "fonts/Roboto/Roboto-Bold-Condensed.ttf"));
                        tabView.setPadding(0, 0, 0, 0);
                        mTextView.setPadding(0, 0, 0, 0);
                        int textWidth = mTextView.getWidth();
                        if (textWidth == 0) {
                            mTextView.measure(0, 0);
                            textWidth = mTextView.getMeasuredWidth();
                        }

                        int tabWidth = tabView.getWidth();
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();

                        int margin = (tabWidth - textWidth) / 2;
                        params.leftMargin = margin + getPX(3);
                        params.bottomMargin = -getPX(16);
                        params.rightMargin = margin + getPX(3);
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }
                    // changeTabsFont();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        };
        tabLayout.post(runnable);

    }

    @OnClick(R.id.frref_heder_left_menu)
    public void onBaskFr() {
        getAWork().showMenu();
    }


    @Override
    public boolean onBackPressed() {
        getAWork().showV3FRoute();
        return true;
    }

    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(runnable);
        if (swipeContainer != null && swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
        getAWork().hideProgressDevault();
        viewPager.removeAllViews();
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();
        getAWork().hideWorkProgressFull();
    }


    public void hideRefreshing() {
        swipeContainer.setRefreshing(false);
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

}
