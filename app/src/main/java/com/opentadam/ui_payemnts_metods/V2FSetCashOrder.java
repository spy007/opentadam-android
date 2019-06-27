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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusUpdMenu;
import com.opentadam.data.ClientData;
import com.opentadam.data.DialogClient;
import com.opentadam.data.IResponseDialog;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.CardAdditionRef;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;

public class V2FSetCashOrder extends BaseFr implements IResponseDialog {

    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.f_s_c_title)
    public TextView title;
    @InjectView(R.id.fsco_sl_panel_menu_title)
    public TextView menuTitle;
    @InjectView(R.id.fsco_sl_panel_menu_title_sub)
    public TextView menuTitleSub;
    protected boolean isHideContractor;
    @InjectView(R.id.ic_plus_card)
    ImageView icPlusCard;
    @InjectView(R.id.cont_cash)
    LinearLayout contCash;
    @InjectView(R.id.add_card)
    LinearLayout addCard;
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
    @InjectView(R.id.fsco_sl_panel_menu)
    FrameLayout panelMenu;
    @InjectView(R.id.fsco_sl_panel_menu_title_sub_cont)
    View subCont;
    @InjectView(R.id.body_fsco)
    View bodyFsco;

    @InjectView(R.id.bask)
    ImageView bask;
    @InjectView(R.id.fsco_progress)
    com.opentadam.ui_payemnts_metods.ProgressView progressView;
    private PaymentMethod paymentMethodDeleteCard;
    private int vendor;

    public V2FSetCashOrder() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new V2FSetCashOrder().withViewId(R.layout.f_set_cash_order);
    }

    public static Fragment newInstance(int vendor) {
        return new V2FSetCashOrder()
                .withArgument("vendor", vendor)
                .withViewId(R.layout.f_set_cash_order);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vendor = getArguments().getInt("vendor", -1);
        if (vendor == Constants.VENDOR_FROUTE) {
            bask.setImageDrawable(initDrawable(R.drawable.ic_arrow_back_black_24dp));
        }
        bodyFsco.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        unitUI();
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
                    subCont.setVisibility(View.GONE);
                    initIconPaqy(-1);
                    menuTitle.setText("");
                    menuTitleSub.setText("");
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
    }

    public void unitUI() {

        if (Injector.getWorkSettings() == null) {
            getAWork().restartAll();
            return;
        }
        addCard.setVisibility(Injector.getWorkSettings().getCardPaymentAllowed()
                && !isHideContractor ? View.VISIBLE : View.GONE);


        getAWork().showWorkProgress();
        getAccount();
    }

    private void initIconPaqy(int id) {
        for (View v : arrIconPay) {
            int idIcon = v.getId();
            v.setVisibility(idIcon == id ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.fsco_close_menu)
    public void hideMenu() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void showMenuCard(final AccountState accountState
            , final ClientData.CashList cashList
            , final int finalI
            , final View view) {

        final boolean isDebet = accountState != null && accountState.isDebet();
        final ClientData clientData = getClientData();
        //  открыть меню карты
        initIconPaqy(getTipeCard(cashList.name));

        String[] split = cashList.name.split(" \\*");
        menuTitle.setText(split[0]);
        menuTitleSub.setText(split[1]);
        subCont.setVisibility(View.VISIBLE);
        menyList.removeAllViews();
        String menuV[];
        int iconV[];
        if (!isDebet) {


            String menuVal[] = {
                    getString(R.string.card_menu_val0),
                    null,  //  getString(R.string.card_menu_val1),
                    getString(R.string.card_menu_val2)
            };
            int iconVal[] = {
                    R.drawable.ic_success_def_text,
                    R.drawable.v3_ic_plus,
                    R.drawable.ic_delete
            };


            menuV = menuVal;
            iconV = iconVal;
        } else {

            String menuVal[] = {
                    getString(R.string.send_debet_client),
                    getString(R.string.card_menu_val2)
            };
            int iconVal[] = {
                    R.drawable.v3_ic_plus,
                    R.drawable.ic_delete
            };


            menuV = menuVal;
            iconV = iconVal;
        }

        for (int i = 0; i < (isDebet ? 2 : 3); i++) {
            String text = menuV[i];
            if (text == null) {
                continue;
            }
            int id = iconV[i];
            final View viewCard = getViewMenu();
            TextView valueMenu = viewCard.findViewById(R.id.value_menu);
            ImageView icon = viewCard.findViewById(R.id.icon);

            icon.setImageDrawable(initDrawable(id));
            icon.setVisibility(View.VISIBLE);


            valueMenu.setText(text);
            final int finalICard = i;
            viewCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalICard) {
                        case 0:
                            if (isDebet) {
                                //   getAWork().showFDebetInfo(accountState);
                                getAWork().showFCardSettings(accountState, -1);
                            } else {
                                onClickPosPayment(clientData, finalI, view);
                            }
                            break;
                        case 1:
                            if (isDebet) {
                                showDialogDeleteCard(cashList);
                            }
                            break;
                        case 2:
                            showDialogDeleteCard(cashList);

                            break;
                    }
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });
            menyList.addView(viewCard);

        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }, 50);
    }

    private void showDialogDeleteCard(ClientData.CashList cashList) {
        paymentMethodDeleteCard = cashList.paymentMethod;
        DialogClient.showV2TwoButtonDialog(getString(R.string.title_delete)
                , cashList.name + " "
                        + getString(R.string.text_delete_card)
                , getString(R.string.cancel_button_dialog_repl),
                getString(R.string.delete_card_button), getIResponseDialog());
    }

    @OnClick(R.id.add_card)
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
                            progressView.setVisibility(View.VISIBLE);
                            RESTConnect restConnect = Injector.getRC();
                            /* if (restConnect == null) return;*/
                            restConnect.findAddCard(getClientData().getLatLngStartAdress(), new IGetApiResponse() {
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

                                    getAWork().showFWebViewAllClass(redirectUrl
                                            , Constants.VENDOR_FSCO, vendorPayment);
                                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    progressView.setVisibility(View.GONE);
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

    private View getViewMenu() {

        return LayoutInflater.from(menyList.getContext())
                .inflate(R.layout.section_menu_list
                        , contCash
                        , false);
    }

    private void getTarif() {

        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/
        Injector.getWorkSettings().setServiceId(null);
        initCashView();
        restConnect.getService(getClientData().getPaymentMethodSelect(),
                Injector.getClientData().latLngTarifHTPSRequest, new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        getAWork().bodyGetTarif(apiResponse);
                        //     getAWork().showV2FSetCashOrder();

                    }
                });

    }

    private void initCashView() {

        contCash.removeAllViews();
        final ClientData clientData = getClientData();
        final AccountState accountState = clientData.accountState;

        final ArrayList<ClientData.CashList> cashLists = clientData
                .getCashList();
        if (cashLists == null) {
            return;
        }

        int size = cashLists.size();
        final View view1 = LayoutInflater.from(contCash.getContext())
                .inflate(R.layout.section_account
                        , contCash
                        , false);
        view1.setVisibility(View.GONE);

        for (int i = 0; i < size; i++) {
            final ClientData.CashList cashList = cashLists.get(i);


            PaymentMethod paymentMethod = cashList.paymentMethod;
            if (isHideContractor
                    && paymentMethod != null
                    && "contractor".equals(paymentMethod.kind)) {
                continue;
            }


            final View view = LayoutInflater.from(contCash.getContext())
                    .inflate(R.layout.section_cash
                            , contCash
                            , false);

            final boolean isCreditCard = paymentMethod != null
                    && "credit_card".equals(paymentMethod.kind);


            if (accountState != null && i == 1) {

                TextView summ = view1.findViewById(accountState.isDebet()
                        ? R.id.summ_debet : R.id.summ);
                String summValue = accountState.getSummValue();


                if (accountState.isDebet()) {

                    summValue = getString(R.string.fsco_prefix_debet) + summValue.replace("-", " ");
                    TextView buttonShowDebet = view1
                            .findViewById(R.id.button_show_debet);

                    TextView infoPay = view1
                            .findViewById(R.id.sa_info_pay);

                    buttonShowDebet.setVisibility(View.VISIBLE);
                    summ.setVisibility(View.VISIBLE);
                    infoPay.setVisibility(View.VISIBLE);

                    buttonShowDebet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getAWork().showFCardSettings(accountState, -1);
                        }
                    });
                }
                summ.setText(String.format("%s%s",
                        summValue,
                        Injector.getWorkSettings().getCurrencyShort()));

                contCash.addView(view1);

                if (accountState.isDebet()) {
                    view1.setVisibility(View.VISIBLE);
                }
            }


            view.setTag(cashList.paymentMethod);
            TextView nameTipeCash = view.findViewById(R.id.name);
            ImageView icon = view.findViewById(R.id.icon);
            ImageView iconCh = view.findViewById(R.id.icon_ch);

            TextView summ = view.findViewById(R.id.summ);
            LinearLayout parentCont = view.findViewById(R.id.parent_cont);
            if (cashList.summ != null) {
                summ.setText(cashList.summ);
                summ.setVisibility(View.VISIBLE);
            } else
                summ.setVisibility(View.GONE);


            final int idIcon = cashList.idIcon;
            Drawable drawable = getResources().getDrawable(idIcon);
            icon.setImageDrawable(isCreditCard ? drawable : initDrawable(idIcon));
            nameTipeCash.setText(cashList.name);
            iconCh.setVisibility(clientData.cashPos == i ? View.VISIBLE : View.INVISIBLE);
            if (clientData.cashPos == i) {
                if (!isCreditCard) {
                    TintIcons.tintImageViewBrend(icon);
                }
                nameTipeCash.setTextColor(initColor(R.color.colorPrimary));
                summ.setTextColor(initColor(R.color.colorPrimary));
            }


            final int finalI = i;
            final int finalI1 = i;
            parentCont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isCreditCard) {

                        showMenuCard(accountState
                                , cashList
                                , finalI
                                , view);
                        return;
                    }

/*


                    if (accountState != null && accountState.isDebet() && finalI != 0) {
                        getAWork().showFDebetInfo(accountState);
                        return;
                    }
*/
                    if (clientData.cashPos != finalI1) {
                        onClickPosPayment(clientData, finalI, view);
                    }

                }
            });

            contCash.addView(view);
        }
        bodyFsco.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);

    }

    private void getAccount() {
        if(!Injector.getClientData().isEnabledCard()){
            getPaymentMethod();
            return;
        }

        RESTConnect restConnect = Injector.getRC();
        /*if (restConnect == null) return;*/
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

                getClientData().accountState = apiResponse.accountState;

                getPaymentMethod();
            }
        });
    }

    private void getPaymentMethod() {
        RESTConnect restConnect = Injector.getRC();
        /*        if (restConnect == null)
                    return;*/
        restConnect.getPaymentMethod(getLatLng(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                getClientData().paymentMethods = apiResponse.paymentMethods;
                initCashView();
            }
        });
    }


    protected void onClickPosPayment(ClientData clientData, int finalI, View view) {
        clientData.cashPos = finalI;
        Object tag = view.getTag();
        if (tag instanceof PaymentMethod) {
            PaymentMethod paymentMethodSelect = (PaymentMethod) tag;
            clientData.setPaymentMethodSelect(paymentMethodSelect);
        } else
            clientData.setPaymentMethodSelect(new PaymentMethod("cash"));

        App.bus.post(new BusUpdMenu());
        getTarif();
    }


    private IResponseDialog getIResponseDialog() {
        return this;
    }

    public void deleteCard() {
        RESTConnect restConnect = Injector.getRC();
        /*   if (restConnect == null) return;*/
        LatLng latLngStartAdress = getClientData().getLatLngStartAdress();
        restConnect.deleteCard(
                latLngStartAdress,
                paymentMethodDeleteCard.id,
                new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }

                        getClientData().cashPos = 0;
                        getClientData().setPaymentMethodSelect(new PaymentMethod("cash"));
                        unitUI();
                    }
                });
    }


    @OnClick(R.id.bask)
    public void onBask() {

        if (!isVisible())
            return;
        if (vendor == Constants.VENDOR_FROUTE)
            getAWork().showV3RestoryFRoute();
        else
            getAWork().showMenu();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV3RestoryFRoute();
        return true;
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


}