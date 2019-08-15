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

package com.opentadam.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.Gson;
import com.opentadam.App;
import com.opentadam.BuildConfig;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusArayPingOrderInfo;
import com.opentadam.bus.BusDisabledTarif;
import com.opentadam.bus.BusEnabledStorageFotoPermission;
import com.opentadam.bus.BusErrorCodeServers;
import com.opentadam.bus.BusInicializeMenuArrayList;
import com.opentadam.bus.BusPreloadMenu;
import com.opentadam.bus.BusRestartAWork;
import com.opentadam.bus.BusUpdArrTariff;
import com.opentadam.bus.BusUpdGeozoneTarif;
import com.opentadam.bus.BusUpdMenu;
import com.opentadam.bus.HiveBus;
import com.opentadam.data.ClientData;
import com.opentadam.data.DeviceScreen;
import com.opentadam.data.DisplayInfo;
import com.opentadam.edit_order.EFEditCost;
import com.opentadam.edit_order.EFSetCashOrder;
import com.opentadam.edit_order.EV2FAddressFeed;
import com.opentadam.edit_order.EV2FCommentOrder;
import com.opentadam.edit_order.EV2FOptions;
import com.opentadam.edit_order.ObjecEditList;
import com.opentadam.menu.FLeftMenu;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.UtilitesErrorIGetApiResponse;
import com.opentadam.network.UtilitesErrorIGetApiResponseObject;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.AssigneeCall;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.SendCreateRequest;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.start.StartActivity;
import com.opentadam.ui.cash.FWebViewAllClass;
import com.opentadam.ui.creating_an_order.FCommetAndOptions;
import com.opentadam.ui.creating_an_order.ObjectRestoryV3Route;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.ui.execution_of_orders.FFinishInfo;
import com.opentadam.ui.execution_of_orders.FSearchCar;
import com.opentadam.ui.execution_of_orders.FStatusInfo;
import com.opentadam.ui.frends.FSetNameAndMail;
import com.opentadam.ui.frends.FSettings;
import com.opentadam.ui.frends.VFProfil;
import com.opentadam.ui.frends.referal.V2FReferral;
import com.opentadam.ui.frends.referal_new_reg.FragmentNewReg;
import com.opentadam.ui.order.FProlongation;
import com.opentadam.ui.order.V2FAddressFeed;
import com.opentadam.ui.order.V2FBonuses;
import com.opentadam.ui.order.V2FEditPrivateAdress;
import com.opentadam.ui.order.V2FFullTextSearch;
import com.opentadam.ui.order.V2FOrderHistoryInfo;
import com.opentadam.ui.order.V2FPrivateFullTextSearch;
import com.opentadam.ui.order.V2FSetTimeOrder;
import com.opentadam.ui.order.V2FShortOrdersHistory;
import com.opentadam.ui.order.V2FShortOrdersPrivate;
import com.opentadam.ui.order.addaddress.FAddEditAdress;
import com.opentadam.ui.qrCode.CreateQRCodeFragment;
import com.opentadam.ui.qrCode.ScannerQRFragment;
import com.opentadam.ui.registration.V2RegistrationFragment;
import com.opentadam.ui.registration.V2SmsCodeFragment;
import com.opentadam.ui_payemnts_metods.FCardSettings;
import com.opentadam.ui_payemnts_metods.V2FSetCashOrder;
import com.opentadam.utils.UIOrder;
import com.opentadam.utils.Utilites;
import com.opentadam.view.FDialogCustom;
import com.squareup.otto.Subscribe;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.push.YandexMetricaPush;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.opentadam.Injector.getClientData;
import static com.opentadam.utils.Utilites.initColor;


public class AWork extends BaseActivity {

    private static final int ZBAR_CAMERA_PERMISSION = 91;
    private final WeakHandler mHandler = new WeakHandler(Looper.getMainLooper());
    public int sizeCurrentOrders = 0;
    public ArrayList<ShortOrderInfo> shortOrderInfos = null;
    public boolean isManualAdress = false;
    public boolean blokPing = true;
    @InjectView(R.id.drawer_layout)
    public DrawerLayout drawer;
    public long pushOrderId = -1;
    public AccountState accountState;
    public boolean isDisabledPing;
    public String nameParentFragmentMaps;
    public boolean isInitMaps = false;
    @InjectView(R.id.error_sustem_layot)
    LinearLayout errorLustemLayot;
    //
    @InjectView(R.id.work_dialog)
    FrameLayout workDialog;
    @InjectView(R.id.upd_dialog)
    FrameLayout updDialog;
    @InjectView(R.id.work_container)
    FrameLayout workContainer;
    @InjectView(R.id.progress_devault)
    FrameLayout progressDevault;
    @InjectView(R.id.progress_devault_bg_none)
    FrameLayout progressDevaultUpdate;
    @InjectView(R.id.body)
    View bodyApp;
    @InjectView(R.id.cont_menu_edit)
    LinearLayout contMenuEdit;
    @InjectView(R.id.order_edit)
    View orderEdit;

    @InjectView(R.id.updateAppLinearLayout)
    View updateAppLinearLayout;

    @InjectView(R.id.progress_devault_progress_none)
    View progressDevaultUpdateHideProress;
    @InjectView(R.id.error_work_container)
    View errorWorkContainer;
    private boolean isVissibleAct = false;
    private ObjectRestoryV3Route objectRestoryV3Route;
    private CountDownTimer mStatusDownTimer;
    private OrderInfo mOrderInfo;
    private boolean isInitBody = false;
    private Timer timerRefresher;
    private boolean isUpdate = false;
    private boolean isFon;
    private boolean isShowFSplash = false;
    private DisplayInfo displayInfo;
    private Bundle bundle;
    private boolean isEnabledEditTariff = true;
    private float hashSlideOffset;
    private boolean isEnabledRestBonusClient = true;
    private UtilitesErrorIGetApiResponse utilitesErrorIGetApiResponse;

    public static void show(Context context) {
        Intent intent = new Intent(context, AWork.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public SendCreateRequest getSendCreateRequest() {
        return sendCreateRequest;
    }

    public void setSendCreateRequest(SendCreateRequest sendCreateRequest) {
        this.sendCreateRequest = sendCreateRequest;
    }

    private SendCreateRequest sendCreateRequest;

    public UtilitesErrorIGetApiResponse getUtilitesErrorIGetApiResponse() {
        return utilitesErrorIGetApiResponse;
    }

    public void setUtilitesErrorIGetApiResponse(UtilitesErrorIGetApiResponse val) {
        utilitesErrorIGetApiResponse = val;
    }

    public void setObjectRestoryV3Route(ObjectRestoryV3Route objectRestoryV3Route) {
        this.objectRestoryV3Route = objectRestoryV3Route;
    }

    public void updLocale() {
        App.app.initLocation();
        App.app.finish();
        Injector.getClientData().isRecreateLocale = true;
        restartAll();
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        workContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }


    }

    public void getBonusClient() {
        RESTConnect restConnect = Injector.getRC();
        LatLng latLng = Injector.getClientData().getMarkerLocation();
        restConnect.getBonusClient(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) return;

                TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
                mRoute.bonuses = apiResponse.bonuses;

                App.bus.post(new BusUpdMenu());
            }
        });

    }

    public void showProgressDevault() {
        progressDevault.setVisibility(View.VISIBLE);
    }

    public void hideProgressDevault() {
        progressDevaultUpdateHideProress.setVisibility(View.GONE);
        progressDevaultUpdate.setVisibility(View.GONE);
        progressDevault.setVisibility(View.GONE);
    }

    public void showWorkCont() {
        workContainer.setVisibility(View.VISIBLE);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            workContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

    }

    public synchronized void showErrorIGetApiResponse(ApiResponse apiResponse) {
        if (utilitesErrorIGetApiResponse != null && utilitesErrorIGetApiResponse.isSnackbarHide())
            utilitesErrorIGetApiResponse.showMessage();
        if (utilitesErrorIGetApiResponse != null) utilitesErrorIGetApiResponse.hideAnimePin();

        addErrorIds(apiResponse);
        if (isDisabledPing) return;
        showBody();

        isDisabledPing = true;
        utilitesErrorIGetApiResponse = UtilitesErrorIGetApiResponse.newInstance(this, apiResponse)
                .setBodyApp(bodyApp)
                .setErrorWorkContainer(errorWorkContainer);

        utilitesErrorIGetApiResponse
                .build();
    }

    private void addErrorIds(ApiResponse apiResponse) {
        UtilitesErrorIGetApiResponseObject val = apiResponse.utilitesErrorIGetApiResponseObject;
        ClientData clientData = Injector.getClientData();
        ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap = clientData.getArrayMap();
        int size = arrayMap.size();

        for (int i = 0; i < size; i++) {
            UtilitesErrorIGetApiResponseObject value = arrayMap.valueAt(i);
        }
        arrayMap.put(apiResponse.path, val);
    }

    @Override
    protected void onCreate(Bundle s) {
        isLandscapeTest();
        bundle = s;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setVolumeControlStream(AudioManager.STREAM_RING);
        setVolumeControlStream(AudioManager.STREAM_ALARM);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) App.app.setBaseLocale();
        App.app.activate();
        super.onCreate(s);
        handlePayload(getIntent());
        setContentView(R.layout.a_work);
        ButterKnife.inject(this);
        if (Injector.restServer == null) {
            restartAll();
            return;
        }

        checkForUpdatesApp();

        initActivity();
        if (App.app.hashBC.test != null) alert(App.app.hashBC.test);
    }

    private void handlePayload(Intent intent) {
        // Получение вашего payload.
        String payload = intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD);
        if(payload == null) return;
        showMessage(payload);
    }

    private void checkForUpdatesApp() {
        updateAppLinearLayout.setVisibility(
                BuildConfig.VERSION_CODE < App.app.hashBC.minimalCodeGoogleVersion ? View.VISIBLE : View.GONE);
        if(BuildConfig.VERSION_CODE < App.app.hashBC.currentCodeGoogleVersion && !Injector.isDisabledPingUpdateApp){
            Injector.isDisabledPingUpdateApp = true;
            showUpdateApp();
        }
    }

    private void initActivity() {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (slideOffset - hashSlideOffset > 0 && isEnabledRestBonusClient) {
                    isEnabledRestBonusClient = false;
                    getBonusClient();
                }
                hashSlideOffset = slideOffset;
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isEnabledRestBonusClient = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        if (bundle == null && isShowSplashAnime()) {
            isShowFSplash = true;
            showFSplash();
        }
        initLeftMenu();
        initOther(bundle);

        if (bundle != null && !Injector.getClientData().isRecreateLocale) showBody();
        App.bus.register(this);
    }

    private void initFirebaseDynamicLinks() {
        //
// com.hivetaxi.dev_ext://order?client_id=2gis&product_id=4000008902355&start_latitude=54.981565078013304&start_longitude=73.31610135734081&from_address=%D0%BE%D0%B1%D0%BB%20%D0%9E%D0%BC%D1%81%D0%BA%D0%B0%D1%8F%2C%20%D0%B3%20%D0%9E%D0%BC%D1%81%D0%BA%2C%20%D0%BF%D1%80-%D0%BA%D1%82%20%D0%9A%D0%BE%D0%BC%D0%B0%D1%80%D0%BE%D0%B2%D0%B0%2C%2029&end_latitude=54.98245431712552&end_longitude=73.31880502402782&to_address=%D0%BE%D0%B1%D0%BB%20%D0%9E%D0%BC%D1%81%D0%BA%D0%B0%D1%8F%2C%20%D0%B3%20%D0%9E%D0%BC%D1%81%D0%BA%2C%20%D1%83%D0%BB%2070%20%D0%BB%D0%B5%D1%82%20%D0%9E%D0%BA%D1%82%D1%8F%D0%B1%D1%80%D1%8F%2C%2015
        Intent intent = getIntent();
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) return;

                        Uri deepLink = data.getLink();

                        if (deepLink != null && deepLink.getQueryParameter("referral") != null) {
                            String referrerCode = deepLink.getQueryParameter("referral");
                            if (referrerCode != null)
                                Injector.getSettingsStore().setRefererrClient(referrerCode);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    public void showBody() {
        bodyApp.setVisibility(View.VISIBLE);
    }

    public void hideBody() {
        bodyApp.setVisibility(View.GONE);
    }


    @Subscribe
    public void onBusRestartAWork(BusRestartAWork e) {
        restartAll();
    }

    public void getTarif(LatLng markerLocation) {
        Injector.getClientData().latLngTarifHTPSRequest = markerLocation;
        Injector.getRC()
                .getService(Injector.getClientData()
                        .getPaymentMethodSelect(), markerLocation, new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (isFinishing()) return;
                        if (apiResponse != null
                                && apiResponse.error != null
                                && Injector.isOfflineStatus(apiResponse.retrofitError)) {
                            showErrorIGetApiResponse(apiResponse);
                            return;
                        }
                        bodyGetTarif(apiResponse);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean b = requestCode == ZBAR_CAMERA_PERMISSION && grantResults.length > 0;
        if (b) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bodyShowGRScanner();
            } else {
                alert(getString(R.string.error_permis_camera));
                return;
            }

        }


        if (requestCode == Constants.PERMISSION_REQUEST_LOCATION && grantResults.length == 1)
            return;

        if (requestCode == Constants.PERMISSION_REQUEST_FOTO && grantResults.length == 1) {
            boolean isStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!isStorage) {
                final String message = getString(R.string.err_storage_foto);
                getSnackbar(message, getString(R.string.snackbar_title_media)
                        , Constants.PERMISSION_REQUEST_FOTO);

            } else {
                App.bus.post(new BusEnabledStorageFotoPermission(true));
            }
        }
    }


    private void getSnackbar(String message, final String messageToast, final int codeRec) {
        final Snackbar finalGrant = Snackbar.make(bodyApp, message, Snackbar.LENGTH_INDEFINITE);
        showBody();
        View snackbarView = finalGrant.getView();

        TextView snackTextView = snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.WHITE);
        snackTextView.setMaxLines(5);
        finalGrant.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
            }

        });

        finalGrant.setAction(getString(R.string.name_item_menu_setting), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApplicationSettings(codeRec);
                finalGrant.dismiss();
            }

        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PERMISSION_REQUEST_FOTO && resultCode == RESULT_OK) {
            App.bus.post(new BusEnabledStorageFotoPermission(true));
            return;
        }

        if (requestCode == Constants.PERMISSION_REQUEST_LOCATION) return;


        if (requestCode == Constants.REFERRER_CLIENT_INVITE || requestCode == Constants.SEND_REFERAL_LINKS_RESAULT) {

            if (requestCode == Constants.SEND_REFERAL_LINKS_RESAULT) {
                showFReferral("ext-bonus-referral-vip");
                String result = getString(R.string.awork_ok_send_sms);
                showMessage(result);

                return;
            }
            if (resultCode == RESULT_OK) {
                String result = getString(R.string.awork_ok_send_sms);
                showMessage(result);

            } else {
                showMessage(getString(R.string.awork_error_send));
            }
            showFReferral("ext-bonus-referral-vip");
        }
    }

    public void showMessage(String msg) {
        final Snackbar finalGrant = Snackbar
                .make(bodyApp, msg, Snackbar.LENGTH_LONG);
        View snackbarView = finalGrant.getView();

        TextView snackTextView = snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.WHITE);
        snackTextView.setMaxLines(5);
        finalGrant.show();
    }

    private void openApplicationSettings(final int codeRec) {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent
                , codeRec
        );
    }

    @Subscribe
    public void onBusErrorCodeServers(BusErrorCodeServers e) {
        if (e.gsonError != null) {
            ErrorCodeServers errorCodeServers = new Gson()
                    .fromJson(e.gsonError, ErrorCodeServers.class);
            if (errorCodeServers.code == -10009 && isEnabledEditTariff) {
                isEnabledEditTariff = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing())
                            return;
                        isEnabledEditTariff = true;
                    }
                }, 1000);
            }
        }
    }

    private void initOther(Bundle s) {
        if (!checkPlayServices()) alert(getString(R.string.info_restart));

        showWorkProgress();
        App.app.getGATracker().setScreenName("aWork");
        App.app.getGATracker().send(new HitBuilders.ScreenViewBuilder().build());
        timeSynchronizationServers(null);

        timerRefresher = new Timer();
        timerRefresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDisabledPing) {
                            timerRefresher.cancel();
                            return;
                        }


                        if (timerRefresher != null && !isUpdate)
                            isUpdate = true;
                        else
                            timerRefresher.cancel();
                    }
                });
            }
        }, 10000, 10000);

        if (s != null) {
            setBGWork();
            isFon = true;
        }
        getDisplayInfo();
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null) onNewIntent(intent);
    }

    private void initBody() {
        if (isShowFSplash) return;
        App.bus.post(new BusPreloadMenu());
        setDefPag();
        hideWorkProgress();
    }

    public void hideSplashFragment() {
        if (isFinishing()) return;
        isShowFSplash = false;
        show();
        Fragment f = getSupportFragmentManager().
                findFragmentById(R.id.work_splash);
        if (f != null)
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        initBody();
    }

    private void showFSplash() {
        hide();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.work_splash, V2FSplash.newInstance())
                .commitAllowingStateLoss();
    }

    private boolean isShowSplashAnime() {
        AssetManager assets = getResources().getAssets();
        if (assets == null) return false;
        try {
            String[] nameFiles = assets.list("anime"); // массив имен файлов
            for (String name : nameFiles) {
                boolean equals = "data_splash.json".equals(name);

                if (equals) return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        handlePayload(intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null && App.app.hashBC.APPLICATION_ID.equals(intent.getScheme()) && "2gis".equals(data.getQueryParameter("client_id"))) {
            String tarifId = data.getQueryParameter("product_id");
            String finishAddress = data.getQueryParameter("to_address");
            String finishLat = data.getQueryParameter("end_latitude");
            String finishLon = data.getQueryParameter("end_longitude");

            String startAddress = data.getQueryParameter("from_address");
            String startLat = data.getQueryParameter("start_latitude");
            String startLon = data.getQueryParameter("start_longitude");

            final ArrayList<ClientAddress> route = new ArrayList<>();
            if (startAddress != null)
                route.add(new ClientAddress(new Address(startAddress, startLat, startLon)));

            if (finishAddress != null)
                route.add(new ClientAddress(new Address(finishAddress, finishLat, finishLon)));

            sendCreateRequest = new SendCreateRequest.Builder()
                    .setPaymentMethod(getClientData().getPaymentMethodSelect())
                    .setTariff(Long.parseLong(tarifId))
                    .setRoute(route).buidl();
        }

        switch (action) {

            case Constants.UPD_LOCALE:
                Injector.getClientData().isShowSettings = intent
                        .getBooleanExtra("settings", false);
                intent.putExtra("settings", false);
                break;

            case Constants.PUSH:
                pushOrderId = intent.getLongExtra("orderId", -1);
                break;
        }
    }

    private int pxToDp(float px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) ((px / displayMetrics.density) + 0.5);
    }

    public DisplayInfo getDisplayInfo() {
        // узнаем размеры экрана из класса Display
        if (displayInfo != null) return displayInfo;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        int heightPixels = metricsB.heightPixels;
        int dpHeight = pxToDp((float) heightPixels);

        int widthPixels = metricsB.widthPixels;
        int dpWidth = pxToDp((float) widthPixels);

        displayInfo = new DisplayInfo(heightPixels, dpHeight, widthPixels, dpWidth);
        return displayInfo;
    }

    public void initAdressToRoute(int modeAdress, Address address, int indexAdress) {
        switch (modeAdress) {
            case Constants.ADD_ADRESS:
                addAdress(address);
                break;
            case Constants.EDIT_ADRESS:
                editAdress(address, indexAdress);

                break;
            case Constants.INSERT_ADRESS:
                insertAdress(address, indexAdress);
                break;
        }
    }

    private void insertAdress(Address address, int indexAdress) {
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        createRequest.insertAdressToRoute(address, indexAdress);
        showFRouteCreaton();
    }

    private void editAdress(Address address, int indexAdress) {
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        createRequest.editAdressToRoute(address, indexAdress);

        if (indexAdress != 0) {
            isManualAdress = false;
            showFRouteCreaton();
        } else uploadTariffChangeAdress(createRequest);
    }

    private void addAdress(Address address) {
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        createRequest.addAdressToRoute(address);

        if (createRequest.getSizeRoute() > 1) {
            isManualAdress = false;
            showFRouteCreaton();
        } else uploadTariffChangeAdress(createRequest);
    }

    private void uploadTariffChangeAdress(CreateRequest createRequest) {
        LatLng markerLocation;
        ArrayList<GpsPosition> routeLocation = createRequest.getRouteLocation();
        if (routeLocation == null || routeLocation.size() == 0) {
            markerLocation = getClientData().getMarkerLocation();
        } else {
            GpsPosition g = routeLocation.get(0);
            markerLocation = g == null ? Injector.getClientData().getMarkerLocation()
                    : new LatLng(g.lat, g.lon);
        }

        Injector.getClientData().setMarkerLocation(markerLocation);
        getTarif(markerLocation);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(Injector.getAppContext(), getString(R.string.error_google_services), Toast.LENGTH_LONG).show();
                closeApp();
            }
            return false;
        }
        return true;
    }

    private void infoChangeTarif() {
        BaseFr f = (BaseFr) getSupportFragmentManager().findFragmentById(R.id.work_container);
        if (f == null)
            return;
        boolean b = f instanceof V2FAddressFeed;

        if (b && isUpdate) alert(getString(R.string.update_geo_zone));
    }

    @Subscribe
    public void onBusUpdGeozoneTarif(BusUpdGeozoneTarif e) {
        if (blockUI()) return;
        infoChangeTarif();
    }

    private boolean blockUI() {
        return !isVissibleAct || isFinishing();
    }

    @Subscribe
    public void onBusDisabledTarif(BusDisabledTarif e) {
        if (blockUI()) return;
        hideWorkProgress();
        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.work_container);

        if (f != null && !(f instanceof V3FRoute)) {
            showV3FRoute(false);

        } else if (!isInitBody) {
            isInitBody = true;
            initBody();
        }
    }

    @Subscribe
    public void onBusUpdArrTariff(BusUpdArrTariff e) {
        if (blockUI()) return;
        updateLeftMenu();
        hideWorkProgress();
        if (isManualAdress) {
            isManualAdress = false;
            showFRouteCreaton();
        }

        if (!isInitBody) {
            isInitBody = true;
            initBody();
        }
    }


    public void updGeozoneTariff(double latitude, double longitude, int distBase) {
        if (isEnabledEditTariff()) return;
        int distance = 1000;
        if (getClientData().getDefTarif() != null) {
            Tarif tarif = Injector.getClientData().getDefTarif();
            float[] results = new float[1];
            double lat = tarif.lat;
            double lon = tarif.lon;
            android.location.Location.distanceBetween(lat,
                    lon,
                    latitude,
                    longitude, results);

            distance = (int) results[0];
            tarif.lat = latitude;
            tarif.lon = longitude;
            LatLng latLngTarifDef = new LatLng(tarif.lat, tarif.lon);
            Injector.getSettingsStore().setLatLngTarifDef(latLngTarifDef);
            Injector.getClientData().setDefTarif(tarif);
        }

        if (distance > distBase) getCurrentTarif();
    }

    private boolean isEnabledEditTariff() {
        if (isManualAdress) return false;
        return Injector.getClientData().getCreateRequest() == null;
    }

    public void getCurrentTarif() {
        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.work_container);

        if (isEnabledEditTariff() && !isFon)
            return;
        if (isFon && !(f instanceof V3FRoute)) {
            isFon = false;
            return;
        }

        isFon = false;
        LatLng markerLocation = Injector.getClientData().getMarkerLocation();
        getTarif(markerLocation);
    }

    public void bodyGetTarif(ApiResponse apiResponse) {
        if (apiResponse == null || apiResponse.service == null) {
            HiveBus.postBusDisabledTarif(null, getString(R.string.error_server_response));
            closeApp();
            return;
        }

        Service service = apiResponse.service;
        ClientData clientData = Injector.getClientData();
        if (clientData.service != null) {
            clientData.service.setKind(service.getKind());
        }
        if ("stub".equals(service.getKind())) {
            HiveBus.postBusDisabledTarif(service.getKind(), service.getMessage());
            return;
        }

        if (service.getTariffs().size() == 0) {
            HiveBus.postBusDisabledTarif(null, getString(R.string.error_list_tarif_empty));
            return;
        }

        clientData.service = service;
        clientData.initService(service);
        HiveBus.postBusEnabledTarif();
    }

    public void timeSynchronizationServers(final LatLng latLng) {
        Injector.getRC()
                .timeSynchronizationServers(latLng, new IGetApiResponse() {
                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (isFinishing())
                            return;

                        if (apiResponse == null || apiResponse.error != null) {
                            if (!blockUI()) {
                                showErrorIGetApiResponse(apiResponse);
                            }
                            return;
                        }


                        Utilites.getStrigIso(apiResponse.timeSynchronizationServers);
                        if (latLng != null)
                            return;
                        getCurrentTarif();
                    }
                });
    }


    public void hideWorkProgressFull() {
    }

    public void restartAll() {
        finish();
        Intent intent = new Intent(this, StartActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isShowNeedsProlongation(ArrayList<ShortOrderInfo> sh) {

        for (ShortOrderInfo shortOrderInfo : sh) {

            if (shortOrderInfo.needsProlongation) {
                closePing();

                ArrayList<Address> route = shortOrderInfo.route;
                Address routeOne = route.get(0);
                Address routeFin = route.get(route.size() - 1);
                String addressOne = routeOne.getNameAdressSearsh();
                if (addressOne == null) addressOne = getString(R.string.point_to_maps);
                String nameAdressSearsh = routeFin.getNameAdressSearsh();
                if (nameAdressSearsh == null) nameAdressSearsh = getString(R.string.point_to_maps);
                String addressFin = route.size() == 1 ? getString(R.string.adr_value_def) : nameAdressSearsh;

                showFProlongation(addressOne, addressFin, shortOrderInfo.id);
                return true;
            }
        }
        return false;
    }

    private void closePing() {
        blokPing = true;
        if (mStatusDownTimer != null) mStatusDownTimer.cancel();
    }

    private void startPingShortOrderInfos(ArrayList<ShortOrderInfo> sh) {
        blokPing = false;
        shortOrderInfos = sh;
        sizeCurrentOrders = sh.size();
        App.bus.post(new BusInicializeMenuArrayList());
        pingShortOrderInfos(sh);
    }

    private void pingShortOrderInfos(final ArrayList<ShortOrderInfo> sh) {
        if (mStatusDownTimer != null) mStatusDownTimer.cancel();
        mStatusDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (isDisabledPing) {
                    closePing();
                    cancel();
                }
            }

            public void onFinish() {
                if (sh != null && shortOrderInfos != null) {

                    if (sh.size() == 0) {
                        closePing();
                        return;
                    }

                    if (sh.size() == shortOrderInfos.size() && sh.size() != 1) {
                        // изменение статуса проверяем если больше одного заказа

                        for (ShortOrderInfo shortOI : shortOrderInfos) {
                            for (ShortOrderInfo shOI : sh) {

                                if (shortOI.id == shOI.id) {

                                    if (shortOI.state != shOI.state) {
                                        toPing(sh);
                                        activateFSearchCar(shOI);
                                        return;
                                    }
                                }
                            }
                        }

                    } else if (sh.size() < shortOrderInfos.size()) {
                        // заказ удален на сервере
                        for (ShortOrderInfo shortOI : shortOrderInfos) {
                            for (ShortOrderInfo shOI : sh) {
                                if (shortOI.id == shOI.id)
                                    shortOI.id = 0;
                            }

                            if (shortOI.id != 0) {
                                toPing(sh);
                                App.bus.post(new BusPreloadMenu());
                                activateFSearchCar(shortOI);
                                return;
                            }
                        }

                    } else if (sh.size() > shortOrderInfos.size()) {
                        // заказ добавлен на сервере
                        for (ShortOrderInfo shOI : sh) {
                            for (ShortOrderInfo shortOI : shortOrderInfos) {
                                if (shortOI.id == shOI.id)
                                    shOI.id = 0;
                            }

                            if (shOI.id != 0) {
                                toPing(sh);
                                App.bus.post(new BusPreloadMenu());
                                activateFSearchCar(shOI);
                                return;
                            }
                        }

                    }
                    if (!isDisabledPing) toPing(sh);
                }
            }
        };
        mStatusDownTimer.start();
    }

    private void activateFSearchCar(ShortOrderInfo shOI) {
        ArrayList<Address> route = shOI.route;
        Address oneAdress = route.get(0);
        GpsPosition gpsPosition = oneAdress.position;
        if (gpsPosition != null) showFSearchCar(gpsPosition, shOI.id);
    }

    private void toPing(ArrayList<ShortOrderInfo> sh) {
        shortOrderInfos = sh;
        sizeCurrentOrders = sh.size();
        App.bus.post(new BusInicializeMenuArrayList());
        getPingOrders();
    }

    private void showInfoThisCurrentOrder(ArrayList<ShortOrderInfo> sh) {
        ShortOrderInfo shortOrderInfo = sh.get(0);
        shortOrderInfos = sh;
        sizeCurrentOrders = sh.size();
        if (shortOrderInfo == null) return;
        activateFSearchCar(shortOrderInfo);
    }

    public void showFProfil() {
        getAWork().showTopFragment(VFProfil.newInstance());
    }

    public void showMenu() {
        openDrawer();
    }

    public void delFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .remove(f)
                .commitAllowingStateLoss();
    }

    private void setDefPag() {
        Fragment f;
        if (Injector.getClientData().isShowSettings) {
            Injector.getClientData().isShowSettings = false;
            f = FSettings.newInstance();
        } else if (Injector.getClientData().isShowProfile) {
            Injector.getClientData().isShowProfile = false;
            f = VFProfil.newInstance();
        } else
            f = V3FRoute.newInstance();
        showTopFragment(f);
        if (Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null) != null)
            getCurrentOrders();
    }

    private void initLeftMenu() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cont_left_menu, FLeftMenu.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
    }

    public void initCallDisp() {
        App.bus.post(new BusUpdMenu(R.drawable.ic_call_while_24dp));
        String dispatcherCall = Injector.getClientData().getDispatcherCall();
        if (dispatcherCall == null)
            return;
        if ("via server".equals(dispatcherCall)) {
            // заказ звонка
            getCallDisp();

        } else {
            // звонок по номеру dispatcherCall
            Intent intent = new Intent(Intent.ACTION_DIAL
                    , Uri.parse("tel:" + dispatcherCall));
            startActivity(Intent
                    .createChooser(intent, getString(R.string.call)));
        }

    }

    public void showFCardSettings(AccountState a, int parentView) {
        showTopFragment(FCardSettings.newInstance(a, parentView));
    }

    public void showFSettings() {
        showTopFragment(FSettings.newInstance());
    }

    public void toglLeftMenu() {
        if (drawer.isDrawerOpen(GravityCompat.START)) closeDrawer();
        else openDrawer();
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    private void getCallDisp() {
        Injector.getRC().getCallDisp(new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {
                    showErrorIGetApiResponse(apiResponse);
                    return;
                }
                showPrivateDialog((BaseFr) FDialogCustom.newInstance());
            }
        });
    }

    public void showPrivateDialog(BaseFr baseFr) {
        if (isFinishing())
            return;
        workDialog.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.work_dialog, baseFr)
                .commitAllowingStateLoss();
    }

    public void hidePrivateDialog() {
        if (isFinishing() || workDialog == null)
            return;
        workDialog.setVisibility(View.GONE);
        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.work_dialog);
        if (f != null)
            getSupportFragmentManager()
                    .beginTransaction().remove(f)
                    .commitAllowingStateLoss();
    }

    public void showPushFSCar() {

        if (shortOrderInfos != null) {
            for (ShortOrderInfo sh : shortOrderInfos) {
                if (sh.id == getAWork().pushOrderId) {
                    sh.getStartGpsPosition();
                    showFSearchCar(sh.getStartGpsPosition(), getAWork().pushOrderId);
                    return;
                }
            }
        }

        if (pushOrderId > 0) showFSearchCar(null, pushOrderId);
    }

    private void showFProlongation(String addressOne, String addressFin, long idRoute) {
        showTopFragment(FProlongation.newInstance(addressOne, addressFin, idRoute));
    }

    public void showV2FShortOrdersPrivate() {
        showTopFragment(V2FShortOrdersPrivate.newInstance());
    }

    public void showV2FMypoint(long id) {
        showTopFragment(V2FMypoint.newInstance(id));
    }

    public void showFAddressFeed(int indexAdress, boolean isStart) {
        showTopFragment(V2FAddressFeed.newInstance(indexAdress, isStart));
    }

    public void showFStatusInfo(
            long idRoute,
            int state,
            String color,
            String brandModel,
            String regNumInfoText) {

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.work_container, FStatusInfo.newInstance(idRoute, state, color, brandModel,
                        regNumInfoText))
                .commitAllowingStateLoss();
    }

    public void removeFStatusInfo(FStatusInfo fStatusInfo) {

        getSupportFragmentManager()
                .beginTransaction()
                .remove(fStatusInfo)
                .commitAllowingStateLoss();
    }

    public void showFFinishInfo() {
        showTopFragment(FFinishInfo.newInstance());
    }

    public OrderInfo getOrderInfo() {
        return this.mOrderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        mOrderInfo = orderInfo;
    }

    private AWork getAWork() {
        return this;
    }

    public void setBGWork() {
        workContainer.setBackgroundColor(initColor(R.color.bg_while));
    }

    public void showWorkProgress() {
    }

    public void hideWorkProgress() {
    }

    public void showV2FShortOrdersHistory() {
        showTopFragment(V2FShortOrdersHistory.newInstance());
    }

    public void showFSearchCar(GpsPosition gpsPosition, long id) {
        showTopFragment(FSearchCar.newInstance(gpsPosition, id));
    }

    public void showFAddEditAdress() {
        showTopFragment(FAddEditAdress.newInstance());
    }

    public void showV2FRegistration(boolean isProfil, boolean isRestart) {
        showTopFragment(V2RegistrationFragment.newInstance(isProfil, isRestart));
    }

    public void showFRouteCreaton() {
        showV3FRoute();
    }

    public void showV2FSmsCode(String phoneUser, long id, String value, boolean isProfil, boolean isRestart) {
        showTopFragment(V2SmsCodeFragment.newInstance(phoneUser, id, value, isProfil, isRestart));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        HiveBus.postBusWindowFocusChanged();
    }

    public void showV3FRoute() {
        showV3RestoryFRoute();
    }

    public void showV3FRoute(boolean isShowMenu) {
        showTopFragment(V3FRoute.newInstance(false, isShowMenu));
    }

    public void showV3FRoute(boolean isPage0, boolean isShowMenu) {
        showTopFragment(V3FRoute.newInstance(isPage0, isShowMenu));
    }

    private void showV3FRoute(ObjectRestoryV3Route objectRestoryV3Route) {
        showTopFragment(V3FRoute.newInstance(objectRestoryV3Route));
    }

    public void showV2FFullTextSearch(int indexAdress, int modeAdress) {
        showTopFragment(V2FFullTextSearch.newInstance(indexAdress, modeAdress));
    }

    public void showV2FSetTimeOrder() {
        showTopFragment(V2FSetTimeOrder.newInstance());
    }

    @Override
    public void onBackPressed() {
        if (isFinishing())
            return;
        BaseFr f = (BaseFr) getSupportFragmentManager()
                .findFragmentById(R.id.upd_dialog);
        if (f != null) {
            hideUpdateApp();
            return;
        }
        f = (BaseFr) getSupportFragmentManager()
                .findFragmentById(R.id.work_dialog);

        if (f != null) {
            hidePrivateDialog();
            return;
        }

        f = (BaseFr) getSupportFragmentManager()
                .findFragmentById(R.id.work_container);

        if (Injector.getClientData().isRestartAll) {
            closeApp();
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
            return;

        } else if (f != null && f.isVisible() && f.onBackPressed()) {
            return;
        }
        closeApp();
    }

    public void alert(String text) {
        showMessage(text);
    }

    public void showTopFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.work_container, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();

        String name = f.getClass().getName();


        int idMenu = -1;
        if (f instanceof V3FRoute) {
            idMenu = R.drawable.ic_local_taxi_white;
        } else if (f instanceof V2FSetCashOrder) {
            idMenu = R.drawable.ic_card_menu;
        } else if (f instanceof V2FMypoint) {
            idMenu = R.drawable.ic_my_point_white_24dp;
        } else if (f instanceof V2FShortOrdersHistory) {
            idMenu = R.drawable.ic_my_orders;
        } else if (f instanceof V2FShortOrdersPrivate || f instanceof FSearchCar) {
            idMenu = R.drawable.ic_qa_client_address;
        } else if (f instanceof V2FReferral) {
            idMenu = R.drawable.ic_friends;
        }

        if (idMenu > 0) App.bus.post(new BusUpdMenu(idMenu));
    }

    @OnClick(R.id.error_sustem_layot)
    public void hideErrMess() {
        errorLustemLayot.setVisibility(View.GONE);
    }

    @OnClick(R.id.updateAppSendTextView)
    public void onUpdateAppSendTextView() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
            startActivity(Intent.createChooser(intent, getString(R.string.fmenu_create_chooser)));

        } catch (Exception e) {
            alert(getString(R.string.error_upd_google));
        }
    }

    @Override
    protected void onDestroy() {
        App.bus.unregister(this);
        Injector.getClientData().isNotNullmarkerLocation = false;
        Service service = Injector.getClientData().service;
        if (service != null) {
            service.isNoRegReferall = false;
        }
        if (utilitesErrorIGetApiResponse != null) {
            utilitesErrorIGetApiResponse.finish();
        }
        utilitesErrorIGetApiResponse = null;
        if (timerRefresher != null)
            timerRefresher.cancel();

        closePing();
        hideWorkProgress();

        stopService(new Intent(this, AWork.class));

        super.onDestroy();
        App.app.mustDie(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.app.mMyGoogleLocation.startLocationUpdates();
        App.uiActivated = true;
        isVissibleAct = true;
        initFirebaseDynamicLinks();
        getPingOrders();
        if (App.app.hashBC.keyAppMetrica != null) {
            if(!App.app.initYandexMetrica) App.app.initYandexMetrica(App.app.hashBC.keyAppMetrica);
            // Возобновляет сессию.
            YandexMetrica.resumeSession(this);
        }
    }

    @Override
    protected void onPause() {
        App.app.mMyGoogleLocation.setMemoryLocation(Injector.getClientData().getMarkerLocation());
        sizeCurrentOrders = 0;
        hideWorkProgress();
        App.app.mMyGoogleLocation.stopLocationUpdates();
        App.uiActivated = false;
        isVissibleAct = false;
        super.onPause();
        if (App.app.hashBC.keyAppMetrica != null) {
            // Приостанавливает сессию.
            YandexMetrica.pauseSession(this);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void closeApp() {
        Injector.getClientData().clear();
        Injector.isDisabledPingUpdateApp = false;
        App.app.finish();
        finish();
    }

    public void sendProlongation(int posProlongation, long idRoute) {
        showWorkProgress();
        Injector.getRC().sendProlongation(posProlongation, idRoute, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing() || blockUI())
                    return;
                restartAll();
            }
        });
    }

    public void showV2FBonuses(long idRoute, boolean isRoute) {
        showTopFragment(V2FBonuses.newInstance(idRoute, isRoute));
    }

    public void showV2FOrderHistoryInfo(long orderId, String time) {
        showTopFragment(V2FOrderHistoryInfo.newInstance(orderId, time));
    }

    public void showV2FeditPrivateAdress(long id, boolean isAdd) {
        showTopFragment(V2FEditPrivateAdress.newInstance(id, isAdd));
    }

    public void showV2FPrivateFullTextSearch(long id) {
        showTopFragment(V2FPrivateFullTextSearch.newInstance(id));
    }

    public Fragment getBaseFr() {
        return getSupportFragmentManager().findFragmentById(R.id.work_container);
    }

    public void showV2FSetCashOrder() {
        showTopFragment(V2FSetCashOrder.newInstance());
    }

    public void getCurrentOrders() {
        showWorkProgress();
        Injector.getRC().getOrderCurrent("getCurrentOrders", new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {
                    showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (("getCurrentOrders" + Constants.PATH_ORDERS_CURRENT).equals(apiResponse.path)) {
                    bodyGetCurrentOrders(apiResponse.shortOrderInfos);
                }
            }
        });
    }

    private void bodyGetCurrentOrders(ArrayList<ShortOrderInfo> sh) {
        if (sh == null || sh.size() == 0) {
            Fragment f = getSupportFragmentManager()
                    .findFragmentById(R.id.work_container);
            Injector.getClientData().initMemoryListNamesCost();

            if (f instanceof FSearchCar
                    || f instanceof FStatusInfo
                    || f instanceof FFinishInfo)
                showV3FRoute();
            return;
        }

        shortOrderInfos = sh;

        if (sizeCurrentOrders != sh.size())
            updateLeftMenu();

        sizeCurrentOrders = sh.size();
        App.bus.post(new BusPreloadMenu());

        if (sh.size() == 1) {
            startPingShortOrderInfos(sh);
            showInfoThisCurrentOrder(sh);
            updateLeftMenu();

            return;

        }

        if (sh.size() > 1) {
            startPingShortOrderInfos(sh);
            showV2FShortOrdersPrivate();
            updateLeftMenu();
            return;
        }

        closePing();
    }

    private void updateLeftMenu() {
        App.bus.post(new BusUpdMenu());
    }

    public void getPingOrders() {
        bodyGetPingOrders(Injector.getClientData().getPingArrayShortOrderInfo());

/*        Injector.getRC().getOrderCurrent("getPingOrders", new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {
                    showErrorIGetApiResponse(apiResponse);
                    return;
                }

                if (("getPingOrders" + Constants.PATH_ORDERS_CURRENT).equals(apiResponse.path)) {
                    bodyGetPingOrders(apiResponse.shortOrderInfos);
                }
            }
        });*/
    }

    @Subscribe
    public void onBusArayPingOrderInfo(BusArayPingOrderInfo e) {
        getPingOrders();
    }

    private void bodyGetPingOrders(ArrayList<ShortOrderInfo> sh) {
        if (isFinishing()) return;
        if (sh != null) {
            sizeCurrentOrders = sh.size();
            shortOrderInfos = sh;
            updateLeftMenu();
        }

        if (blokPing) return;
        if (sh == null) {
            closePing();
            return;
        }
        if (sh.size() > 0) {
            if (!isShowNeedsProlongation(sh)) pingShortOrderInfos(sh);
            else closePing();
        } else {
            closePing();
        }
    }

    public void showFWebViewAllClass(String redirectUrl, int vendor, String vendorPayment) {
        showTopFragment(FWebViewAllClass.newInstance(redirectUrl, vendor, vendorPayment));
    }

    public void showFEditCost(UIOrder uiOrder) {
        showTopFragment(FEditCost.newInstance(uiOrder));
    }

    private void initPageEdit(ArrayList<ObjecEditList> objecEditLists
            , final long idRoute
            , final double[] latLonOneList
            , final boolean isContractor) {

        contMenuEdit.removeAllViews();
        for (ObjecEditList objecEditList : objecEditLists) {
            View view = LayoutInflater.from(contMenuEdit.getContext())
                    .inflate(R.layout.item_menu_edit
                            , contMenuEdit
                            , false);

            TextView value = (TextView) view;
            value.setText(objecEditList.nameMenuEdit);
            final int posObjecEditList = objecEditList.posObjecEditList;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (posObjecEditList) {
                        case 0:
                            showEV2FAddressFeed(idRoute, latLonOneList);
                            break;
                        case 1:
                            showEFSetCashOrder(idRoute, latLonOneList, isContractor);
                            break;
                        case 2:
                            showEV2FOptions(idRoute, latLonOneList);
                            break;
                        case 3:
                            showEV2FCommentOrder(idRoute, latLonOneList);
                            break;
                        case 4:
                            showEFEditCost(idRoute, latLonOneList);
                            break;
                        case 5:
                            initCallDisp();
                            break;
                        case 6:
                            if (getOrderInfo() == null || getOrderInfo().assignee == null
                                    || getOrderInfo().assignee.call == null)
                                break;

                            sendCallDriver(getOrderInfo().assignee.call);
                            break;
                        case 7:
                            sendCallServers(getOrderInfo().idRoute);
                            break;
                    }
                    onHideMenuEdit();
                }
            });
            contMenuEdit.addView(view);
        }
    }

    private void showEFEditCost(long idRoute, double[] latLonOneList) {
        showTopFragment(EFEditCost.newInstance(latLonOneList, idRoute));
    }

    private void showEV2FCommentOrder(long idRoute, double[] latLonOneList) {
        showTopFragment(EV2FCommentOrder.newInstance(latLonOneList, idRoute));
    }

    private void showEV2FOptions(long idRoute, double[] latLonOneList) {
        showTopFragment(EV2FOptions.newInstance(latLonOneList, idRoute));
    }

    private void showEV2FAddressFeed(long idRoute, double[] latLonOneList) {
        showTopFragment(EV2FAddressFeed.newInstance(latLonOneList, idRoute));
    }

    private void showEFSetCashOrder(long idRoute, double[] latLonOneList) {
        showTopFragment(EFSetCashOrder.newInstance(latLonOneList, idRoute));
    }

    private void showEFSetCashOrder(long idRoute, double[] latLonOneList, boolean isContractor) {
        showTopFragment(EFSetCashOrder.newInstance(latLonOneList, idRoute, isContractor));
    }

    public void onShowMenuEdit(ArrayList<ObjecEditList> objecEditLists
            , long idRoute
            , double[] latLonOneList
            , boolean isContractor) {
        initPageEdit(objecEditLists, idRoute, latLonOneList, isContractor);
        orderEdit.setVisibility(View.VISIBLE);
    }

    private void sendCallServers(long idRoute) {
        getAWork().showWorkProgress();
        Injector.getRC().sendCallServers(idRoute, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (isFinishing()) return;
                if (apiResponse == null || apiResponse.error != null) {
                    showErrorIGetApiResponse(apiResponse);
                    return;
                }
                hideWorkProgress();
                showMessage(getString(R.string.info_reguest_driver));
            }
        });
    }

    private void sendCallDriver(final AssigneeCall call) {
        try {
            String url = "tel:" + call.numbers.get(0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showMessage(getString(R.string.error_phone_number_info));
        }
    }

    @OnClick(R.id.close_menu_edit)
    public void onHideMenuEdit() {
        orderEdit.setVisibility(View.GONE);
    }

    public void showV3RestoryFRoute() {
        if (objectRestoryV3Route == null) {
            // isPage0 == true старт с простой страницы иначе сложная
            boolean skipPage0tScreen = !Injector
                    .getWorkSettings()
                    .isSkipFirstScreen();

            showV3FRoute(skipPage0tScreen, false);
        } else {
            showV3FRoute(objectRestoryV3Route);
        }
    }

    private void isLandscapeTest() {
        if (Injector.deviceScreen != null) return;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();

        display.getMetrics(metricsB);

        float heightPixels = metricsB.heightPixels;
        float dpHeight = pxToDp(heightPixels);

        float widthPixels = metricsB.widthPixels;
        float dpWidth = pxToDp(widthPixels);

        Injector.deviceScreen = new DeviceScreen(heightPixels, widthPixels, dpWidth, dpHeight);
    }

    public void showFReferral(String lptype) {
        showTopFragment(V2FReferral.newInstance(lptype));
    }

    public void showFSetNameAndMail(int typeName) {
        showTopFragment(FSetNameAndMail.newInstance(typeName));
    }

    public void showUpdateApp() {
        if (isFinishing()) return;
        updDialog.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.upd_dialog, FUpdateApp.instance())
                .commitAllowingStateLoss();
    }

    public void showGRScaner() {
        if (isFinishing()) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
            return;
        }
        bodyShowGRScanner();
    }

    private void bodyShowGRScanner() {
        updDialog.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.upd_dialog, ScannerQRFragment.instance())
                .commitAllowingStateLoss();
    }

    public void hideUpdateApp() {
        if (isFinishing()) return;
        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.upd_dialog);
        if (f == null) return;
        updDialog.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .remove(f)
                .commitAllowingStateLoss();
    }

    public void showFCommetAndOptions() {
        showTopFragment(FCommetAndOptions.newInstance());
    }

    public void showV2FSetCashOrder(int vendorFroute) {
        showTopFragment(V2FSetCashOrder.newInstance(vendorFroute));
    }

    public void showFragmentNewReg() {
        showTopFragment(FragmentNewReg.newInstance());
    }

    public void showSupportUrl() {
        showTopFragment(FShowSupportUrl.newInstance());
    }

    public void initQR(String refCode) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.work_container);
        if (fragment instanceof FragmentNewReg) {
            FragmentNewReg fragmentNewReg = (FragmentNewReg) fragment;
            fragmentNewReg.initRegToScannerQR(refCode);
        }
    }

    public void showCreateQRCodeFragment(String referralCode) {
        if (isFinishing()) return;
        updDialog.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.upd_dialog, CreateQRCodeFragment.instance(referralCode))
                .commitAllowingStateLoss();
    }
}
