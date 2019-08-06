package com.opentadam.ui.registration.api

import com.opentadam.Constants
import com.opentadam.network.rest.*
import retrofit.http.Header
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteService {
    @POST(Constants.PATH_REG_PHONE)
    fun findPhone(@Body submitRequest: SubmitRequest): Call<Submitted>

    @GET(Constants.PATH_GET_CALL_OR_SMS)
    fun reSubmit(@Query("id") id: Long,
                          @Query("confirmationType") confirmationType: String): Call<EmptyObject>

    @GET(Constants.PATH_REG_CODE)
    fun findConfirm(@Query("id") id: Long, @Query("code") code: String):
                    Call<Confirmed>

    @POST(Constants.PATH_FCM)
    fun findToken(@Header("Date") date: String,
                           @Header("Authentication") authentication: String,
                           @Body fsmInfo: FsmInfo): Call<EmptyObject>
}