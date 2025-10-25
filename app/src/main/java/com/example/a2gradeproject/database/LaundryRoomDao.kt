package com.example.a2gradeproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryRoomDao {
    // DB에 데이터 삽입/수정 (suspend 키워드 사용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(laundryRoom: LaundryRoom)

    // DB 데이터 실시간 구독 (Flow 사용)
    @Query("SELECT * FROM laundry_room_table")
    fun getAllLaundryRooms(): Flow<List<LaundryRoom>>
}