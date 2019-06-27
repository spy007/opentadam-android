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

package com.opentadam.ui.frends.referal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.network.ApiResponse;
import com.opentadam.network.IGetApiResponse;
import com.opentadam.network.RESTConnect;
import com.opentadam.network.rest.TempObjectUIMRoute;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TranslateSumm;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.opentadam.Injector.getClientData;

public class FReferalBonusProgramm extends BaseFr implements IPageSelected {

    @InjectView(R.id.fref_copy)
    TextView codValue;
    @InjectView(R.id.fref_none_send_share)
    View noneSendShare;
    @InjectView(R.id.fref_currency_rub)
    TextView currencyRub;
    @InjectView(R.id.fref_currency_kop)
    TextView currencyKop;
    @InjectView(R.id.fref_cound_frends)
    TextView coundFrends;

    @InjectView(R.id.fref_currency_rub_remove_bonus)
    TextView rubRemoveBonus;
    @InjectView(R.id.fref_currency_kop_remove_bonus)
    TextView kopRemoveBonus;
    @InjectView(R.id.fref_currency_rub_add_bonus)
    TextView rubAddBonus;
    @InjectView(R.id.fref_currency_kop_add_bonus)
    TextView kopAddBonus;
    @InjectView(R.id.fref_popup)
    LinearLayout frefPopup;
    @InjectView(R.id.fref_popup_data)
    TextView popupData;
    @InjectView(R.id.fref_popup_rub)
    TextView popupRub;
    @InjectView(R.id.fref_popup_kop)
    TextView popupKop;

    @InjectView(R.id.fref_period_info)
    TextView periodInfo;

    @InjectView(R.id.fref_cont_none)
    View contNone;
    @InjectView(R.id.fref_basic_cont)
    View basicCont;
    @InjectView(R.id.fref_ext_send)
    View fextSend;
    @InjectView(R.id.fref_cont_ref_code)
    View contRefCode;
    @InjectView(R.id.fref_typ)
    TextView frefTyp;
    @InjectView(R.id.fref_typ_popup_rub)
    TextView typRub;
    @InjectView(R.id.fref_typ_popup_kop)
    TextView typKop;

    @InjectView(R.id.frb_body_bonus)
    View body;

    @InjectView(R.id.fref_cont_base_bonus)
    View contBaseBonus;
    @InjectView(R.id.fref_currency_rub_base_bonus)
    TextView rubBaseBonus;
    @InjectView(R.id.fref_currency_kop_base_bonus)
    TextView kopBaseBonus;
    @InjectView(R.id.body_empty)
    View bodyEmpty;
    @InjectView(R.id.frefb_empty_page)
    View emptyPage;

    @InjectView(R.id.fref_la_view)
    com.airbnb.lottie.LottieAnimationView plAnimate;

    private ArrayList<Transaction> transactionSeq;
    private Result refferalState;
    private BigDecimal maxValueAddBunus;
    private int sizeTransaction;
    private String lptype;

    public static Fragment newInstance(String lptype) {
        return new FReferalBonusProgramm()
                .withArgument("lptype", lptype)
                .withViewId(R.layout.f_referral_bonus_programm);
    }

    public int getSizeTransaction() {
        return sizeTransaction;
    }

    public BigDecimal getMaxValueAddBunus() {
        return maxValueAddBunus;
    }

    public void setMaxValueAddBunus(BigDecimal val) {
        maxValueAddBunus = val;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lptype = getArguments().getString("lptype", "none");
        plAnimate.setVisibility("ext-bonus-referral-vip".equals(lptype) ? View.VISIBLE : View.GONE);
        frefPopup.setBackgroundResource(R.drawable.fref_popover_center);
        updateUI(true);

    }

    @OnClick(R.id.createQRCodeRef)
    public void onCreateQRCodeRef() {
        if (refferalState.shareMessage == null)
            return;

        getAWork().showCreateQRCodeFragment(refferalState.shareMessage);
    }

    @Override
    public boolean isEnabledSC() {
        return true;
    }

    @Override
    public void updateUI(boolean isEnabledProgress) {
        // hidePopup();
        plAnimate.setVisibility("ext-bonus-referral-vip".equals(lptype) ? View.VISIBLE : View.GONE);
        switch (lptype) {
            case "none":
                noneSendShare.setVisibility(View.GONE);
                contNone.setVisibility(View.GONE);
                break;
            case "basic-bonus":
                noneSendShare.setVisibility(View.GONE);
                contBaseBonus.setVisibility(View.GONE);
                contNone.setVisibility(View.GONE);
                break;
            case "ext-bonus-referral-vip":


                break;
        }

        getFullReferall();
    }

    private void showProgressRest() {

        body.setVisibility(View.GONE);
    }

    public void hideProgressRest() {
        body.setVisibility(View.VISIBLE);
    }

    private void getTransactions() {
        final RESTConnect restConnect = Injector.getRC();

        restConnect.getLoyaltyProgramTransactions(Injector.getClientData().getMarkerLocation(),
                null, new IGetApiResponse() {


                    @Override
                    public void getApiResponse(ApiResponse apiResponse) {
                        if (!isVisible())
                            return;
                        hideswipe();
                        if (apiResponse == null || apiResponse.error != null) {
                            getAWork().showErrorIGetApiResponse(apiResponse);
                            return;
                        }

                        ArrayList<Transaction> transaction = apiResponse.transaction;
                        maxValueAddBunus = BigDecimal.ZERO;
                        int size = transaction.size();
                        if (size == 0) {
                            initPlaceholder();
                        }


                        for (Transaction tr : transaction) {
                            DateTime dateTime = new DateTime(tr.date);
                            tr.millis = dateTime.getMillis();
                            if (tr.balance.compareTo(maxValueAddBunus) > 0) {
                                maxValueAddBunus = tr.balance;
                            }
                        }

                        sizeTransaction = size;
                        if (sizeTransaction > 1) {

                            Comparator<Transaction> transactionComparator = new Comparator<Transaction>() {
                                @Override
                                public int compare(Transaction o1, Transaction o2) {
                                    if (o1.millis == o2.millis) return 0;
                                    else if (o1.millis > o2.millis) return 1;
                                    else return -1;
                                }
                            };
                            Collections.sort(transaction, transactionComparator);
                        }
                        transactionSeq = transaction;
                        initPeriodAddRemoveBonus();
                        initPeriod();
                        getChildFragmentManager().beginTransaction().replace(R.id.fref_cont_graf
                                , PlaceholderFragment.newInstance()).commitAllowingStateLoss();

                    }
                });

    }

    private void initPlaceholder() {
        bodyEmpty.setVisibility(View.INVISIBLE);
        emptyPage.setVisibility(View.VISIBLE);
    }

    private void getFullReferall() {
        final RESTConnect restConnect = Injector.getRC();

        restConnect.getReferralData(Injector.getClientData().getMarkerLocation(), new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                refferalState = apiResponse.refferalState;
                lptype = refferalState.lptype;
                plAnimate.setVisibility("ext-bonus-referral-vip".equals(lptype) ? View.VISIBLE : View.GONE);
                switch (lptype) {

                    case "none":
                        hideswipe();
                        initUINone();
                        break;

                    case "basic-bonus":

                        initUIBase();
                        break;

                    case "ext-bonus-referral-vip":
                        //     showProgressRest();
                        getTransactions();
                        initUIExt();
                        break;
                }

            }
        });
    }

    private void hideswipe() {

        V2FReferral v2FReferral = (V2FReferral) getParentFragment();
        if (v2FReferral == null)
            return;

        v2FReferral.hideRefreshing();
    }

    private void initUIExt() {

        initUI();
        fextSend.setVisibility(View.VISIBLE);
        contRefCode.setVisibility(View.VISIBLE);
    }

    private void initUIBase() {
        noneSendShare.setVisibility(View.VISIBLE);
        contBaseBonus.setVisibility(View.VISIBLE);
        contNone.setVisibility(View.VISIBLE);
        getAWork().hideProgressDevault();
        getBonusClient();

    }

    private void initBalanceBase(String balanceString) {
        TranslateSumm translateSumm = TranslateSumm.invoke(balanceString);
        rubBaseBonus.setText(getStringIntejerFormat(translateSumm.getRub()));
        kopBaseBonus.setText(translateSumm.getKop());
        contBaseBonus.setVisibility(View.VISIBLE);
        hideswipe();
    }


    private void getBonusClient() {

        RESTConnect restConnect = Injector.getRC();

        // redmine.hivecompany.ru/issues/11663
        // LatLng latLng = Injector.getSettingsStore().getLatLngTarifDef();

        LatLng latLng = Injector.getClientData().getMarkerLocation();
        restConnect.getBonusClient(latLng, new IGetApiResponse() {
            @Override
            public void getApiResponse(ApiResponse apiResponse) {
                if (!isVisible())
                    return;

                if (apiResponse == null || apiResponse.error != null) {
                    getAWork().showErrorIGetApiResponse(apiResponse);
                    return;
                }

                TempObjectUIMRoute mRoute = getClientData().getTempObjectUIMRoute();
                mRoute.bonuses = apiResponse.bonuses;
                initBalanceBase(apiResponse.bonuses.gefBalanceString());
            }
        });
    }

    private void initUINone() {
        noneSendShare.setVisibility(View.VISIBLE);
        contNone.setVisibility(View.VISIBLE);
        getAWork().hideProgressDevault();
    }

    @OnClick(R.id.fref_popup)
    public void hidePopup() {

        PlaceholderFragment placeholderFragment = (PlaceholderFragment)
                getChildFragmentManager().findFragmentById(R.id.fref_cont_graf);
        if (placeholderFragment == null)
            return;

        placeholderFragment.hideBegunokPopup();
        frefPopup.setVisibility(View.GONE);
    }

    public void initDataPopup(float x1, float y, FrefObjectInitPopup frefObjectInitPopup) {
        if (sizeTransaction == 0
                || frefObjectInitPopup.getTransaction().typ < 0)
            return;

        int dpX = frefObjectInitPopup.getDpX();
        float korrectX = frefObjectInitPopup.getKorrectX();
        int dpY = frefObjectInitPopup.getDpY();
        int idBgPopup = frefObjectInitPopup.getIdBgPopup();
        Transaction transaction = frefObjectInitPopup.getTransaction();
        if (transaction != null) {


            frefTyp.setText(transaction.getValStringTyp());
            transaction.amount.toString();
            TranslateSumm translateSumm = TranslateSumm.invoke(transaction.amount.toString(), true);

            typRub.setText((transaction.typ == 23 ? "" : "+") + translateSumm.getRub());
            typKop.setText(translateSumm.getKop());
            //   typCur.setText(Injector.getWorkSettings().currency.sign);

            if (transaction.typ == 23) {
                typRub.setTextColor(initColor(R.color.fref_color_remove_bonus));
                typKop.setTextColor(initColor(R.color.fref_color_remove_bonus));

            } else {
                typRub.setTextColor(initColor(R.color.fref_color_add_bunus));
                typKop.setTextColor(initColor(R.color.fref_color_add_bunus));

            }

            String balance = transaction
                    .balance
                    .setScale(2, RoundingMode.HALF_UP)
                    .toString();

            translateSumm = TranslateSumm.invoke(balance, true);

            String dateValuePopup = transaction.getDatePoup();
            popupData.setText(dateValuePopup);

            popupRub.setText(getStringIntejerFormat(translateSumm.getRub()));
            popupKop.setText(translateSumm.getKop());


        }

        frefPopup.setBackgroundResource(idBgPopup);
        frefPopup.setX(x1 - (float) getPX(dpX) - korrectX);
        frefPopup.setY(y + (float) getPX(dpY) - (float) getPX(14));
        frefPopup.setVisibility(View.VISIBLE);
    }

    private void initPeriod() {
        DateTimeFormatter formatterPeriod = DateTimeFormat.forPattern("d MMMM")
                .withLocale(Locale.getDefault());

        int size = transactionSeq.size();
        if (size == 0)
            return;
        String dateStart = transactionSeq.get(0).date;
        DateTime dateTimeStart = new DateTime(dateStart);
        String dateFin = transactionSeq.get(size - 1).date;
        DateTime dateTimeFin = new DateTime(dateFin);
        String toUpperCaseTimeStart = formatterPeriod.print(dateTimeStart)
                .toUpperCase(Locale.getDefault());
        String toUpperCaseTimeFin = formatterPeriod.print(dateTimeFin).toUpperCase(Locale.getDefault());
        String dateValuePeriod = toUpperCaseTimeStart
                + (!toUpperCaseTimeStart.equals(toUpperCaseTimeFin) ? " - "
                + toUpperCaseTimeFin : "");

        periodInfo.setText(dateValuePeriod);

    }

    public ArrayList<Transaction> getTransactionSeq() {
        return transactionSeq;
    }

    private void initUI() {
        initReferralCode();
        initBalance();

        coundFrends.setText(refferalState.friends / 1000 == 0 ? String.valueOf(refferalState.friends) : (refferalState.friends / 1000) + "K");

    }


    private void initPeriodAddRemoveBonus() {
        BigInteger zero = BigInteger.ZERO;
        BigDecimal bigDecimalZero = new BigDecimal(zero);
        BigDecimal plusBalance = new BigDecimal(zero);
        BigDecimal minusBalance = new BigDecimal(zero);
        for (Transaction tr : transactionSeq) {

            BigDecimal amount = tr.amount;

            int compareTo = amount.compareTo(bigDecimalZero);
            switch (compareTo) {
                case -1:
                    minusBalance = minusBalance.add(amount).setScale(2, RoundingMode.HALF_UP);
                    break;
                case 1:
                    plusBalance = plusBalance.add(amount).setScale(2, RoundingMode.HALF_UP);

                    break;
            }

        }

        if (minusBalance.compareTo(bigDecimalZero) == 0) {
            rubRemoveBonus.setText("0");
            kopRemoveBonus.setText("00");
        } else {
            TranslateSumm translateSumm = TranslateSumm.invoke(minusBalance.toString());
            rubRemoveBonus.setText(getStringIntejerFormat(translateSumm.getRub()));
            kopRemoveBonus.setText(translateSumm.getKop());
        }
        if (plusBalance.compareTo(bigDecimalZero) == 0) {
            rubAddBonus.setText("0");
            kopAddBonus.setText("00");
        } else {

            TranslateSumm translateSumm = TranslateSumm.invoke(plusBalance.toString());
            rubAddBonus.setText(getStringIntejerFormat(translateSumm.getRub()));
            kopAddBonus.setText(translateSumm.getKop());
        }

    }

    private void initReferralCode() {
        String referralCode = refferalState.referralCode;
        if (referralCode == null)
            return;

        StringBuilder sh = new StringBuilder();
        int length = referralCode.length();
        for (int i = 0; i < length; i++) {
            char ch = referralCode.charAt(i);
            sh.append(" " + " ").append(ch);
        }

        codValue.setText(sh.toString().trim());
    }

    private void initBalance() {

        String balanceString = refferalState
                .balance
                .setScale(2, RoundingMode.HALF_UP).toString();
        TranslateSumm translateSumm = TranslateSumm.invoke(balanceString);

        currencyRub.setText(getStringIntejerFormat(translateSumm.getRub()));
        currencyKop.setText(translateSumm.getKop());
    }


    @OnClick({R.id.fref_send_share, R.id.fref_none_send_share})
    public void sendShareMessage() {
        if (refferalState.shareMessage == null)
            return;

        Intent shareIntent = ShareCompat.IntentBuilder.from(getAWork())
                .setType("text/plain")
                .setText(refferalState.shareMessage)
                .setChooserTitle(R.string.fref_chooser_title)
                .createChooserIntent();

        // Проверка для избежания ошибки ActivityNotFoundException
        ComponentName componentName = shareIntent.resolveActivity(getAWork().getPackageManager());

        if (componentName != null) {
            startActivity(shareIntent);
        }
    }


    @OnClick(R.id.fref_copy)
    public void onCopyText() {
        if (refferalState == null || refferalState.referralCode == null)
            return;
        copyText(refferalState.referralCode);
    }

    private void copyText(String copiedText) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getAWork()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("TAG", copiedText);
        if (clipboard == null || clip == null)
            return;

        clipboard.setPrimaryClip(clip);
        alert(getString(R.string.fref_copy_ref_kode));
    }

}
