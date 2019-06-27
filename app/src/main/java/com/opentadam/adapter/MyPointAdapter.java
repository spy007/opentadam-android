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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.R;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.utils.TintIcons;

import java.util.List;

public class MyPointAdapter extends RecyclerView.Adapter<MyPointAdapter.MyViewHolder> {

    private List<ClientAddress> clientAddressesPrivatePoint;
    private IMypoint iMypoint;


    public MyPointAdapter(List<ClientAddress> clientAddressesPrivatePoint, IMypoint iM) {
        this.clientAddressesPrivatePoint = clientAddressesPrivatePoint;
        iMypoint = iM;
    }

    @NonNull
    @Override
    public MyPointAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.point_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holder.listItemView = view.findViewById(R.id.list_item_view);
        holder.name = view.findViewById(R.id.name_item_view);
        holder.icon = view.findViewById(R.id.icon);
        TintIcons.tintImageViewBrend(holder.icon);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPointAdapter.MyViewHolder holder, int position) {
        final ClientAddress clientAddress = clientAddressesPrivatePoint.get(position);

        String notNullNamePrivate =
                clientAddress.getNotNullNamePrivate(R.string.no_name_my_point);

        holder.name.setText(notNullNamePrivate);

        holder.listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMypoint.onClickPos(clientAddress);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientAddressesPrivatePoint.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout listItemView;
        TextView name;
        ImageView icon;

        MyViewHolder(View itemView) {
            super(itemView);
        }


    }
}
