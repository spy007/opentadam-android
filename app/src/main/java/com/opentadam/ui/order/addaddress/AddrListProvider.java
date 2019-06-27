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

package com.opentadam.ui.order.addaddress;

import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.ClientAddress;

import java.util.ArrayList;
import java.util.List;

import static com.opentadam.Injector.getClientData;

public class AddrListProvider extends AbstractDataProvider {
    private List<Data> addrs;
    private IHAddr ihAddr;

    public AddrListProvider(IHAddr ihAddr) {
        this.ihAddr = ihAddr;
        CreateRequest createRequest = getClientData().getCreateRequest();
        int count = createRequest.getSizeRoute();
        addrs = new ArrayList<>();
        for (int i = 1; i < count; i++) {

            addrs.add(new Data(addrs.size(), createRequest.getTextStartAdress(i)));
        }

    }


    @Override
    public int getCount() {
        return addrs.size();
    }

    @Override
    public Data getItem(int index) {
        return addrs.get(index);
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final Data item = addrs.remove(fromPosition);
        CreateRequest createRequest = getClientData().getCreateRequest();
        ClientAddress remove = createRequest.getRoute().remove(fromPosition + 1);
        createRequest.getRoute().add(toPosition + 1, remove);
        addrs.add(toPosition, item);
    }

    @Override
    public void removeItem(int position) {
        if (addrs.size() - 1 < position)
            return;
        addrs.remove(position);
        CreateRequest createRequest = getClientData().getCreateRequest();
        createRequest.getRoute().remove(position + 1);
        if (addrs.size() == 0)
            ihAddr.removeItem(position);
    }

    @Override
    public int findDragableLine() {
        int ind = 0;
        for (Data d : addrs) {
            if (d.id == 1000)
                return ind;
            ind++;
        }
        return addrs.size() - 1;
    }

    @Override
    public void editItem(int position) {
        ihAddr.editItem(position);
    }

}