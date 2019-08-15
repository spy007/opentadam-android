package com.opentadam.ui.registration.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClient(client: ClientEntity)

    @Query(RoomConfig.SELECT_CLIENTS)
    fun getClients(): Flowable<List<ClientEntity>>
}