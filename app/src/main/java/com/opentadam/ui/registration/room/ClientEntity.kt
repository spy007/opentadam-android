package com.opentadam.ui.registration.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = RoomConfig.TABLE_CLIENT)
data class ClientEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        var clientId: Long) {

    companion object {
        fun create(clientId: Long): ClientEntity = ClientEntity(clientId = clientId)
    }
}