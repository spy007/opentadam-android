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

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusShowMapsFFullTextSearch;
import com.opentadam.data.ClientData;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.utils.Utilites;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;


public class V2FPrivateFullTextSearch extends V2FFullTextSearch {
    @InjectView(R.id.set_adr_to_route)
    TextView buttonSet;
    private long id;

    public static Fragment newInstance(long id) {
        return new V2FPrivateFullTextSearch().withArgument("id", id);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong("id");
        }

        buttonSet.setText(R.string.done_button);
        title.setText(R.string.my_adress_edit_title);
        meaSearchAddress.setHint(R.string.create_adress);

    }

    @Override
    public void onIcBask() {
        onBackPressed();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBus();
    }

    @Override
    public boolean onBackPressed() {
        if (id != 0)
            getAWork().showV2FeditPrivateAdress(id, false);
        else
            getAWork().showV2FMypoint(0);
        return true;
    }


    @Override
    public void itemAdress(Address address) {


        String name = address.name;

        if (name != null)
            onClickItemEdit(address);
    }

    @Override
    public void onClickItemEdit(Address address) {


        boolean b =
                address.getNameHouseSearsh() != null;

        if (b) {
            isClickItemEdit = true;
            addText = true;
            meaSearchAddress.setText(String.format("%s, %s, ", address.getNameSitySearsh(), address.getNameHouseSearsh()));
            meaSearchAddress.setSelection(meaSearchAddress.getText().length());
        } else {

            boolean isAdd = false;
            if (id != 0) {
                ClientAddress clientAddress = Injector.getSettingsStore().getPrivateOrderAddress(id);
                clientAddress.address = address;
                Injector.getSettingsStore().replOrderAddress(id, clientAddress);
            } else {
                isAdd = true;
                ClientAddress clientAddress = new ClientAddress(address);
                id = System.currentTimeMillis();
                clientAddress.id = id;
                clientAddress.namePrivate = null; // getString(R.string.def_name_add_private_adress);
                Injector.getSettingsStore().addOrderAddressToListPrivatePoint(clientAddress);
            }
            getAWork().showV2FeditPrivateAdress(id, isAdd);
        }
    }

    ////////

    @Subscribe
    public void onBusShowMapsFFullTextSearch(BusShowMapsFFullTextSearch event) {
        if (!isVisible()) return;
        Utilites.hideSoftKeyboard(getAWork(), contMapSearch);
        Bitmap iconDrawableToMaps = Utilites.createIconDrawableToMaps(36, 36, R.drawable.car_location);
        pin.setImageBitmap(iconDrawableToMaps);
        markerDefCont.setVisibility(View.VISIBLE);
        contMarkerCenter.setVisibility(View.GONE);
        contMapSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public LatLng getLatLngStart(Location location) {
        if (id != 0) {
            ClientAddress privateClientAddress = Injector.getSettingsStore().getPrivateOrderAddress(id);
            GpsPosition position = privateClientAddress.address.position;
            return position.getLatLng();
        } else {
            LatLng latLng = null;
            Location location1 = App.app.mMyGoogleLocation.showCurrentLocation();
            if (location != null) {

                latLng = new LatLng(location1.getLatitude(), location1.getLongitude());

            }

            ClientData clientData = getClientData();
            if (clientData.service != null && latLng == null) {

                latLng = clientData.getLatLngCenterAdress();

            }
            return latLng;
        }
    }

    @OnClick(R.id.set_adr_to_route)
    public void onSetAdrMaps() {
        boolean isAdd = false;
        if (id != 0) {

            ClientAddress clientAddress = Injector.getSettingsStore().getPrivateOrderAddress(id);
            clientAddress.address = tempAdressMaps;
            Injector.getSettingsStore().replOrderAddress(id, clientAddress);
        } else {
            isAdd = true;
            ClientAddress clientAddress = new ClientAddress(tempAdressMaps);
            id = System.currentTimeMillis();
            clientAddress.id = id;
            clientAddress.namePrivate = null; //getString(R.string.def_name_add_private_adress);
            Injector.getSettingsStore().addOrderAddressToListPrivatePoint(clientAddress);
        }

        getAWork().showV2FeditPrivateAdress(id, isAdd);
    }
}
