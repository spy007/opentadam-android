package com.contactskotlin.data.di

import com.opentadam.ui.registration.di.RoomModule
import com.opentadam.ui.registration.mvp.presenter.V2RegistrationPresenter
import com.opentadam.ui.registration.mvp.presenter.V2SmsCodePresenter
import dagger.Component
import javax.inject.Singleton


@Component(modules = arrayOf(RemoteModule::class, RoomModule::class))
@Singleton
interface AppComponent {
    fun inject(presenter: V2SmsCodePresenter)

    fun inject(presenter: V2RegistrationPresenter)
}