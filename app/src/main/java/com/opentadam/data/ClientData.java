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
package com.opentadam.data;

import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.google.android.gms.maps.model.LatLng;
import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusRestartAWork;
import com.opentadam.bus.BusUpdTarif;
import com.opentadam.bus.HiveBus;
import com.opentadam.model.CreateRequest;
import com.opentadam.network.UtilitesErrorIGetApiResponseObject;
import com.opentadam.network.rest.AccountState;
import com.opentadam.network.rest.Bonuses;
import com.opentadam.network.rest.Capabilities;
import com.opentadam.network.rest.DispatcherCall;
import com.opentadam.network.rest.GpsPosition;
import com.opentadam.network.rest.Option;
import com.opentadam.network.rest.OrderInfo;
import com.opentadam.network.rest.PaymentMethod;
import com.opentadam.network.rest.Service;
import com.opentadam.network.rest.ShortOrderInfo;
import com.opentadam.network.rest.Tarif;
import com.opentadam.network.rest.TempObjectUIMRoute;

import org.osmdroid.api.IGeoPoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.opentadam.Injector.setLibDefTarif;


public class ClientData {
    public static final int ID_CONTRACTOR = 0;
    public static final int ID_CREDIT_CARD = 3;
    public static final int ID_CASH = 2;
    public Service service;
    public List<Tarif> arrTarif;
    public String pattern;
    public boolean isShowProfile = false;
    public Uri mOutputFileUri;
    public int cashPos = 0;
    public List<PaymentMethod> paymentMethods;
    public boolean isRestartAll = false;
    public LatLng latLngTarifHTPSRequest;
    public boolean isShowSettings = false;
    public boolean isAkceptShow = false;
    public AccountState accountState;
    public boolean isRecreateLocale;
    public int hashDefPosTariff;
    public boolean isNotNullmarkerLocation;
    public ArrayList<OrderInfo> pingArrayOrderInfo = new ArrayList<>();
    private LatLng markerLocation;
    private ArrayList<CashList> cashList;
    private PaymentMethod paymentMethodSelect;
    private TempObjectUIMRoute tempObjectUIMRoute;
    private int defPosTariff = 0;
    private CreateRequest createRequest;
    private ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap;

    public ArrayList<ShortOrderInfo> getPingArrayShortOrderInfo() {
        ArrayList<ShortOrderInfo> shortOrderInfoArrayList = new ArrayList<>();
        for (OrderInfo orderInfo : pingArrayOrderInfo) {
            shortOrderInfoArrayList.add(orderInfo.mapperShortOrderInfo());
        }

        return shortOrderInfoArrayList;
    }

    public ClientData() {
        paymentMethodSelect = new PaymentMethod("cash");
        markerLocation = App.app.mMyGoogleLocation.getMemoryLocation();
        this.isNotNullmarkerLocation = false;
    }

    public LatLng getMarkerLocation() {
        return markerLocation;
    }

    public void setMarkerLocation(LatLng markerLocation) {

        boolean latLngDisabled = isLatLngDisabled(markerLocation);
        if (latLngDisabled)
            return;
        this.markerLocation = markerLocation;
        this.isNotNullmarkerLocation = true;
    }

    public boolean isLatLngDisabled(LatLng markerLocation) {
        if (markerLocation == null)
            return true;
        double latitude = markerLocation.latitude;
        double longitude = markerLocation.longitude;

        BigDecimal wholeValue = BigDecimal.valueOf(latitude).setScale(0, BigDecimal.ROUND_DOWN);
        boolean b = latitude - wholeValue.doubleValue() == 0;
        BigDecimal wholeValue1 = BigDecimal.valueOf(longitude).setScale(0, BigDecimal.ROUND_DOWN);
        boolean b1 = longitude - wholeValue1.doubleValue() == 0;
        return b || b1;
    }

    public boolean isLatLngDisabled(double[] latLon) {
        if (latLon == null || latLon.length != 2) {
            return true;
        }
        double latitude = latLon[0];
        double longitude = latLon[1];

        BigDecimal wholeValue = BigDecimal.valueOf(latitude).setScale(0, BigDecimal.ROUND_DOWN);
        boolean b = latitude - wholeValue.doubleValue() == 0;
        BigDecimal wholeValue1 = BigDecimal.valueOf(longitude).setScale(0, BigDecimal.ROUND_DOWN);
        boolean b1 = longitude - wholeValue1.doubleValue() == 0;
        return b || b1;
    }

    public boolean isLatLngDisabled(IGeoPoint mapCenter) {
        if (mapCenter == null)
            return true;
        LatLng markerLocation = new LatLng(mapCenter.getLatitude(), mapCenter.getLongitude());
        return isLatLngDisabled(markerLocation);
    }

    public boolean isLatLngDisabled(Location location) {
        if (location == null)
            return true;
        LatLng markerLocation = new LatLng(location.getLatitude(), location.getLongitude());
        return isLatLngDisabled(markerLocation);
    }

    public ArrayMap<String, UtilitesErrorIGetApiResponseObject> getArrayMap() {
        return arrayMap;
    }

    public void setArrayMap(ArrayMap<String, UtilitesErrorIGetApiResponseObject> arrayMap) {
        this.arrayMap = arrayMap;
    }

    public PaymentMethod getPaymentMethodSelect() {
        return paymentMethodSelect == null ? new PaymentMethod("cash") : paymentMethodSelect;
    }

    public void setPaymentMethodSelect(PaymentMethod paymentMethodSelect) {
        this.paymentMethodSelect = paymentMethodSelect;
    }

    private boolean isEnabledBonus() {
        getPaymentMethodSelect();
        String kind = paymentMethodSelect.kind;

        return !"contractor".equals(kind);
    }


    public void initMemoryListNamesCost() {
        clear();
    }


    public boolean isEnabledShowV2Bonuses() {
        if (!isEnabledBonus())
            return false;


        TempObjectUIMRoute mRoute = getTempObjectUIMRoute();
        Bonuses bonuses = mRoute.bonuses;
        if (bonuses == null)
            return false;
        float balance = bonuses.gefBalance();

        Capabilities capabilities = bonuses.capabilities;
        if (capabilities == null)
            return false;
        String type = capabilities.type;
        float min;
        if ("options".equals(type)) {
            Float[] options = capabilities.options;
            if (options == null || options.length == 0)
                return false;
            min = options[0];
        } else
            min = capabilities.min;

        return balance >= min;


    }

    public Resources getResources() {
        return Injector.getAppContext().getResources();
    }

    // Injector.getAppContext().getString
    //
    public ArrayList<CashList> getCashList() {
        ArrayList<CashList> cashLists = new ArrayList<>();

        if (paymentMethods == null) {
            return cashLists;
        }

        int size = paymentMethods.size();
        for (int i = 0; i < size; i++) {
            PaymentMethod paymentMethod = paymentMethods.get(i);
            String kind = paymentMethod.kind;
            if ("contractor".equals(kind)) {

                cashLists.add(new CashList(paymentMethod, R.drawable.ic_counteragent_def_text,
                        paymentMethod.name, null, ID_CONTRACTOR, null));
            } else if ("cash".equals(kind)) {

                cashLists.add(new CashList(null, R.drawable.ic_cash_def_text

                        , getResources().getString(R.string.method_cash), null, ID_CASH, null));

            } else if ("credit_card".equals(kind) && Injector.getWorkSettings().getCardPaymentAllowed()) {

                String nameCard = paymentMethod.name;
                int length = nameCard.length();
                String tipeCard = getTipeCard(nameCard);
                int ic_credit_card = getIcCreditCard(nameCard);
                String cardNameMenu = "••••" + nameCard.substring(length - 4, length);
                nameCard = "*" + cardNameMenu;
                String nameCardPaynent = tipeCard + " " + nameCard;

                cashLists.add(new CashList(paymentMethod, ic_credit_card
                        , nameCardPaynent, null, ID_CREDIT_CARD, cardNameMenu));
            }
        }


        this.cashList = cashLists;
        return cashLists;
    }

    private int getIcCreditCard(String nameCard) {
        if (nameCard.startsWith("2")) {
            return R.drawable.acq_mirlogo;
        }
        if (nameCard.startsWith("4")) {
            return R.drawable.acq_visalogo;
        }

        if (nameCard.startsWith("5")) {
            return R.drawable.acq_master;
        }

        if (nameCard.startsWith("6")) {
            return R.drawable.acq_maestro;
        }

        return R.drawable.ic_credit_card_def_text;
    }

    private String getTipeCard(String nameCard) {
        if (nameCard.startsWith("2")) {
            return "МИР";
        }
        if (nameCard.startsWith("4")) {
            return "VISA";
        }

        if (nameCard.startsWith("5")) {
            return "MasterCard";
        }
        if (nameCard.startsWith("6")) {
            return "Maestro";
        }
        return getResources().
                getString(R.string.value_num_card);
    }

    public String getNameCashServersData() {
        if (getCashList() == null
                || getCashList().size() == 0
                || cashPos > getCashList().size() - 1) {
            cashPos = 0;
            return null;
        }
        this.cashList = getCashList();
        CashList cashList = this.cashList.get(cashPos);
        return cashList.name;

    }

    public boolean isEnabledService() {

        return service == null || !"stub".equals(service.getKind());
    }


    public String getMessService() {

        if (service == null || service.getMessage() == null)
            return "";

        return service.getMessage();
    }

    public String getNameCash() {
        if (getCashList() == null
                || getCashList().size() == 0
                || cashPos > getCashList().size() - 1) {
            cashPos = 0;
            return getResources().getString(R.string.name_cash);
        }
        this.cashList = getCashList();
        CashList cashList = this.cashList.get(cashPos);
        String name = cashList.name;

        return name == null ? getResources().getString(R.string.name_cash) : name;
    }

    public LatLng getLatLngStartAdress() {
        if (createRequest == null
                || createRequest.getRouteLocation() == null
                || createRequest.getRouteLocation().size() == 0
                || createRequest.getRouteLocation().get(0) == null)
            return null;
        return createRequest.getRouteLocation().get(0).getLatLng();
    }

    public int getDefPosTariff() {
        return defPosTariff;
    }

    public void setDefPosTariff(int i) {
        defPosTariff = i;
        setLibDefTarif(getDefTarif());
        updTarif();
    }


    public String getPhoneClient() {
        return Injector.getSettingsStore().readString(Constants.REG_PHONE_CLIENT, null);
    }

    public TempObjectUIMRoute getTempObjectUIMRoute() {
        if (this.tempObjectUIMRoute == null)
            this.tempObjectUIMRoute = new TempObjectUIMRoute();
        return this.tempObjectUIMRoute;
    }

    public void setTempObjectUIMRoute(TempObjectUIMRoute tempObjectUIMRoute) {
        this.tempObjectUIMRoute = tempObjectUIMRoute;
    }

    public LatLng getLatLngEditAdress(int indexAdress) {
        ArrayList<GpsPosition> routeLocation = getCreateRequest().getRouteLocation();
        if (routeLocation.size() - 1 <= indexAdress)
            return null;

        GpsPosition gpsPosition = routeLocation.get(indexAdress);
        return new LatLng(gpsPosition.lat, gpsPosition.lon);
    }

    public CreateRequest getCreateRequest() {
        if (createRequest == null)
            createRequest = new CreateRequest();
        return createRequest;
    }

    public void setCreateRequest(CreateRequest createRequest) {
        this.createRequest = createRequest;
    }

    public Tarif getDefTarif() {
        return arrTarif == null ? null : arrTarif.get(defPosTariff);
    }

    public void setDefTarif(Tarif tarif) {
        if (arrTarif != null)
            arrTarif.set(defPosTariff, tarif);
        setLibDefTarif(getDefTarif());
    }

    public String getNameDefTarif() {
        if (getDefTarif() == null)
            return null;
        return getDefTarif().name;
    }

    public Long getIdDefTarif() {
        if (getDefTarif() == null)
            return null;
        return getDefTarif().id;
    }

    public LatLng getLatLngCenterAdress() {
        GpsPosition position = service.getAddress().position;
        return new LatLng(position.lat, position.lon);
    }

    public void initService(Service service) {
        List<Tarif> tariffs = service.getTariffs();
        if (defPosTariff > tariffs.size() - 1) {
            defPosTariff = 0;
        } else {
            // проверка на наличие в новом списке
            Tarif defTarif = getDefTarif();

            if (defTarif != null) {
                String nameDef = defTarif.name;
                String name = tariffs.get(defPosTariff).name;

                if (!nameDef.equals(name)) {
                    defPosTariff = 0;
                    HiveBus.postBusUpdGeozoneTarif();
                }
            }
        }


        arrTarif = tariffs;
        Tarif tariff = tariffs.get(defPosTariff);

        if (tariff == null)
            return;

        if (markerLocation != null) {

            tariff.lat = markerLocation.latitude;
            tariff.lon = markerLocation.longitude;
        }
        this.service = service;
        HiveBus.postBusUpdArrTariff();
        updTarif();
    }

    public void updTarif() {
        Tarif defTarif = getDefTarif();
        if (defTarif == null) {
            // что-то пошло не так - отсутствуют тарифы и не можем взять дефолтный
            // ClientData.java:429
            App.bus.post(new BusRestartAWork());
            return;
        }

        ArrayList<Option> options = defTarif.options;
        TempObjectUIMRoute mRoute = getTempObjectUIMRoute();
        mRoute.optionsClient = new ArrayList<>();
        ArrayList<Option> optionsClients = mRoute.getOptionsClient();

        for (Option option : options) {

            if (option.mandatory) {
                String name = option.name;
                boolean b = false;
                for (Option optionsClient : optionsClients) {
                    if (optionsClient.name.equals(name)) {
                        b = true;
                    }
                }
                if (!b) {
                    optionsClients.add(option);
                }
            }

        }

        App.bus.post(new BusUpdTarif());
    }


    public void clear() {
        this.tempObjectUIMRoute = null;
        this.createRequest = null;
    }

    public String getDispatcherCall() {
        if (service == null)
            return null;
        DispatcherCall dispatcherCall = Injector
                .getWorkSettings()
                .getDispatcherCall();

        String allow = dispatcherCall.allow;
        if ("direct".equals(allow))
            return dispatcherCall.number;
        if ("via server".equals(allow))
            return dispatcherCall.allow;

        return null;
    }

    public float getCostChangeStep() {
        Tarif defTarif = getDefTarif();
        return defTarif == null
                || defTarif.costChangeStep == null
                || !getDefTarif().costChangeAllowed ?
                0 : defTarif.costChangeStep.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public boolean getCostChangeAllowed() {
        Tarif defTarif = getDefTarif();
        return defTarif != null && defTarif.costChangeAllowed;
    }

    public boolean isEnabledCard() {
        if (paymentMethods == null)
            return false;
        for (PaymentMethod paymentMethod : paymentMethods) {
            if ("credit_card".equals(paymentMethod.kind)) {
                return true;
            }

        }
        return false;
    }

    public static class CashList {

        public final int idIcon;
        public final String name;
        public final int nameIdResours;
        public final String summ;
        public PaymentMethod paymentMethod;
        public String cardNameMenu;

        CashList(PaymentMethod paymentMethod, int idIcon
                , String name, String summ, int nameIdResours, String cardNameMenu) {
            this.paymentMethod = paymentMethod;
            this.idIcon = idIcon;
            this.name = name;
            this.summ = summ;
            this.nameIdResours = nameIdResours;
            this.cardNameMenu = cardNameMenu;
        }
    }
}
