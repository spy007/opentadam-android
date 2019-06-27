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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.ClientData;
import com.opentadam.data.IResponseDialog;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.CardAdditionRef;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.ui.AWork;
import com.opentadam.ui.BaseFr;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;


public class FCardSettings extends BaseFr implements IResponseDialog {
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.fsco_sl_panel_menu_title)
    public TextView menuTitle;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.cont_list_card)
    LinearLayout contListCard;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.title_value_debet)
    TextView titleValueDebet;
    @InjectView(R.id.value_debet)
    TextView valueDebet;
    @InjectView(R.id.bask)
    ImageView bask;
    @InjectView(R.id.f_c_s_send)
    View send;
    @InjectView(R.id.button_debet)
    View buttonDebet;
    @InjectView(R.id.fsco_sl_panel_menu_list)
    LinearLayout menyList;
    @InjectView(R.id.fsco_sl_panel)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @InjectViews({
            R.id.fsco_add_pay_ic,
            R.id.fsco_maestro,
            R.id.fsco_master,
            R.id.fsco_viza,
            R.id.fsco_mir
    })
    View[] arrIconPay;
    @InjectView(R.id.fsco_sl_panel_body)
    FrameLayout panelBody;
    private Long paymentMethodDeleteCardId;
    private AccountState accountState;
    private int defPosCart = -1;
    private int parentView = 0;
    private int step = 0;

    public FCardSettings() {
    }

    public static Fragment newInstance() {
        return new FCardSettings().withViewId(R.layout.f_card_settings);
    }

    public static Fragment newInstance(AccountState a
            , int parentView) {
        String accountState = new Gson().toJson(a);
        return new FCardSettings()
                .withArgument("accountState", accountState)
                .withArgument("parentView", parentView)
                .withViewId(R.layout.f_card_settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            String a = getArguments().getString("accountState");
            accountState = new Gson().fromJson(a, AccountState.class);
            parentView = getArguments().getInt("parentView");
            bask.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }

        AWork aWork = getAWork();
        if (aWork == null)
            return;
        int end = aWork.getDisplayInfo() == null ? 400 : aWork.getDisplayInfo().heightPixels / 2;
        swipeContainer.setProgressViewEndTarget(true,
                end);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOf) {


            }

            @Override
            public void onPanelStateChanged(View panel
                    , SlidingUpPanelLayout.PanelState previousState
                    , SlidingUpPanelLayout.PanelState newState) {
                if (panelBody == null)
                    return;

                panelBody.setClickable(newState != SlidingUpPanelLayout.PanelState.COLLAPSED);

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {

                    initIconPaqy(-1);
                    menuTitle.setText("");

                    menyList.removeAllViews();
                } else {
                    panelBody.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    });
                }
            }

        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAccount();
            }
        });

        if (accountState != null) {
            getAWork().accountState = accountState;
            initUI();
        } else if (getAWork().accountState == null) {
            getAccount();
        } else {

            getAWork().showFCardSettings(getAWork().accountState, -1);

        }

    }

    private void getAccount() {
        if(!Injector.getClientData().isEnabledCard()){
            return;
        }
        swipeContainer.setRefreshing(true);
        RESTConnect restConnect = Injector.getRC();
        /*     if (restConnect == null) return;*/
        LatLng latLngStartAdress = getClientData().getLatLngStartAdress();
        restConnect.getAccount(latLngStartAdress, new IGetApiResponse() {

            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                  //  getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                getAWork().hideWorkProgress();
                getClientData().accountState = apiResponse.accountState;
                accountState = apiResponse.accountState;
                initUI();

            }
        });
    }

    private void initUI() {
        getAWork().showWorkProgress();
        RESTConnect restConnect = Injector.getRC();
        /*  if (restConnect == null) return;*/
        restConnect.getPaymentMethod(getLatLng(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (Constants.PATH_PAYMENT_METHOD.equals(apiResponse.path)) {

                    getClientData().paymentMethods = apiResponse.paymentMethods;

                    getNameCard();
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }

    private void getNameCard() {
        contListCard.removeAllViews();
        final boolean isDebetClient = accountState != null && accountState.isDebet();
        contListCard.setVisibility(View.VISIBLE);

        final ArrayList<ClientData.CashList> cashLists = Injector.getClientData()
                .getCashList();
        int size = cashLists.size();
        for (int i = 0; i < size; i++) {
            final ClientData.CashList cashList = cashLists.get(i);
            if (cashList == null || cashList.paymentMethod == null)
                continue;

            PaymentMethod paymentMethod = cashList.paymentMethod;
            String kind = paymentMethod.kind;
            if ("credit_card".equals(kind)) {
                if (defPosCart == -1)
                    defPosCart = i;

                View view = LayoutInflater.from(contListCard.getContext())
                        .inflate(R.layout.cont_list_card, contListCard, false);
                TextView nameCardView = view.findViewById(R.id.name_card);
                TextView numCard = view.findViewById(R.id.num_card);
                View clcPoints = view.findViewById(R.id.clc_points);
                clcPoints.setVisibility(View.VISIBLE);

                View[] arrPoint = {
                        view.findViewById(R.id.clc_point0),
                        view.findViewById(R.id.clc_point1),
                        view.findViewById(R.id.clc_point2),
                        view.findViewById(R.id.clc_point3)};


                ImageView iconCard = view.findViewById(R.id.icon);
                ImageView iconCardPayMetod = view.findViewById(R.id.icon_pay_metod);
                final int idIcon = cashList.idIcon;
                Drawable drawable = getResources().getDrawable(idIcon);
                iconCardPayMetod.setImageDrawable(drawable);
                iconCardPayMetod.setVisibility(View.VISIBLE);
                if (isDebetClient)
                    iconCard.setVisibility(View.INVISIBLE);
                if (isDebetClient && defPosCart == i) {

                    iconCard.setImageDrawable(initDrawable(R.drawable.ic_check_circle));
                    iconCard.setVisibility(View.VISIBLE);
                    nameCardView.setTextColor(initColor(R.color.colorPrimary));
                    numCard.setTextColor(initColor(R.color.colorPrimary));
                    paymentMethodDeleteCardId = paymentMethod.id;

                    for (View v : arrPoint) {
                        v.setBackgroundResource(R.drawable.bg_krug_primary);
                    }
                }

                String nameCard = cashList.name;
                String[] split = nameCard.split("\\*");

                numCard.setText(split[1]);
                nameCardView.setText(split[0]);


                final int finalI = i;
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isDebetClient) {
                            defPosCart = finalI;
                            getNameCard();
                            // debetCard();
                        }
                    }
                };
                nameCardView.setOnClickListener(onClickListener);
                iconCard.setOnClickListener(onClickListener);

                contListCard.addView(view);
            }

        }

        String summValue = accountState.getSummValue().replace("-", "");
        if (isDebetClient && step == 0 && parentView == 0) {
            contListCard.setVisibility(View.GONE);
            titleValueDebet.setVisibility(View.VISIBLE);

            buttonDebet.setVisibility(View.VISIBLE);
            valueDebet.setText(String.format("%s%s",
                    summValue,
                    Injector.getWorkSettings().getCurrencyShort()));
            valueDebet.setVisibility(View.VISIBLE);
            send.setVisibility(View.GONE);

        } else {
            buttonDebet.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
            titleValueDebet.setVisibility(View.GONE);
            title.setText(R.string.f_c_s_set_debet_null);
            valueDebet.setText(String.format("%s%s",
                    summValue,
                    Injector.getWorkSettings().getCurrencyShort()));
            valueDebet.setVisibility(View.VISIBLE);
        }

    }

    private void initIconPaqy(int id) {
        for (View v : arrIconPay) {
            int idIcon = v.getId();
            v.setVisibility(idIcon == id ? View.VISIBLE : View.GONE);
        }
    }


    @OnClick(R.id.add_pay)
    public void onAddCard() {
        //  добавить способ оплаты
        initIconPaqy(R.id.fsco_add_pay_ic);
        menuTitle.setText(R.string.fsco_title_menu);

        menyList.removeAllViews();
        String menuVal[] = {
                getString(R.string.sml_val_card),
                "PayPal",
                "QIWI"
        };

        int iconVal[] = {
                R.drawable.ic_fsco_add_card,
                R.drawable.ic_fsco_add_pay,
                R.drawable.ic_fsco_add_qiwi
        };

        for (int i = 0; i < 1; i++) {
            final View view = LayoutInflater.from(menyList.getContext())
                    .inflate(R.layout.section_menu_list
                            , menyList
                            , false);
            TextView valueMenu = view.findViewById(R.id.value_menu);
            valueMenu.setText(menuVal[i]);
            ImageView icon = view.findViewById(R.id.icon);
            icon.setImageDrawable(initDrawable(iconVal[i]));
            icon.setVisibility(View.VISIBLE);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:

                            RESTConnect restConnect = Injector.getRC();
                            /*  if (restConnect == null) return;*/
                            restConnect.findAddCard(getClientData().getLatLngStartAdress()
                                    , new IGetApiResponse() {
                                        @Override
                                        public void getApiResponse(ApiResponse apiResponse) {
                                            if (!isVisible())
                                                return;

                                            if (apiResponse == null || apiResponse.error != null) {
                                                getAWork().showErrorIGetApiResponse(apiResponse);
                                                return;
                                            }
                                            CardAdditionRef cardAdditionRef = apiResponse.cardAdditionRef;

                                            String redirectUrl = cardAdditionRef.redirectUrl;
                                            String vendorPayment = cardAdditionRef.vendor;

                                            getAWork().showFWebViewAllClass(redirectUrl,
                                                    Constants.VENDOR_FCS, vendorPayment);

                                        }
                                    });
                            break;
                        case 1:

                            break;
                        case 2:


                            break;
                    }
                }
            });
            menyList.addView(view);

        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }, 50);

    }

    @OnClick(R.id.button_debet)
    public void buttonDebet() {
        step = 1;
        getNameCard();
    }

    @OnClick(R.id.f_c_s_send)
    public void debetCard() {
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null)
            return;*/
        LatLng latLngStartAdress = getClientData().getLatLngStartAdress();
        restConnect.debetCard(
                latLngStartAdress,
                paymentMethodDeleteCardId,
                new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        getAccount();
                        showCustomOneButtonDialog(getString(R.string.f_c_s_mesage_dialog));
                    }
                });
    }

    private void showCustomOneButtonDialog(String message) {

        if (!isVisible() || getAWork().isFinishing())
            return;


        final Dialog dialog = new Dialog(getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view =
                inflater.inflate(R.layout.dialog_one_button, null);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        dialogTitle.setVisibility(View.GONE);
        dialogMess.setText(message);

        TextView yesDialog = view.findViewById(R.id.dialog_yes);
        yesDialog.setText(R.string.f_c_s_close);

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAWork().showV2FSetCashOrder();
                //  getAccount();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @OnClick(R.id.bask)
    public void onBaskIcon() {

        if (!isVisible())
            return;

        if (accountState != null)
            onBackPressed();
        else
            getAWork().showMenu();
    }

    @Override
    public boolean onBackPressed() {
        if (step == 1) {
            step = 0;
            getNameCard();
        } else if (accountState == null)
            getAWork().showV3FRoute();
        else if (parentView < 0) {
            getAWork().showV2FSetCashOrder();
        } else {
            getAWork().showV3FRoute();
        }
        return true;
    }

    @OnClick(R.id.fsco_close_menu)
    public void hideMenu() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public Context getContectApp() {
        return null;
    }

    @Override
    public void responseAction(String name) {

    }

    @Override
    public void addPfotoCamera(boolean b) {

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if (swipeContainer != null && swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
    }
}
