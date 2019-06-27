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

package com.opentadam.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusRestartAWork;
import com.opentadam.bus.BusShowMapsFFullTextSearch;
import com.opentadam.network.rest.Address;
import com.opentadam.network.rest.AddressTypes;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.utils.TintIcons;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdressAdapter extends RecyclerView.Adapter<AdressAdapter.MyViewHolder> {

    private List<Address> addresses;

    private IAdressAdapter iAdressAdapter;


    public AdressAdapter(List<Address> addresses, IAdressAdapter iAdressAdapter) {
        this.addresses = addresses;
        this.iAdressAdapter = iAdressAdapter;

    }

    @NonNull
    @Override
    public AdressAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.v2_adress_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdressAdapter.MyViewHolder holder, final int position) {
        if (position > getItemCount() - 1) {
            // что-то пошло не так - в списке адаптера позиций меньше чем в заданном массиве
            // или нулл от бека в позиции адреса или пересоздана активность в норм режиме так быть не должно
            // RecyclerView.java line 5817 перезагрузим и пусть заново вводит иначе никак
            App.bus.post(new BusRestartAWork());
            return;
        }

        ImageView iconAdress = holder.iconAdress;
        final Address address = addresses.get(position);

        holder.emptyView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        TextView street = holder.textAdress;
        TextView sity = holder.sity;

        if (position == 0 && iAdressAdapter.getIndex() != 0) {

            sity.setText("");
            int colorItem = Injector.getAppContext().getResources().getColor(R.color.colorPrimary);
            iconAdress
                    .setImageDrawable(TintIcons
                            .getVectorDrawableCompatIcon(R.drawable.ic_set_loc));
            TintIcons.tintImageViewBrend(iconAdress);
            TintIcons.setStuleReg(address.name, street, "fonts/Roboto/Roboto-Bold.ttf", 21);
            street.setTextColor(colorItem);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    App.bus.post(new BusShowMapsFFullTextSearch());
                }
            });
            return;
        }

        AddressTypes types = address.types;
        int pos = 0;
        if (types != null && types.aliasType != null) {
            pos = types.aliasType;

        }


        iconAdress
                .setImageDrawable(TintIcons.getVectorDrawableCompat(pos));
        TintIcons.tintImageViewBrend(iconAdress);
        String nameAdress = address.getNameAdressSearsh(); //objmAdressList.nameAdress;
        if (nameAdress == null)
            nameAdress = Injector.getClientData().getResources().getString(R.string.point_to_maps);
        String nameSity = address.getNameSitySearsh();  //objmAdressList.sity == null ? "" : objmAdressList.sity;
        String alias = address.getNameAliasSearsh(); //objmAdressList.alias;

        if (alias == null) {
            if (nameAdress.equals(nameSity))
                nameSity = address.getParentNameSitySearsh();
            street.setText(nameAdress);
            if (nameSity == null)
                sity.setVisibility(View.GONE);

            sity.setText(nameSity);
        } else {
            street.setText(alias);
            if (nameSity == null)
                sity.setVisibility(View.GONE);

            else {
                String text = nameSity + ", " + nameAdress;

                if (nameSity.equals(nameAdress))
                    text = nameSity;

                sity.setText(text);
            }

        }

        address.valueStreet = street.getText().toString();

        String pattern = Injector.getClientData().pattern;
        if (pattern != null) {

            pattern = pattern.toLowerCase();
            String valueStreet = address.valueStreet.toLowerCase();
            String temp = valueStreet.replace(pattern, "&" + pattern + "@");
            int start = temp.indexOf("&");

            int stop = temp.indexOf("@");
            if (start != -1 && stop != -1) {


                String value = address.valueStreet;
                String value0 = value.substring(0, start);
                String value1 = value.substring(start, stop - 1);
                String value2 = value.substring(stop - 1, address.valueStreet.length());

                street.setText(Html.fromHtml(value0 + "<b>" + value1 + "</b>" + value2));
            }
        }

        if (address.idParent != null) {
            // UI Мои адреса
            final ClientAddress privateClientAddress = Injector.getSettingsStore().getPrivateOrderAddress(address.idParent);

            iconAdress
                    .setImageDrawable(TintIcons
                            .getVectorDrawableRes(R.drawable.ic_my_point_white_24dp));
            TintIcons.tintImageViewBrend(iconAdress);

            final boolean b = privateClientAddress.getName() == null;
            final String stringNameAdress = b ?
                    privateClientAddress.getStringNameAdress(Injector.getAppContext()
                            .getString(R.string.no_name_my_point)) : privateClientAddress.getName();


            street.setText(stringNameAdress);
            holder.sity
                    .setText(b ? privateClientAddress.getTextStartAdressDopInfo(
                            Injector.getClientData().getResources().getString(R.string.porch_min)
                            , Injector.getClientData().getResources().getString(R.string.flat_min)
                            , Injector.getClientData().getResources().getString(R.string.point_to_maps)) : "");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iAdressAdapter.itemAdress(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.icon_adress)
        ImageView iconAdress;
        @InjectView(R.id.sity)
        TextView sity;
        @InjectView(R.id.text_adress)
        TextView textAdress;
        @InjectView(R.id.empty_view)
        View emptyView;

        View itemView;


        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.itemView = itemView;
        }
    }
}
