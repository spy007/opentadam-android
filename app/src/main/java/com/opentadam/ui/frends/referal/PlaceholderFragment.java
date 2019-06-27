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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.opentadam.App;
import com.opentadam.R;
import com.opentadam.bus.BusTouchChartDown;
import com.opentadam.bus.BusTouchChartUp;
import com.opentadam.ui.BaseFr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnTouch;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

// график
// Generate some random values.
public class PlaceholderFragment extends BaseFr {
    @InjectView(R.id.chart)
    public LineChartView chart;
    @InjectView(R.id.fref_begunok_popup)
    View begunokPopup;
    @InjectView(R.id.pf_cont_point_balanse)
    FrameLayout contPointBalanse;

    private int maxNumberOfLines = 1;
    private int numberOfPoints;
    private float[][] randomNumbersTab;
    private ValueShape shape = ValueShape.CIRCLE;

    private float hashX;
    private int segment = -1;
    private ArrayList<Transaction> transactionSeq;
    private ArrayList<Float[]> coordPopupAddPoint;

    private int sizeTransaction;

    public PlaceholderFragment() {
    }

    public static Fragment newInstance() {
        return new PlaceholderFragment()
                .withViewId(R.layout.fref_line_chart);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FReferalBonusProgramm fReferalBonusProgramm = (FReferalBonusProgramm) getParentFragment();
        if (fReferalBonusProgramm == null || fReferalBonusProgramm.getTransactionSeq() == null)
            return;
        sizeTransaction = fReferalBonusProgramm.getSizeTransaction();
        transactionSeq = fReferalBonusProgramm.getTransactionSeq();
        numberOfPoints = transactionSeq.size();
        if (numberOfPoints == 0) {
            fReferalBonusProgramm.setMaxValueAddBunus(new BigDecimal(100.00f)
                    .setScale(2, RoundingMode.HALF_UP));

            numberOfPoints = 2;
            transactionSeq.add(0, new Transaction(System.currentTimeMillis() - 60 * 60 * 1000));
            transactionSeq.add(1, new Transaction(System.currentTimeMillis()));
        }
        if (numberOfPoints == 1) {
            numberOfPoints = 2;
            transactionSeq.add(0, new Transaction(transactionSeq.get(0)));
        }
        randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        final ViewTreeObserver viewTreeObserver = chart.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = chart.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                initGraf(fReferalBonusProgramm);
            }
        });

    }

    private void initGraf(FReferalBonusProgramm fReferalBonusProgramm) {
        // Generate some random values.
        generateValues(fReferalBonusProgramm.getMaxValueAddBunus());
        generateData();


        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(true);

        resetViewport();
    }


    private void showBegunokPopup() {
        begunokPopup.setVisibility(View.VISIBLE);
    }


    private FrefObjectInitPopup getFrefObjectInitPopup(ObjecInitBGPopup objecInitBGPopup) {

        return FrefObjectInitPopup.invoke()
                .setTransaction(transactionSeq.get(segment)
                        //    refferalState.statistics.get(segment)
                )
                .setDpX(objecInitBGPopup.getDpX())
                .setKorrectX(objecInitBGPopup.getKorrectX())
                .setDpY(objecInitBGPopup.getDpY())
                .setIdBgPopup(objecInitBGPopup.getIdBgPopup());

    }

    void hideBegunokPopup() {
        hashX = 0;
        begunokPopup.setVisibility(View.GONE);
    }

    private void generateValues(BigDecimal maxValueAddBunus) {

        float floatMaxValueAddBunus = maxValueAddBunus.floatValue();
        final float floatValueOneProcent = floatMaxValueAddBunus / 100f;

        float width = chart.getWidth();
        float height = chart.getHeight();

        coordPopupAddPoint = new ArrayList<>();
        contPointBalanse.removeAllViews();

        float widthSegment = width / (numberOfPoints - 1);
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                boolean isFinishPoint = j == numberOfPoints - 1;
                Transaction transaction = transactionSeq.get(j);

                BigDecimal balance = transaction.balance;
                float floatValue = balance.floatValue();

                float valueDataProcent = floatValue / floatValueOneProcent;
                float v = valueDataProcent * 0.9f;
                if (v < 1.0f) {
                    v += floatValueOneProcent;
                }
                randomNumbersTab[i][j] = v;
                //
                View view = LayoutInflater.from(contPointBalanse.getContext())
                        .inflate(R.layout.pf_point_balanse, contPointBalanse, false);
                if (isFinishPoint) {
                    view.setBackgroundResource(R.drawable.bg_pl_krug_primary);
                }

                Float[] floatsCoordinates = new Float[2];
                floatsCoordinates[0] = widthSegment * j;
                floatsCoordinates[1] = height - height * randomNumbersTab[i][j] / 100.0f;
                coordPopupAddPoint.add(floatsCoordinates);

                float x = widthSegment * j - getPX(6);
                float y = height + getPX(3) - height * randomNumbersTab[i][j] / 100.0f;

                view.setX(x);
                view.setY(y);

                contPointBalanse.addView(view);
            }

        }
    }

    private void getSegment(float x, float widthSegment, float chartWidth) {
        FReferalBonusProgramm fReferalBonusProgramm = (FReferalBonusProgramm) getParentFragment();
        if (fReferalBonusProgramm == null || Math.abs(hashX - x) < widthSegment / 2.0f)
            return;

        for (int i = 0; i < numberOfPoints; i++) {
            hashX = widthSegment * (i);
            if (Math.abs(hashX - x) < widthSegment * 0.9f && segment != i) {

                segment = i;

                boolean isFinishPoint = segment == numberOfPoints - 1;
                Float[] floats = coordPopupAddPoint.get(segment);

                float defX = floats[0] + getPX(3);
                float defY = isFinishPoint ?
                        floats[1] + getPX(20) : floats[1] + getPX(16);

                begunokPopup.setX(defX);
                begunokPopup.setY(defY);
                Transaction transaction = transactionSeq.get(segment);
                if (transaction.typ > 0)
                    showBegunokPopup();
                else hideBegunokPopup();


                ObjecInitBGPopup objecInitBGPopup = ObjecInitBGPopup.init()
                        .setDpX(90)
                        .setDpY(20)
                        .setIdBgPopup(R.drawable.fref_popover_center);


                if (numberOfPoints == 2 && segment == 0 || (float) getPX(86) - defX > 0) {

                    // левый
                    objecInitBGPopup
                            .setDpX(0)
                            .setIdBgPopup(R.drawable.fref_popover_left);
                } else if (chartWidth - (float) getPX(64) - defX < 0) {
                    // правый

                    objecInitBGPopup
                            .setDpX(180)
                            .setIdBgPopup(R.drawable.fref_popover_right);
                    if (isFinishPoint) {
                        objecInitBGPopup.setDpY(10);
                    }
                }

                fReferalBonusProgramm.initDataPopup(defX, defY, getFrefObjectInitPopup(objecInitBGPopup));

                break;
            }


        }

    }

    @OnTouch(R.id.chart)
    public boolean onTouchChart(View view, MotionEvent event) {
        if (sizeTransaction == 0)
            return true;
        float x = event.getX();
        float width = chart.getWidth();

        float widthSegment = width / (numberOfPoints - 1);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                App.bus.post(new BusTouchChartDown());
                segment = -1;
                getSegment(x, widthSegment, width);
                break;
            case MotionEvent.ACTION_MOVE: // движение

                getSegment(x, widthSegment, width);
                break;
            case MotionEvent.ACTION_UP: // отпускание
                App.bus.post(new BusTouchChartUp());
                break;
        }

        return true;

    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;// - getPX(8);
        v.top = 100; // + getPX(8);
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private void generateData() {

        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int j = 0; j < numberOfPoints; ++j) {
            PointValue pointValue = new PointValue(j, randomNumbersTab[0][j]);

            values.add(pointValue);
        }

        Line line = new Line(values);

        line.setColor(initColor(R.color.fref_color_primary));

        line.setShape(shape);

        line.setCubic(true);


        line.setFilled(true);
        line.setHasGradientToTransparent(true);
         /*
        if (pointsHaveDifferentColor){
            line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
        }
        */
        line.setHasLabels(false);

        line.setHasLabelsOnlyForSelected(false);

        line.setHasLines(true);

        line.setHasPoints(false);
        lines.add(line);

        LineChartData data = new LineChartData(lines);

        data.setAxisXBottom(null);
        data.setAxisYLeft(null);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    @Override
    public void onResume() {
        super.onResume();

        FReferalBonusProgramm fReferalBonusProgramm = (FReferalBonusProgramm) getParentFragment();
        fReferalBonusProgramm.hideProgressRest();
    }


}
