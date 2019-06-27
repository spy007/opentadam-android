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

package com.opentadam.ui.execution_of_orders;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.SettingsStore;
import com.opentadam.bus.BusArayPingOrderInfo;
import com.opentadam.bus.BusDialogPressed;
import com.opentadam.bus.BusStateFinishOrderId;
import com.opentadam.data.DialogClient;
import com.opentadam.edit_order.ObjecEditList;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.Assignee;
import com.opentadam.network.rest.AssigneeCall;
import com.opentadam.network.rest.Car;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.Cost;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.LinesInfo;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.Params;
import com.opentadam.network.rest.Path;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.Tarif;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.creating_an_order.interface_class.IUtilsV3Route;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.UtilitesDataClient;
import com.opentadam.utils.UtilitesOrder;
import com.opentadam.yandex_google_maps.FGoogle;
import com.opentadam.yandex_google_maps.FOsmMaps;
import com.opentadam.yandex_google_maps.IUPmaps;
import com.squareup.otto.Subscribe;

import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.InjectView;
import butterknife.OnClick;


public class FSearchCar extends BaseFr implements IUtilsV3Route {

    private static final String LAT_LON_ONE_LIST = "latLonOneList";
    @InjectView(R.id.animation_view)
    LottieAnimationView animationView;
    @InjectView(R.id.value_status_body)
    TextView searchState;
    @InjectView(R.id.search_adress_cont)
    LinearLayout searchAdressCont;
    @InjectView(R.id.route_cost_value)
    TextView routeCostValue;
    @InjectView(R.id.search_car_cont)
    LinearLayout searchCarCont;
    @InjectView(R.id.search_color)
    TextView searchColor;
    @InjectView(R.id.search_brand)
    TextView searchBrand;
    @InjectView(R.id.search_reg_num_info)
    TextView searchRegNumInfo;
    @InjectView(R.id.search_info_text)
    TextView searchInfoText;

    @InjectView(R.id.image_status_body)
    ImageView imageStatus;
    @InjectView(R.id.search_info_call)
    ImageView searchInfoCall;

    @InjectView(R.id.work_car_pin)
    ImageView workCarPin;
    @InjectView(R.id.value_currency_short)
    TextView valueCurrencyShort;
    @InjectView(R.id.route_cost_value_prefix)
    TextView valueCostPrefix;
    @InjectView(R.id.route_cost_value_dec)
    TextView costValueDec;
    @InjectView(R.id.button_delete_route)
    TextView buttonDeleteRoute;
    @InjectView(R.id.marker_def)
    ImageView markerDef;
    @InjectView(R.id.frame_maps)
    FrameLayout frameMaps;
    @InjectView(R.id.name_cost)
    TextView nameCost;
    @InjectView(R.id.cont_options)
    LinearLayout contOptions;
    @InjectView(R.id.cont_zoom)
    FrameLayout contZoom;
    @InjectView(R.id.cont_comment_to_route)
    LinearLayout contCommentToRoute;
    @InjectView(R.id.icon_pay)
    ImageView iconPay;
    private double[] latLonOneList;
    private int mStatusActual = Constants.STATE_CREATE;

    private boolean initializedMap;

    private boolean activateRoute = false;
    private Dialog dialog;


    private double mSumCoordRoute = -1;
    private ArrayList<Address> getRoute;
    private AssigneeCall call;
    private OrderInfo orderInfo;
    private long idRoute;


    private boolean mIsMoving;
    private Timer mTimer;


    public static Fragment newInstance(GpsPosition gpsPosition, long idRoute) {
        double[] latLon = null;
        if (gpsPosition != null) {
            latLon = new double[2];
            latLon[0] = gpsPosition.lat;
            latLon[1] = gpsPosition.lon;
        }


        return new FSearchCar().withViewId(R.layout.f_search_car)
                .withArgument(LAT_LON_ONE_LIST, latLon).withArgument("idRoute", idRoute);
    }
    // Запросы

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latLonOneList = getArguments().getDoubleArray(LAT_LON_ONE_LIST);
            idRoute = getArguments().getLong("idRoute");
        }

    }

    @OnClick(R.id.show_menu_left)
    public void onShowMenuLeft() {
        getAWork().showMenu();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAWork().pushOrderId = -1;
        buttonDeleteRoute.setVisibility(View.GONE);

    }


    private void initMaps() {

        SettingsStore settingsStore = Injector.getSettingsStore();
        switch (settingsStore.getDefMars()) {
            case SettingsStore.MAPS_GOOGLE:
                FGoogle fGoogle = (FGoogle) FGoogle.newInstance("searshCar");


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

    public IUPmaps getIuPmaps() {
        return (IUPmaps) getChildFragmentManager().findFragmentById(R.id.cont_maps);
    }

    @Override
    public void initOsm() {
        FOsmMaps fOsmMaps = (FOsmMaps) FOsmMaps.newInstance("searshCar");

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cont_maps, fOsmMaps)
                .commitAllowingStateLoss();
    }

    private void init() {
         getAWork().getPingOrders();
        getAWork().setTitle(R.string.current_order);
    }

    @OnClick(R.id.button_change_order)
    public void onShowMenuEdit() {

        Tarif defTariff = Injector.getClientData().getDefTarif();
        if (orderInfo == null || defTariff == null)
            return;

        boolean isEnabledOptions = defTariff.options.size() != 0;
        ArrayList<ObjecEditList> objecEditLists = new ArrayList<>();
        int state = orderInfo.state;
        switch (state) {
            case Constants.STATE_CREATE:
                objecEditLists.add(new ObjecEditList(
                        getString(R.string.e_o_change_detalies_address), 0));

                if (!orderInfo.isContractor())
                    objecEditLists.add(new ObjecEditList(
                            getString(R.string.e_o_change_payment_method), 1));

                if (isEnabledOptions)
                    objecEditLists.add(new ObjecEditList(
                            getString(R.string.e_o_change_options)
                            , 2));

                objecEditLists.add(new ObjecEditList(
                        getString(R.string.e_o_change_comment), 3));

                break;

            case Constants.STATE_SET:
                objecEditLists.add(new ObjecEditList(
                        getString(R.string.e_o_change_detalies_address), 0));

                if (!orderInfo.isContractor())
                    objecEditLists.add(new ObjecEditList(
                            getString(R.string.e_o_change_payment_method), 1));


                objecEditLists.add(new ObjecEditList(
                        getString(R.string.e_o_change_comment), 3));

                break;

            case Constants.STATE_WAIT:

            case Constants.STATE_WORK:
                if (orderInfo.isContractor()) {
                    alert(getString(R.string.fsco_disabled_edit_order));
                    return;
                }
                objecEditLists.add(new ObjecEditList(
                        getString(R.string.e_o_change_payment_method), 1));

                break;

        }

        if (latLonOneList != null)
            getAWork().onShowMenuEdit(objecEditLists
                    , idRoute
                    , latLonOneList
                    , orderInfo.isContractor());
    }

    @OnClick(R.id.search_info_call)
    public void onSearchInfoCall() {
        ArrayList<ObjecEditList> objecEditLists = new ArrayList<>();
        objecEditLists.add(new ObjecEditList(
                getString(R.string.f_s_c_call_disp), 5));
        if (call != null && call.allow != null && !call.allow.equals("no")) {
            if (call.allow.equals("direct")) {
                objecEditLists.add(new ObjecEditList(
                        getString(R.string.f_s_c_call_driver), 6));
            } else {
                objecEditLists.add(new ObjecEditList(
                        getString(R.string.f_s_c_request_call_driver), 7));
            }
        }
        if (latLonOneList != null && orderInfo != null && idRoute != 0)
            getAWork().onShowMenuEdit(objecEditLists
                    , idRoute
                    , latLonOneList
                    , orderInfo.isContractor());
    }

    @Override
    public void onMapReady() {
        if (!isVisible()) return;

        init();
        animationView.setAnimation("data.json");
        animationView.loop(true);
        animationView.setVisibility(View.GONE);
        animationView.playAnimation();


        getIuPmaps().setPaddingButtonMaps(getPX(10), getPX(60), getPX(10), getPX(180));


        if (latLonOneList != null)
            setCenterActivate();

    }

    @Override
    public void allStopProgressPin() {

    }

    private void setCenterActivate() {
        double lat = latLonOneList[0];
        double lon = latLonOneList[1];
        LatLng latLng = new LatLng(lat, lon);

        initializeMap(latLng);
        getAWork().setBGWork();
    }

    private void initializeMap(LatLng latLng) {
        if (initializedMap) return;

        initializedMap = true;
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.initializeMap(latLng);

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        ping();
        initMaps();
    }


    private void ping() {
        if (!isVisible()) return;

        RESTConnect restConnect = Injector.getRC();
        String name = getClass().getName();
        restConnect.setTAG(name);

        restConnect.getOrderInfo(idRoute, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible()) return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (apiResponse.orderInfo == null)
                    return;

                initOrderInfo(apiResponse.orderInfo);
            }
        });
    }


    private void getLinesInfo() {
        RESTConnect restConnect = Injector.getRC();
        String name = getClass().getName();
        restConnect.setTAG(name);

        restConnect.getLinesInfo(new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                LinesInfo linesInfo = apiResponse.linesInfo;
                if (linesInfo == null)
                    return;
                Path path = linesInfo.path;
                if (path == null)
                    return;
                if (path.coordinates.size() == 0)
                    return;

                initLinesInfo(path);
            }
        }, idRoute);
    }


/*    @OnTouch(R.id.frame_maps)
    public boolean onTouch(View v, MotionEvent event) {
        if (!isVisible())
            return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mIsMoving = true;
            if (mTimer != null) { mTimer.cancel(); }
            mTimer = new Timer();

            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mIsMoving = false;

                    ping();
                }
            }, 5000);

        }

        return false;
    }*/

    private int getIconDrawable(OrderInfo orderInfo) {
        PaymentMethod paymentMethod = orderInfo.paymentMethod;
        String kind = paymentMethod.kind;
        switch (kind) {
            case "contractor":
                return R.drawable.ic_counteragent;
            case "cash":
                return R.drawable.ic_cash;
            case "credit_card":
                return R.drawable.ic_credit_card;
        }
        return 0;

    }

    @Subscribe
    public void onBusArayPingOrderInfo(BusArayPingOrderInfo e) {

        for (OrderInfo orderInfo : e.pingArrayOrderInfo) {
            if (orderInfo.idRoute == idRoute) {
                initOrderInfo(orderInfo);
                return;
            }
        }

        ping();
    }

    @Subscribe
    public void onBusStateFinishOrderId(BusStateFinishOrderId e) {
        if (e.orderInfo.idRoute == idRoute) {
            initOrderInfo(e.orderInfo);
            return;
        }

        ping();
    }

    private void initOrderInfo(OrderInfo orderInfo) {
        if (!isVisible())
            return;
        this.orderInfo = orderInfo;

        if (latLonOneList == null) {

            latLonOneList = new double[2];
            GpsPosition gpsPositionStart = orderInfo.getGpsPositionStart();
            latLonOneList[0] = gpsPositionStart.lat;
            latLonOneList[1] = gpsPositionStart.lon;
            setCenterActivate();
        }

        if (mIsMoving)
            return;

        nameCost.setText(
                orderInfo.getNameCost() == null ?
                        getString(R.string.cash)
                        :
                        orderInfo.getNameCost());

        int iconDrawable = getIconDrawable(orderInfo);
        iconPay.setImageDrawable(TintIcons
                .getVectorDrawableRes(iconDrawable));
        TintIcons.tintImageViewBrend(iconPay);
        updateUIDopInfo(orderInfo);

        getAWork().setOrderInfo(orderInfo);
        Assignee assignee = orderInfo.assignee;

        if (assignee != null) {
            call = assignee.call;
        }
        getRoute = orderInfo.getAddresses();

        final int state = orderInfo.state;

        if (state == Constants.STATE_DONE) {

            getAWork().showFFinishInfo();
            exitWork();
            return;
        }

        searchInfoCall.setVisibility(View.VISIBLE);

        frameMaps.setClickable(false);
        if (state == Constants.STATE_CREATE) {
            frameMaps.setClickable(true);

            markerDef.setVisibility(View.VISIBLE);


            String time = orderInfo.time;


            if (time != null) {

                if (UtilitesDataClient.isShowTextPreorder(time)) {
                    imageStatus.setImageResource(R.drawable.ic_status_time_black48);

                    boolean enabledDataPreorder = UtilitesDataClient.isEnabledDataPreorder(time);
                    time = UtilitesDataClient.getStrigIso(time, enabledDataPreorder);

                    time = enabledDataPreorder ? "\n" + time : time;
                    String textTime = getString(R.string.preorder_info_text_car);
                    searchState.setText(String.format(textTime, time));

                } else {
                    imageStatus.setImageResource(R.drawable.ic_status_search_black_48);
                    searchState.setText(R.string.search_car);
                }

            } else {

                imageStatus.setImageResource(R.drawable.ic_status_search_black_48);
                searchState.setText(R.string.search_car);
            }

        } else {
            LatLng position = getRoute.get(0).position.getLatLng();
            IUPmaps iuPmaps = getIuPmaps();
            switch (state) {
                case Constants.STATE_SET:

                    markerDef.setVisibility(View.GONE);


                    if (iuPmaps != null)
                        iuPmaps.initClientDinamiMarkerPos(position);


                    break;
                case Constants.STATE_WAIT:
                    iuPmaps = getIuPmaps();
                    if (iuPmaps != null)
                        iuPmaps.initClientDinamiMarkerPos(position);
                    markerDef.setVisibility(View.GONE);

                    break;
                case Constants.STATE_WORK:

                    iuPmaps = getIuPmaps();
                    if (iuPmaps != null)
                        iuPmaps.removeClientDinamiMarker(position);
                    getHachSumCoord(getRoute);
                    if (!activateRoute) {

                        activateRoute = true;
                        iuPmaps = getIuPmaps();
                        if (iuPmaps != null)
                            iuPmaps.addMarkerAddress(getRoute);

                    }
                    markerDef.setVisibility(View.GONE);

                    break;
                default:
                    iuPmaps = getIuPmaps();
                    if (iuPmaps != null)
                        iuPmaps.removeClientDinamiMarker(position);
                    markerDef.setVisibility(View.GONE);

                    exitFSCar();
                    return;
            }
        }

        showAssignee(assignee, state);
        showSearchState(state);


        showRoute(orderInfo);

        showCost(orderInfo);
    }


    private void showRoute(OrderInfo orderInfo) {
        searchAdressCont.removeAllViews();
        ArrayList<Address> route = orderInfo.getAddresses();
        if (route == null)
            return;

        int size = route.size();
        if (size == 0)
            return;

        for (int i = 0; i < size; i++) {
            View view = LayoutInflater.from(searchAdressCont.getContext())
                    .inflate(R.layout.row_adress_info, searchAdressCont, false);
            TextView number = view.findViewById(R.id.number);
            number.setText(String.valueOf(i + 1));

            TextView adressContent = view.findViewById(R.id.adress_content);
            TextView adressDop = view.findViewById(R.id.adress_d_i);
            adressDop.setVisibility(View.GONE);
            if (i == 0) {
                String txt = "";
                ClientAddress clientAddress = orderInfo.route.get(0);
                String entrance = clientAddress.entrance;
                String flat = clientAddress.flat;
                if (entrance != null) {
                    txt += getString(R.string.porch_min) + entrance + ", ";
                }

                if (flat != null) {
                    txt += getString(R.string.flat_min) + flat + ", ";
                }

                if (!"".equals(txt)) {
                    adressDop.setVisibility(View.VISIBLE);
                    adressDop.setText(txt.substring(0, txt.length() - 2));
                }
            }


            String text = route.get(i) == null || route.get(i).getMapsValueStringAdress() == null
                    ? getString(R.string.adr_value_def) : route.get(i).getMapsValueStringAdress();

            adressContent.setText(text);

            searchAdressCont.addView(view);
        }
    }

    private void updateUIDopInfo(OrderInfo orderInfo) {
        if (orderInfo == null)
            return;
        UtilitesOrder.instance()
                .initDetalsOrder(orderInfo, contOptions);


        String commentToRoute = orderInfo.comment;
        if (commentToRoute != null) {
            contCommentToRoute.removeAllViews();
            View view = LayoutInflater.from(contOptions.getContext())
                    .inflate(R.layout.comment_to_route
                            , contCommentToRoute
                            , false);
            TextView commentValue = view.findViewById(R.id.comment_value);
            commentValue.setText(commentToRoute);

            contCommentToRoute.addView(view);
        }
    }


    private void initLinesInfo(Path path) {
        List<List<Double>> coord = path.coordinates;


        List<LatLng> mPoints = new ArrayList<>();
        int max = coord.size();
        for (int i = 0; i < max; i++) {
            if (coord.size() == 0)
                continue;
            double lat = coord.get(i).get(1);
            double lon = coord.get(i).get(0);
            mPoints.add(new LatLng(lat, lon));
        }

        if (mPoints.size() > 0) {
            mPoints.add(0, orderInfo.getGpsPositionStart().getLatLng());

            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.initPolyline(mPoints);
        }
    }


    private void exitWork() {
        if (dialog != null) dialog.dismiss();
        Injector.getClientData().setCreateRequest(null);

        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.exitWork();

    }

    private void getHachSumCoord(ArrayList<Address> route) {
        double sumCoordRoute = 0;
        for (Address address : route) {

            if (address == null
                    || address.position == null
                    || address.position.lat == null
                    || address.position.lon == null
            )
                return;


            double lat = address.position.lat;
            double lon = address.position.lon;
            sumCoordRoute += lat + lon;

        }


        if (sumCoordRoute != mSumCoordRoute) {
            mSumCoordRoute = sumCoordRoute;

            getLinesInfo();
        }

    }

    private void showCost(OrderInfo order) {

        orderInfo = order;

        Cost cost = order.cost;

        cost.amount = cost.fixed == null ? cost.amount : cost.fixed;


        Float usedBonuses = order.usedBonuses;
        if (usedBonuses != null && usedBonuses != 0) {
            cost.amount -= usedBonuses;

        }

        UIOrder uiOrder = new UIOrder(cost);
        String textDec = uiOrder.getTextDec();
        String valueType = uiOrder.getValueType();
        String summ = uiOrder.getSumm();

        if (valueType != null) {
            if (valueType.equals(getString(R.string.tilda))) {
                valueCostPrefix.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
                final LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) valueCostPrefix.getLayoutParams();
                layoutParams.setMargins(getPX(8), 0, 0, 0);
            }

            valueCostPrefix.setText(valueType);
        }

        if (summ != null)
            routeCostValue.setText(summ);

        if (textDec != null) {
            costValueDec.setText(textDec);
            costValueDec.setVisibility(View.VISIBLE);
        } else
            costValueDec.setVisibility(View.GONE);
        valueCurrencyShort.setText(Injector.getWorkSettings().getCurrencyShort());
        valueCostPrefix.setVisibility(cost.fixed == null ? View.VISIBLE : View.GONE);
    }


    private Car initCarData(Car car) {
        String regNum = car.regNum;
        String color = car.color;
        String alias = car.alias;
        String formatRegNum = UtilitesDataClient.formatRegNum(regNum);
        searchRegNumInfo.setText(formatRegNum);


        if (alias != null)
            searchBrand.setText(alias);
        if (color != null)
            searchColor.setText(color);

        return car;
    }

    private void showAssignee(Assignee assignee, int state) {

        if (assignee != null) {
            GpsPosition location = assignee.location;
            showCarToMaps(location, state);

            Car car = assignee.car;
            if (car != null) {

                String color = car.color;
                showDialogWait(state, color, initCarData(car));
                playInfo(state);

            }
        }


    }

    private void showCarToMaps(GpsPosition location, int state) {

        if (location != null) {
            showCarLocation(location, state);

        } else if (state != Constants.STATE_WORK) {
            searchInfoText.setVisibility(View.GONE);
            searchInfoText.setText("");
            LatLng latLngClient = orderInfo.getAddresses().get(0).position.getLatLng();
            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.animateCarForMap(latLngClient.latitude, latLngClient.longitude);
        }

    }

    private void showDialogWait(int state, String color, Car car) {
        boolean isComing = orderInfo.isComing;
        if (state == Constants.STATE_WAIT && isComing)
            return;

        boolean b = state == Constants.STATE_WAIT || state == Constants.STATE_SET;
        App.app.isEnabledPoush = !b;
        if (mStatusActual != state
                && b) {
            String regNumInfoText = UtilitesDataClient.formatRegNum(car.regNum);
            getAWork()
                    .showFStatusInfo(idRoute, state,
                            color,
                            car.alias,
                            regNumInfoText);
        }

    }


    @OnClick(R.id.zoom_minus)
    public void onZoomMinus() {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.moveCameraZoomMinus();


    }

    @OnClick(R.id.zoom_plus)
    public void onZoomPlus() {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.moveCameraZoomPlus();

    }

    private void showSearchState(int state) {
        if (!isVisible())
            return;
        if (getAWork().blokPing)
            getAWork().blokPing = false;

        getAWork().getPingOrders();

        if (state == Constants.STATE_CREATE) {
            animationView.setVisibility(View.VISIBLE);
            contZoom.setVisibility(View.VISIBLE);
            LatLng latLngClient = orderInfo.getAddresses().get(0).position.getLatLng();
            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.setCenterMaps(latLngClient);
        } else {
            contZoom.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.clearMarkerCar();
        }

        boolean b = state == Constants.STATE_SET
                || state == Constants.STATE_WORK
                || state == Constants.STATE_WAIT;

        if (!b) {
            IUPmaps iuPmaps = getIuPmaps();
            if (iuPmaps != null)
                iuPmaps.removeCarMarker();
        }

        switch (state) {
            case Constants.STATE_CREATE:
                Injector.getSettingsStore().writeLong("time_start", 0);
                buttonDeleteRoute.setVisibility(View.VISIBLE);
                IUPmaps iuPmaps = getIuPmaps();
                if (iuPmaps != null)
                    iuPmaps.removeCarMarker();


                searchInfoText.setText("");
                searchCarCont.setVisibility(View.GONE);
                snowAllCarToMaps();


                break;
            case Constants.STATE_SET:
                buttonDeleteRoute.setVisibility(View.VISIBLE);

                imageStatus.setImageResource(R.drawable.ic_status_assigned_black48);

                searchState.setText(getString(R.string.car_assigned));

                searchCarCont.setVisibility(View.VISIBLE);

                break;
            case Constants.STATE_WAIT:
                buttonDeleteRoute.setVisibility(View.VISIBLE);

                imageStatus.setImageResource(R.drawable.ic_status_arrived_black48);


                searchState.setText(getString(R.string.car_wait));


                // реальное расстояние

                searchCarCont.setVisibility(View.VISIBLE);
                break;
            case Constants.STATE_WORK:
                buttonDeleteRoute.setVisibility(View.GONE);

                imageStatus.setImageResource(R.drawable.ic_status_inprogress_black48);

                searchState.setText(R.string.order_work);
                // реальное время

                long currentTimeMillis = System.currentTimeMillis();

                long timeStart = Injector.getSettingsStore().readLong("time_start", 0);
                if (timeStart == 0) {
                    Injector.getSettingsStore().writeLong("time_start", currentTimeMillis);
                    timeStart = currentTimeMillis;
                }

                Period period = new Period(timeStart, currentTimeMillis);
                int hours = period.getHours();
                int minutes = period.getMinutes();
                searchInfoText.setText(String.format("%s:%s"
                        , hours < 10 ? ("0" + hours) : hours
                        , minutes < 10 ? ("0" + minutes) : minutes));
                searchCarCont.setVisibility(View.VISIBLE);
                searchInfoText.setVisibility(View.VISIBLE);
                break;
            case Constants.STATE_DONE:
                buttonDeleteRoute.setVisibility(View.GONE);
                searchCarCont.setVisibility(View.GONE);

                break;


            default:
                playInfo(state);
                Injector.getClientData().setTempObjectUIMRoute(null);
                Injector.getClientData().setCreateRequest(null);
                getAWork().setObjectRestoryV3Route(null);
                getAWork().getCurrentOrders();

        }
    }


    private void exitFSCar() {
        Injector.getClientData().setCreateRequest(null);
        exitWork();

        getAWork().restartAll();
    }


    private void snowAllCarToMaps() {


        Long tarif = Injector.getClientData().getIdDefTarif();
        if (tarif == null)
            return;

        GpsPosition position = getRoute.get(0).position;
        RESTConnect restConnect = Injector.getRC();

        String name = getClass().getName();
        restConnect.setTAG(name);

        restConnect.getDrivers(
                position.getLatLng(),
                new Params(Injector.getClientData().getPaymentMethodSelect(), tarif),
                new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            //  getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        if (apiResponse.driverList.size() == 0) {
                            return;
                        }

                        IUPmaps iuPmaps = getIuPmaps();
                        if (iuPmaps != null)
                            iuPmaps.addArrayPointToMap(apiResponse.driverList, latLonOneList);
                    }
                });


    }

    @Override
    public void responseDialog() {
        if (!isVisible())
            return;
        if (getAWork() == null)
            return;
        getAWork().restartAll();
    }

    private void playInfo(int state) {
        if (mStatusActual != state) {
            mStatusActual = state;

        }
    }


    @SuppressLint({"DefaultLocale", "StringFormatInvalid"})
    private void showCarLocation(GpsPosition location, int state) {
        if (location == null)
            return;
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.showCarLocation(location);


        if (state == Constants.STATE_WORK || orderInfo == null
                || orderInfo.getAddresses() == null
                || orderInfo.getAddresses().size() == 0
                || orderInfo.getAddresses().get(0) == null
                || orderInfo.getAddresses().get(0).position == null
                || orderInfo.getAddresses().get(0).position.getLatLng() == null) {

            return;
        }


        searchInfoText.setVisibility(View.VISIBLE);
        LatLng latLngClient = orderInfo.getAddresses().get(0).position.getLatLng();

        double lat = location.lat;
        double lon = location.lon;
        LatLng latLngCar = new LatLng(lat, lon);

        int v = (int) SphericalUtil.computeDistanceBetween(latLngCar, latLngClient);
        if (state == Constants.STATE_WAIT)
            searchInfoText.setText(String.format(getString(R.string.f_s_c_distance_metr), v));

        boolean isStateSet = state == Constants.STATE_SET;
        if (isStateSet) {
            int sec = v / 8;
            int minutes = sec / 60;
            String string = getResources().getString(R.string.fsv_value_car_arr);
            searchInfoText.setText(String.format(string, minutes));

        }


        iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.setLatLngBounds(latLngCar, latLngClient, isStateSet);

    }


    @OnClick(R.id.button_delete_route)
    public void deleteRoute() {
        if (!isVisible())
            return;

        if (dialog != null)
            dialog.dismiss();

        dialog = DialogClient.showDefaultDialog(null,
                getString(R.string.help_ordering_deleted),
                getAWork(), getClass().getName());
    }

    @Subscribe
    public void onBusDialogPressed(BusDialogPressed e) {
        if (!isVisible()) return;

        if (e.className.equals(getClass().getName())) {

            if (dialog != null)
                dialog.cancel();
            RESTConnect restConnect = Injector.getRC();
            String name = getClass().getName();
            restConnect.setTAG(name);

            restConnect.deleteRoute(idRoute, new IGetApiResponse() {
                @Override
                public void getApiResponse(ApiResponse apiResponse) {

                    if (!isVisible())
                        return;

/*                    if (apiResponse == null || apiResponse.error != null) {
                        getAWork().showErrorIGetApiResponse(apiResponse);
                        return;
                    }*/

                    getAWork().showMessage(getString(R.string.ordering_deleted));
                    Injector.getClientData().hashDefPosTariff = -10;
                    getAWork().getCurrentOrders();
                    Injector.getClientData().setTempObjectUIMRoute(null);
                    Injector.getClientData().setCreateRequest(null);
                    getAWork().setObjectRestoryV3Route(null);
                    getAWork().showV3FRoute();
                }
            });

        }
    }


    @Override
    public boolean onBackPressed() {
        getAWork().closeApp();
        return true;
    }


    @Override
    public void onDestroyView() {

        if (mTimer != null)
            mTimer.cancel();

        animationView.cancelAnimation();
        animationView.setVisibility(View.GONE);

        getAWork().getPingOrders();
        getAWork().hideWorkProgress();

        super.onDestroyView();
    }

    @Override
    public void onCameraIdle(LatLng mLocation, boolean isEnabledUpdTarif) {
    }

    @Override
    public void onCameraMoveStarted(int i) {
    }
}
