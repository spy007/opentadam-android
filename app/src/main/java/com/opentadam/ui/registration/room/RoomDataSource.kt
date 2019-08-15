package com.opentadam.ui.registration.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(ClientEntity::class, ConfirmEntity::class), version = 1)
abstract class RoomDataSource : RoomDatabase() {

    abstract fun clientDao(): ClientDao

    abstract fun confirmDao(): ConfirmDao

    companion object {
        fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RoomDataSource::class.java, RoomConfig.DATABASE_NAME)
                        .build()
    }
}