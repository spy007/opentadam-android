package com.contactskotlin.data.di

import com.opentadam.network.RESTConnect
import com.opentadam.ui.registration.di.RoomModule
import dagger.Component
import javax.inject.Singleton


@Component(modules = arrayOf(RemoteModule::class, RoomModule::class))
@Singleton
interface AppComponent {
    fun inject(restConnect: RESTConnect)
}