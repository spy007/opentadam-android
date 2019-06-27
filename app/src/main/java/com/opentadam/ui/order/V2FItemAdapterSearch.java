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

package com.opentadam.ui.order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.adapter.AdressAdapter;
import com.opentadam.adapter.IAdressAdapter;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.ui.BaseFr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.InjectView;


public class V2FItemAdapterSearch extends BaseFr implements IAdressAdapter {

    @InjectView(R.id.adress_list_view)
    RecyclerView adressListView;

    private String pos;
    private boolean isStartAdress;

    public V2FItemAdapterSearch() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String pos, boolean isStartAdress) {

        return new V2FItemAdapterSearch()
                .withViewId(R.layout.v2_fitem_adapter_search)
                .withArgument("isStartAdress", isStartAdress)
                .withArgument("pos", pos);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            pos = getArguments().getString("pos");
            isStartAdress = getArguments().getBoolean("isStartAdress", true);
        }

        if (pos != null) {

            switch (pos) {
                case "getMyAdressList":
                    getMyAdressList();
                    break;
                case "getHistoryOrders":
                    getHistoryOrders();
                    break;
                case "getAdressGeocodeServers":
                    getAdressGeocodeServers(Injector.getClientData().getMarkerLocation());
                    break;
            }
        }
    }

    private void getMyAdressList() {
        List<ClientAddress> listPrivatePoint = Injector.getSettingsStore().getListPrivatePoint();
        if (listPrivatePoint == null || listPrivatePoint.size() == 0)
            return;

        //Comparator для сортировки списка или массива объектов по имени
        Comparator<ClientAddress> NameComparatorTrakUser = new Comparator<ClientAddress>() {
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

        if (listPrivatePoint.size() > 1)
            Collections.sort(listPrivatePoint, NameComparatorTrakUser);

        List<Address> addresses = new ArrayList<>();
        for (ClientAddress clientAddress : listPrivatePoint) {

            if (clientAddress == null || clientAddress.address == null)
                continue;

            Address address = clientAddress.address;
            address.idParent = clientAddress.id;


            addresses.add(address);
        }

        if (addresses.size() != 0) {
            initUI(addresses);
        }
    }

    private void getHistoryOrders() {
        RESTConnect restConnect = Injector.getRC();

        restConnect.getHistoryOrders(new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }
                ArrayList<ShortOrderInfo> shortOrderInfos = apiResponse.shortOrderInfos;
                if(shortOrderInfos == null && !isStartAdress){
                    shortOrderInfos = new ArrayList<>();
                }

                List<Address> addresses = new ArrayList<>();

                    for (ShortOrderInfo shortOrderInfo : shortOrderInfos) {
                        ArrayList<Address> route = shortOrderInfo.route;
                        for (Address address : route) {
                            if (isEnabledAddress(address, addresses))
                                addresses.add(address);
                        }

                    }

                    initUI(addresses);

            }
        });
    }

    private void getAdressGeocodeServers(LatLng latLng) {
        RESTConnect restConnect = Injector.getRC();
/*        if (restConnect == null) {
            getAWork().showErrorNetDialog();
            return;
        }*/
        restConnect.getAdressGeocodeServers(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                   // getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                List<Address> addresses = apiResponse.addresses;
                if (addresses != null && addresses.size() != 0) {
                    initUI(addresses);
                }
            }
        }, null);
    }


    private boolean isEnabledAddress(Address address, List<Address> addresses) {
        for (Address address1 : addresses) {
            String mapsValueStringAdress = address.getMapsValueStringAdress();
            if (mapsValueStringAdress.equals(address1.getMapsValueStringAdress()))
                return false;
        }
        return true;
    }

    private void initUI(List<Address> addresses) {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        if (getIndex() != 0) {
            Address mapsAdress = new Address(getString(R.string.fitem_menu_maps));
            addresses.add(0, mapsAdress);
        }

        AdressAdapter adressAdapter = new AdressAdapter(addresses, this);
        adressListView.setAdapter(adressAdapter);
        adressListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public int getIndex() {
        V2FFullTextSearch parentFragment = (V2FFullTextSearch) getParentFragment();
        if (parentFragment != null) {
            return parentFragment.getIndex();
        }
        return 0;
    }

    @Override
    public void setMarginTopList(int dp) {
        V2FFullTextSearch parentFragment = (V2FFullTextSearch) getParentFragment();
        if (parentFragment != null) {
            parentFragment.setMarginTopList(dp);
        }

    }

    @Override
    public void itemAdress(Address address) {
        V2FFullTextSearch parentFragment = (V2FFullTextSearch) getParentFragment();
        if (parentFragment != null) {
            parentFragment.onClickItemEdit(address);
        }
    }
}
