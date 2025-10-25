package com.su.washcall.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(laundryRoom: LaundryRoom) // ◀ suspend 없음!

    @Query("SELECT * FROM laundry_room_table")
    fun getAllLaundryRooms(): Flow<List<LaundryRoom>>
}