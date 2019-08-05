package com.contactskotlin.data.di

import com.opentadam.ui.registration.mvp.presenter.V2RegistrationPresenter
import dagger.Component
import javax.inject.Singleton


@Component(modules = arrayOf(RemoteModule::class))
@Singleton
interface AppComponent {
    fun inject(presenter: V2RegistrationPresenter)
}