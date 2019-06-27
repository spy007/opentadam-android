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
package com.opentadam.bus;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.opentadam.network.rest.Address;
import com.opentadam.ui.order.UpdUIAdressMapsYaGoogle;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;
import java.util.List;

public class HiveBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    private static  HiveBus bus;
    private ArrayList<String> arrayList;

    public HiveBus() {
        super(ThreadEnforcer.ANY);
        bus = this;
    }

    @Override
    public void register(Object object) {
        if (object == null)
            return;

        if (arrayList == null)
            arrayList = new ArrayList<>();

        Log.d("error_bus", "register:" + object.getClass().getName());
        if (arrayList.contains(object.getClass().getName())) {

            Log.e("error_bus", "содержит:" + object.getClass().getName());
        }


        super.register(object);
    }

    @Override
    public void unregister(Object object) {

        try {
            if (arrayList != null && arrayList.contains(object.getClass().getName())) {
                arrayList.remove(object.getClass().getName());
            }

            if (arrayList != null) {
                for (String s : arrayList) {
                    Log.d("error_bus", "список: " + s);
                }
                Log.d("error_bus", "количество: " + arrayList.size());
            }

            super.unregister(object);
        } catch (NullPointerException | IllegalArgumentException e) {
            Log.e("error_bus", " mes=" + e.getMessage());
        }

    }

    public static void postBusGPSLocation(Location location) {
        if (location == null)
            return;
        bus.post(new BusGPSLocation(location));
    }


    public static void postBusDialogPressed(String className) {
        bus.post(new BusDialogPressed(className));
    }


    public static void postBusUpdArrTariff() {
        bus.post(new BusUpdArrTariff());
    }

    @Override
    public void post(final Object event) {
        if (event == null)
            return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    HiveBus.super.post(event);
                }
            });
        }
    }

    public static void postBusEnabledUserLatLon() {
        bus.post(new BusEnabledUserLatLon());
    }


    public static void postBusDisabledTarif(String kind, String message) {
        bus.post(new BusDisabledTarif(kind, message));
    }

    public static void postBusUpdGeozoneTarif() {
        bus.post(new BusUpdGeozoneTarif());
    }

    public static void postBusUpdUIAdressMaps(Address geo) {
        bus.post(new BusUpdUIAdressMaps(geo));
    }

    public static void postBusAdressListGeocode(List<Address> addresses) {
        bus.post(new BusAdressListGeocode(addresses));
    }

    public static void postBusUpdUIAdressMapsYaGoogle(Address adress) {
        bus.post(new UpdUIAdressMapsYaGoogle(adress));
    }

    public static void postBusEnabledTarif() {
        bus.post(new BusEnabledTarif());
    }

    public static void postBusWindowFocusChanged() {
        bus.post(new BusWindowFocusChanged());
    }
}
