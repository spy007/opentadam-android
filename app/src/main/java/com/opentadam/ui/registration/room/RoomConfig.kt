package com.opentadam.ui.registration.room

class RoomConfig {
    companion object {

        const val DATABASE_NAME = "registration.db"

        const val TABLE_CLIENT = "Client"

        const val TABLE_CONFIRM = "Confirm"

        private const val SELECT_FROM = "SELECT * FROM "

        const val SELECT_CLIENTS = SELECT_FROM + TABLE_CLIENT

        const val SELECT_CONFIRM = SELECT_FROM + TABLE_CONFIRM
    }
}