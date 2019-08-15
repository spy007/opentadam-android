package com.opentadam.ui.registration.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface ConfirmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfirm(confirm: ConfirmEntity)

    @Query(RoomConfig.SELECT_CONFIRM)
    fun getConfirm(): Flowable<List<ConfirmEntity>>
}