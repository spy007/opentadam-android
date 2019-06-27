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


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.FetchAddressIntentService;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.SettingsStore;
import com.opentadam.adapter.AdressAdapter;
import com.opentadam.adapter.EditAdressFPA;
import com.opentadam.adapter.IAdressAdapter;
import com.opentadam.bus.BusKeyPreIme;
import com.opentadam.bus.BusShowMapsFFullTextSearch;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.creating_an_order.interface_class.IUtilsV3Route;
import com.opentadam.utils.Utilites;
import com.opentadam.yandex_google_maps.FGoogle;
import com.opentadam.yandex_google_maps.FOsmMaps;
import com.opentadam.yandex_google_maps.IUPmaps;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnPageChange;
import butterknife.OnTextChanged;

import static com.opentadam.Injector.getClientData;

public class V2FFullTextSearch extends BaseFr implements IAdressAdapter
        , IUtilsV3Route {
    private static final String INDEX_ADRESS = "indexAdress";
    private static final String MODE_ADRESS = "modeAdress";
    private final WeakHandler mHandler = new WeakHandler();
    @InjectView(R.id.mea_search_address)
    public com.opentadam.ui.creating_an_order.LockEditText meaSearchAddress;
    @InjectView(R.id.title)
    public TextView title;
    /*
        @OnClick(R.id.ffuls_show_maps)
        public void showMaps() {
            App.bus.post(new BusShowMapsFFullTextSearch());
        }*/
/*    @InjectView(R.id.apin_full_ts)
    public com.hivetaxi.ui.creating_an_order.animate.APinFroute aPinFrouteEServis;*/
    boolean isClickItemEdit = false;
    boolean addText = false;
    Address tempAdressMaps;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.heder_menu)
    LinearLayout hederMenu;
    @InjectView(R.id.adress_manual_list_view)
    RecyclerView adressManualListView;
    @InjectView(R.id.ic_clear_edit)
    ImageView icClearEdit;
    @InjectView(R.id.indicator0)
    View indicator0;
    @InjectView(R.id.indicator1)
    View indicator1;
    @InjectView(R.id.indicator2)
    View indicator2;
    @InjectView(R.id.nearest_address)
    LinearLayout contPrivateTab;
    @InjectView(R.id.cont_love_tab)
    LinearLayout contLoveTab;
    @InjectView(R.id.cont_history_tab)
    LinearLayout contHistoryTab;
    @InjectView(R.id.nearest_address)
    LinearLayout contNearestAddress;
    @InjectView(R.id.marker_center_add)
    ImageView pin;
    @InjectView(R.id.marker_def_cont)
    View markerDefCont;
    @InjectView(R.id.v3_cont_marker_center)
    View contMarkerCenter;
    @InjectView(R.id.v3_profress_pin)
    View profressPin;
    @InjectView(R.id.v3_profress_pin_double)
    View profressPinDouable;
    @InjectView(R.id.res_geocode)
    TextView resGeocode;
    @InjectView(R.id.cont_map_search)
    LinearLayout contMapSearch;
    @InjectView(R.id.glob_cont)
    FrameLayout globCont;
    @InjectView(R.id.step_2)
    FrameLayout step2;

    @InjectView(R.id.hide_cursor)
    View hideCursor;
    @InjectView(R.id.marker_center_passender)
    ImageView markerCenterPassender;
    @InjectView(R.id.set_adr_to_route)
    View setAdrToRoute;
    private ArrayList<Address> mAdressList;
    private int pos = 0;
    private EditAdressFPA adapter;
    private boolean mEditing = false;
    private int tempLength = 0;
    private AdressAdapter adressAdapter;
    private int indexAdress = -1;
    private int modeAdress = -1;

    ///////////////

    private long timeRest;
    private boolean isReg = false;
    private ArrayList<String> v2FItemAdapterSearches;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Boolean isShowKeyBoord = null;
    private boolean isEnabledKryList = false;

    private Runnable runnable;
    private Animator animatorLoopProgressPin;
    private Animator animatorLoopProgressPinDouble;
    private LatLng latLngGeocode;

    //////////////////
    public V2FFullTextSearch() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int i, int b) {

        return new V2FFullTextSearch()
                .withArgument(INDEX_ADRESS, i).withArgument(MODE_ADRESS, b)
                .withViewId(R.layout.v2_ffull_text_search);
    }

    private void animateStopProgressPin() {

        if (profressPin == null || profressPinDouable == null)
            return;
        markerCenterPassender.setVisibility(View.VISIBLE);
        profressPinDouable.setVisibility(View.GONE);
        profressPin.setVisibility(View.GONE);
    }

    public IUPmaps getIuPmaps() {
        return (IUPmaps) getChildFragmentManager().findFragmentById(R.id.cont_maps);
    }

    private void animateShowProgressPin() {
        if (profressPin == null)
            return;

        if (animatorLoopProgressPin == null)
            animateLoopProgressPin();

        markerCenterPassender.setVisibility(View.GONE);
        profressPin.setVisibility(View.VISIBLE);
        profressPinDouable.setVisibility(View.VISIBLE);


    }

    private void animateLoopProgressPin() {
        if (profressPin == null || profressPinDouable == null)
            return;


        if (animatorLoopProgressPin != null) {
            animatorLoopProgressPin.cancel();
        }


        if (animatorLoopProgressPinDouble != null) {
            animatorLoopProgressPinDouble.cancel();
        }

        animatorLoopProgressPin = AnimatorInflater.loadAnimator(getAWork()
                , R.animator.fr_progress_ping);

        animatorLoopProgressPin.setTarget(profressPin);
        animatorLoopProgressPinDouble = AnimatorInflater.loadAnimator(getAWork()
                , R.animator.fr_progress_ping_double);


        animatorLoopProgressPin.start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVisible() || animatorLoopProgressPinDouble == null)
                    return;

                animatorLoopProgressPinDouble.setTarget(profressPinDouable);
                animatorLoopProgressPinDouble.start();
            }
        }, 250);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.v2_ffull_text_search, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getAWork().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            indexAdress = getArguments().getInt(INDEX_ADRESS);
            modeAdress = getArguments().getInt(MODE_ADRESS, 0);
        }

        initMaps();
        animateStopProgressPin();

        isReg = Injector.getSettingsStore()
                .readString(Constants.REG_KEY_CLIENT, null) != null;

        initUIHeder();
        initTitleAndSEdit();

        viewPager.setAdapter(adapter);
        if (v2FItemAdapterSearches.size() != 0)
            showIndicator(v2FItemAdapterSearches.get(0));

        viewPager.setCurrentItem(pos);

        // поиск по патерну
        mAdressList = new ArrayList<>();


        adressAdapter = new AdressAdapter(mAdressList, this);
        adressManualListView.setAdapter(adressAdapter);
        adressManualListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                if (globCont == null || isEnabledKryList || contMapSearch.getVisibility() == View.VISIBLE)
                    return;

                isEnabledKryList = true;
                Rect r = new Rect();
                //создаём прямоугольник r с координатами видимого пространства

                globCont.getWindowVisibleDisplayFrame(r);
                //Вычисляем разницу между высотой нашего View и высотой видимого пространства
                final int height = globCont.getRootView().getHeight();

                int heightDiff = height - (r.bottom - r.top);

                int length = meaSearchAddress.getText().length();
                if (heightDiff < 250) {
                    if (isShowKeyBoord != null && !isShowKeyBoord) {
                        isEnabledKryList = false;
                        return;
                    }
                    setVisibilityStep2();
                    reboutClear();
                } else if (isShowKeyBoord == null || !isShowKeyBoord) {

                    if (length != 0) {
                        step2.setVisibility(View.GONE);
                        updUIHederMenu(1);
                    }

                    isShowKeyBoord = true;

                    tempLength = 0;
                    mAdressList.clear();
                    adressAdapter.notifyDataSetChanged();
                    //          Log.d("ghggffc", "открыта клавиатура");
                }


                CreateRequest createRequest = getClientData().getCreateRequest();
                boolean b = createRequest.getSizeRoute() > 2
                        || indexAdress < 0;
                if (!isShowKeyBoord && !b) {
                    hideCursor.requestFocus();
                }
                isEnabledKryList = false;
            }
        };

        globCont.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private void initMaps() {


        SettingsStore settingsStore = Injector.getSettingsStore();
        switch (settingsStore.getDefMars()) {
            case SettingsStore.MAPS_GOOGLE:
                FGoogle fGoogle = (FGoogle) FGoogle.newInstance("search");


                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.cont_maps, fGoogle)
                        .commitAllowingStateLoss();
                break;
            case SettingsStore.MAPS_OSM:
                //  requestPermissionsStorage();
                initOsm();
                break;
        }

    }

    @Override
    public void initOsm() {
        FOsmMaps fOsmMaps = (FOsmMaps) FOsmMaps.newInstance("search");

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cont_maps, fOsmMaps)
                .commitAllowingStateLoss();
    }

    private void reboutClear() {
        updHederVisibility();
        tempLength = 0;
        isClickItemEdit = false;
        addText = false;
        mAdressList.clear();
        setVisibilityStep2();
        updUIHederMenu(0);
        CreateRequest createRequest = getClientData().getCreateRequest();
        boolean b = createRequest.getSizeRoute() > 2
                || indexAdress < 0;
        if (b) {

            if (isShowKeyBoord == null) {
                isShowKeyBoord = true;
                Utilites.showSoftKeyboard(getContext());
                meaSearchAddress.requestFocusFromTouch();
                meaSearchAddress.requestFocus();

            }
        } else {
            isShowKeyBoord = false;
            String textStartAdress = createRequest
                    .getTextStartAdress(indexAdress);
            icClearEdit.setVisibility(View.VISIBLE);
            if (textStartAdress != null) {
                meaSearchAddress.setText(textStartAdress);
                meaSearchAddress.setSelection(textStartAdress.length());

                icClearEdit.setVisibility(View.VISIBLE);
            } else {
                icClearEdit.setVisibility(View.GONE);
            }

        }

        if (!isShowKeyBoord && !b) {

            hideCursor.setFocusableInTouchMode(true);
            hideCursor.requestFocus();
        }
    }

    private void setVisibilityStep2() {


        step2.setVisibility(Injector.getClientData().isAkceptShow && modeAdress != Constants.EDIT_ADRESS
                ? View.VISIBLE : View.GONE);

    }

    @OnTextChanged(R.id.mea_search_address)
    public void afterTextChanged(final Editable s) {
        final String text = s.toString().trim();
        final int length = text.length();
        icClearEdit.setVisibility(length == 0 ? View.GONE : View.VISIBLE);

        if (isShowKeyBoord == null) {
            isShowKeyBoord = false;
        }

        if (!mEditing && isShowKeyBoord) {
            if (length != 0) {
                step2.setVisibility(View.GONE);
                updUIHederMenu(1);
            }

            mEditing = true;
            if (length < 2) {

                mAdressList.clear();
                adressAdapter.notifyDataSetChanged();
                mEditing = false;
                return;
            }


            if (runnable != null)
                mHandler.removeCallbacks(runnable);

            runnable = new Runnable() {
                @Override
                public void run() {
                    bodyTextChanged(text, length);
                }
            };

            mHandler.postDelayed(
                    runnable, length == 2 ? 1000 : 300);

            mEditing = false;

            //   bodyTextChanged(s, length);
        }
        CreateRequest createRequest = getClientData().getCreateRequest();
        boolean b = createRequest.getSizeRoute() > 2
                || indexAdress < 0;
        if (!isShowKeyBoord && !b) {

            hideCursor.setFocusableInTouchMode(true);
            hideCursor.requestFocus();
        }
    }

    private void bodyTextChanged(String text, int length) {
        if (!isVisible())
            return;

        updUIHederMenu(length);

        if (text.endsWith(", "))
            text = removeLastChar(text);
        if (isClickItemEdit) {
            isClickItemEdit = false;
            getListAdressGeocode(text);
            mEditing = false;
            return;
        }

        if (length > 1) {
            tempLength = length;
            getListAdressGeocode(text);

        } else if (length == 0 || tempLength - length > 0) {
            tempLength = 0;
            //   clAddress();
        }


    }

    @Override
    public void onResume() {

        hideKeyboard(meaSearchAddress);
        super.onResume();
        //    mapPause.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {

        mHandler.removeCallbacksAndMessages(null);
        hideKeyboard(meaSearchAddress);
        super.onPause();

    }

    @OnPageChange(R.id.view_pager)
    public void onPageSelected(int position) {
        pos = position;

        showIndicator(v2FItemAdapterSearches.get(position));
    }

    private void initTitleAndSEdit() {


        title.setText(getString(indexAdress == 0
                ? R.string.start_adress_route : R.string.add_adress));


        meaSearchAddress.setHint(getString(indexAdress == 0
                ? R.string.hint_edit_adress0 : R.string.to_vojge));
    }

    private void showIndicator(String namePos) {
        if (namePos.equals("getMyAdressList")) {
            indicator1.setVisibility(View.VISIBLE);
            indicator0.setVisibility(View.INVISIBLE);
            indicator2.setVisibility(View.INVISIBLE);
        } else if (namePos.equals("getHistoryOrders")) {
            indicator2.setVisibility(View.VISIBLE);
            indicator0.setVisibility(View.INVISIBLE);
            indicator1.setVisibility(View.INVISIBLE);
        }
        if (namePos.equals("getAdressGeocodeServers")) {
            indicator0.setVisibility(View.VISIBLE);
            indicator1.setVisibility(View.INVISIBLE);
            indicator2.setVisibility(View.INVISIBLE);
        }

    }

    private void initUIHeder() {
        setVisibilityStep2();


        boolean b1 = updHederVisibility();

        boolean b = !isReg;

        contHistoryTab
                .setVisibility(b ? View.GONE
                        : View.VISIBLE);

        contNearestAddress
                .setVisibility(indexAdress != 0 ? View.GONE
                        : View.VISIBLE);
        boolean[] isAddFragment = new boolean[3];
        isAddFragment[0] = !b1;
        isAddFragment[1] = !b;
        isAddFragment[2] = indexAdress == 0;
        v2FItemAdapterSearches = new ArrayList<>();
        if (isAddFragment[0])
            v2FItemAdapterSearches.add("getMyAdressList");

        if (isAddFragment[1])
            v2FItemAdapterSearches.add("getHistoryOrders");

        if (isAddFragment[2])
            v2FItemAdapterSearches.add("getAdressGeocodeServers");


        adapter = new EditAdressFPA(getChildFragmentManager(), v2FItemAdapterSearches, indexAdress == 0);
    }

    private boolean updHederVisibility() {
        List<ClientAddress> listPrivatePoint = Injector.getSettingsStore().getListPrivatePoint();
        int sizeLovAdress = listPrivatePoint.size();

        boolean b1 = sizeLovAdress == 0;
        if (!isReg && indexAdress == 0)
            hederMenu.setVisibility(View.VISIBLE);
        else if (!isReg && b1) {
            hederMenu.setVisibility(View.GONE);
        } else
            hederMenu.setVisibility(View.VISIBLE);

        contLoveTab
                .setVisibility(b1 ? View.GONE
                        : View.VISIBLE);
        return b1;
    }

    @OnClick(R.id.step_2)
    public void onStep2() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        ArrayList<ClientAddress> route = createRequest.getRoute();
        route.add(null);

        getAWork().showV3RestoryFRoute();
    }

    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length() - 2);
    }

    private void updUIHederMenu(int length) {

        if (length == 0) {
            Injector.getClientData().pattern = null;

            viewPager.setVisibility(View.VISIBLE);
            if (isReg && indexAdress == 0)
                hederMenu.setVisibility(View.VISIBLE);
            adressManualListView.setVisibility(View.GONE);

            icClearEdit.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.GONE);
            hederMenu.setVisibility(View.GONE);
            adressManualListView.setVisibility(View.VISIBLE);

            icClearEdit.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.nearest_address)
    public void onContPrivateTab() {
        initItem("getAdressGeocodeServers");

    }

    @OnClick(R.id.cont_love_tab)
    public void onContLoveTab() {
        initItem("getMyAdressList");

    }

    private void initItem(String name) {
        pos = v2FItemAdapterSearches.indexOf(name);
        if (viewPager == null)
            return;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null)
            return;
        int count = adapter.getCount();
        if (pos == -1 || count - 1 < pos)
            return;
        viewPager.setCurrentItem(pos);
    }

    @OnClick(R.id.cont_history_tab)
    public void onContHistoryTab() {
        initItem("getHistoryOrders");

    }

    private void getListAdressGeocode(final String text) {

        LatLng latLngTarifDef = Injector.getSettingsStore().getLatLngTarifDef();
        LatLng latLng = null;
        Double latitude = null;
        Double longitude = null;
        if (latLngTarifDef != null) {
            latitude = latLngTarifDef.latitude;
            longitude = latLngTarifDef.longitude;
        } else if (App.app.mMyGoogleLocation != null
                && App.app.mMyGoogleLocation.showCurrentLocation() != null) {

            Location currentLocation = App.app.mMyGoogleLocation.showCurrentLocation();
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

        }

        if (latitude != null)
            latLng = new LatLng(latitude, longitude);

        getAdressPatern(text, latLng);
    }

    private void getAdressPatern(final String text, LatLng latLng) {
        timeRest = System.currentTimeMillis();
        RESTConnect restConnect = Injector.getRC();

        restConnect.getAdressPatern(text, latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (apiResponse.timeRest != null && apiResponse.timeRest != timeRest)
                    return;
                List<Address> addresses = apiResponse.addresses;
                if (addresses != null && addresses.size() != 0) {
                    if (getIndex() != 0) {
                        Address mapsAdress = new Address(getString(R.string.fitem_menu_maps));
                        addresses.add(0, mapsAdress);
                    }
                    initAdapterData(addresses);
                }

            }
        }, timeRest);
        getClientData().pattern = text;
    }


    private void initAdapterData(List<Address> addresses) {
        mAdressList.clear();
        if (addresses == null || addresses.size() == 0) {
            adressAdapter.notifyDataSetChanged();
            return;
        }

        initDataList(addresses);
        adressAdapter.notifyDataSetChanged();
    }

    private void initDataList(List<Address> addresses) {
        for (Address a : addresses) {
            if (a != null)
                mAdressList.add(a);
        }
    }

    void unregisterBus() {
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroyView() {
        Injector.getClientData().isAkceptShow = false;
        removeGOLListener(globCont.getViewTreeObserver(), onGlobalLayoutListener);
        Injector.getClientData().pattern = null;
        viewPager.clearOnPageChangeListeners();
        viewPager.removeAllViews();

        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getAWork().hideWorkProgress();
        hideKeyboard(meaSearchAddress);

        if (animatorLoopProgressPin != null) {
            animatorLoopProgressPin.cancel();
        }

        if (animatorLoopProgressPinDouble != null) {
            animatorLoopProgressPinDouble.cancel();
        }

        super.onDestroyView();

    }

    @OnClick(R.id.ic_clear_edit)
    public void onClearEdit() {
        if (meaSearchAddress == null)
            return;

        meaSearchAddress.setText("");
        if (isShowKeyBoord == null) {
            isShowKeyBoord = false;
        }
        if (!isShowKeyBoord) {
            Utilites.showSoftKeyboard(getContext());
            meaSearchAddress.requestFocusFromTouch();
            meaSearchAddress.requestFocus();
        }


    }

    @Override
    public void itemAdress(Address address) {


        String name = address.name;
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();

        String name1 = getStringNameFin(createRequest);
        if (name1 == null)
            onClickItemEdit(address);

        else if (createRequest.getSizeRoute() == 0
                || !name1.equals(name))
            onClickItemEdit(address);
    }

    @Override
    public int getIndex() {
        return indexAdress;
    }

    @Override
    public void setMarginTopList(int dp) {
        // adressManualListView
        // viewPager

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) adressManualListView.getLayoutParams();
        layoutParams.topMargin = getPX(dp);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams1.topMargin = getPX(dp);
    }


    //////////////////// maps

    private String getStringNameFin(CreateRequest createRequest) {
        if (createRequest.getSizeRoute() == 0)
            return null;
        ClientAddress clientAddress = createRequest.getRoute().get(createRequest.getSizeRoute() - 1);
        if (clientAddress == null)
            return null;
        if (clientAddress.address == null)
            return null;

        return clientAddress.address.name;
    }

    public void onClickItemEdit(Address address) {


        boolean b =
                address.getNameHouseSearsh() != null && (!addText || indexAdress == 0);
        if (b) {
            isClickItemEdit = true;
            addText = true;
            meaSearchAddress.setText(String
                    .format("%s, %s, "
                            , address.getNameSitySearsh()
                            , address.getNameHouseSearsh()));
            meaSearchAddress.setSelection(meaSearchAddress.getText().length());
        } else {
            if (indexAdress == 0) {
                LatLng latLng = address.position.getLatLng();
                getClientData().setMarkerLocation(latLng);
            } else {
                CreateRequest createRequest = getClientData().getCreateRequest();
                ArrayList<GpsPosition> routeLocation = createRequest.getRouteLocation();
                if (routeLocation != null
                        && routeLocation.size() != 0
                        && routeLocation.get(0) != null
                        && routeLocation.get(0).getLatLng() != null) {
                    GpsPosition gpsPosition = routeLocation.get(0);
                    LatLng latLng = gpsPosition.getLatLng();
                    getClientData().setMarkerLocation(latLng);
                }
            }


            getAWork().isManualAdress = true;
            getAWork().initAdressToRoute(modeAdress, address, indexAdress);
        }
    }

    private void typeGeocode(LatLng latLng) {
        animateShowProgressPin();
        getAdressGeocodeServers(latLng);

        //
    }

    private void getAdressGeocodeServers(final LatLng latLng) {
        latLngGeocode = latLng;
        RESTConnect restConnect = Injector.getRC();
        timeRest = System.currentTimeMillis();
        restConnect.getAdressGeocodeServers(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {

                    startIntentService(latLng);
                    return;
                }

                if (apiResponse.timeRest != null && apiResponse.timeRest != timeRest)
                    return;

                List<Address> addresses = apiResponse.addresses;
                if (addresses != null && addresses.size() != 0) {
                    updUIAdressMaps(addresses.get(0));
                } else {

                    startIntentService(latLng);
                }
            }
        }, timeRest);
    }

    @NonNull
    private Address getNullAdress(double mLatitude, double mLongitude) {
        return new Address(getString(R.string.point_to_maps)
                , mLatitude, mLongitude);
    }


    private void startIntentService(LatLng latLng) {
        Intent intent = new Intent(getAWork(), FetchAddressIntentService.class);
        intent.putExtra(Constants.LAT_GOOGLE, latLng.latitude);
        intent.putExtra(Constants.LON_GOOGLE, latLng.longitude);
        getAWork().startService(intent);
    }

    @Subscribe
    public void onBusUpdUIAdressMapsYaGoogle(UpdUIAdressMapsYaGoogle event) {
        if (!isVisible()) return;
        validateResponseGeocoder(event.adress);
    }

    private void validateResponseGeocoder(final Address adress) {
        if (!isVisible()) return;

        updUIAdressMaps(adress);

    }

    private void updUIAdressMaps(Address geo) {
        if (!isVisible()) return;


        boolean isNullRes = geo == null || geo.getTempAdress() == null;
        if (isNullRes) {
            geo = getNullAdress(latLngGeocode.latitude, latLngGeocode.longitude);
        }


        String textStartAdress = isNullRes ?
                getString(R.string.point_to_maps) : geo.getTempAdress();

        resGeocode.setText(textStartAdress);
        tempAdressMaps = geo;
        animateStopProgressPin();
    }

    @OnClick(R.id.set_adr_to_route)
    public void onSetAdrMaps() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        ArrayList<ClientAddress> route = createRequest.getRoute();

        int size = route.size();
        if (modeAdress == Constants.EDIT_ADRESS) {
            route.set(indexAdress > size - 1 ? size - 1 : indexAdress
                    , new ClientAddress(tempAdressMaps));

        } else
            route.add(new ClientAddress(tempAdressMaps));

        getAWork().showV3RestoryFRoute();
    }

    public LatLng getLatLngStart(Location location) {
        if (location == null)
            return null;

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Subscribe
    public void onBusShowMapsFFullTextSearch(BusShowMapsFFullTextSearch event) {
        if (!isVisible()) return;

        contMapSearch.setVisibility(View.VISIBLE);
        setAdrToRoute.setVisibility(View.VISIBLE);
        if (isShowKeyBoord == null) {
            isShowKeyBoord = false;
        }

        if (isShowKeyBoord) {

            hideKeyboard(meaSearchAddress);
            isShowKeyBoord = false;
        }

    }

    @OnClick(R.id.ic_bask)
    public void onIcBask() {
        if (getAWork().isFinishing() || !isVisible() || contMapSearch == null || meaSearchAddress == null)
            return;
        if (isShowKeyBoord) {
            hideKeyboard(meaSearchAddress);
            isShowKeyBoord = false;
            return;

        }

        if (contMapSearch.getVisibility() == View.VISIBLE) {
            contMapSearch.setVisibility(View.GONE);
            setAdrToRoute.setVisibility(View.GONE);
            return;
        }
        if (indexAdress == 1 && meaSearchAddress.getText().length() == 0) {
            CreateRequest createRequest = getClientData().getCreateRequest();
            ArrayList<ClientAddress> route = createRequest.getRoute();
            if (indexAdress < route.size())
                route.remove(indexAdress);
        }
        getAWork().showV3RestoryFRoute();
    }

    @Subscribe
    public void onBusKeyPreIme(BusKeyPreIme event) {
        if (!isVisible()) return;
        onIcBask();
    }

    @Override
    public boolean onBackPressed() {


        onIcBask();

        return true;
    }

    @Override
    public void onCameraIdle(LatLng mLocation, boolean isEnabledUpdTarif) {

        if (mLocation == null)
            return;

        getAWork().updGeozoneTariff(mLocation.latitude, mLocation.longitude, 50);
        typeGeocode(mLocation);
    }


    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onMapReady() {
        IUPmaps iuPmaps = getIuPmaps();
        if (iuPmaps != null)
            iuPmaps.initMapReady(null, false);
    }

    @Override
    public void allStopProgressPin() {

    }
}

