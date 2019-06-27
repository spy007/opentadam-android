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
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.badoo.mobile.util.WeakHandler;
import com.google.gson.Gson;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusForegroundServiceOrderInfo;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.model.LocalizationPushHashMap;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.ui.AWork;
import com.opentadam.utils.SoundPoolClient;
import com.opentadam.utils.TipeSound;

import java.util.Map;

public class FonService extends Service {
    public static final String DATA_PUSH = "dataPush";
    private final WeakHandler mHandler = new WeakHandler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotificationIntent(intent);
        return START_NOT_STICKY;
    }


    private void initNotificationIntent(Intent intent) {
        if (intent != null) {
            Map<String, String> data = new Gson().fromJson(intent.getStringExtra(DATA_PUSH), LocalizationPushHashMap.class).data;
            if (data == null) return;
            initNotificationIntent(data);
        }
    }

    private void initNotificationIntent(final Map<String, String> messageBody) {
        String eventType = messageBody.get("event_type");
        String orderId = messageBody.get("orderId");
        if (orderId == null || eventType == null) {
            stopSelf();
            return;
        }

        String typePush = eventType.replace("action.name.", "");
        String message = App.app.hashBC.localizationPushHashMap.get(typePush);
        ping(Long.valueOf(orderId), message, typePush);
    }

    private void ping(final Long orderId, final String message, final String eventType) {

        RESTConnect restConnect = Injector.getRC();
        String name = getClass().getName();
        restConnect.setTAG(name);

        restConnect.getOrderInfo(orderId, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (apiResponse == null || apiResponse.error != null) return;
                OrderInfo orderInfo = apiResponse.orderInfo;
                if (orderInfo == null) {
                    stopSelf();
                    return;
                }
                orderInfo.idRoute = orderId;
                App.bus.post(new BusForegroundServiceOrderInfo(orderInfo));

                if(message == null) {
                    stopSelf();
                    return;
                }
                String nameFailSound = TipeSound.PUSH.nameFailSound;
                String messageRepl = message.replace("\\n", ". ");
                switch (eventType) {
                    case "assign_driver":
                        messageRepl = messageRepl.replace("{car_color}", orderInfo.assignee.car.color)
                                .replace("{car_brand}", orderInfo.assignee.car.brand)
                                .replace("{car_model}", orderInfo.assignee.car.model)
                                .replace("{car_reg_number}", orderInfo.assignee.car.regNum);
                        nameFailSound =  TipeSound.STATE_SET.nameFailSound;
                        break;
                    case "driver_arrived":
                        messageRepl = messageRepl.replace("{car_color}", orderInfo.assignee.car.color)
                                .replace("{car_brand}", orderInfo.assignee.car.brand)
                                .replace("{car_model}", orderInfo.assignee.car.model)
                                .replace("{car_reg_number}", orderInfo.assignee.car.regNum);
                        nameFailSound =  TipeSound.STATE_WAIT.nameFailSound;
                        break;
                }

                showNotify(orderId, messageRepl, nameFailSound);
            }
        });
    }

    private void showNotify(Long orderId, String message, final String nameFailSound) {
        if (App.app.isEnabledPoush || !App.uiActivated) {

            final SoundPoolClient soundPoolClient = Injector.getSoundPoolClient();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundPoolClient.playSoundWav(nameFailSound);
                }
            }, 1000);
        }

        String CHANNEL_ONE_ID = App.app.hashBC.APPLICATION_ID;
        String CHANNEL_ONE_NAME = getString(R.string.app_name);
        Intent intent = new Intent(this, AWork.class);

        intent.putExtra("orderId", orderId);
        intent.setAction(Constants.PUSH);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent contentIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setSmallIcon(whiteIcon ? R.mipmap.ic_notification : R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setContentText(message) // Текст уведомления
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        Notification notification = builder
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
        stopSelf();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}