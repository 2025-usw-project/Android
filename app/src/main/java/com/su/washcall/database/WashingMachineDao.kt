// 경로: app/src/main/java/com/su/washcall/database/LaundryDao.kt
package com.su.washcall.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryDao {

    // --- 데이터 삽입/갱신 ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaundryRooms(rooms: List<LaundryRoom>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWashingMachines(machines: List<WashingMachine>)


    // --- 데이터 조회 ---

    /**
     * 특정 세탁실의 모든 세탁기 정보를 가져옵니다. (화면에 보여줄 때 사용)
     * Flow를 사용하여 데이터가 변경될 때마다 UI가 자동으로 갱신되도록 합니다.
     */
    @Query("SELECT * FROM washing_machine_table WHERE laundry_room_id = :roomId")
    fun getWashingMachinesByRoom(roomId: Int): Flow<List<WashingMachine>>

    /**
     * 모든 세탁실 목록을 가져옵니다. (세탁실 선택 화면에서 사용)
     */
    @Query("SELECT * FROM laundry_room_table")
    fun getAllLaundryRooms(): Flow<List<LaundryRoom>>


    // --- 데이터 삭제 ---

    @Query("DELETE FROM laundry_room_table")
    suspend fun clearAllLaundryRooms()

    @Query("DELETE FROM washing_machine_table")
    suspend fun clearAllWashingMachines()
}
