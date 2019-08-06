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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.ClientData;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.utils.TintIcons;

import java.util.ArrayList;
import java.util.Formatter;

import butterknife.ButterKnife;

import static com.opentadam.Injector.getClientData;


public abstract class BaseFr extends MvpAppCompatFragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null)
            return null;

        View v = inflater.inflate(getArguments().getInt("viewId"), container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    public void alertToast(String message){
        Toast toast = Toast.makeText(getAWork(), message, Toast.LENGTH_LONG);
        toast .setGravity(Gravity.CENTER, 0, -getPX(50));

        toast.show();
    }

    protected void removeGOLListener(ViewTreeObserver vto, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            vto.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        } else {
            vto.removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    public String getStringIntejerFormat(int val) {
        Formatter f = new Formatter();
        Formatter format = f.format("%,d", val);

        return format.toString();
    }

    public String getStringIntejerFormat(long val) {
        Formatter f = new Formatter();
        Formatter format = f.format("%,d", val);

        return format.toString();
    }

    protected String getStringIntejerFormat(String val) {
        long valLong = Long.parseLong(val);
        Formatter f = new Formatter();
        Formatter format = f.format("%,d", valLong);

        return format.toString();
    }

    protected int getTipeCard(String nameCard) {
        if (nameCard.contains("МИР")) {
            return R.id.fsco_mir;
        }
        if (nameCard.startsWith("VISA")) {
            return R.id.fsco_viza;
        }

        if (nameCard.startsWith("MasterCard")) {
            return R.id.fsco_master;
        }
        if (nameCard.startsWith("Maestro")) {
            return R.id.fsco_maestro;
        }
        return -1;
    }


    protected String getValueSubMenuPay() {

        final ClientData clientData = getClientData();
        final ArrayList<ClientData.CashList> cashLists = clientData
                .getCashList();
        if (cashLists == null || cashLists.size() == 0) {

            return getString(R.string.method_cash);
        }

        if(clientData.cashPos > cashLists.size() -1){
            return getString(R.string.method_cash);
        }

        ClientData.CashList cashList = cashLists.get(clientData.cashPos);
        switch (cashList.nameIdResours) {
            case ClientData.ID_CASH:
                return getString(R.string.method_cash);

            case ClientData.ID_CONTRACTOR:

            case ClientData.ID_CREDIT_CARD:

                return cashList.name;

            default:
                return getString(R.string.method_cash);
        }

    }

    public LatLng getLatLng() {
        CreateRequest createRequest = getClientData().getCreateRequest();
        ArrayList<GpsPosition> routeLocation = createRequest.getRouteLocation();
        if (routeLocation.size() != 0)

            return routeLocation.get(0).getLatLng();
        else
            return App.app.mMyGoogleLocation.getLatLngMyLoc();
    }

    public LatLng getLatLngWorkOrder() {

        ArrayList<GpsPosition> routeLoc = getClientData()
                .getCreateRequest().getRouteLocation();
        if (routeLoc.size() != 0) {
            GpsPosition rl = routeLoc.get(0);
            return new LatLng(rl.lat, rl.lon);
        }

        if (getClientData().getMarkerLocation() != null) {
            return getClientData().getMarkerLocation();
        }

        Location location = App.app.mMyGoogleLocation.showCurrentLocation();
        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());

        }


        String latString = Injector.getSettingsStore().readString("LAT_MEMORY", null);
        String lonString = Injector.getSettingsStore().readString("LON_MEMORY", null);

        if (latString != null) {
            return new LatLng(Double.parseDouble(latString), Double.parseDouble(lonString));
        }

        ClientData clientData = getClientData();
        if (clientData.service != null) {

            return clientData.getLatLngCenterAdress();

        }
        Double[] defaultLatLon = App.app.hashBC.defaultLatLon;
        if (defaultLatLon != null) {
            return new LatLng(defaultLatLon[0], defaultLatLon[1]);
        }
        return new LatLng(0, 0);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        App.bus.register(this);
        Fragment f = this;

        if (!(f instanceof V3FRoute)) {
            getAWork().showWorkCont();
        }
        getAWork().showPushFSCar();
    }

    @Override
    public void onDestroyView() {
        if (getAWork() != null) {
            getAWork().hideProgressDevault();

        }

        App.bus.unregister(this);
        super.onDestroyView();
        ButterKnife.reset(this);
        App.app.mustDie(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.app.mustDie(this);
    }

    public Spanned fromHtml(String html) {

        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public BaseFr withArgument(String name, double value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putDouble(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withArgument(String name, double[] value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putDoubleArray(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withArgument(String name, long value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putLong(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withArgument(String name, int value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putInt(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withArgument(String name, String value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putString(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withArgument(String name, Boolean value) {
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();
        args.putBoolean(name, value);
        setArguments(args);
        return this;
    }

    public BaseFr withViewId(int value) {
        return withArgument("viewId", value);
    }

    protected void alert(String text) {
        // Toast.makeText(Injector.getAppContext(), text, Toast.LENGTH_LONG).show();
        getAWork().showMessage(text);
    }

    protected void hideKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getAWork()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm == null)
            return;
        if (editText.getWindowToken() == null)
            return;

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public Drawable initDrawable(int id) {

        return getActivity() == null ? null : TintIcons
                .getVectorDrawableRes(id);
    }


    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = Injector.getAppContext().getResources().getDisplayMetrics();
        return (int) ((px / displayMetrics.density) + 0.5);
    }


    public void initArrCurrency(TextView[] arrCurrents) {
        for (TextView textView : arrCurrents) {
            textView.setText(Injector.getWorkSettings().getCurrency().sign);
        }

    }


    public int getPX(int DP) {

        final float scale = Injector.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (DP * scale + 0.5f);
    }

    protected int initColor(int id) {
        return getActivity() == null ? -1 : ContextCompat.getColor(getActivity(), id);

    }

    public AWork getAWork() {
        return (AWork) getActivity();
    }

    public void responseDialog() {

    }


    @Override
    public void onResume() {
        super.onResume();
        getAWork().showPushFSCar();

    }


    public boolean onBackPressed() {
        return false;
    }
}

