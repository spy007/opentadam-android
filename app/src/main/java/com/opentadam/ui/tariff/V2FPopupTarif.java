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

package com.opentadam.ui.tariff;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusUpdArrTariff;
import com.opentadam.bus.HiveBus;
import com.opentadam.data.ClientData;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class V2FPopupTarif extends BaseFr implements IScroll {

    @InjectView(R.id.cont_tarif_manual)
    LinearLayout contTarifManual;
    @InjectView(R.id.sl_panel_tarif)
    SlidingUpPanelLayout mLayout;

    @InjectView(R.id.cont_blok)
    FrameLayout contBlok;
    ///////////

    @InjectView(R.id.cont_sections)
    LinearLayout contSections;
    @InjectView(R.id.tarif_hscroll)
    V2SubcategoryScrollView tarifHscroll;
    private int screenWidth;
    private int defPos;
    private boolean isActivateScroll = false;


    public V2FPopupTarif() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.v2_fpopup_tarif, container, false);
        ButterKnife.inject(this, inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Tarif> arrTarif = Injector.getClientData().arrTarif;

        if (arrTarif == null) {
/*            alert(getString(R.string.error_set_tarif));
            getAWork().closeApp();*/
            return;
        }

        initBody();
    }

    private void initBody() {
        mLayout.setTouchEnabled(false);
        screenWidth = getAWork().getWindowManager()
                .getDefaultDisplay().getWidth();
        tarifHscroll.setIScroll(this);
        tarifHscroll.setDisplayWidth(screenWidth);

        initUi();
        setCenterInit();
    }

    private void setCenterInit() {

        ClientData clientData = Injector.getClientData();
        defPos = clientData.getDefPosTariff();
        List<Tarif> arrTarif = clientData.arrTarif;
        int sizeTariffList = arrTarif.size();

        final int widthSection = sizeTariffList > 1 ? (int) (screenWidth * 0.8) : screenWidth;

        final View content = getAWork().findViewById(Window.ID_ANDROID_CONTENT);

        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeGOLListener(content.getViewTreeObserver(), this);
                if (!isActivateScroll && isVisible() && tarifHscroll != null) {

                    tarifHscroll.smoothScrollTo(defPos * widthSection, 0);
                }
                isActivateScroll = true;
            }
        });


    }


    private void colorSet(boolean b, ImageView carBrend, TextView[] txt) {
        TintIcons.tintImageViewOther(carBrend, b ? R.color.text_sec_time : R.color.colorPrimary);

        for (TextView textView : txt) {

            textView.setTextColor(initColor(b ? R.color.text_sec_time :

                    textView.getId() == R.id.text_info ?
                            R.color.text_default_color : R.color.colorPrimary));
        }

    }


    @Subscribe
    public void onBusUpdArrTariff(BusUpdArrTariff e) {

        initUi();

    }

    private void initUi() {
        if (!isVisible())
            return;

        ClientData clientData = Injector.getClientData();
        defPos = clientData.getDefPosTariff();
        List<Tarif> arrTarif = clientData.arrTarif;
        int sizeTariffList = arrTarif.size();

        int widthSection = sizeTariffList > 1 ? (int) (screenWidth * 0.8) : screenWidth;


        contSections.removeAllViews();

        for (int i = 0; i < sizeTariffList; i++) {

            Tarif tarif = arrTarif.get(i);

            LinearLayout view = (LinearLayout) LayoutInflater.from(contSections.getContext())
                    .inflate(R.layout.v2_section_tarif, contSections, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

            if (i == sizeTariffList - 1)
                view.setPadding(getPX(24), 0, getPX(sizeTariffList == 1 ? 16 : 6), 0);


            layoutParams.width = widthSection;

            ArrayMap<String, Integer> arrCarIcon = TintIcons.getArrCarIconFull();
            ImageView carBrend = view.findViewById(R.id.car_brend);
            if (tarif.icon != null) {
                carBrend.setImageResource(arrCarIcon.get(arrCarIcon.containsKey(tarif.icon) ?
                        tarif.icon : "counteragent_tariff"));

            } else
                carBrend.setImageResource(arrCarIcon.get("counteragent_tariff"));

            TextView nameTariff = view.findViewById(R.id.name_tariff);
            TextView costTarif = view.findViewById(R.id.route_cost_value);
            TextView currencyShort = view.findViewById(R.id.currency_short);
            TextView prefixTarif = view.findViewById(R.id.prefix_tarif);
            TextView textInfo = view.findViewById(R.id.text_info);
            TextView costKm = view.findViewById(R.id.cost_km);
            TextView costKmCurrencyShort = view.findViewById(R.id.cost_km_currency_short);
            TextView costMinutes = view.findViewById(R.id.cost_minutes);
            TextView costMinutesCurrencyShort = view.findViewById(R.id.cost_minutes_currency_short);
            TextView textView[] = {nameTariff, costTarif, currencyShort, prefixTarif, textInfo, costKm
                    , costKmCurrencyShort, costMinutes, costMinutesCurrencyShort};

            colorSet(i != clientData.getDefPosTariff(), carBrend, textView);

            prefixTarif.setText(R.string.prefix_popup_tarif_min_cost);

            costTarif.setText(Utilites.getDefCostBigDec(tarif.minCost));
            currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());

            //    costKmCurrencyShort.setText(Injector.getWorkSettings().getCurrencyShort()+"/км");
            //   costMinutesCurrencyShort.setText(Injector.getWorkSettings().getCurrencyShort()+"/мин");

            nameTariff.setText(tarif.name);
            String description = tarif.description;
            if (description != null) {
                textInfo.setText(description.trim());
                textInfo.setVisibility(View.VISIBLE);
            }


            contSections.addView(view);
            if (sizeTariffList > 1 && i == sizeTariffList - 1) {

                LinearLayout linearLayout = new LinearLayout(getAWork());

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                        , LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout
                        .setLayoutParams(params);
                params.width = (int) (screenWidth * 0.2);
                contSections.addView(linearLayout);
            }

        }


    }

    ////////

    @OnClick(R.id.close_popup)
    public void onClosePopup() {
        contBlok.setClickable(false);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }

    public void showPopupTarif() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        contBlok.setClickable(true);
    }


    @Override
    public void onDrawFinish(int pos) {

        if (pos > Injector.getClientData().arrTarif.size() - 1)
            pos = 0;
        Injector.getClientData().setDefPosTariff(pos);
        HiveBus.postBusUpdArrTariff();
    }

    @Override
    public void closePopup() {
        onClosePopup();
    }
}
