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

package com.opentadam.start;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusArayPingOrderInfo;
import com.opentadam.bus.BusForegroundServiceOrderInfo;
import com.opentadam.bus.BusStateFinishOrderId;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.ui.AWork;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class ForegroundService extends Service {
    private boolean isRegBus;
    private LongSparseArray<ShortOrderInfo> shortOrderInfoLongSparseArray = new LongSparseArray<>();
    private CountDownTimer mStatusDownTimer;
    private boolean isActivateGetPingOrders;
    private int sizePingArray;
    private ArrayList<OrderInfo> pingArrayOrderInfo = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Injector.getClientData().pingArrayOrderInfo = new ArrayList<>();
        activateService();

        return START_STICKY;
    }

    @Subscribe
    public void onBusForegroundServiceOrderInfo(BusForegroundServiceOrderInfo e) {
        ShortOrderInfo shortOrderInfo = e.orderInfo.mapperShortOrderInfo();
        if (shortOrderInfo.state > Constants.STATE_WORK) {
            shortOrderInfoLongSparseArray.remove(shortOrderInfo.id);
            if(shortOrderInfo.state == Constants.STATE_DONE) App.bus.post(new BusStateFinishOrderId(e.orderInfo));
        } else shortOrderInfoLongSparseArray.put(shortOrderInfo.id, shortOrderInfo);

        getBodyPing();
    }

    private void activateService() {
        if (!isRegBus) {
            isRegBus = true;
            App.bus.register(this);
            initNotificationIntent();
            getPingOrders();
        }
    }

    private void getPingOrders() {

        if (isActivateGetPingOrders || Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null) == null)
            return;
        Injector.getRC().getOrderCurrent("ForegroundService", new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isRegBus) return;
                if (apiResponse == null || apiResponse.error != null) return;


                if (("ForegroundService" + Constants.PATH_ORDERS_CURRENT).equals(apiResponse.path)) {
                    isActivateGetPingOrders = true;
                    ArrayList<ShortOrderInfo> shortOrderInfos = apiResponse.shortOrderInfos;
                    bodyGetPingOrders(shortOrderInfos);
                }
            }
        });
    }

    private void bodyGetPingOrders(ArrayList<ShortOrderInfo> shortOrderInfos) {
        if (shortOrderInfos.isEmpty()) shortOrderInfoLongSparseArray.clear();
        else {
            for (ShortOrderInfo shortOrderInfo : shortOrderInfos)
                shortOrderInfoLongSparseArray.put(shortOrderInfo.id, shortOrderInfo);
            getBodyPing();
        }

        pingShortOrderInfos();
    }

    private void pingShortOrderInfos() {
        if (mStatusDownTimer != null) mStatusDownTimer.cancel();
        mStatusDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!isRegBus) cancel();
            }

            public void onFinish() {
                getBodyPing();
            }
        };

        mStatusDownTimer.start();
    }

    private void getBodyPing() {
        Log.e("jkkjkjkj", "sise = " + shortOrderInfoLongSparseArray.size());
        if (!isActivateGetPingOrders) {
            getPingOrders();
        } else  {
            updateShortOrderInfoLongSparseArray();
        }
        pingShortOrderInfos();
    }

    private void updateShortOrderInfoLongSparseArray() {
        sizePingArray = shortOrderInfoLongSparseArray.size();
        pingArrayOrderInfo = new ArrayList<>();
        int size = shortOrderInfoLongSparseArray.size();
        if (size == 0){
            initResultPingArray();
        }
        for (int i = 0; i < size; i++) ping(shortOrderInfoLongSparseArray.keyAt(i));
    }

    private void ping(long idRoute) {

        RESTConnect restConnect = Injector.getRC();
        String name = getClass().getName();
        restConnect.setTAG(name);

        restConnect.getOrderInfo(idRoute, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                sizePingArray--;
                if (apiResponse == null || apiResponse.error != null) {
                  if(sizePingArray == 0)  initResultPingArray();
                    return;
                }
                OrderInfo orderInfo = apiResponse.orderInfo;
                if (orderInfo == null) {
                    if(sizePingArray == 0)  initResultPingArray();
                    return;
                }
                pingArrayOrderInfo.add(orderInfo);
                if(sizePingArray == 0)  initResultPingArray();
            }
        });
    }

    private void initResultPingArray() {
        Injector.getClientData().pingArrayOrderInfo = pingArrayOrderInfo;
        App.bus.post(new BusArayPingOrderInfo(pingArrayOrderInfo));
    }

    private void initNotificationIntent() {

        Intent notificationIntent = new Intent(this, AWork.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent
                .getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String CHANNEL_ONE_ID = App.app.hashBC.APPLICATION_ID;
        String CHANNEL_ONE_NAME = getString(R.string.app_name);
        NotificationChannel notificationChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }


        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setSmallIcon(whiteIcon ? R.mipmap.ic_notification : R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        isRegBus = false;
        if (mStatusDownTimer != null) mStatusDownTimer.cancel();
        App.bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }
}
