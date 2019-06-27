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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;

public class UtilitesMenu {

    public UtilitesMenu() {

    }

    public static String getVersionName(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            0);

            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }

    }

    public static String getStringVersionName(Context context) {
        String versionName = getVersionName(context);
        String[] versionNameArr = versionName.split("\\.");


        StringBuilder apVer = new StringBuilder(versionNameArr[0]);
        int length = versionNameArr.length;
        for (int i = 1; i < length; i++) {
            apVer.append(i == length - 1 ? " (build " + versionNameArr[i] + ")" : "." + versionNameArr[i]);

        }

        return String.format("%s %s", context.getResources()
                .getString(R.string.get_version), apVer);
    }

    public static Bitmap loadBitmap(Context context) {
        return BitmapFactory.decodeFile(context.getFilesDir().toString() + "MyAvatar");
    }

    public static String getRegKey() {
        return Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null);
    }
}
