/*
 *
 *  * Copyright (C) 2019 TadamGroup, LLC.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 * /
 */

package com.opentadam.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.network.rest.GpsPosition;

import java.util.ArrayList;
import java.util.List;

import static com.opentadam.Injector.getClientData;

public class CreateRequest {

    public String comment;
    private ArrayList<ClientAddress> route;


    public ArrayList<ClientAddress> getRoute() {
        if (route == null)
            route = new ArrayList<>();
        return route;
    }

    public void setRoute(ArrayList<ClientAddress> route) {
        this.route = route;
    }


    public int getSizeRoute() {

        return getRoute().size();
    }

    public void addAdressToRoute(Address address) {
        ClientAddress clientAddress = new ClientAddress(address);
        getRoute().add(clientAddress);
    }

    public void insertAdressToRoute(Address address, int pos) {
        getRoute().add(pos + 1, new ClientAddress(address));

    }

    public void editAdressToRoute(Address address, int pos) {
        CreateRequest createRequest = getClientData().getCreateRequest();
        ArrayList<ClientAddress> route = createRequest.getRoute();
        if (pos > route.size() - 1) {

            return;
        }
        if (route.size() == 0) {
            addAdressToRoute(address);
            return;
        }

        if (route.get(route.size() - 1) == null) {
            route.remove(route.size() - 1);
            addAdressToRoute(address);
            return;
        }

        ClientAddress clientAddress = getRoute().get(pos);
        clientAddress.address = address;

        initPos0Live(address, pos, clientAddress);


    }

    private void initPos0Live(Address address, int pos, ClientAddress clientAddress) {

        List<ClientAddress> listPrivatePoint = Injector.getSettingsStore().getListPrivatePoint();
        final boolean b = listPrivatePoint == null || listPrivatePoint.size() == 0;
        if (pos != 0 || b || address.idParent == null)
            return;

        for (ClientAddress clientAddress1 : listPrivatePoint) {
            final Long id = clientAddress1.id;
            if (id.equals(address.idParent)) {
                clientAddress.entrance = clientAddress1.entrance;
                clientAddress.flat = clientAddress1.flat;
                clientAddress.comment = clientAddress1.comment;
                clientAddress.namePrivate = clientAddress1.namePrivate;
                clientAddress.id = clientAddress1.id;
            }

            //  final boolean b1 = address.equalsObj(address1);

        }
    }


    public String getTextStartAdress(int i) {
        if (getRoute() == null)
            return null;
        if (getRoute().size() - 1 < i)
            return null;
        ClientAddress clientAddress = getRoute().get(i);
        if (clientAddress == null)
            return null;
        return getStringNameAdress(clientAddress);
    }

    @NonNull
    private String getStringNameAdress(ClientAddress clientAddress) {

        if (clientAddress == null)
            return Injector.getClientData().getResources().getString(R.string.point_to_maps);

        Address address = clientAddress.address;
        if (address == null)
            return Injector.getClientData().getResources().getString(R.string.point_to_maps);
        return address.getStringNameAdress();
    }


    public String getTextStartAdressDopInfo() {
        if (getRoute().size() == 0) {

            return null;
        }

        ClientAddress clientAddress = getRoute().get(0);


        String flat = clientAddress.flat;// Квартира
        String entrance = clientAddress.entrance;// Подъезд
        String comment = clientAddress.comment;// комментарий к адресу

        String text = "";
/*        String text1 = getAliasAdress(0);
        if (text1 != null)
            text += text1 + ", ";*/

        if (entrance != null && !"".equals(entrance))
            text += Injector.getClientData().getResources().getString(R.string.porch_min) + entrance + ", ";

        if (flat != null && !"".equals(flat))
            text += Injector.getClientData().getResources().getString(R.string.flat_min) + flat + ", ";

        if (comment != null && !"".equals(comment))
            text += " " + comment + ", ";

        return "".equals(text) ? null : String.copyValueOf(text.toCharArray(), 0, text.length() - 2);
    }

    @Nullable
    public String getAliasAdress(int i) {
        ArrayList<ClientAddress> route = getRoute();
        if (route == null || route.size() == 0)
            return null;

        ClientAddress clientAddress = route.get(i);
        if (clientAddress == null)
            return null;

        Address address = clientAddress.address;
        if (address == null)
            return null;

        String alias = address.getNameAliasSearsh();
        if (alias != null) {
            String nameSity = address.getNameSitySearsh();
            if (nameSity != null) {
                String nameAdress = address.getNameAdressSearsh();
                if (nameAdress == null)
                    nameAdress = Injector.getClientData().getResources().getString(R.string.point_to_maps);
                String text = nameSity + ", " + nameAdress;

                if (nameSity.equals(nameAdress))
                    text = nameSity;

                return text;
            }

        }
        return null;
    }

    public String getStartNameParent(int i) {
        ClientAddress clientAddress = getRoute().get(i);
        if (clientAddress == null)
            return "";
        Address address = clientAddress.address;
        //    com.hivetaxi.model.CreateRequest.getStartNameParent (CreateRequest.java:191)
        if (address == null)
            return "";

        return address.getNameSitySearsh();
    }

    public ArrayList<GpsPosition> getRouteLocation() {
        ArrayList<GpsPosition> gpsPositions = new ArrayList<>();
        for (ClientAddress clientAddress : getRoute()) {

            if (clientAddress == null)
                continue;
            if (clientAddress.address == null)
                continue;
            if (clientAddress.address.position == null)
                continue;

            gpsPositions.add(clientAddress.address.position);
        }

        return gpsPositions;
    }

}
