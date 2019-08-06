package com.opentadam.ui.registration.di

import com.opentadam.App
import com.opentadam.ui.registration.room.RoomDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideRoomDataSource() =
            RoomDataSource.buildDatabase(App.app.applicationContext)
}