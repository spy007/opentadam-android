package com.opentadam.ui.registration.api

import com.opentadam.Constants
import com.opentadam.network.rest.SubmitRequest
import com.opentadam.network.rest.Submitted
import com.opentadam.ui.registration.api.model.ContactsDTO
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RemoteService {
    @GET("contacts")
    fun requestContacts(): Single<ContactsDTO>

    @POST(Constants.PATH_REG_PHONE)
    fun findPhone(@Body submitRequest: SubmitRequest): Single<Submitted>
}