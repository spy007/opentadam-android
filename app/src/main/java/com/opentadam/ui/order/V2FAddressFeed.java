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


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.data.DialogClient;
import com.opentadam.data.IResponseDialog;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.rest.ClientAddress;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.Utilites;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class V2FAddressFeed extends BaseFr implements IResponseDialog {
    private static final java.lang.String INDEX_ADRESS = "index_adress";
    private static final java.lang.String IS_START = "is_start";
    @InjectView(R.id.ic_maps)
    public ImageView icMaps;
    @InjectView(R.id.ic_clear_edit)
    public ImageView icSearshAdress;
    @InjectView(R.id.button_add_private_adress)
    public View buttonAddPrivateAdress;


    @InjectView(R.id.adress_value)
    public TextView adressValue;

    @InjectView(R.id.private_redactor_name_text)
    public TextView privateRedactorNameText;
    @InjectView(R.id.private_redactor_name)
    public EditText privateRedactorName;
    @InjectView(R.id.cont_private)
    public LinearLayout contPrivate;


    @InjectView(R.id.edit_flat)
    public EditText editFlat;
    @InjectView(R.id.edit_porcht)
    public EditText editPorcht;
    @InjectView(R.id.redactor_dop_info)
    public EditText redactorDopInfo;
    @InjectView(R.id.text_flat)
    public TextView textFlat;
    @InjectView(R.id.text_porch)
    public TextView textPorch;
    @InjectView(R.id.text_dop_info)
    public TextView textDopInfo;
    @InjectView(R.id.parent_view)
    public LinearLayout parentView;
    protected int bazeDiff = -1;
    protected boolean isShowKeyBoord = false;
    @InjectView(R.id.hide_cursor_text)
    TextView hideCursorText;
    private int indexAdress;
    private Animation showPanelAnimation;
    private Animation hidePanelAnimation;
    private boolean isStart;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public static Fragment newInstance(int i, boolean isStart) {
        return new V2FAddressFeed()
                .withViewId(R.layout.f_address_feed)
                .withArgument(IS_START, isStart)
                .withArgument(INDEX_ADRESS, i);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_address_feed, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unitUIFA();

    }

    protected void unitUIFA() {
        if (getArguments() != null) {
            indexAdress = getArguments().getInt(INDEX_ADRESS);
            isStart = getArguments().getBoolean(IS_START);
        }

        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        if (createRequest == null)
            return;

        setDefEdit(editFlat);
        setDefEdit(editPorcht);
        setDefEdit(redactorDopInfo);
        preLoadAnimation();
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (parentView == null)
                    return;
                Rect r = new Rect();
                //создаём прямоугольник r с координатами видимого пространства

                parentView.getWindowVisibleDisplayFrame(r);
                //Вычисляем разницу между высотой нашего View и высотой видимого пространства
                final int height = parentView.getRootView().getHeight();

                int heightDiff = height - (r.bottom - r.top);


                if (bazeDiff == -1)
                    bazeDiff = heightDiff + 10;

                //    isShowKeyBoord = heightDiff > bazeDiff * 4;
                boolean b = heightDiff > bazeDiff * 4;
                if (!b) {
                    // закрыта клавиатура
                    if (isShowKeyBoord) {
                        isShowKeyBoord = false;
                        hideCursor();
                        initTextSizeEdit();
                    }
                } else {
                    // открыта
                    isShowKeyBoord = true;
                    initTextSizeEdit();
                }

            }

        };

        parentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        ArrayList<ClientAddress> route = createRequest.getRoute();
        if (route == null || route.size() == 0)
            return;

        ClientAddress clientAddress = route.get(0);
        initUi(clientAddress);
    }

    protected void initTextSizeEdit() {
        if (privateRedactorName.getText().length() == 0) {
            privateRedactorName.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                    , privateRedactorName.isFocused() ? 21 : 16);
        }
        if (editFlat.getText().length() == 0) {
            editFlat.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                    , editFlat.isFocused() ? 21 : 16);
        }

        if (editPorcht.getText().length() == 0) {
            editPorcht.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                    , editPorcht.isFocused() ? 21 : 16);
        }

        if (redactorDopInfo.getText().length() == 0) {
            redactorDopInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP
                    , redactorDopInfo.isFocused() ? 21 : 16);
        }
    }

    @OnFocusChange({R.id.private_redactor_name, R.id.edit_flat
            , R.id.edit_porcht, R.id.redactor_dop_info})
    public void onFocus(View v, boolean hasFocus) {


        if (privateRedactorName == null || privateRedactorName.getText() == null)
            return;

        if (editFlat == null || editFlat.getText() == null)
            return;

        if (editPorcht == null || editPorcht.getText() == null)
            return;

        if (redactorDopInfo == null || redactorDopInfo.getText() == null)
            return;

        if (isShowKeyBoord)
            initTextSizeEdit();

        switch (v.getId()) {
            case R.id.private_redactor_name:

                if (privateRedactorName.getText().toString().length() == 0) {

                    privateRedactorNameText.startAnimation(hasFocus ? showPanelAnimation : hidePanelAnimation);
                    privateRedactorNameText.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);

                }


                break;
            case R.id.edit_flat:
                if (editFlat.getText().length() == 0) {
                    textFlat.startAnimation(hasFocus ? showPanelAnimation : hidePanelAnimation);
                    textFlat.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);

                }
                break;
            case R.id.edit_porcht:
                if (editPorcht.getText().length() == 0) {
                    textPorch.startAnimation(hasFocus ? showPanelAnimation : hidePanelAnimation);
                    textPorch.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                }
                break;
            case R.id.redactor_dop_info:
                if (redactorDopInfo.getText().length() == 0) {
                    textDopInfo.startAnimation(hasFocus ? showPanelAnimation : hidePanelAnimation);
                    textDopInfo.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                } else if (hasFocus) {
                    int length = redactorDopInfo.getText().length();

                    redactorDopInfo.setSelection(length);
                }
                break;
        }

        EditText editText = (EditText) v;
        editText.setHintTextColor(initColor(hasFocus ? R.color.colorTansparent
                : R.color.text_sec_time));
        final int length = editText.getText().length();
        if (length == 0)
            editText.startAnimation(!hasFocus ? showPanelAnimation : hidePanelAnimation);


    }

    protected void preLoadAnimation() {

        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        showPanelAnimation = AnimationUtils.loadAnimation(Injector.getAppContext(), R.anim.fadein);
        showPanelAnimation.setAnimationListener(al);
        hidePanelAnimation = AnimationUtils.loadAnimation(Injector.getAppContext(), R.anim.fadeout);
        hidePanelAnimation.setAnimationListener(al);

    }

    protected void initUi(ClientAddress clientAddress) {

        if (clientAddress == null)
            return;
        if (clientAddress.flat != null)
            editFlat.setText(clientAddress.flat);
        String poscht = clientAddress.entrance;
        if (poscht != null)
            editPorcht.setText(poscht);
        String comment = clientAddress.comment;
        if (comment != null)
            redactorDopInfo.setText(comment);

        String textStartAdress = clientAddress.getStringNameAdress(getString(R.string.point_to_maps));

        adressValue.setText(textStartAdress);
    }

    @OnTextChanged(R.id.private_redactor_name)
    public void afterPrivateRedactorName(Editable s) {
        if (s.length() == 0)
            setDefEdit(privateRedactorName);
        else
            setActiveEdit(privateRedactorName, privateRedactorNameText);
    }

    @OnTextChanged(R.id.edit_flat)
    public void afterTextChangedFlat(Editable s) {
        if (s.length() == 0)
            setDefEdit(editFlat);
        else
            setActiveEdit(editFlat, textFlat);
    }

    @OnTextChanged(R.id.edit_porcht)
    public void afterTextChangedPorcht(Editable s) {
        if (s.length() == 0)
            setDefEdit(editPorcht);
        else
            setActiveEdit(editPorcht, textPorch);
    }

    @OnTextChanged(R.id.redactor_dop_info)
    public void afterTextChangedDop(Editable s) {
        if (s.length() == 0)
            setDefEdit(redactorDopInfo);
        else
            setActiveEdit(redactorDopInfo, textDopInfo);
    }

    @OnEditorAction({R.id.redactor_dop_info, R.id.edit_porcht})
    public boolean actionDopInfo(EditText v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            redactorDopInfo.requestFocus();
            return true;
        }

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(v);
            return true;
        }
        return false;
    }

    protected void hideCursor() {
        hideCursorText.setFocusable(true);
        hideCursorText.setFocusableInTouchMode(true);
        hideCursorText.requestFocus();
    }

    void setActiveEdit(EditText edit, View v) {
        v.setVisibility(View.VISIBLE);
        edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
    }

    protected void setDefEdit(EditText edit) {

        edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    }

    @OnClick(R.id.private_redactor_done_button)
    public void onRedactorDoneButton() {
        if (editFlat == null || editFlat.getText() == null) {

            if (!isStart)
                getAWork().showFRouteCreaton();
            else
                getAWork().showV2FFullTextSearch(1, Constants.ADD_ADRESS);
            return;
        }

        String flat = editFlat.getText().toString();
        String poscht = editPorcht.getText().toString();
        String comment = redactorDopInfo.getText().toString();


        setAdressToRoute(flat, poscht, comment);
        if (!isStart)
            getAWork().showFRouteCreaton();
        else
            getAWork().showV2FFullTextSearch(1, Constants.ADD_ADRESS);
    }

    @OnClick(R.id.button_add_private_adress)
    public void buttonAddPrivateAdress() {
        DialogClient.showDialogAddPrivateAdress(this);
    }


    private ClientAddress setAdressToRoute(String flat, String poscht, String comment) {
        CreateRequest createRequest = Injector.getClientData().getCreateRequest();
        if (createRequest == null
                || createRequest.getRoute() == null
                || createRequest.getRoute().size() == 0
                || createRequest.getRoute().get(0) == null)
            return null;
        ClientAddress clientAddress = createRequest.getRoute().get(0);

        clientAddress.flat = "".equals(flat) ? null : flat;
        clientAddress.entrance = "".equals(poscht) ? null : poscht;
        clientAddress.comment = "".equals(comment) ? null : comment;
        return clientAddress;
    }


    @OnClick({R.id.adress_value, R.id.ic_clear_edit})
    public void onAdressValue() {
        getAWork().showV2FFullTextSearch(indexAdress, Constants.EDIT_ADRESS);
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showFRouteCreaton();
        return true;
    }

    @OnClick(R.id.ic_bask)
    public void onIcBask() {
        if (!isVisible())
            return;
        getAWork().showV3FRoute();
    }

    @Override
    public void onDestroyView() {
        removeGOLListener(parentView.getViewTreeObserver(), onGlobalLayoutListener);
        getAWork().hideWorkProgress();
        Utilites.hideSoftKeyboard(getAWork(), textDopInfo);

        super.onDestroyView();
    }

    @Override
    public Context getContectApp() {
        return getContext();
    }

    @Override
    public void responseAction(String name) {
        String flat = editFlat.getText().toString();
        String poscht = editPorcht.getText().toString();
        String comment = redactorDopInfo.getText().toString();


        ClientAddress clientAddress = setAdressToRoute(flat, poscht, comment);

        if (clientAddress != null) {


            clientAddress.id = System.currentTimeMillis();
            clientAddress.namePrivate = name;
            Injector.getSettingsStore().addOrderAddressToListPrivatePoint(clientAddress);
        }
    }

    @Override
    public void addPfotoCamera(boolean b) {

    }
}
