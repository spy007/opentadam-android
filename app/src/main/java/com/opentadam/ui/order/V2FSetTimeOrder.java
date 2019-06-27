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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFr;
import com.opentadam.view.TimeWheelView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

public class V2FSetTimeOrder extends BaseFr {

    @InjectView(R.id.timer_prev_cont)
    LinearLayout timerPrevCont;
    @InjectView(R.id.v2_title)
    TextView v2Title;
    @InjectView(R.id.cont_date)
    LinearLayout contDate;
    @InjectView(R.id.cont_city_time_zone)
    View contCityTimeZone;
    @InjectView(R.id.city_time_zone)
    TextView cityTtimeZone;
    private ArrayList<String> mDateList = new ArrayList<>();
    private ArrayList<String> mTimeList = new ArrayList<>();
    private TimeWheelView wvaDate;
    private TimeWheelView wvaTime;
    private String formattedDateIso;
    private boolean isTomorrow = false;
    private List<String> mBufTimeList;
    //  private DefSwitch switcher;
    private String dateValue;

    public V2FSetTimeOrder() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new V2FSetTimeOrder().withViewId(R.layout.f_set_time_order);
    }

    private TempObjectUIMRoute.TimeOrder getTimeOrder() {
        return Injector.getClientData().getTempObjectUIMRoute().getTimeOrder();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() == null)
            return;

        v2Title.setText(getString(R.string.title_time));

        mDateList.add(getString(R.string.current));
        mDateList.add(getString(R.string.tomorrow));
        mTimeList = getParametr();

        TempObjectUIMRoute.TimeOrder timeOrder = getTimeOrder();


        if (timeOrder == null) {
            isTomorrow = false;
        } else if (timeOrder.posTime < 0) {
            isTomorrow = false;
        } else {

            int gurrentTimePos = getGurrentTimePos();

            String posDate = timeOrder.posDate;
            if (posDate.equals(getString(R.string.tomorrow)))
                isTomorrow = true;

            if (gurrentTimePos < 1) {
                mDateList.remove(0);
                updUISetTime(mTimeList, mDateList);
            } else {
                ArrayList<String> buf = new ArrayList<>();
                for (int i = gurrentTimePos; i < mTimeList.size(); i++) {
                    buf.add(mTimeList.get(i));
                }
                updUISetTime(buf, mDateList);
            }

            wvaDate.setSeletion(posDate.equals(getString(R.string.tomorrow)) ? 1 : 0);


            wvaTime.setSeletion(timeOrder.posTime);
        }

        //   initSityTimeZone();
        contCityTimeZone.setVisibility(View.GONE);
        activateSetTime();
    }

    /*    private void initSityTimeZone() {
            long abs = Math.abs(Injector.deltaCorrectTimeDefault);
            if (//!switcher.isActive &&
                    abs > 1800000) {
                CreateRequest createRequest = getClientData().getCreateRequest();
                String startNameParent = createRequest.getStartNameParent(0);
                cityTtimeZone.setText(startNameParent);
                contCityTimeZone.setVisibility(View.VISIBLE);
            } else
                contCityTimeZone.setVisibility(View.GONE);
        }*/
    private ArrayList<String> getParametr() {
        ArrayList<String> timeList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String hyr = i < 10 ? "0" + i : "" + i;

            for (int j = 0; j < 4; j++) {
                String min = j == 0 ? "00" : "" + j * 15;
                String res = hyr + ":" + min;
                Log.e("jkkjhj", "res = " + res);
                timeList.add(res);
            }
        }
        return timeList;
    }

    private void updUISetTime(List<String> timeList, List<String> dateList) {
        mBufTimeList = timeList;
        if (getView() == null)
            return;

        if (wvaTime == null) {
            wvaTime = getView().findViewById(R.id.set_time);
            wvaTime.setOffset(1);

            wvaTime.setOnTimeWheelViewListener(new TimeWheelView.OnTimeWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {

                    getStringValueDate();
                }
            });
        }

        if (wvaDate == null) {
            wvaDate = getView().findViewById(R.id.set_date);
            wvaDate.setOffset(2);


            wvaDate.setOnTimeWheelViewListener(new TimeWheelView.OnTimeWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    wvaTime.removeAllViews();
                    wvaTime.init(wvaTime.context);
                    wvaTime.setItems(item.equals(getString(R.string.tomorrow)) ?
                            mTimeList : mBufTimeList);
                    wvaTime.setSeletion(0);
                    getStringValueDate();
                }
            });

        }


        wvaDate.setItems(dateList);
        wvaTime.setItems(isTomorrow ? mTimeList : timeList);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contDate.getLayoutParams();
        layoutParams.setMargins(0, -wvaDate.getItemHeight(), 0, 0);
        getStringValueDate();
    }

    private int getGurrentTimePos() {
        long cts = System.currentTimeMillis() + 60 * 20 * 1000; // +20min
        org.joda.time.DateTime dateTime = new DateTime(cts);


        getStringValueDate();

        int hur = dateTime.getHourOfDay();
        int min = dateTime.getMinuteOfHour();
        int max = mTimeList.size();


        for (int i = 0; i < max; i++) {


            String timeList0 = i == 0 ? "00:00" : mTimeList.get(i - 1);
            String[] arrTimeList0 = timeList0.split(":");
            int hurL0 = Integer.parseInt(arrTimeList0[0]);
            int minL0 = Integer.parseInt(arrTimeList0[1]);

            String timeList1 = mTimeList.get(i);
            String[] arrTimeList1 = timeList1.split(":");
            int minL1 = Integer.parseInt(arrTimeList1[1]);
            int hurL1 = Integer.parseInt(arrTimeList1[0]);
            if (hur == hurL0) {

                if (hurL1 == 0 && hur == 0 && min > 0 && min < 20)
                    return 0;

                if (minL1 == 0 && hur < hurL1)
                    return i;

                if (min <= minL1 && min >= minL0) {
                    return i;
                }
            }


        }

        return 0;
    }

    private void getStringValueDate() {
        long cts = System.currentTimeMillis() + 60 * 20 * 1000; // +20min
        if (wvaDate != null
                && wvaDate.getSeletedItem().equals(getString(R.string.tomorrow))) {
            cts += 85200000;// -20min;
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM")
                .withLocale(Locale.getDefault());
        dateValue = formatter.print(cts);


        DateTimeFormatter formatterISO = DateTimeFormat.forPattern("yyyy-MM-dd")
                .withLocale(Locale.ENGLISH);
        formattedDateIso = formatterISO.print(cts) + "T";

    }


    private void activateSetTime() {
        if (wvaDate == null) {
            int gurrentTimePos = getGurrentTimePos();

            if (gurrentTimePos < 1) {
                mDateList.remove(0);
                updUISetTime(mTimeList, mDateList);
            } else {
                ArrayList<String> buf = new ArrayList<>();
                for (int i = gurrentTimePos; i < mTimeList.size(); i++) {
                    buf.add(mTimeList.get(i));
                }
                updUISetTime(buf, mDateList);
            }
            wvaDate.setSeletion(0);
            wvaTime.setSeletion(0);
        }

        getStringValueDate();
    }

    @OnClick(R.id.set_time_preorder)
    public void setTimePreorder() {
        if (!isVisible() || getAWork() == null || getAWork().isFinishing())
            return;
        String time = formattedDateIso + wvaTime.getSeletedItem()
                + ":00";


        TempObjectUIMRoute.TimeOrder timeOrder = new TempObjectUIMRoute.TimeOrder(wvaTime.getSeletedIndex(),
                wvaDate.getSeletedItem(),
                wvaTime.getSeletedItem(),
                time, dateValue);


        Injector.getClientData().getTempObjectUIMRoute()
                .setTimeOrder(timeOrder);

        getAWork().showV3RestoryFRoute();

    }

    @OnClick(R.id.v2_bask)
    public void onv2Bask() {
        if (getAWork().isFinishing())
            return;
        getAWork().showV3RestoryFRoute();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV3RestoryFRoute();
        return true;
    }
}
