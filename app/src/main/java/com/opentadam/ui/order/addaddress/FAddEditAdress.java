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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.opentadam.Constants;
import com.opentadam.R;
import com.opentadam.ui.BaseFr;

import butterknife.InjectView;
import butterknife.OnClick;


public class FAddEditAdress extends BaseFr implements IHAddr {

    @InjectView(R.id.aa_list)
    RecyclerView recyclerView;

    private RecyclerViewTouchActionGuardManager guardManager;


    private RecyclerView.LayoutManager layoutManager;
    private AddrListAdapter adapter;
    private RecyclerView.Adapter wrappedAdapter;
    private RecyclerViewDragDropManager dragDropManager;


    public FAddEditAdress() {
        // Required empty public constructor
    }


    public static Fragment newInstance() {
        return new FAddEditAdress().withViewId(R.layout.f_add_edit_adress);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new LinearLayoutManager(getAWork(), LinearLayoutManager.VERTICAL, false);

        dragDropManager = new RecyclerViewDragDropManager();

        guardManager = new RecyclerViewTouchActionGuardManager();
        guardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        guardManager.setEnabled(true);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(animator);

        adapter = new AddrListAdapter(new AddrListProvider(this));


        wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);  // requires *wrapped* adapter

        guardManager.attachRecyclerView(recyclerView);
        dragDropManager.attachRecyclerView(recyclerView);
    }


    @Override
    public void onPause() {
        dragDropManager.cancelDrag();
        super.onPause();
    }

    @OnClick(R.id.bask)
    public void onBask() {
        getAWork().showV3FRoute();
    }


    @Override
    public boolean onBackPressed() {
        getAWork().showV3FRoute();
        return true;
    }

    @Override
    public void onDestroyView() {

        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (guardManager != null) {
            guardManager.release();
            guardManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        adapter = null;
        layoutManager = null;
        super.onDestroyView();
    }

    @Override
    public void removeItem(int position) {
        getAWork().showV3FRoute();
    }

    @Override
    public void editItem(int position) {
        getAWork().showV2FFullTextSearch(position + 1, Constants.EDIT_ADRESS);
    }
}