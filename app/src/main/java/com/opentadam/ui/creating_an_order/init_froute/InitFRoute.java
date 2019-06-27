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

package com.opentadam.ui.creating_an_order.init_froute;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.Injector;

import com.opentadam.data.ClientData;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.creating_an_order.V3FRoute;
import com.opentadam.yandex_google_maps.IUPmaps;
import com.opentadam.yandex_google_maps.UtilitesMaps;
import com.opentadam.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static com.opentadam.Injector.getClientData;

public class InitFRoute {
    private final V3FRoute v3FRoute;
    private TextView payName;
    private ImageView payIc;
    private View contTime;
    private IUPmaps iuPmaps;
    private TextView adressValueTime;
    private TextView adressSubValueTime;
    private TextView frouteSendText;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private View[] marginButtonView;
    private View fraAnimate0;
    private View fraAnimate2;
    private View fraAnimate1;
    private View contAnimateAdress0;

    private InitFRoute(V3FRoute val) {
        v3FRoute = val;
    }

    public static InitFRoute newInstance(V3FRoute val) {
        return new InitFRoute(val);
    }

    public Animator[] initAnimateAdress0() {


        if (fraAnimate0 == null
                || fraAnimate2 == null
                || fraAnimate1 == null)
            return null;


        fraAnimate0.setAlpha(0);
        fraAnimate1.setAlpha(0);
        fraAnimate2.setAlpha(0);

        Animator animateAnimate0 = AnimatorInflater.loadAnimator(v3FRoute.getAWork()
                , R.animator.fr_progress_a0);

        Animator animateAnimate1 = AnimatorInflater.loadAnimator(v3FRoute.getAWork()
                , R.animator.fr_progress_a1);
        Animator animateAnimate2 = AnimatorInflater.loadAnimator(v3FRoute.getAWork()
                , R.animator.fr_progress_a2);

        animateAnimate0.setTarget(fraAnimate0);
        animateAnimate1.setTarget(fraAnimate1);
        animateAnimate2.setTarget(fraAnimate2);

        contAnimateAdress0.setVisibility(View.VISIBLE);

        return new Animator[]{animateAnimate0, animateAnimate1, animateAnimate2};
    }

    public void initTimeOrder() {
        if (!v3FRoute.isVisible())
            return;
        TempObjectUIMRoute.TimeOrder timeOrder = Injector.getClientData()
                .getTempObjectUIMRoute().getTimeOrder();
        contTime.setVisibility(timeOrder == null ? View.GONE : View.VISIBLE);
        if (iuPmaps != null) {
            iuPmaps.setMarginMapsDefault();
        }
        initPanelHeight();
        initValueTextSend();
        if (timeOrder == null) {
            adressValueTime.setText("");
            adressSubValueTime.setText("");
        } else {
            adressValueTime.setText(timeOrder.timeValue);
            adressSubValueTime.setText(String.format(v3FRoute.getString(R.string.froute_value_preorder_data), timeOrder.getTimeDMG()));
        }
    }

    private void initValueTextSend() {
        if (!v3FRoute.isVisible())
            return;
        TempObjectUIMRoute tempObjectUIMRoute = Injector.getClientData()
                .getTempObjectUIMRoute();
        boolean isOreorder = tempObjectUIMRoute != null && tempObjectUIMRoute.getTimeOrder() != null;

        frouteSendText.setText(v3FRoute.getString(isOreorder
                ? R.string.fr_send_button_preorder_text : R.string.fr_send_button_text));

    }

    public void initPanelHeight() {
        if (!v3FRoute.isVisible())
            return;
        slidingUpPanelLayout.setPanelHeight(v3FRoute.getPX(v3FRoute.getPageDP()));
        UtilitesMaps
                .instanse()
                .setMarginButtonView(v3FRoute.getPageDP() - 3, marginButtonView);
    }

    public void initPayCont() {
        if (!v3FRoute.isVisible())
            return;
        final ClientData clientData = getClientData();


        final ArrayList<ClientData.CashList> cashLists = clientData
                .getCashList();
        if (cashLists == null || cashLists.size() == 0) {
            payName.setText(v3FRoute.getString(R.string.cash));
            payIc.setImageDrawable(v3FRoute.initDrawable(R.drawable.ic_cash_def_text));
            return;
        }

        if (clientData.cashPos > cashLists.size() - 1) {
            clientData.cashPos = 0;
        }

        ClientData.CashList cashList = cashLists.get(clientData.cashPos);

        switch (cashList.nameIdResours) {
            case ClientData.ID_CASH:
                payName.setText(v3FRoute.getString(R.string.cash));
                payIc.setImageDrawable(v3FRoute.initDrawable(cashList.idIcon));
                break;
            case ClientData.ID_CONTRACTOR:
                payName.setText(cashList.name);
                payIc.setImageDrawable(v3FRoute.initDrawable(cashList.idIcon)
                );
                break;
            case ClientData.ID_CREDIT_CARD:
                payName.setText(cashList.cardNameMenu);
                payIc.setImageDrawable(v3FRoute.initDrawable(R.drawable.ic_credit_card));
                break;
        }


    }

    public InitFRoute setPayName(TextView val) {
        payName = val;
        return this;
    }

    public InitFRoute setPayIc(ImageView val) {
        payIc = val;
        return this;
    }

    public InitFRoute setContTime(View val) {
        contTime = val;
        return this;
    }

    public InitFRoute setIuPmaps(IUPmaps val) {
        iuPmaps = val;
        return this;
    }

    public InitFRoute setAdressValueTime(TextView val) {
        adressValueTime = val;
        return this;
    }

    public InitFRoute setAdressSubValueTime(TextView val) {
        adressSubValueTime = val;
        return this;
    }

    public InitFRoute setFrouteSendText(TextView val) {
        frouteSendText = val;
        return this;
    }

    public InitFRoute setSlidingUpPanelLayout(SlidingUpPanelLayout v) {
        slidingUpPanelLayout = v;
        return this;
    }

    public InitFRoute setMarginButtonView(View[] v) {
        marginButtonView = v;
        return this;
    }

    public InitFRoute setFraAnimate0(View v) {
        fraAnimate0 = v;
        return this;
    }

    public InitFRoute setfraAnimate2(View v) {
        fraAnimate2 = v;
        return this;
    }

    public InitFRoute setFraAnimate1(View v) {
        fraAnimate1 = v;
        return this;
    }

    public InitFRoute setcontAnimateAdress0(View v) {
        contAnimateAdress0 = v;
        return this;
    }
}
