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

package com.opentadam.ui.creating_an_order;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.SettingsStore;
import com.opentadam.bus.BusCountLines2;
import com.opentadam.bus.BusDisabledTarif;
import com.opentadam.bus.BusEnabledTarif;
import com.opentadam.bus.BusGPSLocation;
import com.opentadam.bus.BusSetFullTariff;
import com.opentadam.bus.BusStartOrStopPinAnimate;
import com.opentadam.bus.BusUpdArrTariff;
import com.opentadam.bus.BusUpdMenu;
import com.opentadam.bus.BusUpdMenuPay;
import com.opentadam.bus.BusUpdTarif;
import com.opentadam.bus.BusUpdateDefMars;
import com.opentadam.bus.BusUpdateInterface;
import com.opentadam.data.ClientData;
import com.opentadam.data.DialogClient;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.CostModifier;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.Estimation;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Option;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.ResultSendOrders;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.Settings;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFrReversGeocode;
import com.opentadam.ui.creating_an_order.adapter.FullMyFragmentPagerAdapter;
import com.opentadam.ui.creating_an_order.adapter.SmallMyPagerAdapter;
import com.opentadam.ui.creating_an_order.animate.CoverFlow;
import com.opentadam.ui.creating_an_order.init_froute.InitFRoute;
import com.opentadam.ui.creating_an_order.interface_class.IUtilsV3Route;
import com.opentadam.ui.creating_an_order.rest_froute.RestFroute;
import com.opentadam.ui.order.UpdUIAdressMapsYaGoogle;
import com.opentadam.utils.CustomTypefaceSpan;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.Utilites;
import com.opentadam.view.LongTextView;
import com.opentadam.yandex_google_maps.FGoogle;
import com.opentadam.yandex_google_maps.FOsmMaps;
import com.opentadam.yandex_google_maps.IUPmaps;
import com.opentadam.yandex_google_maps.UtilitesMaps;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static com.opentadam.Injector.deviceScreen;
import static com.opentadam.Injector.getClientData;


public class V3FRoute extends BaseFrReversGeocode implements IUtilsV3Route {

    private static final int PAG1_DP = 290;
    private static final int PAG0_DP = 190;
    private static final int HEIGHT_TIME_CONT = 56;
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());

    public boolean isBlockSendServers = false;
    @InjectView(R.id.fr_error_ic_net)
    public ImageView errorIcNet;
    @InjectView(R.id.apin_froute_error_service)
    public com.opentadam.ui.creating_an_order.animate.APinFroute aPinFrouteEServis;
    @InjectView(R.id.sl_panel)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @InjectView(R.id.sl_panel_menu_set_payments)
    SlidingUpPanelLayout slidingMenuSetPayments;
    @InjectViews({
            R.id.frame_maps
            , R.id.cont_set_location_def
    })

    View[] marginButtonView;
    @InjectViews({R.id.fr_separator_adress
            , R.id.frmb_cont_comment_pay
            //   , R.id.fr_cont_time_pay
            , R.id.fr_cont_adress1
            , R.id.time_order_cont
            , R.id.adress_pos
    })
    View[] hideView;
    @InjectViews({
            R.id.fr_tab_bar
            , R.id.frame_maps
            , R.id.cont_set_location_def
    })
    View[] hideExpandetViewD;
    @InjectViews({
            R.id.ibv_info_blok
            , R.id.buttom_urgent1
            , R.id.fr_cont_cost
            , R.id.frame_maps
            , R.id.cont_set_location_def
            , R.id.sub_menu_button
    })

    View[] hideErrorRegionView;
    @InjectView(R.id.small_pager_container)
    PagerContainer smallContainer;
    @InjectView(R.id.full_pager_container)
    PagerContainer fullContainer;
    @InjectView(R.id.fr_cont_full_tarif)
    View contFullTarif;
    @InjectView(R.id.buttom_urgent1)
    View buttomUrgent1;
    @InjectView(R.id.fr_set_location_def)
    View setLocationDef;
    @InjectView(R.id.edit_cost)
    ImageView editCost;
    @InjectView(R.id.route_cost_value_prefix)
    TextView valueCostPrefix;
    @InjectView(R.id.route_cost_value)
    TextView routeCostValue;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;
    //
    @InjectView(R.id.currency_short)
    TextView currencyShort;
    @InjectView(R.id.set_options)
    TextView setOptions;
    @InjectView(R.id.frame_maps)
    FrameLayout frameMaps;
    @InjectView(R.id.cont_info_km_min)
    View contInfoKmMin;
    @InjectView(R.id.adress_dop_info)
    TextView adressDopInfo;
    @InjectView(R.id.frmb_comment)
    TextView commentOrder;
    @InjectView(R.id.ic_plus_button)
    ImageView icPlusButton;
    @InjectView(R.id.adress_value_item1)
    TextView adressValueItem1;
    @InjectView(R.id.adress_value_item0)
    TextView adressValueItem0;
    @InjectView(R.id.adress_value_time)
    TextView adressValueTime;
    @InjectView(R.id.adress_sub_value_time)
    TextView adressSubValueTime;
    @InjectView(R.id.fr_cont_min_size)
    LinearLayout contMinSize;
    @InjectView(R.id.fr_lines_def)
    FrameLayout linesDef;
    @InjectView(R.id.fr_revers_page)
    View reversPage;
    @InjectView(R.id.fr_error_tarif_work)
    View errorTarifWork;
    @InjectView(R.id.fr_error_tarif_text)
    TextView errorTarifText;
    @InjectView(R.id.fr_error_cont_marker_center)
    View errorContMarkerCenter;
    @InjectView(R.id.fr_error_location_def)
    View errorLocationDef;
    @InjectView(R.id.fr_error_ic_servis)
    ImageView errorIcServis;
    @InjectView(R.id.v3_marker_center_big)
    ImageView markerCenterBig;
    @InjectView(R.id.v3_value_min0)
    TextView valueMin;
    @InjectView(R.id.v3_text_min0)
    TextView textMin;
    @InjectView(R.id.v3_cont_value_time)
    View contValueTime;
    @InjectViews({R.id.v3_cont_value_time, R.id.v3_marker_center_big})
    View[] viewsHidePinMaps;
    @InjectViews({
            R.id.fr_revers_page
            , R.id.cont_set_location_def
            , R.id.fr_tab_bar})
    View[] viewsHideUIPinMaps;
    @InjectView(R.id.v3_marker_center_passender)
    View passender;
    @InjectView(R.id.sub_menu_button)
    View subMenuButton;
    @InjectView(R.id.fr_cont_cost)
    View contCost;
    @InjectView(R.id.fra_animate0)
    View fraAnimate0;
    @InjectView(R.id.fra_animate1)
    View fraAnimate1;
    @InjectView(R.id.fra_animate2)
    View fraAnimate2;
    @InjectView(R.id.value_km_min)
    TextView valueKmMin;
    @InjectView(R.id.fr_cont_animate_adress0)
    View contAnimateAdress0;
    @InjectView(R.id.cont_cash_payments)
    LinearLayout contCashPayments;
    @InjectView(R.id.msp_progress)
    com.opentadam.ui_payemnts_metods.ProgressView progressView;
    @InjectView(R.id.cont_maps)
    FrameLayout contMaps;
    @InjectView(R.id.fone_cont_maps)
    View foneContMaps;
    @InjectView(R.id.fr_send_order_servers)
    LinearLayout sendOrderServers;
    @InjectView(R.id.frmb_pay_ic)
    ImageView payIc;
    @InjectView(R.id.frmb_pay_name)
    TextView payName;
    @InjectView(R.id.fr_cont_time)
    View contTime;
    @InjectView(R.id.frouteo_sl_panel_time_preorder)
    SlidingUpPanelLayout slidingPanelTimePreorder;
    @InjectView(R.id.tipe_adress1)
    TextView tipeAdress1;
    @InjectView(R.id.sity_adress1)
    TextView sityAdress1;
    @InjectView(R.id.sity_adress0)
    TextView sityAdress0;
    @InjectView(R.id.froute_send_text)
    TextView frouteSendText;
    @InjectView(R.id.froute_bcb_layout)
    View bCBLoayout;
    @InjectView(R.id.fr_hint_tarif)
    TextView hintTarif;
    @InjectView(R.id.froute_bcb_layout_hint)
    View layoutHint;
    @InjectView(R.id.route_cost_value_prefix_hint)
    TextView valueCostPrefixHint;
    @InjectView(R.id.route_cost_value_hint)
    TextView routeCostValueHint;
    @InjectView(R.id.route_cost_value_dec_hint)
    TextView costValueDecHint;
    @InjectView(R.id.currency_short_hint)
    TextView currencyShortHint;
    @InjectView(R.id.fr_error_not_available)
    View notAvailable;
    @InjectView(R.id.apin_froute)
    com.opentadam.ui.creating_an_order.animate.APinFroute aPinFroute;
    private boolean isPage0;
    private int currentItemSmall;
    private boolean isShowMenu;
    private boolean isRestartInitSmallTariff = false;
    private UIOrder uiOrder;
    private Address tempAdress0;
    private boolean isOnClickRevertPag = false;
    private Animator animatorPages;
    private Runnable runnablePages;
    private boolean isBusDisabledTarif;
    private Runnable runnableCameraIdle;
    private Animator animateAnimate0;
    private Animator animateAnimate1;
    private Animator animateAnimate2;
    private Runnable runnableAnimateAdress0;
    private boolean isRecreate;
    private boolean isClickMyLoc;
    private String objectRestoryV3RouteVendor;
    private ViewTreeObserver.OnGlobalLayoutListener listenerAdressValueItem1;
    private ViewTreeObserver.OnGlobalLayoutListener listenerCountOptions;
    private boolean isErrorNet;

    private RestFroute restFroute;


    public V3FRoute() {
        // Required empty public constructor
    }

    public static Fragment newInstance(boolean isPage0) {
        return new V3FRoute().withViewId(R.layout.v3_froute)
                .withArgument("isPage0", isPage0);
    }

    public static Fragment newInstance(boolean isPage0, boolean isShowMenu) {

        return new V3FRoute().withViewId(R.layout.v3_froute)
                .withArgument("isPage0", isPage0)
                .withArgument("isShowMenu", isShowMenu);
    }

    public static Fragment newInstance(ObjectRestoryV3Route objectRestoryV3Route) {

        return new V3FRoute().withViewId(R.layout.v3_froute)
                .withArgument("isPage0", objectRestoryV3Route.isPage0)
                .withArgument("objectRestoryV3RouteVendor", objectRestoryV3Route.vendor)
                .withArgument("isShowMenu", objectRestoryV3Route.isShowMenu);
    }

    public static Fragment newInstance() {
        // isPage0 == true старт с простой страницы иначе сложная
        boolean isPage0 = !Injector
                .getWorkSettings()
                .isSkipFirstScreen();

        return new V3FRoute().withViewId(R.layout.v3_froute)
                .withArgument("isPage0", isPage0);
    }

    protected void setModeErrorNet() {
        isErrorNet = true;
    }

    public int getPageDP() {
        if (isPage0) {
            return PAG0_DP;
        } else {
            return contTime.getVisibility() == View.GONE ? PAG1_DP : PAG1_DP + HEIGHT_TIME_CONT;
        }
    }

    @Subscribe
    public void onBusCountLines2(BusCountLines2 e) {
        if (!isVisible())
            return;
        adressDopInfo.setVisibility(e.isTwoLines ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isErrorNet) {
            showErrorNet();
            return;
        }

        if (getArguments() != null) {
            objectRestoryV3RouteVendor = getArguments().getString("objectRestoryV3RouteVendor", null);
            isPage0 = getArguments().getBoolean("isPage0", true);
            isShowMenu = getArguments().getBoolean("isShowMenu", false);
            if (!getClientData().isEnabledService()) {

                objectRestoryV3RouteVendor = null;
            }
        }

        restFroute = RestFroute.newInstance(this);

        initUI();

    }

    private void gisCreateRequest() {

        final SendCreateRequest sendCreateRequest = getAWork().getSendCreateRequest();
        if(sendCreateRequest == null)
            return;
        getAWork().showProgressDevault();

        RESTConnect restConnect = Injector.getRC();
        restConnect.sendOrders(sendCreateRequest, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                getAWork().hideProgressDevault();
                getAWork().setSendCreateRequest(null);
                if (apiResponse.errorCodeServers != null) {
                    alert(getString(R.string.error_gis) + " " + apiResponse.errorCodeServers.message);
                    return;
                }

                ResultSendOrders resultSendOrders = apiResponse.resultSendOrders;
                if (resultSendOrders == null) {
                    alert(getString(R.string.error_gis));
                    return;
                }

                Injector.getClientData().initMemoryListNamesCost();
                long orderId = resultSendOrders.id;
                getAWork().showFSearchCar(sendCreateRequest.clientAddresses.get(0).address.position, orderId);
                getAWork().showMessage(getString(R.string.info_gis_create));
            }
        });


    }


    public InitFRoute getInitFRoute() {
        return InitFRoute.newInstance(this)
                .setContTime(contTime)
                .setIuPmaps(getIuPmaps())
                .setAdressValueTime(adressValueTime)
                .setAdressSubValueTime(adressSubValueTime)
                .setPayName(payName)
                .setFrouteSendText(frouteSendText)
                .setSlidingUpPanelLayout(slidingUpPanelLayout)
                .setMarginButtonView(marginButtonView)
                .setFraAnimate0(fraAnimate0)
                .setFraAnimate1(fraAnimate1)
                .setfraAnimate2(fraAnimate2)
                .setcontAnimateAdress0(contAnimateAdress0)
                .setPayIc(payIc);
    }

    public IUPmaps getIuPmaps() {
        return (IUPmaps) getChildFragmentManager().findFragmentById(R.id.cont_maps);
    }

    private void initUI() {

        Settings workSettings = Injector
                .getWorkSettings();

        reversPage.setVisibility(workSettings
                .isEnabledRevers() ? View.VISIBLE : View.GONE);

        restFroute.getRestInit();

        if (Injector.getClientData().isRecreateLocale) {
            getAWork().showFProfil();
            return;
        }

        getAWork().setObjectRestoryV3Route(null);

        disabledSlidingUpPanelLayout();

        editCost.setVisibility(isEnabledAddPrice() ? View.VISIBLE : View.GONE);
        editCost.setEnabled(false);

        if (Injector.getSettingsStore().isOnRegClient())
            getAWork().getBonusClient();


        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initTimeOrder();


        ///

        initPages(isPage0);


        Animator[] animators = initFRoute.initAnimateAdress0();
        if (animators != null) {
            animateAnimate0 = animators[0];
            animateAnimate1 = animators[1];
            animateAnimate2 = animators[2];
        }

        animateAdress0LoopProgress();


        initHederMenuRoute();

        initSmallTariff();

        getAWork().showWorkCont();
        getAWork().showBody();

        initFRoute.initPayCont();

        TintIcons.tintImageViewOther(payIc, R.color.bg_while);

        initMaps();

    }

    private void disabledSlidingUpPanelLayout() {
        slidingUpPanelLayout.setEnabled(false);
        slidingUpPanelLayout.setTouchEnabled(false);
    }

    private void showErrorNet() {
        for (View v : hideErrorRegionView) {
            v.setVisibility(View.INVISIBLE);
        }

        for (View v : hideExpandetViewD) {
            v.setVisibility(View.INVISIBLE);
        }

        FOsmMaps fOsmMaps = (FOsmMaps) FOsmMaps.newInstance("errorNet");


        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cont_maps, fOsmMaps)
                .commitAllowingStateLoss();

        errorIcServis.setVisibility(View.GONE);
        errorIcNet.setVisibility(View.VISIBLE);
        notAvailable.setVisibility(View.VISIBLE);
        errorContMarkerCenter.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) errorContMarkerCenter.getLayoutParams();
        layoutParams.bottomMargin = 0;

        getAWork().showBody();
        LatLng markerLocation = Injector.getClientData().getMarkerLocation();
        if (markerLocation == null || markerLocation.latitude == 0 || markerLocation.longitude == 0)
            return;
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.setCenterMaps(markerLocation);
    }

    private String getCurrentInterfaceName() {
        return isPage0 ? "simple" : "advanced";
    }

    @Subscribe
    public void onBusUpdateInterface(BusUpdateInterface event) {
        if (!isVisible())
            return;
        reversPage.setVisibility(event.enabledRevers ? View.VISIBLE : View.GONE);

        if (!event.mainInterface.contains(getCurrentInterfaceName())) {
            initPages(!isPage0);
        }
    }

    @Subscribe
    public void onBusUpdateDefMars(BusUpdateDefMars event) {
        if (!isVisible())
            return;
        foneContMaps.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isVisible()) {
                    foneContMaps.setVisibility(View.GONE);
                }
            }
        }, 500);
        initMaps();
    }

    private void initMaps() {


        SettingsStore settingsStore = Injector.getSettingsStore();
        switch (settingsStore.getDefMars()) {

            case SettingsStore.MAPS_GOOGLE:
                FGoogle fGoogle = (FGoogle) FGoogle.newInstance();


                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.cont_maps, fGoogle)
                        .commitAllowingStateLoss();
                break;
            case SettingsStore.MAPS_OSM:
                initOsm();
                break;
        }


    }

    @Override
    public void initOsm() {
        FOsmMaps fOsmMaps = (FOsmMaps) FOsmMaps.newInstance(!isPage0);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cont_maps, fOsmMaps)
                .commitAllowingStateLoss();

    }

    @Subscribe
    public void updateMenuPay(BusUpdMenuPay e) {
        if (!isVisible())
            return;

        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initPayCont();
    }

    @OnClick(R.id.frmb_cont_pay)
    public void showV2FSetCashOrder() {

        if (!Injector.getSettingsStore().isOnRegClient())
            return;
        setObjectRestoryV3Route("MenuSetPayments");
        showMenuSetPayments();
    }

    private void initCashView() {
        contCashPayments.removeAllViews();
        final ClientData clientData = getClientData();
        final AccountState accountState = clientData.accountState;

        final ArrayList<ClientData.CashList> cashLists = clientData
                .getCashList();
        if (cashLists == null) {
            return;
        }

        int size = cashLists.size();
        final View view1 = LayoutInflater.from(contCashPayments.getContext())
                .inflate(R.layout.section_account
                        , contCashPayments
                        , false);
        view1.setVisibility(View.GONE);
        for (int i = 0; i < size; i++) {
            final ClientData.CashList cashList = cashLists.get(i);


            PaymentMethod paymentMethod = cashList.paymentMethod;

            final View view = LayoutInflater.from(contCashPayments.getContext())
                    .inflate(R.layout.section_cash
                            , contCashPayments
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

                            getAWork().showFCardSettings(accountState, Constants.VENDOR_FROUTE);
                        }
                    });
                }
                summ.setText(String.format("%s%s",
                        summValue,
                        Injector.getWorkSettings().getCurrencyShort()));

                contCashPayments.addView(view1);


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
            String name = cashList.name;
            nameTipeCash.setText(name);
            iconCh.setVisibility(clientData.cashPos == i ? View.VISIBLE : View.INVISIBLE);
            if (clientData.cashPos == i) {
                if (!isCreditCard) {
                    TintIcons.tintImageViewBrend(icon);
                }
                nameTipeCash.setTextColor(initColor(R.color.colorPrimary));
                summ.setTextColor(initColor(R.color.colorPrimary));
            }

            final int finalI = i;
            parentCont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clientData.cashPos == finalI) {
                        //   progressView.setVisibility(View.GONE);
                        return;
                    }
                    isRecreate = true;

                    onClickPosPayment(clientData, finalI, view);

                }
            });

            contCashPayments.addView(view);
        }
    }

    private void onClickPosPayment(ClientData clientData, int finalI, View view) {
        progressView.setVisibility(View.VISIBLE);
        clientData.cashPos = finalI;
        Object tag = view.getTag();
        if (tag instanceof PaymentMethod) {
            PaymentMethod paymentMethodSelect = (PaymentMethod) tag;
            clientData.setPaymentMethodSelect(paymentMethodSelect);
        } else
            clientData.setPaymentMethodSelect(new PaymentMethod("cash"));

        App.bus.post(new BusUpdMenu());
        restFroute.getTarif(progressView, isRecreate);
    }

    private void showMenuSetPayments() {
        initCashView();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;
                slidingMenuSetPayments.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }, 50);

    }

    @OnClick(R.id.fr_cont_time_close)
    public void closeTimePreorde() {
        Injector.getClientData()
                .getTempObjectUIMRoute().setTimeOrder(null);
        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initTimeOrder();

    }

    public void animateAdress0LoopProgress() {

        animateAnimate0.start();
        animateAnimate1.start();
        animateAnimate2.start();

        runnableAnimateAdress0 = new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;
                if (contAnimateAdress0.getVisibility() == View.VISIBLE) {
                    animateCancelAdress0();
                    animateAdress0LoopProgress();
                }
            }
        };
        mHandler.postDelayed(runnableAnimateAdress0, 1000);


    }

    private void animateCancelAdress0() {
        if (animateAnimate0 != null) {
            animateAnimate0.cancel();
        }
        if (animateAnimate1 != null) {
            animateAnimate1.cancel();
        }
        if (animateAnimate2 != null) {
            animateAnimate2.cancel();
        }

        fraAnimate0.setAlpha(0);
        fraAnimate1.setAlpha(0);
        fraAnimate2.setAlpha(0);

        mHandler.removeCallbacks(runnableAnimateAdress0);
    }

    private void animateHidePinMaps(View[] viewsHidePinMaps) {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.animateHidePinMaps();

        subMenuButton.animate()
                .translationY(getPX(isPage0 ? 200 : 320))
                .setDuration(250);

        contCost.animate()
                .translationY(getPX(isPage0 ? 200 : 320))
                .setDuration(250);

        for (View view : viewsHideUIPinMaps) {
            view
                    .animate()
                    .alpha(0)
                    .setDuration(250);
        }

        for (View view : viewsHidePinMaps) {
/*            view
                    .animate()
                    .alpha(0)
                    .y(getPX(24))
                    .scaleX(0).scaleY(0)
                    .setDuration(250);*/
        }

    }

    private void animateShowPinMaps(View[] viewsHidePinMaps) {


        subMenuButton.animate()
                .translationY(0)
                .setDuration(250);

        contCost.animate()
                .translationY(0)
                .setDuration(250);


        for (View view : viewsHideUIPinMaps) {
            view
                    .animate()
                    .alpha(1)
                    .setDuration(250);
        }
        for (View view : viewsHidePinMaps) {
            view
                    .animate()
                    .alpha(1)
                    .y(0)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(250);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;
                hideValuePin();
                allShowProgressPin();
            }
        }, 250);

    }

    @Subscribe
    public void onBusStartOrStopPinAnimate(BusStartOrStopPinAnimate event) {
        if (!isVisible())
            return;
        if (event.isStart) {
            allShowProgressPin();
        } else {
            allStopProgressPin();
        }
    }

    private void allShowProgressPin() {
        if (aPinFroute == null) {
            return;
        }
        aPinFroute.animateShowProgressPin(passender);
        if (isBusDisabledTarif) {
            aPinFrouteEServis.animateShowProgressPin(errorIcServis);
        }
    }

    @Subscribe
    public void onBusDisabledTarif(BusDisabledTarif event) {
        if (!isVisible()) return;

        if (event.kind == null || "stub".equals(event.kind)) {
            hideMapsElementUI(event);
        } else
            showMapsElementUI();

        allStopProgressPin();
    }

    @Override
    public void allStopProgressPin() {
        aPinFroute.animateStopProgressPin();
        aPinFrouteEServis.animateStopProgressPin();
        if (textMin.getVisibility() == View.VISIBLE) {
            passender.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onBusEnabledTarif(BusEnabledTarif event) {
        if (!isVisible()) return;
        typeGeocodePin();
        showMapsElementUI();
        //   allStopProgressPin();

    }

    private void showMapsElementUI() {

        if (!isBusDisabledTarif)
            return;
        getAWork().showTopFragment(V3FRoute.newInstance(isPage0));

    }

    private void hideMapsElementUI(BusDisabledTarif event) {
        String message = event.message;
        swowDisMes(message);
    }

    private void swowDisMes(String message) {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.clearMarkers();
        if (isBusDisabledTarif)
            return;

        isBusDisabledTarif = true;
        iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.initErrorGeozoneMapReady();

        for (View v : hideErrorRegionView) {
            v.setVisibility(View.INVISIBLE);
        }
        errorContMarkerCenter.setVisibility(View.VISIBLE);
        errorTarifWork.setVisibility(View.VISIBLE);
        errorLocationDef.setVisibility(View.VISIBLE);

        errorTarifText.setText(message);
    }

    @Override
    public boolean onBackPressed() {
        if (isErrorNet) {
            System.exit(0);
            return true;
        }
        if (isBusDisabledTarif)
            return false;

        if (contFullTarif.getVisibility() == View.VISIBLE) {
            contFullTarif.setVisibility(View.GONE);
            return true;
        }


        Injector.getClientData().setTempObjectUIMRoute(null);
        getClientData().setCreateRequest(null);
        getAWork().closeApp();
        return true;

    }

    @OnClick(R.id.fr_revers_page)
    public void onRevertPag() {
        objectRestoryV3RouteVendor = "onRevertPag";
        mHandler.removeCallbacks(runnablePages);


        if (animatorPages != null) {
            animatorPages.cancel();
        }
        animatorPages = AnimatorInflater.loadAnimator(getAWork(),
                isPage0 ? R.animator.fr_pages_right : R.animator.fr_pages_left);

        animatorPages.setTarget(getView());


        animatorPages.start();


        runnablePages = new Runnable() {
            @Override
            public void run() {
                if (!isVisible())
                    return;
                initPages(!isPage0);
                IUPmaps iuPmaps = getIuPmaps();
                if (iuPmaps != null)
                    iuPmaps.initMapReady(!isPage0);

            }
        };
        mHandler.postDelayed(runnablePages, 150);


        isOnClickRevertPag = !isOnClickRevertPag;


    }

    private void initPages(boolean b) {
        isPage0 = b;
        if (b) {
            showPage0();
        } else {
            showPage1();
        }

        if (!isVisible())
            return;
        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initTimeOrder();

        initHederMenuRoute();
    }

    @Subscribe
    public void onBusUpdArrTariff(BusUpdArrTariff e) {
        if (!isVisible())
            return;
        initSmallTariff();

    }

    @Subscribe
    public void onBusUpdTarif(BusUpdTarif e) {
        if (!isVisible())
            return;
        if (Injector.getClientData().hashDefPosTariff != Injector.getClientData().getDefPosTariff()) {
            initOptionsList();
            if (getClientData().getMarkerLocation() != null) {
                getDrivers(getClientData().getMarkerLocation());
                getAWork().getBonusClient();
            }
        }
        Injector.getClientData().hashDefPosTariff = Injector.getClientData().getDefPosTariff();
        getCost();
        contFullTarif.setVisibility(View.GONE);
        if (isRestartInitSmallTariff) {
            isRestartInitSmallTariff = false;
            initSmallTariff();
            return;
        }
        currentItemSmall = Injector.getClientData().getDefPosTariff();
        ViewPager viewPager = smallContainer.getViewPager();
        if (viewPager == null)
            return;

        viewPager.setCurrentItem(currentItemSmall);
    }

    @Subscribe
    public void onBusSetFullTariff(BusSetFullTariff e) {
        if (!isVisible())
            return;

        isRestartInitSmallTariff = true;
        Injector.getClientData().setDefPosTariff(e.pos);
    }

    private void initSmallTariff() {

        final ClientData clientData = Injector.getClientData();
        List<Tarif> arrTarif = clientData.arrTarif;
        if (arrTarif == null || arrTarif.size() == 0) {
            return;
        }

        initFullListTariff(clientData, arrTarif);

    }

    private void initFullListTariff(final ClientData clientData, List<Tarif> arrTarif) {
        smallContainer.setVisibility(View.INVISIBLE);
        smallContainer.setVisibility(View.VISIBLE);
        final ViewPager pager = smallContainer.getViewPager();
        currentItemSmall = clientData.getDefPosTariff();
        pager.setAdapter(new SmallMyPagerAdapter(arrTarif
                , currentItemSmall
                , new ISelectItem() {
            @Override
            public void onClickPosition(int position) {


                if (clientData.getDefPosTariff() == position) {

                    return;
                }

                ///
                currentItemSmall = position;

                Injector.getClientData().setDefPosTariff(position);

            }
        }, new IIsClickDefault() {
            @Override
            public void onClickPosition(int pos) {


                final boolean cl = pager.getCurrentItem() == clientData.getDefPosTariff()
                        && clientData.getDefPosTariff() == pos;
                if (!cl)
                    return;


                initFullTariff();
                contFullTarif.setVisibility(View.VISIBLE);


            }
        }));

        pager.setClipChildren(false);
        pager.setOffscreenPageLimit(35);
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) pager.getLayoutParams();
        FrameLayout.LayoutParams layoutLinesDef =
                (FrameLayout.LayoutParams) linesDef.getLayoutParams();

        layoutParams.width = (int) (deviceScreen.widthPixels / 3);
        layoutLinesDef.width = (int) (deviceScreen.widthPixels / 3);
        pager.setPageMargin(0);
        pager.setCurrentItem(currentItemSmall);
    }

    private void initFullTariff() {
        final ViewPager pager = fullContainer.getViewPager();

        ClientData clientData = Injector.getClientData();
        List<Tarif> arrTarif = clientData.arrTarif;
        if (arrTarif == null || arrTarif.size() == 0) {
            return;
        }

        currentItemSmall = clientData.getDefPosTariff();
        FullMyFragmentPagerAdapter pagerAdapter =
                new FullMyFragmentPagerAdapter(getChildFragmentManager(), arrTarif);
        pager.setAdapter(pagerAdapter);

        fullContainer.setOverlapEnabled(true);
        pager.setClipChildren(false);
        pager.setOffscreenPageLimit(35);

        new CoverFlow.Builder()
                .with(pager)
                .scale(0.3f)
                .pagerMargin(getResources().getDimensionPixelSize(R.dimen.overlap_pager_margin))
                .spaceSize(0f)
                .build();

        pager.setCurrentItem(currentItemSmall);

    }

    private void showPage1() {

        for (View v : hideView) {
            v.setVisibility(View.VISIBLE);
        }
        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initPanelHeight();

    }

    private void clearOrder() {
        if (!isVisible())
            return;

        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        if (createRequest != null) {
            ArrayList<ClientAddress> route = createRequest.getRoute();
            int size = route.size();
            if (size > 1) {
                while (route.size() != 1) {
                    route.remove(route.size() - 1);
                }
            }

            createRequest.setRoute(route);
            createRequest.comment = null;
        }

        TempObjectUIMRoute tempObjectUIMRoute = Injector.getClientData().getTempObjectUIMRoute();
        if (tempObjectUIMRoute != null) {
            tempObjectUIMRoute.optionsClient = null;

            tempObjectUIMRoute.setTimeOrder(null);
        }
        sityAdress1.setText("");
        Injector.getClientData().cashPos = 0;
        adressValueItem1.setText(getString(R.string.to_vojge));
        Injector.getClientData().setPaymentMethodSelect(new PaymentMethod("cash"));
        getAWork().getTarif(Injector.getClientData().latLngTarifHTPSRequest);

    }

    private void showPage0() {
        clearOrder();

        slidingUpPanelLayout.setPanelHeight(getPX(getPageDP()));


        for (View v : hideView) {
            if (v.getId() == R.id.adress_pos)
                v.setVisibility(View.INVISIBLE);
            else
                v.setVisibility(View.GONE);
        }

        UtilitesMaps
                .instanse()
                .setMarginButtonView(getPageDP() - 1, marginButtonView);

    }

    @Override
    public void onMapReady() {
        if (!isVisible()) return;
        if (Injector.getClientData().isRecreateLocale) {
            getAWork().showFProfil();
            return;
        }

        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.initMapReady(null, !isPage0);
        //  if (gisCreateRequest()) return;
        gisCreateRequest();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Injector.getClientData().hashDefPosTariff == -10)
            Injector.getClientData().updTarif();
    }

    @Override
    public void onDestroyView() {
        removeGOLListener(adressValueItem0.getViewTreeObserver(), listenerCountOptions);
        removeGOLListener(adressValueItem1.getViewTreeObserver(), listenerAdressValueItem1);
        animateCancelAdress0();

        if (animatorPages != null) {
            animatorPages.cancel();
        }

        aPinFroute.finish();
        aPinFrouteEServis.finish();
        super.onDestroyView();

    }

    @Subscribe
    public void onBusGPSLocation(BusGPSLocation e) {
        if (!isVisible()) return;
        Location location = e.location;

        double lon = location.getLongitude();
        double lat = location.getLatitude();

        Injector.getSettingsStore().writeString("LAT_MEMORY", String.valueOf(lat));
        Injector.getSettingsStore().writeString("LON_MEMORY", String.valueOf(lon));


    }

    private void showDialogInfoUrgent() {
        DialogClient.showOneButtonDialog(
                getString(R.string.title_urgent_dialog),
                App.app.hashBC.textInfoAddPrice,
                this);
    }

    private boolean isEnabledAddPrice() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        boolean b = false;

        if (createRequest != null) {
            List<ClientAddress> route = createRequest.getRoute();
            b = route.size() > 1;
        }


        return Injector.getClientData().getCostChangeAllowed() && b;

    }

    private void getCost() {
        if (!Injector.getClientData().isEnabledService()) {
            return;
        }
        contInfoKmMin.setVisibility(View.GONE);
        valueKmMin.setText("");
        editCost.setEnabled(false);
        uiOrder = null;
        getAWork().showWorkProgress();
        editCost.setVisibility(isEnabledAddPrice() ? View.VISIBLE : View.GONE);
        restFroute.getCostRest();
    }

    private void showInfoKmRoute(double metr) {

        double unscaledVal = metr / 1000f;
        BigDecimal bigDecimalDistanceKm = BigDecimal.valueOf(unscaledVal)
                .setScale(1,
                        BigDecimal.ROUND_CEILING);
        float averageSpeed = Injector
                .getWorkSettings()
                .getAverageSpeed();

        double v = unscaledVal / averageSpeed;
        int hur = (int) v;

        String km = String.format(getString(R.string.f_r_info_km),
                bigDecimalDistanceKm.toString());

        int minutes = (int) (60 * (v - hur));


        String m = getString(R.string.minutes_reduced);

        String time = String.format("%s%s",
                hur != 0 ? hur + ":" : "",
                minutes < 10 && hur != 0 ?
                        "0" + minutes
                        : minutes + (hur != 0 ? "" : m));

        valueKmMin.setText(
                String.format("%s %s", km, time));

        contInfoKmMin.setVisibility(View.VISIBLE);
    }

    public void bodyGetCost(ApiResponse apiResponse) {
        if (!isVisible())
            return;
        Estimation estimation = apiResponse.estimation;
        double distance = estimation.distance;
        if (distance > 0) {
            showInfoKmRoute(distance);
        }
        editCost.setEnabled(true);
        if (isRecreate) {

            slidingMenuSetPayments.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        }
        uiOrder = new UIOrder(estimation);
        CostModifier costModifier = uiOrder.getCostModifier();
        if (costModifier != null) {

            buttomUrgent1.setVisibility(View.VISIBLE);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInfoUrgent();
                }
            };

            buttomUrgent1.setOnClickListener(onClickListener);


        } else {
            buttomUrgent1.setVisibility(View.GONE);
        }


        String textDec = uiOrder.getTextDec();
        String valueType = uiOrder.getValueType();
        String summ = uiOrder.getSumm();
        Tarif defTarif = getClientData().getDefTarif();

        String hint = defTarif.hint;
        boolean showEstimation = defTarif.showEstimation;

        if ((hint == null || "".equals(hint)) && showEstimation) {
            initBaseCBLayout(textDec, valueType, summ);
        } else if ((hint != null && !"".equals(hint)) && showEstimation) {
            initHINTCBLayout(textDec, valueType, summ);
            initHintTarif(hint);
        } else if ((hint != null && !"".equals(hint)) && !showEstimation) {
            initHintTarif(hint);
            bCBLoayout.setVisibility(View.GONE);
            layoutHint.setVisibility(View.GONE);
        } else {
            bCBLoayout.setVisibility(View.GONE);
            hintTarif.setVisibility(View.GONE);
            layoutHint.setVisibility(View.GONE);
        }
    }

    private void initHintTarif(String hint) {
        hintTarif.setVisibility(View.VISIBLE);
        hintTarif.setText(hint);
    }

    private void initHINTCBLayout(String textDec, String valueType, String summ) {
        bCBLoayout.setVisibility(View.GONE);
        layoutHint.setVisibility(View.VISIBLE);

        if (valueType != null) {

            valueCostPrefixHint.setVisibility(View.VISIBLE);
            valueCostPrefixHint.setText(valueType);
        } else {
            valueCostPrefixHint.setVisibility(View.GONE);
        }

        if (summ != null) {
            routeCostValueHint.setText(summ);

        }

        if (textDec != null) {
            costValueDecHint.setText(textDec);
            costValueDecHint.setVisibility(View.VISIBLE);
        } else
            costValueDecHint.setVisibility(View.GONE);
        currencyShortHint.setText(Injector.getWorkSettings().getCurrencyShort());
    }

    private void initBaseCBLayout(String textDec, String valueType, String summ) {
        bCBLoayout.setVisibility(View.VISIBLE);
        hintTarif.setVisibility(View.GONE);
        layoutHint.setVisibility(View.GONE);
        if (valueType != null) {

            valueCostPrefix.setVisibility(View.VISIBLE);
            valueCostPrefix.setText(valueType);
        } else {
            valueCostPrefix.setVisibility(View.GONE);
        }

        if (summ != null) {
            routeCostValue.setText(summ);

        }

        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        currencyShort.setText(Injector.getWorkSettings().getCurrencyShort());
    }

    @OnClick(R.id.fr_cont_full_tarif)
    public void onContFullTarif() {
        contFullTarif.setVisibility(View.GONE);
    }

    @OnClick({R.id.fr_set_location_def, R.id.fr_error_location_def})
    public void setMyLocClickUsers() {
        if (!isVisible())
            return;

        ClientData clientData = Injector.getClientData();
        CreateRequest createRequest = clientData.getCreateRequest();

        if (!isVisible() || createRequest == null) {
            return;
        }
        isClickMyLoc = true;
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.setMyLocation();

        adressValueItem0.setText("");
        animateStartAdress0();
    }

    @OnClick(R.id.ic_show_left_menu)
    public void onShowLeftMenu() {
        getAWork().toglLeftMenu();
    }

    // доработать
    @OnClick(R.id.edit_cost)
    public void onEditCost() {
        if (!isEnabledAddPrice())
            return;

        if (uiOrder == null) {
            editCost.setEnabled(false);
            return;
        }

        setObjectRestoryV3Route("FEditCost");
        getAWork().showFEditCost(uiOrder);
    }

    private void setObjectRestoryV3Route(String vendor) {
        getAWork().setObjectRestoryV3Route(new ObjectRestoryV3Route(isPage0, false, vendor));
    }

    @OnClick({R.id.froute_sl_panel_time_preorder_close_menu})
    public void onHideTimeOrderPopu() {
        slidingPanelTimePreorder.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @OnClick(R.id.time_order_cont)
    public void onShowTimeOrderPopu() {
        slidingPanelTimePreorder.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @OnClick(R.id.time_preorder_15)
    public void setTimePreorder15() {

        initDeltaPreorder(15);
    }

    private void initDeltaPreorder(int minutes) {
        slidingPanelTimePreorder.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        long cts = System.currentTimeMillis() + 60 * minutes * 1000; // +15min
        DateTime dateTime = new DateTime(cts);
        DateTimeFormatter formatterISO = DateTimeFormat.forPattern("yyyy-MM-dd")
                .withLocale(Locale.ENGLISH);
        int hur = dateTime.getHourOfDay();
        int min = dateTime.getMinuteOfHour();

        String formattedDateIso = formatterISO.print(cts) + "T";
        String timeHurMin = (hur < 10 ? "0" + hur : hur) + ":" + (min < 10 ? "0" + min : min);
        String time = formattedDateIso + timeHurMin +
                ":00";

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM")
                .withLocale(Locale.getDefault());
        String dateValue = formatter.print(cts);

        TempObjectUIMRoute.TimeOrder timeOrder = new TempObjectUIMRoute.TimeOrder(-100,
                "",
                timeHurMin,
                time, dateValue);

        Injector.getClientData().getTempObjectUIMRoute()
                .setTimeOrder(timeOrder);

        InitFRoute initFRoute = getInitFRoute();
        initFRoute.initTimeOrder();
    }

    @OnClick(R.id.time_preorder_30)
    public void setTimePreorder30() {
        initDeltaPreorder(30);
    }

    @OnClick({
            R.id.time_preorder_other_time})
    public void onSetTimeOrder() {
        setObjectRestoryV3Route("V2FSetTimeOrder");
        getAWork().showV2FSetTimeOrder();
    }

    @OnClick(R.id.adress_value_item0)
    public void onStartAdress() {
        setObjectRestoryV3Route("V2FFullTextSearch");
        getAWork().showV2FFullTextSearch(0, Constants.EDIT_ADRESS);

    }

    @OnClick(R.id.fr_cont_adress1)
    public void onAdressValueItem1() {
        CreateRequest createRequest = getClientData().getCreateRequest();

        if (createRequest.getSizeRoute() == 0 || createRequest.getSizeRoute() == 1) {
            setObjectRestoryV3Route("V2FFullTextSearch");
            getAWork().showV2FFullTextSearch(-1, Constants.ADD_ADRESS);
        } else if (createRequest.getSizeRoute() == 2) {
            setObjectRestoryV3Route("V2FFullTextSearch");
            getAWork().showV2FFullTextSearch(1, Constants.EDIT_ADRESS);
        } else if (createRequest.getSizeRoute() > 2) {
            setObjectRestoryV3Route("showFAddEditAdress");
            getAWork().showFAddEditAdress();
        }

    }

    @OnClick(R.id.ic_plus_button)
    public void onPlusButton() {
        setObjectRestoryV3Route("V2FFullTextSearch");
        getAWork().showV2FFullTextSearch(-1, Constants.ADD_ADRESS);
    }

    @OnClick(R.id.set_porch)
    public void onShowFAddressFeed() {
        setObjectRestoryV3Route("FAddressFeed");
        getAWork().showFAddressFeed(0, false);
    }

    @OnClick(R.id.fr_send_order_servers)
    public void sendServers(View view) {

        ClientData clientData = Injector.getClientData();
        final CreateRequest createRequest = clientData.getCreateRequest();
        if (createRequest == null)
            return;


        String readRegKey = Injector.getSettingsStore()
                .readString(Constants.REG_KEY_CLIENT, null);

        if (readRegKey == null) {
            DialogClient.alertInfo(getResources().getString(R.string.necessary_register), getAWork());
            getAWork().showV2FRegistration(false, true);
            return;
        }


        if (clientData.isEnabledShowV2Bonuses()) {
            setObjectRestoryV3Route("V2FBonuses");
            getAWork().showV2FBonuses(0, true);
        } else
            sendServersBody();
    }

    @SuppressLint("DefaultLocale")
    private void initOptionsList() {
        int size = getSizeOptions();

        if (size > 0) {
            Typeface font = Typeface.createFromAsset(Injector.getAppContext().getAssets()
                    , "fonts/Roboto/Roboto-Bold.ttf");
            setOptions.setVisibility(View.VISIBLE);
            String siseString = String.valueOf(size);
            String text = String.format(getString(R.string.frmb_menu_options_value), siseString);


            SpannableString redName = new SpannableString(text);
            redName.setSpan(new CustomTypefaceSpan(font)
                    , text.length() - siseString.length(), text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


            setOptions.setText(redName);
        } else {
            setOptions.setVisibility(View.GONE);
            setOptions.setText("");
        }
        CreateRequest createRequest = getClientData().getCreateRequest();
        if (createRequest != null) {
            initCommentText(createRequest);
        }
    }

    private int getSizeOptions() {
        final TempObjectUIMRoute mRoute = Injector.getClientData().getTempObjectUIMRoute();
        final ArrayList<Option> optionsClient = mRoute.getOptionsClient();
        return optionsClient.size();
    }

    private void initVtoAdressDopInfo() {
        //  initVto(adressDopInfo);
    }


    private void initVtoCountOptions() {
        initVto(setOptions);
    }

    private void initVto(TextView textView) {
        ViewTreeObserver vtoCountOptions = textView.getViewTreeObserver();
        ViewTreeObserver.OnGlobalLayoutListener listenerCountOptions = LongTextView
                .getOnGlobalLayoutListener(textView, getContext(), false);
        vtoCountOptions.addOnGlobalLayoutListener(listenerCountOptions);
    }

    private void initHederMenuRoute() {
        initVtoCountOptions();
        initVtoAdressDopInfo();

        contInfoKmMin.setVisibility(View.GONE);
        CreateRequest createRequest = getClientData().getCreateRequest();


        if (createRequest != null) {
            List<ClientAddress> route = createRequest.getRoute();
            int size1 = route.size();

            frameMaps.setClickable(false);
            setLocationDef.setVisibility(View.VISIBLE);


            for (int i = 0; i < size1; i++) {
                ClientAddress clientAddress = route.get(i);
                if (clientAddress == null && i != size1 - 1) {
                    route.remove(i);
                    break;
                }

            }

            int count = createRequest.getSizeRoute();


            initCommentText(createRequest);

            if (count > 1) {
                icPlusButton.setVisibility(View.VISIBLE);
                adressValueItem1.setTextColor(initColor(R.color.text_default_color));
                tipeAdress1.setVisibility(View.VISIBLE);
            }

            if (count == 2) {


                ClientAddress clientAddress = createRequest.getRoute().get(1);
                if (clientAddress == null) {
                    adressValueItem1.setText(getString(R.string.to_vojge));
                } else {
                    String textAdress = createRequest.getTextStartAdress(1);
                    //   initInfoKmTime(createRequest.getRouteLocation());
                    adressValueItem1.setText(textAdress.trim());
                    String sityAdress1NameParent = createRequest.getStartNameParent(1);
                    String alias = createRequest.getAliasAdress(1);
                    String text = alias == null ? sityAdress1NameParent : alias;

                    sityAdress1.setText(text != null ? text : "");
                }

            }
            if (count > 2) {
                String sityNameParentStart = createRequest.getStartNameParent(0);
                adressValueItem1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                StringBuilder textValue = new StringBuilder();

                for (int i = 1; i < count; i++) {
                    String sityNameParent = createRequest.getStartNameParent(i);
                    if (sityNameParent == null)
                        sityNameParent = "";
                    final String imgString =
                            " # ";
                    String value = sityNameParent + " ";
                    if (sityNameParent.equals(sityNameParentStart))
                        value = "";

                    String textStartAdress = createRequest.getTextStartAdress(i);

                    textValue.append(value).append(textStartAdress).append(i == count - 1 ? ""
                            : imgString);
                }


                listenerAdressValueItem1 = LongTextView
                        .getSetCountOnGlobalLayoutListener(adressValueItem1, count - 1);
                adressValueItem1.getViewTreeObserver().addOnGlobalLayoutListener(listenerAdressValueItem1);


                String trim = textValue.toString().trim();

                SpannableString text = new SpannableString(trim);
                for (int i = 0; i < trim.length(); i++) {
                    char ch = trim.charAt(i);
                    if (ch == '#') {
                        insertSmiles(text, i);
                    }
                }


                adressValueItem1.setText(text);
                //    initInfoKmTime(createRequest.getRouteLocation());

            }


            if (count > 0) {
                String textStartAdress = createRequest.getTextStartAdress(0);

                if (textStartAdress == null)
                    return;
                contAnimateAdress0.setVisibility(View.GONE);
                animateCancelAdress0();

                adressValueItem0.setText(textStartAdress.trim());

                //      adressValueItem0.setVisibility(View.VISIBLE);


                String textStartAdressDopInfo = createRequest.getTextStartAdressDopInfo();

                if (textStartAdressDopInfo != null)
                    adressDopInfo.setText(textStartAdressDopInfo.trim());

                String sityAdress0NameParent = createRequest.getStartNameParent(0);


                if (sityAdress0NameParent != null) {
                    sityAdress0.setText(sityAdress0NameParent);

                }

                initStartItem0Adress();
            }

            initOptionsList();
            getCost();
        }

    }

    private void initCommentText(CreateRequest createRequest) {
        if (createRequest.comment != null) {
            String replace = createRequest.comment.trim().replace("\n", " ");

            commentOrder.setText(Utilites.capitalize(replace));
        } else {
            int size = getSizeOptions();
            commentOrder.setText(getString(size == 0 ?
                    R.string.frmb_comment_value : R.string.frmb_comment_value_lingh));
        }
    }

    // перенести
    private void insertSmiles(SpannableString text, int pos) {

        ImageSpan is = new ImageSpan(getContext(), R.drawable.ic_next_arrow_client,
                ImageSpan.ALIGN_BASELINE) {
            public void draw(Canvas canvas, CharSequence text, int start,
                             int end, float x, int top, int y, int bottom,
                             Paint paint) {
                Drawable b = getDrawable();
                canvas.save();

                int transY = bottom - b.getBounds().bottom;
                // this is the key
                transY -= paint.getFontMetricsInt().descent / 2;

                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        };
        text.setSpan(is, pos, pos + 1, Spannable.SPAN_COMPOSING);
    }


    private void hideValuePin() {

        if (valueMin != null)
            valueMin.setVisibility(View.GONE);
        if (textMin != null)
            textMin.setVisibility(View.GONE);
    }

    private void showValuePin() {
        passender.setVisibility(View.GONE);
        allStopProgressPin();
        if (valueMin != null)
            valueMin.setVisibility(View.VISIBLE);
        if (textMin != null)
            textMin.setVisibility(View.VISIBLE);

    }

    private void initStartItem0Adress() {

        listenerCountOptions = LongTextView
                .setStartAdressCountOnGLListener(adressValueItem0);
        adressValueItem0.getViewTreeObserver().addOnGlobalLayoutListener(listenerCountOptions);
    }

    @SuppressLint("DefaultLocale")
    private void getDrivers(final LatLng latLng) {

        if (isBlockSendServers || isBusDisabledTarif) {
            passender.setVisibility(View.VISIBLE);
            allStopProgressPin();
            return;
        }

        if (isErrorNet) {
            allStopProgressPin();
            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.clearMarkers();
            return;
        }
        restFroute.getDrivers(latLng);
    }

    public void bodyGetDrivers(ApiResponse apiResponse, LatLng latLng) {
        final List<Driver> driverList = apiResponse.driverList;

        double distanceBetweenMin = -1;


        for (Driver driver : driverList) {
            if (driver == null || latLng == null)
                return;

            driver.rotade = (int) (Math.random() * 360);

            if (driver.location == null || driver.location.getLatLng() == null)
                continue;

            LatLng lngDriver = driver.location.getLatLng();
            double distanceBetween =
                    SphericalUtil.computeDistanceBetween(latLng, lngDriver);
            if (distanceBetweenMin == -1
                    || distanceBetween < distanceBetweenMin) {
                distanceBetweenMin = distanceBetween;

            }


        }

        float averageSpeed = Injector
                .getWorkSettings()
                .getFloatAverageSpeed(); // метров в минуту

        double valMin = distanceBetweenMin / averageSpeed;


        BigDecimal bigDecimal = BigDecimal.valueOf(valMin)
                .setScale(0,
                        BigDecimal.ROUND_CEILING);

        String text = bigDecimal.toString();
        valueMin.setText(text);

        showValuePin();
        passender.setVisibility(View.GONE);

        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.showDrivers(driverList);
    }

    public void errorGetDrivers() {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.clearMarkers();
        passender.setVisibility(View.VISIBLE);
        allStopProgressPin();
    }

/*    public void showPassender() {
        if (passender != null)
            passender.setVisibility(View.VISIBLE);
    }

    public void hidePassender() {
        if (passender != null)
            passender.setVisibility(View.GONE);
    }*/

    @Override
    public void onCameraIdle(final LatLng latLng, final boolean isEnabledUpdTarif) {
        if (latLng == null || latLng.latitude == 0.0
                || latLng.longitude == 0.0)
            return;
/*
        if(isStartIdle) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isStartIdle = false;
                }
            }, 200);
        }
*/

        if (passender == null || !isVisible())
            return;
        Log.e("Retr", "onCameraIdle");


        CreateRequest createRequest = Injector.getClientData().getCreateRequest();

        if (createRequest == null) {

            return;
        }
        if ((objectRestoryV3RouteVendor != null || isShowMenu)) {
            objectRestoryV3RouteVendor = null;
            getDrivers(latLng);
            return;
        }


        textMin.setVisibility(View.GONE);
        valueMin.setVisibility(View.GONE);
        adressDopInfo.setText("");

        isBlockSendServers = false;
        animateShowPinMaps(viewsHidePinMaps);

        if (runnableCameraIdle != null)
            mHandler.removeCallbacks(runnableCameraIdle);

        runnableCameraIdle = new Runnable() {
            @Override
            public void run() {
                if (textMin == null || !isVisible())
                    return;

                if (isEnabledUpdTarif || isBusDisabledTarif) {

                    getAWork().getCurrentTarif();
                } else if (getClientData().isEnabledService()) {
                    Injector.getClientData().setMarkerLocation(latLng);
                    typeGeocodePin();
                }

            }
        };

        mHandler.postDelayed(runnableCameraIdle, 500);

    }

    @Override
    public void onCameraMoveStarted(int i) {

        CreateRequest createRequest = Injector.getClientData().getCreateRequest();


        if (!isVisible()
                || createRequest == null)

            return;

        if (i == 3 && !isClickMyLoc)
            return;

        if (objectRestoryV3RouteVendor != null || isShowMenu) {

            return;
        }

        isClickMyLoc = false;
        adressValueItem0.setText("");
        animateStartAdress0();
        animateHidePinMaps(viewsHidePinMaps);
        isBlockSendServers = true;

        hideValuePin();

        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.clearMarkers();

    }

    private void animateStartAdress0() {
        animateCancelAdress0();
        contAnimateAdress0.setVisibility(View.VISIBLE);
        animateAdress0LoopProgress();
    }


    @Subscribe
    public void onBusUpdUIAdressMapsYaGoogle(UpdUIAdressMapsYaGoogle event) {

        if (!isVisible() || event == null || event.adress == null) {
            startReverseGeocodingArray(reverseGeocodingLatLng);
            return;
        }


        updUIAdressMaps(event.adress);
    }


    private void typeGeocodePin() {
        reverseGeocodingLatLng = Injector.getClientData().getMarkerLocation();
        adressValueItem0.setText("");
        sityAdress0.setText("");
        getAdressGeocodeServers(reverseGeocodingLatLng);
        reverseGeocodingArrayPos = 0;
    }


    private void getAdressGeocodeServers(final LatLng latLng) {
        reverseGeocodingLatLng = latLng;
        restFroute.getAdressGeocodeServers(latLng);
    }


    public void updUIAdressMaps(Address geo) {
        if (!isVisible()) return;

        adressDopInfo.setVisibility(View.VISIBLE);
        tempAdress0 = geo;
        CreateRequest createRequest = getClientData().getCreateRequest();
        if (createRequest.getSizeRoute() != 0) {


            ArrayList<ClientAddress> route = createRequest.getRoute();
            String stringNameAdress = geo.getStringNameAdress();
            String textStartAdress = createRequest.getTextStartAdress(0);

            if (textStartAdress != null) {

                if (!stringNameAdress.equals(textStartAdress)) {
                    adressValueItem0.setText(textStartAdress.trim());
                    route.set(0, new ClientAddress(geo));
                    adressDopInfo.setText("");
                } else {
                    adressDopInfo.setText(createRequest.getTextStartAdressDopInfo());
                }
            }
            String sityAdress0NameParent = createRequest.getStartNameParent(0);


            if (sityAdress0NameParent != null) {
                sityAdress0.setText(sityAdress0NameParent);

            }

        } else
            setAdressOneStartMap();

        String textStartAdress = geo.getTempAdress();
        if (textStartAdress == null)
            textStartAdress = getString(R.string.point_to_maps);
        adressValueItem0.setText(textStartAdress.trim());
        contAnimateAdress0.setVisibility(View.GONE);
        animateCancelAdress0();

        initStartItem0Adress();

        getCost();
        getDrivers(reverseGeocodingLatLng);
    }

    private void setAdressOneStartMap() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        createRequest.addAdressToRoute(tempAdress0);
        String sityAdress0NameParent = createRequest.getStartNameParent(0);


        if (sityAdress0NameParent != null) {
            sityAdress0.setText(sityAdress0NameParent);

        }
    }


    private void sendServersBody() {
        if (isBlockSendServers || showErrorlimit())
            return;
        sendOrderServers.setAlpha(0.7f);
        sendOrderServers.setClickable(false);
        sendOrderServers.setEnabled(false);
        isBlockSendServers = true;

        restFroute.sendServersBody();

    }

    public void getApiResponseSendOrders(TempObjectUIMRoute mRoute
            , CreateRequest createRequest
            , ApiResponse apiResponse) {
        if (!isVisible())
            return;

        if (apiResponse == null || apiResponse.error != null) {
            getAWork().showErrorIGetApiResponse(apiResponse);
            return;
        }

        mRoute.valueAddCost = 0;
        ResultSendOrders resultSendOrders = apiResponse.resultSendOrders;
        GpsPosition gpsPosition = createRequest.getRouteLocation().get(0);

        Injector.getClientData().initMemoryListNamesCost();

        long orderId = resultSendOrders.id;

        getAWork().showFSearchCar(gpsPosition, orderId);
    }

    @OnClick(R.id.frmb_comment_options)
    public void showFCommetAndOptions() {
        getAWork().showFCommetAndOptions();
    }

    public boolean showErrorlimit() {

        int sizeCurrentOrders = getAWork().sizeCurrentOrders;
        if (sizeCurrentOrders >= App.app.hashBC.limitNumberActiveOrders) {
            alert(getString(R.string.rest_limit_order_info));
            getAWork().showV2FShortOrdersPrivate();
            return true;
        }
        return false;
    }
}

