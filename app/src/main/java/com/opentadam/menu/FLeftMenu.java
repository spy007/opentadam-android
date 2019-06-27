/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentadam.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusInicializeMenuArrayList;
import com.opentadam.bus.BusInitFotoLeftMenu;
import com.opentadam.bus.BusOnNewversionOpenmarketClicked;
import com.opentadam.bus.BusSetNameLeftMenu;
import com.opentadam.bus.BusUpdMenu;
import com.opentadam.bus.BusUpdMenuPay;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.network.rest.VersionApi;
import com.opentadam.network.rest.VersionResult;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.FAboutApplication;
import com.opentadam.utils.TintIcons;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.opentadam.Injector.getClientData;

public class FLeftMenu extends BaseFr {

    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    @InjectView(R.id.cont_profile)
    View contProfile;
    @InjectView(R.id.cont_button_reg)
    View contButtonReg;
    @InjectView(R.id.profil_pfoto_dis)
    ImageView profilPfotoDis;
    @InjectView(R.id.version_name)
    TextView versionName;
    @InjectView(R.id.profil_name)
    TextView profilName;

    @InjectView(R.id.profil_pfoto)
    com.pkmmte.view.CircularImageView profilPfoto;
    @InjectView(R.id.menu_cont)
    LinearLayout menuCont;
    @InjectView(R.id.upd_google)
    LinearLayout updGoogle;
    @InjectView(R.id.item_upd_text)
    TextView itemUpdText;
    private ArrayList<ObjMenuLeft> menuArrayList;
    private int currentPosMenu = R.drawable.ic_local_taxi_white;
    private boolean isActivityCreated;

    public static Fragment newInstance() {

        return new FLeftMenu().withViewId(R.layout.f_left_menu);
    }

    public boolean isActivityCreated() {
        return isActivityCreated;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated = true;
      //  versionSin();
    }

    private boolean isDisabledVersionSin() {
        long valPeriodUpdSec = (System.currentTimeMillis()
                - Injector.getSettingsStore().getTimeDialogUpdApp()) / 1000;

        int periodHour = App.app.hashBC.countHourPeriodUpdateVersionApp;
        return 60 * 60 * periodHour - valPeriodUpdSec > 0;
    }

    private void versionSin() {
        VersionApi versionApi = Injector.getVersionApi();
        if (versionApi == null)
            return;

        versionApi
                .getCurrentVersion(getAWork().getPackageName(), new Callback<VersionResult>() {
                    @Override
                    public void success(VersionResult versionResult, Response response) {
                        if (!isVisible() || versionResult == null)
                            return;
                        // test
                        //  versionResult = new VersionResult(999990, "2.9.999991");
                        try {
                            Integer versionCodeGoogle = versionResult.code;
                            int versionCodeCurrentThis = App.app.hashBC.VERSION_CODE;
                            boolean b = versionCodeGoogle > versionCodeCurrentThis;
                            boolean b1 = !isDisabledVersionSin();
                            if (b && b1) {
                                getAWork().showUpdateApp();
                            }

                            String[] verGoogle = versionResult.version.split("\\.");
                            String[] verDrivers = UtilitesMenu
                                    .getVersionName(getAWork()).split("\\.");

                            int lengthGoogle = verGoogle.length;
                            int lengthDrivers = verDrivers.length;
                            if (lengthGoogle != lengthDrivers) {
                                updGoogle.setVisibility(View.VISIBLE);
                                itemUpdText.setText(String.format("%s%s", getString(R.string.get_new_version), versionResult.version));
                            } else {
                                int serverVersionBuild = Integer.parseInt(verGoogle[lengthGoogle - 1]);
                                int currentVersionBuild = Integer.parseInt(verDrivers[lengthGoogle - 1]);
                                if (serverVersionBuild > currentVersionBuild) {
                                    updGoogle.setVisibility(View.VISIBLE);
                                    itemUpdText.setText(String.format("%s%s", getString(R.string.get_new_version), versionResult.version));

                                }
                            }
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    @OnClick(R.id.cont_profile)
    public void showFProfil() {
        updUIMenu(-1);
        getAWork().showFProfil();
        getAWork().closeDrawer();
    }

    @OnClick(R.id.cont_button_reg)
    public void contButtonReg() {
        updUIMenu(-1);
        getAWork().showV2FRegistration(false, true);
        getAWork().closeDrawer();
    }


    @OnClick(R.id.aw_button_show_f_a_a)
    public void showFAboutApplication() {
        getAWork().closeDrawer();
        getAWork().showTopFragment(FAboutApplication.newInstance());
    }

    @Subscribe
    public void onNewversionOpenmarketClicked(BusOnNewversionOpenmarketClicked e) {
        onNewversionOpenmarketClicked();
    }

    @OnClick(R.id.upd_google)
    public void onNewversionOpenmarketClicked() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getAWork().getPackageName()));

            startActivity(Intent
                    .createChooser(intent, getString(R.string.fmenu_create_chooser)));

        } catch (Exception e) {
            alert(getString(R.string.error_upd_google));
        }

    }

    @Subscribe
    public void initFotoLeftMenu(BusInitFotoLeftMenu e) {
        initFotoLeftMenu();

    }

    public void initFotoLeftMenu() {
        if (UtilitesMenu.loadBitmap(getAWork()) != null) {
            profilPfoto.setImageBitmap(UtilitesMenu.loadBitmap(getAWork()));
            profilPfoto.setVisibility(View.VISIBLE);
            profilPfotoDis.setVisibility(View.GONE);
        } else {
            profilPfoto.setVisibility(View.GONE);
            profilPfotoDis.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void preloadMenu(BusUpdMenu e) {
        preloadMenu();

    }

    private void preloadMenu() {
        initFotoLeftMenu();

        if (UtilitesMenu.getRegKey() != null) {
            contProfile.setVisibility(View.VISIBLE);
            contButtonReg.setVisibility(View.GONE);
        } else {
            contProfile.setVisibility(View.GONE);
            contButtonReg.setVisibility(View.VISIBLE);
        }
        /////
        TintIcons.tintImageViewBrend(profilPfotoDis);
        setNameLeftMenu();

        String format = UtilitesMenu.getStringVersionName(getAWork());
        versionName.setText(format);

        updUIMenu(R.drawable.ic_local_taxi_white);
    }

    @Subscribe
    public void inicializeMenuArrayList(BusInicializeMenuArrayList e) {
        inicializeMenuArrayList();

    }

    public void inicializeMenuArrayList() {
        App.bus.post(new BusUpdMenuPay());
        menuArrayList = new ArrayList<>();

        if (getAWork().sizeCurrentOrders < App.app.hashBC.limitNumberActiveOrders)
            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_local_taxi_white
                    , getString(R.string.navigation_get_taxi), null));

        if (getAWork().sizeCurrentOrders != 0)
            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_qa_client_address,
                    getString(R.string.private_order), null));

        menuArrayList.add(new ObjMenuLeft(R.drawable.ic_my_point_white_24dp
                , getString(R.string.list_my_addresses), null));

        if (UtilitesMenu.getRegKey() != null) {

            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_my_orders
                    , getString(R.string.list_orders_nistory), null));
        }

        Service service = Injector.getClientData().service;

        if (service != null && Injector.getSettingsStore()
                .readString(Constants.REG_KEY_CLIENT, null) != null) {

            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_card_menu
                    , getString(R.string.flm_mode_pay), getValueSubMenuPay()));
        }
        if (UtilitesMenu.getRegKey() != null) {

            String txt = getString(R.string.invite_friends_none);
            TempObjectUIMRoute tempObjectUIMRoute = getClientData()
                    .getTempObjectUIMRoute();

            if (service != null
                    && service.isNoRegReferall
                    && service.getLptype() != null
                    && "ext-bonus-referral-vip".equals(service.getLptype())

                    ) {
                txt = getString(R.string.freg_text_menu);

            } else if (service != null && service.getLptype() != null && tempObjectUIMRoute.bonuses != null) {


                String balanceString = tempObjectUIMRoute.bonuses.gefBalanceString();

                switch (service.getLptype()) {

                    case "basic-bonus":
                    case "ext-bonus-referral-vip":
                        txt = String.format(getString(R.string.menu_fref_basic_ext)
                                , balanceString
                                , Injector.getWorkSettings().getCurrency().sign);
                        break;
                }
            }

            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_friends
                    , txt, null));
        }

        if (Injector.getClientData().getDispatcherCall() != null)
            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_call_while_24dp
                    , getString(R.string.call_dispatcher), null));

        if (App.app.hashBC.supportButtonURL != null) {

            menuArrayList.add(new ObjMenuLeft(R.drawable.ic_support_url
                    , getString(R.string.item_menu_support), null));
        }
/*
        menuArrayList.add(new ObjMenuLeft(R.drawable.ibv_ic_show_demo
                , getString(R.string.ibv_menu_demo), null));*/
    }

/*    private String getValueSubMenuPay() {
        ClientData clientData = Injector.getClientData();

        return clientData.getNameCash();
    }*/

    @SuppressLint("DefaultLocale")
    public void updUIMenu(int selectedId) {
        if (!isVisible()) {
            return;
        }
        if (menuCont == null) {

            return;
        }

        inicializeMenuArrayList();
        menuCont.removeAllViews();
        for (final ObjMenuLeft menu : menuArrayList) {
            View view = LayoutInflater.from(menuCont.getContext())
                    .inflate(R.layout.row_item_menu, menuCont, false);
            ImageView itemMenuIcon = view.findViewById(R.id.item_menu_icon);
            LinearLayout boduItem = view.findViewById(R.id.body_item);
            itemMenuIcon.setImageResource(menu.idItem);

            TextView itemMenuText = view.findViewById(R.id.item_menu_text);
            TextView subMenuText = view.findViewById(R.id.sub_menu_text);

            if (menu.subNameItem != null) {
                subMenuText.setText(menu.subNameItem);
                subMenuText.setVisibility(View.VISIBLE);
            } else {
                subMenuText.setVisibility(View.GONE);
            }


            TextView countMyOrder = view.findViewById(R.id.count_my_order);
            itemMenuText.setText(menu.nameItem);
            if (selectedId == R.drawable.ic_qa_client_address) {
                countMyOrder.setBackgroundResource(R.drawable.bg_krug_count_activ);//background="@drawable/rectangle"
                countMyOrder.setTextColor(ContextCompat.getColor(getAWork(), R.color.text_white));
            }

            if (menu.idItem == R.drawable.ic_qa_client_address) {
                countMyOrder.setVisibility(View.VISIBLE);
                countMyOrder.setText(String.format("%d", getAWork().sizeCurrentOrders));
            } else
                countMyOrder.setVisibility(View.GONE);

            if (selectedId == menu.idItem) {
                //      view.setBackground(TintIcons.getDrawable(R.drawable.rectangle));

                boduItem.setBackgroundResource(R.drawable.rectangle);//background="@drawable/rectangle"
                itemMenuText.setTextColor(ContextCompat.getColor(getAWork(), R.color.text_color));
                subMenuText.setTextColor(ContextCompat.getColor(getAWork(), R.color.text_color));
                TintIcons.tintImageViewOther(itemMenuIcon, R.color.text_color);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosMenu = menu.idItem;
                    switch (menu.idItem) {

                        case R.drawable.ic_local_taxi_white:
                            // getAWork().showFragmentNewReg();
                             getAWork().showV3FRoute();

                            break;

                        case R.drawable.ibv_ic_show_demo:
                            Injector.getSettingsStore().setDefHashVerCode(0);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getAWork().showV3FRoute(false);
                                }
                            }, 280);


                            break;
                        case R.drawable.ic_settings_menu:
                            getAWork().showFSettings();

                            break;

                        case R.drawable.ic_card_menu:
                            getAWork().showV2FSetCashOrder();

                            break;

                        case R.drawable.ic_my_point_white_24dp:
                            getAWork().showV2FMypoint(0);

                            break;
                        case R.drawable.ic_my_orders:

                            getAWork().showV2FShortOrdersHistory();
                            break;

                        case R.drawable.ic_qa_client_address:

                            if (getAWork().sizeCurrentOrders > 1)
                                getAWork().showV2FShortOrdersPrivate();
                            else if (getAWork().shortOrderInfos != null
                                    && getAWork().shortOrderInfos.size() != 0) {
                                ShortOrderInfo adr = getAWork().shortOrderInfos.get(0);
                                long orderId = adr.id;
                                Address adddr = adr.route.get(0);
                                GpsPosition pos = adddr.position;
                                getAWork().showFSearchCar(pos, orderId);
                            }


                            break;

                        case R.drawable.ic_friends:
                            Service service = Injector.getClientData().service;
                            if (service != null && service.getLptype() != null) {
                                getAWork().showFReferral(service.getLptype());

                            } else {
                                getAWork().showFReferral("none");
                            }
                            break;

                        case R.drawable.ic_call_while_24dp:
                            getAWork().initCallDisp();

                            break;

                        case R.drawable.ic_support_url:
                            getAWork().showSupportUrl();
                            break;

                    }


                    getAWork().closeDrawer();
                    updUIMenu(menu.idItem);

                }
            });

            menuCont.addView(view);
        }

        initBannerCassaNova();
    }

    private void initBannerCassaNova() {
        if (App.app.hashBC.menuBottomBannerState) {
            View view = LayoutInflater.from(menuCont.getContext())
                    .inflate(R.layout.row_item_menu_banner, menuCont, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String menuBottomBannerLink = App.app.hashBC.menuBottomBannerLink;
                    if (menuBottomBannerLink == null)
                        return;

                    Uri address = Uri.parse(menuBottomBannerLink);
                    Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                    startActivity(openlink);
                }
            });
            menuCont.addView(view);
        }
    }

    @Subscribe
    public void setNameLeftMenu(BusSetNameLeftMenu e) {
        setNameLeftMenu();

    }

    public void setNameLeftMenu() {
        String name = Injector.getSettingsStore().readString(Constants.PROFIL_NAME, null);
        profilName.setText(name != null && !"".equals(name) ?
                name : getString(R.string.edit_profile));

    }
}
