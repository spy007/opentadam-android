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
package com.opentadam;

public class Constants {
    // масштаб по умолчанию карты
    public static final int DEF_ZOOM_MAP = 16;
    // маршрут
    public static final int ADD_ADRESS = 1;
    public static final int EDIT_ADRESS = 3;
    public static final int INSERT_ADRESS = 4;

    // константы регистрации:
    public static final String REG_ID_CLIENT = "regIdClient";
    public static final String REG_KEY_CLIENT = "regKeyClient";
    // post Создания заказа
    public static final String REG_PHONE_CLIENT = "regPhoneClient";
    // статусы заказа
    public static final int STATE_CREATE = 1;
    public static final int STATE_SET = 2;
    public static final int STATE_WAIT = 3;
    public static final int STATE_WORK = 4;
    public static final int STATE_DONE = 5;
    public static final String REG_USER_MAIL = "REG_USER_MAIL";
    public static final String PROFIL_NAME = "profilName";
    public static final String LAT_GOOGLE = "lat";
    public static final String LON_GOOGLE = "lon";


    public static final int FFINISH_INFO = 1;
    public static final String UPD_LOCALE = "upd_locale";

    public static final String PATH_DELETE_CARD_METOD = "delete_card";

    public static final int PERMISSION_REQUEST_LOCATION = 1;

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    public static final int PERMISSION_REQUEST_FOTO = 5;

    public static final int DISTANSE_BASE_DISABLED = 10;

    public static final String PUSH = "push_data";


    public static final String REFERRER_CLIENT = "referrer";
    public static final int REFERRER_CLIENT_INVITE = 27;
    public static final int SEND_REFERAL_LINKS_RESAULT = 4;


    public static final int VENDOR_FSCO = 0;
    public static final int VENDOR_FCS = 1;
    public static final int VENDOR_FROUTE = 2;

    public static final int TYPE_NAME = 1;
    public static final int TYPE_MAIL = 2;

    public static final String IS_PROFIL = "isProfil";
    public static final String IS_RESTART = "isRestart";

    //  константы запросов

    public static final String PATH_FIND_DISPATCHER_CALL = "/api/client/mobile/1.0/dispatcher-call";
    public static final String PATH_ADD_CARD = "/api/client/mobile/1.0/payment-methods";
    public static final String REG_TOKEN_CLIENT = "TokenClient";
    public static final String PATH_HISTORY = "/api/client/mobile/2.0/history";
    public static final String PATH_LIST_ADRESS_LAT_LON = "/api/client/mobile/2.0/address/nearest";
    public static final String PATH_LIST_ADRESS_GEOCODING = "/api/client/mobile/1.0/address/geocoding";
    public static final String PATH_LINES_INFO = "/api/client/mobile/1.0/orders/{id}/path";
    public static final String PATH_BONUSES = "/api/client/mobile/2.1/bonuses";

    public static final String PATH_REG_PHONE = "/api/client/mobile/1.0/registration/submit";
    public static final String PATH_REG_CODE = "/api/client/mobile/1.0/registration/confirm";
    public static final String PATH_GET_CALL_OR_SMS = "/api/client/mobile/1.0/registration/resubmit";
    public static final String PATH_FCM = "/api/client/mobile/1.0/registration/fcm";
    public static final String PATH_ORDER_INFO = "/api/client/mobile/2.2/orders/{id}";
    public static final String PATH_PAYMENT_METHOD = "/api/client/mobile/1.0/payment-methods";
    public static final String PATH_ESTIMATE = "/api/client/mobile/2.0/estimate";
    public static final String PATH_FIND_SERVICE = "/api/client/mobile/1.1/service";
    public static final String PATH_FIND_ORDERS = "/api/client/mobile/4.0/orders";
    public static final String PATH_ORDERS_CURRENT = "/api/client/mobile/2.0/orders";
    public static final String PATH_DRIVERS = "/api/client/mobile/2.0/drivers";
    public static final String PATH_LIST_COUNTRY = "/api/client/mobile/1.0/countries";
   // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций-по-дням)
    public static final String PATH_GET_LP_BY_DAY = "/api/client/mobile/1.2/loyalty-program/transactions/by-day";

    public static final String PATH_GET_LP_TRANSACTION = "/api/client/mobile/1.2/loyalty-program/transactions/seq";
    public static final String PATH_GET_HISTORY_LIST = "/api/client/mobile/1.2/loyalty-program/transactions/seq-detailed";
    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности
    public static final String PATH_FIND_REFERRAL_DATA = "/api/client/mobile/1.2/loyalty-program";
    public static final String PATH_REG_REFERRAL= "/api/client/mobile/1.0/loyalty-program";
    public static final String PATH_FIND_GET_ACCOUNT = "/api/client/mobile/1.0/account";
    public static final String PATH_TIME_SYNCHRONIZATION = "/api/client/mobile/1.0/time";
    public static final String PATH_FIND_DELETE = "/api/client/mobile/1.0/orders/{id}";
    public static final String PATH_FIND_REQUEST_DRIVER_CALL = "/api/client/mobile/1.0/orders/{id}/request-driver-call";
    public static final String PATH_FIND_SET_PROLONGATION = "/api/client/mobile/1.0/orders/{id}/prolong";
    public static final String PATH_DEBET_CARD = "/api/client/mobile/1.0/debt-paid";
    public static final String PATH_DELETE_CARD = "/api/client/mobile/1.0/payment-methods/{card_id}";
    public static final String PATH_FIND_EDIT_SUBMISSION_DETAILS = "/api/client/mobile/1.0/orders/{id}/submission-details";
    public static final String PATH_FIND_FIX_COST = "/api/client/mobile/1.0/orders/{id}/fix-cost";
    public static final String PATH_FIND_EDIT_PAYMENT_METHOD = "/api/client/mobile/1.0/orders/{id}/payment-method";
    public static final String PATH_FIND_EDIT_OPTIONS = "/api/client/mobile/1.0/orders/{id}/options";
    public static final String PATH_FIND_EDIT_COMMENTS = "/api/client/mobile/1.0/orders/{id}/comment";
    public static final String PATH_FIND_OK_STATUS_WAIT = "/api/client/mobile/1.0/orders/{id}/coming";


}
