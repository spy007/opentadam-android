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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.adapter.IMypoint;
import com.opentadam.adapter.MyPointAdapter;
import com.opentadam.data.DialogClient;
import com.opentadam.data.IResponseDialog;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;

public class V2FMypoint extends BaseFr implements IMypoint, IResponseDialog {
    @InjectView(R.id.sl_panel_my_point)
    SlidingUpPanelLayout slp;
    @InjectView(R.id.my_point_list)
    RecyclerView pointList;
    @InjectView(R.id.name_select)
    TextView nameSelect;


    private ClientAddress clientAddress;
    //Comparator для сортировки списка или массива объектов по имени
    private Comparator<ClientAddress> NameComparatorTrakUser = new Comparator<ClientAddress>() {
        @Override
        public int compare(ClientAddress o1, ClientAddress o2) {
            String name2 = o1.getName();
            if (name2 == null)
                name2 = o1.getStringNameAdress(getString(R.string.no_name_my_point));


            String name = name2.toLowerCase().trim();


            String name3 = o2.getName();
            if (name3 == null)
                name3 = o2.getStringNameAdress(getString(R.string.no_name_my_point));

            String name1 = name3.toLowerCase().trim();
            return name.compareTo(name1);
        }
    };


    public V2FMypoint() {
        // Required empty public constructor
    }

    public static Fragment newInstance(long id) {

        return new V2FMypoint().withViewId(R.layout.f_mypoint).withArgument("id", id);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<ClientAddress> listPrivatePoint = Injector.getSettingsStore().getListPrivatePoint();
        if (listPrivatePoint.size() > 1)
            Collections.sort(listPrivatePoint, NameComparatorTrakUser);

        MyPointAdapter pointAdapter = new MyPointAdapter(listPrivatePoint, this);
        pointList.setAdapter(pointAdapter);
        pointList.setLayoutManager(new LinearLayoutManager(getActivity()));
        slp.setTouchEnabled(false);
        if (getArguments() != null) {
            long idMyAdress = getArguments().getLong("id");
            if (idMyAdress != 0) {
                this.clientAddress = Injector.getSettingsStore().getPrivateOrderAddress(idMyAdress);
                initNameSelect(clientAddress);
            }
        }


    }

    @Override
    public void onClickPos(ClientAddress clientAddress) {

        if (slp.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            return;
        this.clientAddress = clientAddress;
        initNameSelect(clientAddress);
    }

    private void initNameSelect(ClientAddress clientAddress) {
        String notNullNamePrivate =
                clientAddress.getNotNullNamePrivate(R.string.no_name_my_point);
        nameSelect.setText(notNullNamePrivate);

        slp.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @OnClick(R.id.button_hide_top_menu)
    public void onButtonHideTopMenu() {
        slp.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @OnClick(R.id.v2_bask)
    public void onShowLMenu() {
        getAWork().showMenu();
    }


    @Override
    public boolean onBackPressed() {
        getAWork().showV3RestoryFRoute();
        return true;
    }

    @OnClick(R.id.button_add_my_point)
    public void onButtonAddPoint() {
        getAWork().showV2FPrivateFullTextSearch(0);
    }

    @OnClick({R.id.action0, R.id.action1, R.id.action2, R.id.action3})
    public void onActionTopMenu(View v) {
        CreateRequest createRequest = getClientData().getCreateRequest();
        int sizeRoute = createRequest.getSizeRoute();
        Address address = clientAddress.address;
        List<ClientAddress> route = createRequest.getRoute();
        switch (v.getId()) {
            case R.id.action0:

                if (sizeRoute == 0)
                    route.add(this.clientAddress);

                else
                    route.set(0, this.clientAddress);

                LatLng latLng = address.position.getLatLng();
                getClientData().setMarkerLocation(latLng);
                getAWork().showV3FRoute();

                break;
            case R.id.action1:

                if (route.size() != 0) {
                    ClientAddress clientAddress = route.get(0);
                    route.clear();
                    route.add(clientAddress);
                }
                route.add(this.clientAddress);


                getAWork().showV3FRoute();

                break;


            case R.id.action2:
                getAWork().showV2FeditPrivateAdress(this.clientAddress.id, false);
                break;
            case R.id.action3:

                DialogClient.showV2TwoButtonDialog(
                        getString(R.string.fmp_title_delete_adress)
                        , getString(R.string.fmp_message_delete_adress)
                        , getString(R.string.fmp_button_no_delete_adress)
                        , getString(R.string.fmp_button_yes_delete_adress)
                        , this);

                break;
        }

        slp.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }

    @Override
    public Context getContectApp() {
        return null;
    }

    @Override
    public void responseAction(String name) {
        Injector.getSettingsStore().removePrivateOrderAddress(this.clientAddress);
        DialogClient.alertInfo(getString(R.string.my_point_info_delete_poin), getAWork());
        getAWork().showV2FMypoint(0);
    }

    @Override
    public void addPfotoCamera(boolean b) {

    }
}
