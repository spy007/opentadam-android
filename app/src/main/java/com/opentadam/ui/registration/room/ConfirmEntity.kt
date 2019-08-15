package com.opentadam.ui.registration.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = RoomConfig.TABLE_CONFIRM)
data class ConfirmEntity(
        @PrimaryKey(autoGenerate = true) val _id: Int = 0,
        val id: Long = 0,
        val key: String) {

    companion object {
        fun create(id: Long, key: String): ConfirmEntity = ConfirmEntity(id = id, key = key)
    }
}