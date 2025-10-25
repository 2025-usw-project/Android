// 경로: app/src/main/java/com/su/washcall/database/LaundryRoom.kt
package com.su.washcall.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laundry_room_table")
data class LaundryRoom(
    @PrimaryKey
    @ColumnInfo(name = "room_id")
    val roomId: Int,

    @ColumnInfo(name = "room_name")
    val roomName: String
)
