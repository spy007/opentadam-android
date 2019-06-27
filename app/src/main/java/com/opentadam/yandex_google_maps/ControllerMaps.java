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

package com.opentadam.yandex_google_maps;
/*
 Вызываем этот метод когда необходимо разрешить что - либо в геолокации,
 что именно сам разберется по степени важности

 controllerNetwork.showDialogSetEnamledLocation(mLocationProviders.isDisablegGlobalLocation());
// на страницу разрешения геолокации
controllerNetwork.openApplicationSettingsLocation();
*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.opentadam.Constants;
import com.opentadam.ui.AWork;

import java.util.ArrayList;

class ControllerMaps {
    private final FBaseMaps fBaseMaps;

    public ControllerNetwork getControllerNetwork() {

        return controllerNetwork;
    }

    private final ControllerNetwork controllerNetwork;
    private ModelLocationProviders modelLocationProviders;

    private ControllerMaps(FBaseMaps val) {
        fBaseMaps = val;
        controllerNetwork = ControllerNetwork.invoke(val);
    }

    public static ControllerMaps invoke(FBaseMaps fBaseMaps) {
        return new ControllerMaps(fBaseMaps);
    }


    void requestMultiplePermissions() {
        if(!fBaseMaps.isEnabledShowDialogRemiss())
            return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            ActivityCompat.requestPermissions(getAWork(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    Constants.PERMISSION_REQUEST_LOCATION);
        }
    }

    private AWork getAWork() {
        return fBaseMaps.getAWork();
    }

    synchronized ModelLocationProviders getMLocationProviders() {

        AWork aWork = getAWork();
        @SuppressWarnings("deprecation")
        String provider = Settings.Secure.getString(aWork.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String[] s = provider.split(",");
        int length = s.length;
        if (modelLocationProviders == null)
            modelLocationProviders = ModelLocationProviders.invoke();

        modelLocationProviders.setListProvider(s)
                    .setCountProvider(length);
        modelLocationProviders.setIsEnabledGPS(false);
        modelLocationProviders.setIsEnabledNetwork(false);
        modelLocationProviders.setUnknownMethod(false);
        for (String num : s) {
            switch (num) {
                case "gps":
                    modelLocationProviders.setIsEnabledGPS(true);
                    break;
                case "network":
                    modelLocationProviders.setIsEnabledNetwork(true);
                    break;
                case "":
                    break;

                default:
                    ArrayList<String> nameSUnknownMethod = new ArrayList<>();
                    nameSUnknownMethod.add(num);
                    modelLocationProviders.setListNameUnknownMethod(nameSUnknownMethod);
                    modelLocationProviders.setUnknownMethod(true);
                    break;
            }
        }

        boolean b = length == 0 || (length == 1
                && !modelLocationProviders.isEnabledGPS()
                && !modelLocationProviders.isEnabledNetwork());

            // геолокация запрещена пользователем isGeolocationDisabled

            modelLocationProviders.setIsGeolocationDisabled(b);
        boolean b1 = length == 1 && modelLocationProviders.isEnabledGPS();

            // только жпс onlyGPS
            modelLocationProviders.setIsOnlyGPS(b1);
        boolean b2 = length == 1 && modelLocationProviders.isEnabledNetwork();

            // только на базе сети onlyNetwork
            modelLocationProviders.setIsOnlyNetwork(b2);



        return modelLocationProviders;
    }

    // Основной метод управления UI сработает на старте и
    // по результату включил или нет в предложенном окне настроек
    // вызов принудительно для обновления страниц onRequestLoc(null);

    synchronized void onRequestLoc() {


        modelLocationProviders = getMLocationProviders();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            modelLocationProviders.setDisablegGlobalLocation(ActivityCompat.checkSelfPermission(getAWork()
                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getAWork(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED);

            if (fBaseMaps instanceof FOsmMaps) {
                // запрет доступа к карте проверка OSM

                boolean isDisabledStorage = ActivityCompat.checkSelfPermission(getAWork()
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
                modelLocationProviders.setIsDisabledStorage(isDisabledStorage);

            }

        }

        // отключил геолокацию в телефоне или запретил приложению доступ к геолокации
        fBaseMaps.geolocationDisabledAll(modelLocationProviders.isGeolocationDisabled() || modelLocationProviders.isDisablegGlobalLocation());

        // запретил приложению доступ к геолокации
        fBaseMaps.disablegGlobalLocation(modelLocationProviders.isDisablegGlobalLocation());

        // отключил геолокацию в телефоне
        fBaseMaps.geolocationDisabled(!modelLocationProviders.isDisablegGlobalLocation() && modelLocationProviders.isGeolocationDisabled());

        // запретил приложению доступ к карте памяти
        fBaseMaps.disabledStorage(modelLocationProviders.isDisabledStorage());


        // только жпс
        fBaseMaps.onlyGPS(modelLocationProviders.isOnlyGPS());

        // только на базе сети

        fBaseMaps.onlyNetwork(modelLocationProviders.isOnlyNetwork());

        // неизвестный метод
        fBaseMaps.unknownMethod(modelLocationProviders.isUnknownMethod());

        // прочие варианты, жпс+сеть как минимум есть
        fBaseMaps.onlyOtherMetodsLocation(!modelLocationProviders.isOnlyNetwork() && !modelLocationProviders.isOnlyGPS());


    }

    synchronized void changeEnabledStorag(boolean b) {
        ModelLocationProviders modelLocationProviders = getMLocationProviders();
        modelLocationProviders.setIsDisabledStorage(!b);
    }

    public synchronized ModelLocationProviders getModelLocationProviders() {
        return modelLocationProviders;
    }
}
