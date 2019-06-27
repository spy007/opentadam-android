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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.opentadam.R;


public class AddrListAdapter extends RecyclerView.Adapter<HAddr> implements DraggableItemAdapter<HAddr>, IHAddr {
    private AbstractDataProvider provider;

    public AddrListAdapter(AbstractDataProvider provider) {
        this.provider = provider;

        setHasStableIds(true);
    }


    @Override
    public long getItemId(int position) {
        return provider.getItem(position).id;
    }

    @Override
    public int getItemViewType(int position) {
        return provider.getItem(position).viewType;
    }

    @NonNull
    @Override
    public HAddr onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.i_aa_list, parent, false);

        return new HAddr(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HAddr holder, final int position) {
        final Data item = provider.getItem(position);

        holder.update(item, position);

    }


    @Override
    public int getItemCount() {
        return provider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {


        if (fromPosition == toPosition) {
            return;
        }

        provider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;//TODO check
    }

    @Override
    public boolean onCheckCanStartDrag(HAddr holder, int position, int x, int y) {

        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(HAddr holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public void removeItem(int position) {
        provider.removeItem(position);
        notifyItemRemoved(position);
    }

    @Override
    public void editItem(int position) {
        provider.editItem(position);
    }
}