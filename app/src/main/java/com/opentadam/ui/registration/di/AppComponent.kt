package com.contactskotlin.data.di

import com.opentadam.network.RESTConnect
import dagger.Component
import javax.inject.Singleton


@Component(modules = arrayOf(RemoteModule::class))
@Singleton
interface AppComponent {
    fun inject(restConnect: RESTConnect)
}