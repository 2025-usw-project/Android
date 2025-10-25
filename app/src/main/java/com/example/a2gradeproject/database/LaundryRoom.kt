package com.example.a2gradeproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laundry_room_table")
data class LaundryRoom(
    @PrimaryKey
    val id: String, // 세탁실 고유 ID
    val location: String, // 위치
    val isAvailable: Boolean // 사용 가능 여부
)