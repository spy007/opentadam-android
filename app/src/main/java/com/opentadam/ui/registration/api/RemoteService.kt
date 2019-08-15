package com.opentadam.ui.registration.api

import com.opentadam.Constants
import com.opentadam.network.rest.*
import io.reactivex.Single
import retrofit2.http.*

interface RemoteService {
    @POST(Constants.PATH_REG_PHONE)
    fun findPhone(@Body submitRequest: SubmitRequest): Single<Submitted>

    @GET(Constants.PATH_GET_CALL_OR_SMS)
    fun reSubmit(@Query("id") id: Long,
                          @Query("confirmationType") confirmationType: String): Single<EmptyObject>

    @GET(Constants.PATH_REG_CODE)
    fun findConfirm(@Query("id") id: Long, @Query("code") code: String):
            Single<Confirmed>

    @POST(Constants.PATH_FCM)
    fun findToken(@Header("Date") date: String,
                           @Header("Authentication") authentication: String,
                           @Body fsmInfo: FsmInfo): Single<EmptyObject>
}