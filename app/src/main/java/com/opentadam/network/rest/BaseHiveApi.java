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

package com.opentadam.network.rest;


import com.opentadam.Constants;
import com.opentadam.network.model.ParamsRegRef;
import com.opentadam.ui.frends.referal.Result;
import com.opentadam.ui.frends.referal.ResultDayStat;
import com.opentadam.ui.frends.referal.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

public interface BaseHiveApi {

    // запрос звонка диспетчера
    @GET(Constants.PATH_FIND_DISPATCHER_CALL)
    void findDispatcherCall(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Query("phone") String phone,
            Callback<EmptyObject> response);


    @POST(Constants.PATH_FCM)
    void findToken(@Header("Date") String date,
                   @Header("Authentication") String authentication,
                   @Body FsmInfo fsmInfo,
                   Callback<EmptyObject> response);

    @GET(Constants.PATH_GET_CALL_OR_SMS)
    void reSubmit(@Query("id") long id,
                  @Query("confirmationType") String confirmationType,
                  Callback<EmptyObject> response);

    @GET(Constants.PATH_TIME_SYNCHRONIZATION)
    void timeSynchronization(Callback<String> response);

    // новый адрес по координатам
    @GET(Constants.PATH_LIST_ADRESS_LAT_LON)
    void findAddressGeocode(Callback<List<Address>> response);

    // Справочник стран
    @GET(Constants.PATH_LIST_COUNTRY)
    void findCountries(Callback<List<Country>> response);

    // полнотекстовый поиск
    @GET(Constants.PATH_LIST_ADRESS_GEOCODING)
    void findFFullTextSearch(@Query("query") String query,
                             Callback<List<Address>> response);

    //
    @POST(Constants.PATH_REG_PHONE)
    void findPhone(@Body SubmitRequest submitRequest,
                   Callback<Submitted> response);

    @GET(Constants.PATH_REG_CODE)
    void findConfirm(@Query("id") long id, @Query("code") String code,
                     Callback<Confirmed> response);


    @DELETE(Constants.PATH_FIND_DELETE)
    void findDelete(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            Callback<DeleteRoute> response);


    @GET(Constants.PATH_FIND_REQUEST_DRIVER_CALL)
    void findRequestDriverCall(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id, Callback<EmptyObject> response);

    @GET(Constants.PATH_LINES_INFO)
    void findGetLinesInfo(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id, Callback<LinesInfo> response);

    //продлить время ожидания
    @GET(Constants.PATH_FIND_SET_PROLONGATION)
    void findSetProlongation(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id, @Query("minutes") int minutes,
            Callback<Prolongation> response);


    //*******https://github.com/HIVETAXI/client-mobile-api/wiki/Получение-списка-заказов
    @GET(Constants.PATH_ORDERS_CURRENT)
    void findGetShortOrderCurrent(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<ArrayList<ShortOrderInfo>> response);

    // Получение доступных бонусов
    @GET(Constants.PATH_BONUSES)
    void findGetBonuses(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<Bonuses> response);

    // История поездок
    @GET(Constants.PATH_HISTORY)
    void findGetShortOrderHistory(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Query("offset") int offset,
            @Query("length") int length,
            Callback<ArrayList<ShortOrderInfo>> response);

    // ********** Feature #8623 Способ оплаты - контрагент

    @GET(Constants.PATH_PAYMENT_METHOD)
    void findPaymentMethod(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<List<PaymentMethod>> callback);

    // ******** Предварительная-оценка-заказа
    @POST(Constants.PATH_ESTIMATE)
    void findEstimation(
            @Header("Date") String date
            , @Header("Authentication") String authentication,
            @Body Params params
            , Callback<Estimation> callback);

    // *** https://github.com/HIVETAXI/client-mobile-api/wiki/Информация-о-предоставляемом-сервисе
    @POST(Constants.PATH_FIND_SERVICE)
    void findService(
            @Header("Date") String date
            , @Header("Authentication") String authentication,
            @Body Params params
            , Callback<Service> callback);

    // **** https://github.com/HIVETAXI/client-mobile-api/wiki/Создание-заказа
    @POST(Constants.PATH_FIND_ORDERS)
    void findOrders(
            @Header("Date") String date
            , @Header("Authentication") String authentication
            , @Body SendCreateRequest params
            , Callback<ResultSendOrders> callback);

    // *****https://github.com/HIVETAXI/client-mobile-api/wiki/Получение-подробностей-по-заказу
    @GET(Constants.PATH_ORDER_INFO)
    void findGetOrderInfo(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id, Callback<OrderInfo> response);

    // *** https://github.com/HIVETAXI/client-mobile-api/wiki/Ближайшие-водители
    @POST(Constants.PATH_DRIVERS)
    void findDrivers(
            @Header("Date") String date
            , @Header("Authentication") String authentication
             , @Body Params params,
            Callback<List<Driver>> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций-по-дням)
    @GET(Constants.PATH_GET_LP_BY_DAY)
    void findLoyaltyProgramByDay(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<ArrayList<ResultDayStat>> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций)
    @GET(Constants.PATH_GET_LP_TRANSACTION)
    void findLoyaltyProgramTransactions(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Query("oft") Long oft,
            Callback<ArrayList<Transaction>> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Список-транзакций,-детальный)
    @GET(Constants.PATH_GET_HISTORY_LIST)
    void findLoyaltyProgramByDayLis(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Query("oft") Long oft,
            Callback<ArrayList<Transaction>> callback);

    @POST(Constants.PATH_ADD_CARD)
    void findAddCard(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Body EmptyObject params,
            Callback<CardAdditionRef> callback);

    @DELETE(Constants.PATH_DELETE_CARD)
    void deleteCard(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("card_id") long id,
            Callback<DeleteCard> response);

    @GET(Constants.PATH_DEBET_CARD)
    void debetCard(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Query("cardId") long cardId,
            Callback<EmptyObject> response);

    @GET(Constants.PATH_FIND_GET_ACCOUNT)
    void findGetAccount(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<AccountState> response);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности
    @GET(Constants.PATH_FIND_REFERRAL_DATA)
    void findReferralData(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            Callback<Result> response);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-деталей-адреса-подачи-заказа
    @POST(Constants.PATH_FIND_EDIT_SUBMISSION_DETAILS)
    void findEditSubmissionDetails(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            @Body ParamsEditSubmissionDetails params,
            Callback<EmptyObject> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Фиксация-стоимости-заказа
    @GET(Constants.PATH_FIND_FIX_COST)
    void findFixCost(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            @Query("amount") float amount,
            Callback<EmptyObject> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-способа-оплаты-в-заказе
    @POST(Constants.PATH_FIND_EDIT_PAYMENT_METHOD)
    void findEditPaymentMethod(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            @Body EditPaymentMethod params,
            Callback<EmptyObject> callback);


    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-опций-заказа
    @POST(Constants.PATH_FIND_EDIT_OPTIONS)
    void findEditOptions(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            @Body OptionsList params,
            Callback<EmptyObject> callback);


    // https://github.com/HIVETAXI/client-mobile-api/wiki/Редактирование-комментария-к-заказу
    @POST(Constants.PATH_FIND_EDIT_COMMENTS)
    void findEditComments(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            @Body ParamsComments params,
            Callback<EmptyObject> callback);

    @GET(Constants.PATH_FIND_OK_STATUS_WAIT)
    void findOkStatusWait(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @retrofit.http.Path("id") long id,
            Callback<EmptyObject> callback);

    // https://github.com/HIVETAXI/client-mobile-api/wiki/Программа-лояльности-(Регистрация-в-программе-лояльности)
    @POST(Constants.PATH_REG_REFERRAL)
    void findRegReferal(
            @Header("Date") String date,
            @Header("Authentication") String authentication,
            @Body ParamsRegRef params,
            Callback<EmptyObject> callback);
}
