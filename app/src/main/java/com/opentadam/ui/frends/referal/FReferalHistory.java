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

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusTouchChartDown;
import com.opentadam.bus.BusTouchChartUp;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.TranslateSumm;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class FReferalHistory extends BaseFr implements IPageSelected {
    public static final int COUNT_DAY_DEFAULT = 30;
    private final int NUMBER_OF_POINTS = 30;
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.fref_hist_graf)
    LinearLayout histGraf;
    @InjectView(R.id.fref_hist_cont_list)
    LinearLayout histContList;
    @InjectView(R.id.fref_hist_graf_plus)
    FrameLayout histGrafPlus;
    @InjectView(R.id.fref_hist_graf_minus)
    FrameLayout histGrafMinus;
    @InjectView(R.id.fref_hist_title_list)
    TextView titleList;
    @InjectView(R.id.frbh_send)
    View frbhSend;
    @InjectView(R.id.fref_hist_perid)
    TextView periodInfoHistory;
    @InjectView(R.id.hist_popup)
    View histPopup;
    @InjectView(R.id.fref_hist_popup_data)
    TextView popupData;
    @InjectView(R.id.fref_hist_popup_rub)
    TextView popupRub;
    @InjectView(R.id.fref_hist_popup_kop)
    TextView popupKop;

    @InjectView(R.id.fref_hist_minus_popup_rub)
    TextView popupRubMinus;
    @InjectView(R.id.fref_hist_minus_popup_kop)
    TextView popupKopMinus;

    @InjectView(R.id.frb_empty_page)
    View frbEmptyPage;

    @InjectView(R.id.frh_scroll)
    ScrollView frhScroll;

    @InjectView(R.id.frb_body_history)
    View body;

    private ArrayList<ResultDayStat> loyaltyProgramByDay;
    private BigDecimal maxValueAddBunus;
    private Float[] randomNumbersTab;
    private ArrayList<Float[]> coordPoint;
    private int segment = -1;
    private float hashX;
    private float heightGraf;
    private ArrayList<Float[]> coordPopup;
    private String lptype;
    private boolean enabledSC = true;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;

    public static Fragment newInstance(String lptype) {
        return new FReferalHistory()
                .withArgument("lptype", lptype)
                .withViewId(R.layout.f_referral_bhistory);
    }

    @Override
    public boolean isEnabledSC() {
        return enabledSC;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lptype = getArguments().getString("lptype", "none");
        histPopup.setBackgroundResource(R.drawable.fref_popover_center);

        onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (frhScroll == null) {
                    return;
                }


                V2FReferral v2FReferral = (V2FReferral) getParentFragment();
                if (v2FReferral != null && v2FReferral.getCurrentItem() == V2FReferral.POS_HISTORY) {
                    enabledSC = frhScroll.getScrollY() <= 0;
                    v2FReferral.setEnabledSC(enabledSC);

                }

            }
        };
        frhScroll.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
    }

    @Override
    public void updateUI(boolean isEnabledProgress) {
        hidehistPopup();

        switch (lptype) {
            case "none":

                break;
            case "basic-bonus":

            case "ext-bonus-referral-vip":
               // showProgressRest();
                getLoyaltyProgramByDay();
                getLoyaltyProgramByDayList();
        }
    }

    @OnClick(R.id.hist_popup)
    public void hidehistPopup() {
        App.bus.post(new BusTouchChartUp());
        histPopup.setVisibility(View.GONE);
    }

    private void showhistPopup() {
        histPopup.setVisibility(View.VISIBLE);
    }

    @OnTouch(R.id.fref_hist_graf_control)
    public boolean onTouchHistGraf(View view, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        int widthPointHistory = histGrafPlus.getWidth() /
                NUMBER_OF_POINTS;

        float width = histGrafPlus.getWidth() - widthPointHistory;
        float widthSegment = width / (NUMBER_OF_POINTS - 1);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие

                App.bus.post(new BusTouchChartDown());
                segment = -1;
                getSegment(x, widthSegment, width, y);
                break;
            case MotionEvent.ACTION_MOVE: // движение

                getSegment(x, widthSegment, width, y);
                break;
            case MotionEvent.ACTION_UP: // отпускание

                App.bus.post(new BusTouchChartUp());

                break;
        }

        return true;

    }

    private void getLoyaltyProgramByDayList() {
        final RESTConnect restConnect = Injector.getRC();

/*        if (restConnect == null) {
            hideswipe();
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.getLoyaltyProgramByDayList(Injector.getClientData().getMarkerLocation(),
                null, new IGetApiResponse() {


                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;
                        hideswipe();
                        if (apiResponse == null || apiResponse.error != null) {
                            getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        ArrayList<Transaction> transaction = apiResponse.transaction;
                        if (transaction == null || transaction.size() == 0) {
                            errLoadData();
                            return;
                        }

                        int size = transaction.size();
                        if (size > 1) {

                            Comparator<Transaction> loyaltyProgramByDayComparator = new Comparator<Transaction>() {
                                @Override
                                public int compare(Transaction o1, Transaction o2) {
                                    if (o1.getDateLong() == o2.getDateLong()) return 0;
                                    else if (o1.getDateLong() > o2.getDateLong()) return -1;
                                    else return 1;
                                }
                            };

                            Collections.sort(transaction, loyaltyProgramByDayComparator);
                        }
                        histContList.removeAllViews();

                        initTitleList(size);

                        for (Transaction tr : transaction) {

                            String amoutString = tr.amount.setScale(2, RoundingMode.HALF_UP).toString();
                            TranslateSumm translateAmoutString = TranslateSumm.invoke(amoutString, true);

                            View view = LayoutInflater.from(histContList.getContext())
                                    .inflate(R.layout.pf_point_history_list_item, histContList, false);

                            TextView pfRub = view.findViewById(R.id.pf_rub);
                            TextView pfKop = view.findViewById(R.id.pf_kop);

                            //     TextView pfCurrency = view.findViewById(R.id.pf_currency);
                            //     pfCurrency.setText(Injector.getWorkSettings().currency.sign);
                            pfRub.setText(getStringIntejerFormat(translateAmoutString.getRub()));
                            pfKop.setText(translateAmoutString.getKop());

                            FrameLayout plImageCont = view.findViewById(R.id.pl_image_cont);
                            ImageView plImage = view.findViewById(R.id.pl_image);
                            LinearLayout pfValyeSumm = view.findViewById(R.id.pf_valye_summ);

                            TextView plPeriod = view.findViewById(R.id.fp_hist_perid);
                            plPeriod.setText(getValueItemData(tr.date));
                            TextView plText = view.findViewById(R.id.pl_text);
                            plText.setText(tr.getValStringTyp());

                            ImageView pfIcon = view.findViewById(R.id.pf_icon);
                            ///////

                            if (tr.typ != 23) {
                                pfValyeSumm.setBackgroundResource(R.drawable.pf_valye_summ_add);
                                plImage.setImageDrawable(initDrawable(R.drawable.ic_pf_own_bonus));
                                plImageCont.setBackgroundResource(R.drawable.pf_bg_item_plus);

                                int parseColor = Color.parseColor("#3FD8A4");
                                TintIcons.tintImageViewOther(pfIcon, R.color.fref_bg_list_add);
                                pfRub.setTextColor(parseColor);
                                pfKop.setTextColor(parseColor);
                            } else {
                                pfValyeSumm.setBackgroundResource(R.drawable.pf_valye_summ_minus);
                                plImage.setImageDrawable(initDrawable(R.drawable.ic_pf_spisanie));

                                int parseColor = Color.parseColor("#EB5757");
                                TintIcons.tintImageViewOther(pfIcon, R.color.fref_bg_list_minus);
                                pfRub.setTextColor(parseColor);
                                pfKop.setTextColor(parseColor);

                                plImageCont.setBackgroundResource(R.drawable.fl_bg_item_minus);
                            }

                            histContList.addView(view);
                        }

                    }
                });
    }

    private void initTitleList(int size) {
        titleList.setText(String
                .format(getString(R.string.fref_history_title_list), size));
        if (size > 0) {
            titleList.setVisibility(View.VISIBLE);
        }

        if (size >= 30) {
            frbhSend.setVisibility(View.VISIBLE);
        }
    }

    private void getLoyaltyProgramByDay() {
        final RESTConnect restConnect = Injector.getRC();

/*        if (restConnect == null) {
            hideswipe();
            getAWork().showErrorNetDialog();
            return;
        }*/

        restConnect.getLoyaltyProgramByDay(Injector.getClientData().getMarkerLocation(), new IGetApiResponse() {


            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;
                hideswipe();
                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                ArrayList<ResultDayStat> lp = apiResponse.loyaltyProgramByDay;
                if (lp == null) {
                    errLoadData();
                    return;
                }

                int size = lp.size();

                if (size > 1) {

                    Comparator<ResultDayStat> loyaltyProgramByDayComparator = new Comparator<ResultDayStat>() {
                        @Override
                        public int compare(ResultDayStat o1, ResultDayStat o2) {
                            if (o1.getDateLong() == o2.getDateLong()) return 0;
                            else if (o1.getDateLong() > o2.getDateLong()) return 1;
                            else return -1;
                        }
                    };
                    Collections.sort(lp, loyaltyProgramByDayComparator);
                }
                initData(lp);
                initUIObserver(lp);

                hideProgressRest();
                //  getAWork().hideProgressDevault();
            }
        });
    }

    private void showProgressRest() {
     //   progressRest.setVisibility(View.VISIBLE);
        body.setVisibility(View.GONE);
    }

    private void hideProgressRest() {
      //  progressRest.setVisibility(View.GONE);
        body.setVisibility(View.VISIBLE);
    }

    private void initData(ArrayList<ResultDayStat> lp) {
        maxValueAddBunus = BigDecimal.ZERO;
        for (ResultDayStat rds : lp) {

            BigDecimal plusBalance = rds.plusBalance;
            BigDecimal plusBalanceRef = rds.plusBalanceRef;
            BigDecimal minusBalance = rds.minusBalance;


            rds.plusBalanceAll = plusBalance.add(plusBalanceRef);
            if (rds.plusBalanceAll.compareTo(maxValueAddBunus) > 0) {
                maxValueAddBunus = plusBalance;
            }


            if (minusBalance.compareTo(maxValueAddBunus) > 0) {
                maxValueAddBunus = minusBalance;
            }


        }
    }

    private void errLoadData() {
        frbEmptyPage.setVisibility(View.VISIBLE);
        //  alert(message);

    }

    private void getSegment(float x, float widthSegment, float width, float y) {

        if (loyaltyProgramByDay == null
                || Math.abs(hashX - x) < widthSegment / 2.0f)
            return;

        for (int i = 0; i < NUMBER_OF_POINTS; i++) {


            hashX = widthSegment * i;
            if (Math.abs(hashX - x) < widthSegment && segment != i) {
                boolean isGrafPlus = heightGraf > y;
                segment = i;
                ResultDayStat resultDayStat = loyaltyProgramByDay.get(segment);
                if (resultDayStat == null) {
                    hidehistPopup();
                    return;
                }
                //
                Float[] floats = coordPopup.get(segment);

                float defX = floats[0];
                float defY = floats[1];
                // управление вертикаль

                histPopup.setY(isGrafPlus ? defY : heightGraf);

                float centerDefX = defX + widthSegment / 2.0f;
                if (segment == 0 || (float) getPX(10)
                        - defX > 0) {

                    // левый
                    histPopup.setBackgroundResource(R.drawable.fref_popover_left);
                    histPopup.setX(centerDefX);

                } else if (width
                        - (float) getPX(24) - defX < 0) {
                    // правый

                    histPopup.setBackgroundResource(R.drawable.fref_popover_right);
                    histPopup.setX(defX - getPX(159) + widthSegment / 2.0f);

                } else {

                    histPopup.setBackgroundResource(R.drawable.fref_popover_center);
                    histPopup.setX(defX - getPX(159 / 2) + widthSegment / 2.0f);
                }
                initDataPopup();
                showhistPopup();

                break;
            }


        }
    }

    private void initDataPopup() {
        ResultDayStat resultDayStat = loyaltyProgramByDay.get(segment);
        TranslateSumm translateSummAdd = TranslateSumm.invoke(resultDayStat
                .plusBalanceAll.toString(), true);


        popupRub.setText(getStringIntejerFormat(translateSummAdd.getRub()));
        if (!(translateSummAdd.getRub() + translateSummAdd.getKop()).equals("0,0")) {
            popupKop.setText(translateSummAdd.getKop());
        }
        //    popupCurrency.setText(Injector.getWorkSettings().currency.sign);


        TranslateSumm translateSummMinus = TranslateSumm.invoke(resultDayStat
                .minusBalance.toString(), true);

        popupRubMinus.setText(getStringIntejerFormat(translateSummMinus.getRub()));
        if (!(translateSummMinus.getRub() + translateSummMinus.getKop()).equals("0,0")) {
            popupKopMinus.setText(translateSummMinus.getKop());
        }
        //     popupCurrencyMinus.setText(Injector.getWorkSettings().currency.sign);

        popupData.setText(resultDayStat.getDatePoup());

    }

    private String getValueItemData(String date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatterTransaction = DateTimeFormat.forPattern("d MMMM")
                .withLocale(Locale.getDefault());
        DateTimeFormatter formatterTransactionMinutes = DateTimeFormat.forPattern("HH:mm")
                .withLocale(Locale.getDefault());
        DateTime dateTime = new DateTime(date);
        String servers = formatterTransaction.print(dateTime).toUpperCase(Locale.getDefault());


        DateTime currentData = DateTime.now();
        String currentDataString = formatterTransaction.print(currentData).toUpperCase(Locale.getDefault());
        if (currentDataString.equals(servers)) {
            return getString(R.string.fref_today) + ", "
                    + formatterTransactionMinutes.print(dateTime).toUpperCase(Locale.getDefault());
        }

        DateTime dateTimeMinusDay = currentData.minusDays(1);
        String currentDataStringMinusDay = formatterTransaction.print(dateTimeMinusDay).toUpperCase(Locale.getDefault());

        if (currentDataStringMinusDay.equals(servers)) {
            return getString(R.string.fref_yesterday) + ", "
                    + formatterTransactionMinutes.print(dateTime).toUpperCase(Locale.getDefault());
        }

        return servers;

    }

    private void hideswipe() {

        V2FReferral v2FReferral = (V2FReferral) getParentFragment();
        if (v2FReferral == null)
            return;

        v2FReferral.hideRefreshing();
    }

    @Override
    public void onDestroyView() {
        ViewTreeObserver obs = histGrafPlus.getViewTreeObserver();
        obs.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        frhScroll.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);

        super.onDestroyView();
    }

    private void initUIObserver(final ArrayList<ResultDayStat> lp) {
        final ViewTreeObserver viewTreeObserver = histGrafPlus.getViewTreeObserver();
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = histGrafPlus.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                initUI(lp);
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private void initPeriod(ArrayList<ResultDayStat> lp) {
        DateTimeFormatter formatterPeriod = DateTimeFormat.forPattern("d MMMM")
                .withLocale(Locale.getDefault());
        if (lp == null || lp.size() == 0) {
            return;
        }

        int size = lp.size();

        String dateStart = lp.get(0).date;
        DateTime dateTimeStart = new DateTime(dateStart);
        String toUpperCaseTimeStart = formatterPeriod.print(dateTimeStart)
                .toUpperCase(Locale.getDefault());

        String dateFin = lp.get(size - 1).date;
        DateTime dateTimeFin = new DateTime(dateFin);


        String toUpperCaseTimeFin = formatterPeriod.print(dateTimeFin).toUpperCase(Locale.getDefault());

        String dateValuePeriod = toUpperCaseTimeStart
                + (!toUpperCaseTimeStart.equals(toUpperCaseTimeFin) ? " - "
                + toUpperCaseTimeFin : "");

        periodInfoHistory.setText(dateValuePeriod);

    }

    private void initUI(ArrayList<ResultDayStat> lp) {

        initPeriod(lp);

        initGraf(lp);
    }

    private void initGraf(ArrayList<ResultDayStat> lp) {
        int size = lp.size();
        if(size == 0)
            return;

        if (size < NUMBER_OF_POINTS) {
            loyaltyProgramByDay = new ArrayList<>();
            int deltaSize = NUMBER_OF_POINTS - size;


            for (int i = 0; i < NUMBER_OF_POINTS - 1; i++) {
                if (i == deltaSize / 2 + 1) {
                    loyaltyProgramByDay.addAll(lp);
                }
                loyaltyProgramByDay.add(null);
            }

        } else {
            loyaltyProgramByDay = new ArrayList<>(lp);
        }
        histGrafMinus.setRotationX(180.0f);
        randomNumbersTab = new Float[NUMBER_OF_POINTS];
        coordPoint = new ArrayList<>();
        coordPopup = new ArrayList<>();
        float floatMaxValueAddBunus = maxValueAddBunus.floatValue();
        final float floatValueOneProcent = floatMaxValueAddBunus / 100f;
        int widthPointHistory = histGrafPlus.getWidth() /
                NUMBER_OF_POINTS;
        // COUNT_DAY_DEFAULT;
        float width = histGrafPlus.getWidth() - widthPointHistory;
        heightGraf = (float) histGrafPlus.getHeight();


        float widthSegment = width / (NUMBER_OF_POINTS - 1);
        for (int j = 0; j < NUMBER_OF_POINTS; ++j) {
            coordPopup.add(new Float[]{widthSegment * j, heightGraf});
            ResultDayStat resultDayStat = loyaltyProgramByDay.get(j);

            showPlus(floatValueOneProcent, heightGraf, widthSegment, j, resultDayStat, widthPointHistory);

            showMinus(floatValueOneProcent, heightGraf, widthSegment, j, resultDayStat, widthPointHistory);
        }
    }

    private void showPlus(float floatValueOneProcent, float height, float widthSegment
            , int j, ResultDayStat resultDayStat, int widthPointHistory) {
        if (resultDayStat == null) {
            return;
        }
        BigDecimal plusBalanceAll = resultDayStat.plusBalanceAll;
        if (plusBalanceAll.compareTo(BigDecimal.ZERO) != 0) {


            float floatValue = plusBalanceAll.floatValue();

            float valueDataProcent = floatValue / floatValueOneProcent;
            randomNumbersTab[j] = valueDataProcent;
            //
            View view = LayoutInflater.from(histGrafPlus.getContext())
                    .inflate(R.layout.pf_point_history_plus, histGrafPlus, false);
            view.getLayoutParams().width = widthPointHistory;
            Obj obj = new Obj(height, widthSegment, j).invoke(true);
            float x = obj.getX();
            float y = obj.getY();


            view.setX(x);
            view.setY(y);
            view.setBackgroundResource(R.drawable.bg_point_plus_history);
            histGrafPlus.addView(view);
        }
    }

    private void showMinus(float floatValueOneProcent, float height, float widthSegment, int j, ResultDayStat resultDayStat, int widthPointHistory) {
        if (resultDayStat == null) {
            return;
        }
        BigDecimal minusBalanceAll = resultDayStat.minusBalance;
        BigDecimal zero = BigDecimal.ZERO;

        if (minusBalanceAll.compareTo(zero) != 0) {
            float floatValue = minusBalanceAll.floatValue();

            float valueDataProcent = floatValue / floatValueOneProcent;
            randomNumbersTab[j] = valueDataProcent;
            //
            View view = LayoutInflater.from(histGrafMinus.getContext())
                    .inflate(R.layout.pf_point_history_minus, histGrafMinus, false);
            view.getLayoutParams().width = widthPointHistory;
            Obj obj = new Obj(height, widthSegment, j).invoke(false);
            float x = obj.getX();
            float y = obj.getY();

            view.setX(x);
            view.setY(y);
            view.setBackgroundResource(R.drawable.bg_point_minus_history);
            histGrafMinus.addView(view);
        }
    }

    private class Obj {
        private float height;
        private float widthSegment;
        private int j;
        private float x;
        private float y;

        Obj(float height, float widthSegment, int j) {
            this.height = height;
            this.widthSegment = widthSegment;
            this.j = j;
        }

        float getX() {
            return x;
        }

        float getY() {
            return y;
        }

        Obj invoke(boolean isAdd) {
            x = widthSegment * j;
            y = height * (1 - randomNumbersTab[j] / 100.0f);
            Float[] floatsCoordinates = new Float[2];
            floatsCoordinates[0] = x < 0 ? 0 : x;
            floatsCoordinates[1] = y < 0 ? 0 : y;
            coordPoint.add(floatsCoordinates);
            if (isAdd) {
                coordPopup.set(coordPopup.size() - 1, floatsCoordinates);
            }
            return this;
        }
    }
}
