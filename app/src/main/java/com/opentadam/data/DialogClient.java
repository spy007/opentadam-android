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
package com.opentadam.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.DefLocalePhone;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.SettingsStore;
import com.opentadam.bus.HiveBus;
import com.opentadam.ui.AWork;
import com.opentadam.ui.BaseFr;
import com.opentadam.ui.V2FMypoint;
import com.opentadam.ui.frends.FSettings;
import com.opentadam.ui.frends.VFProfil;
import com.opentadam.ui_payemnts_metods.V2FSetCashOrder;
import com.opentadam.utils.TintIcons;

public class DialogClient {


    private static boolean enabledChanged = true;


    public static void alertInfo(String message, Activity act) {
        if (act.isFinishing())
            return;
        final Dialog dialog = new Dialog(act);
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_view, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;

        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        startTimeout(dialog, act);
        dialogMess.setText(message);
        dialog.show();
    }

    private static void startTimeout(final Dialog dialog, final Activity act) {

        CountDownTimer mStability = new CountDownTimer(3000, 10) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (act.isFinishing())
                    return;
                dialog.dismiss();
            }

        };
        mStability.start();
    }

    public static void showV3TwoButtonDialog(String title
            , String message
            , String no
            , String yes
            , final IV3ResponseDialog iResponseDialog) {

        final AWork aWork = iResponseDialog.getAWork();
        if (aWork.isFinishing())
            return;

        final Dialog dialog = new Dialog(aWork);
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view =
                inflater.inflate(R.layout.dialog_two_button, null);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        if (title != null)
            dialogTitle.setText(title);
        else
            dialogTitle.setVisibility(View.GONE);
        dialogMess.setText(message);

        TextView yesDialog = view.findViewById(R.id.dialog_yes);
        yesDialog.setText(yes);
        TextView noDialog = view.findViewById(R.id.dialog_no);

        noDialog.setText(no);

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iResponseDialog.responseAction();
                dialog.dismiss();

            }
        });

        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showV2TwoButtonDialog(String title
            , String message
            , String no
            , String yes
            , final IResponseDialog iResponseDialog) {

        final AWork aWork = iResponseDialog.getAWork();
        if (aWork.isFinishing())
            return;

        final Dialog dialog = new Dialog(aWork);
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view =
                inflater.inflate(R.layout.dialog_two_button, null);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        if (title != null)
            dialogTitle.setText(title);
        else
            dialogTitle.setVisibility(View.GONE);
        dialogMess.setText(message);

        TextView yesDialog = view.findViewById(R.id.dialog_yes);
        yesDialog.setText(yes);
        TextView noDialog = view.findViewById(R.id.dialog_no);

        noDialog.setText(no);

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aWork.getBaseFr() instanceof VFProfil) {
                    aWork.showV2FRegistration(true, false);

                } else if (aWork.getBaseFr() instanceof V2FSetCashOrder) {
                    V2FSetCashOrder v2FSetCashOrder = (V2FSetCashOrder) aWork.getBaseFr();
                    v2FSetCashOrder.deleteCard();
                } else if (aWork.getBaseFr() instanceof V2FMypoint) {
                    iResponseDialog.responseAction(null);
                } else if (aWork.getBaseFr() instanceof FSettings) {
                    iResponseDialog.responseAction("clearSettings");
                }

                dialog.dismiss();

            }
        });

        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public static void showOneButtonDialog(String title, String message, final BaseFr baseFr) {
        if (baseFr == null)
            return;
        if (baseFr.getAWork() == null)
            return;
        if (baseFr.getAWork().isFinishing())
            return;


        final Dialog dialog = new Dialog(baseFr.getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_one_button, null);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        if (title != null)
            dialogTitle.setText(title);
        else
            dialogTitle.setVisibility(View.GONE);
        dialogMess.setText(message);

        TextView yesDialog = view.findViewById(R.id.dialog_yes);
        yesDialog.setText(R.string.ok);

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFr.responseDialog();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static Dialog showDefaultDialog(String title, String message
            , AWork act, final String className) {
        if (act.isFinishing())
            return null;

        final Dialog dialog = new Dialog(act);
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_view, null);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return null;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        TextView dialogMess = view.findViewById(R.id.dialog_mess);
        if (title != null)
            dialogTitle.setText(title);
        else
            dialogTitle.setVisibility(View.GONE);
        dialogMess.setText(message);

        TextView yesDialog = view.findViewById(R.id.dialog_yes);


        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.bus.register(this);
                HiveBus.postBusDialogPressed(className);
                App.bus.unregister(this);
                dialog.dismiss();
                dialog.cancel();
            }
        });
        TextView noDialog = view.findViewById(R.id.dialo_no);
        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialog.show();
        return dialog;
    }

    public static void showDialogAddPrivateAdress(final IResponseDialog iResponseDialog) {
        if (iResponseDialog.getAWork().isFinishing())
            return;

        final Dialog dialog = new Dialog(iResponseDialog.getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_add_private_adress_view, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);


        TextView yesDialog = view.findViewById(R.id.dialog_yes);
        ImageView clear = view.findViewById(R.id.clear_name);
        final EditText editNamePointPrivate = view.findViewById(R.id.edit_name_point_private);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNamePointPrivate.setText("");
            }
        });


        editNamePointPrivate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // текст только что изменили
                if (enabledChanged) {
                    enabledChanged = false;
                    String s1 = s.toString();
                    int length = s1.length();
                    if (length == 1) {
                        s1 = s1.toUpperCase();
                        editNamePointPrivate.setText(s1);
                        editNamePointPrivate.setSelection(1);
                    }
                    enabledChanged = true;
                    if (s.length() != 0)
                        editNamePointPrivate.setError(null);
                }


            }
        });

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNamePointPrivate.getText().toString().trim();
                if ("".equals(name)) {
                    editNamePointPrivate.setError(iResponseDialog.getContectApp()
                            .getString(R.string.error_edit_empty));
                    return;
                }

                iResponseDialog.responseAction(name);

                alertInfo(iResponseDialog.getContectApp().getString(R.string.sucsess_add_adress),
                        iResponseDialog.getAWork());

                dialog.dismiss();

                dialog.cancel();
            }
        });
        TextView noDialog = view.findViewById(R.id.dialog_no);
        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                dialog.cancel();
            }
        });
        dialog.show();

    }

    public static void showProfilPhotoDialog(final IResponseDialog iResponseDialog, boolean b) {
        if (iResponseDialog.getAWork().isFinishing())
            return;

        final Dialog dialog = new Dialog(iResponseDialog.getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_profil_photo_dialog_view, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);
        TextView title = view.findViewById(R.id.dialog_title);

        title.setText(b ? iResponseDialog.getAWork().getString(R.string.profile_dialog_title)
                : iResponseDialog.getAWork().getString(R.string.reload_foto));

        TextView yesDialog = view.findViewById(R.id.ad_camera);


        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iResponseDialog.addPfotoCamera(true);

                dialog.dismiss();

                dialog.cancel();
            }
        });
        TextView noDialog = view.findViewById(R.id.ad_gal);
        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iResponseDialog.addPfotoCamera(false);

                dialog.dismiss();

                dialog.cancel();
            }
        });
        dialog.show();

    }

    public static void showProfilMapsDialog(final IResponseDialogMap iResponseDialogMap, int defMars) {
        if (iResponseDialogMap.getAWork().isFinishing())
            return;

        final Dialog dialog = new Dialog(iResponseDialogMap.getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_maps_dialog_view, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        dialog.setContentView(view);


        LinearLayout contSetGoogle = view.findViewById(R.id.cont_set_google);
        contSetGoogle.setVisibility(isVisibleCont(SettingsStore.MAPS_GOOGLE));
        ImageView checkGoogle = view.findViewById(R.id.check_google);
        TintIcons.tintImageViewBrend(checkGoogle);
        checkGoogle.setVisibility(defMars == SettingsStore.MAPS_GOOGLE ? View.VISIBLE : View.INVISIBLE);

        ImageView checkOsm = view.findViewById(R.id.check_osm);
        TintIcons.tintImageViewBrend(checkOsm);
        checkOsm.setVisibility(defMars == SettingsStore.MAPS_OSM ? View.VISIBLE : View.INVISIBLE);

        ImageView checkYandex = view.findViewById(R.id.check_yandex);
        TintIcons.tintImageViewBrend(checkYandex);
        checkYandex.setVisibility(defMars == SettingsStore.MAPS_YANDEX ? View.VISIBLE : View.INVISIBLE);
        contSetGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Injector.getSettingsStore()
                        .writeInt(SettingsStore.KEY_DEFAULT_MAPS,
                                SettingsStore.MAPS_GOOGLE);
                iResponseDialogMap.setMaps(SettingsStore.MAPS_GOOGLE);

                dialog.dismiss();

                dialog.cancel();
            }
        });
        LinearLayout contSetOsm = view.findViewById(R.id.cont_set_osm);
        contSetOsm.setVisibility(isVisibleCont(SettingsStore.MAPS_OSM));
        contSetOsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Injector.getSettingsStore()
                        .writeInt(SettingsStore.KEY_DEFAULT_MAPS,
                                SettingsStore.MAPS_OSM);
                iResponseDialogMap.setMaps(SettingsStore.MAPS_OSM);

                dialog.dismiss();

                dialog.cancel();
            }
        });
        LinearLayout contSetYandex = view.findViewById(R.id.cont_set_yandex);
        contSetYandex.setVisibility(isVisibleCont(SettingsStore.MAPS_YANDEX));
        contSetYandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Injector.getSettingsStore()
                        .writeInt(SettingsStore.KEY_DEFAULT_MAPS,
                                SettingsStore.MAPS_YANDEX);
                iResponseDialogMap.setMaps(SettingsStore.MAPS_YANDEX);

                dialog.dismiss();

                dialog.cancel();
            }
        });
        dialog.show();

    }

    private static int isVisibleCont(int mapsGoogle) {

        String[] mapTitleSourceArray = Injector
                .getWorkSettings()
                .getMapTitleSourceArray();

        for (String name : mapTitleSourceArray) {
            switch (name) {
                case "google":
                    if (mapsGoogle == SettingsStore.MAPS_GOOGLE)
                        return View.VISIBLE;
                    break;

                case "osm":
                    if (mapsGoogle == SettingsStore.MAPS_OSM)
                        return View.VISIBLE;
                    break;
                case "yandex":
                    if (mapsGoogle == SettingsStore.MAPS_YANDEX)
                        return View.VISIBLE;
                    break;
            }
        }
        return View.GONE;
    }

    public static void showProfilLocaleDialog(final IResponseDialogMap iResponseDialogMap
            , String localeDefault) {
        if (iResponseDialogMap.getAWork().isFinishing())
            return;

        final Dialog dialog = new Dialog(iResponseDialogMap.getAWork());
        LayoutInflater inflater = dialog.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_locale_dialog_view, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
        ArrayMap<String, String> countryLocale = Countries.getCountryLocale();

        ArrayMap<String, String> listCL = new ArrayMap<>();

        DefLocalePhone defLocalePhone = App.app.getDefLocalePhone();
        listCL.put("sys", defLocalePhone.defaultSystemLocal);

        for (int i = 0; i < countryLocale.size(); i++) {
            String key = countryLocale.keyAt(i);
            Log.i("TAG", key);
            String value = countryLocale.valueAt(i);
            Log.i("TAG", value);
            listCL.put(key, value);
        }

        String defKey = listCL.containsKey(localeDefault) ? localeDefault : "en";

        LinearLayout cont = view.findViewById(R.id.cont_locale);
        cont.removeAllViews();

        boolean isAddSys = false;
        for (ArrayMap.Entry entry : listCL.entrySet()) {

            if (listCL.containsKey("sys") && !isAddSys) {
                isAddSys = true;
                bodyDC(iResponseDialogMap, dialog, defKey, cont
                        , defLocalePhone.defaultSystemLocal, "sys");
            }

            String value = (String) entry.getValue();
            final String key = (String) entry.getKey();
            if ("sys".equals(key)) {
                continue;
            }


            bodyDC(iResponseDialogMap, dialog, defKey, cont, value, key);
        }


        dialog.setContentView(view);
        dialog.show();

    }

    private static void bodyDC(final IResponseDialogMap iResponseDialogMap
            , final Dialog dialog
            , String defKey
            , LinearLayout cont
            , String value
            , final String key) {
        View viewItem = LayoutInflater.from(cont.getContext())
                .inflate(R.layout.row_dialog_locale_dialog_view, cont, false);

        ImageView checkLocale = viewItem.findViewById(R.id.check_locale);
        TextView valueLocale = viewItem.findViewById(R.id.value_locale);
        LinearLayout contSetItem = viewItem.findViewById(R.id.cont_set_item);
        checkLocale.setVisibility(defKey.equals(key) ? View.VISIBLE : View.INVISIBLE);
        valueLocale.setText(value);
        contSetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Injector.getSettingsStore().writeString("getLocale", key);

                dialog.dismiss();

                dialog.cancel();

                iResponseDialogMap.getAWork().updLocale();

            }
        });

        cont.addView(viewItem);
    }
}
