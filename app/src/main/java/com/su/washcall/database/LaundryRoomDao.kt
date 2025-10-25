// 경로: app/src/main/java/com/su/washcall/database/LaundryDao.kt
package com.su.washcall.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryRoomDao {

    // --- 데이터 삽입/갱신 ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaundryRooms(rooms: List<LaundryRoom>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWashingMachines(machines: List<WashingMachine>)

    // --- 데이터 조회 ---
    // Flow를 사용하면 DB가 변경될 때마다 UI가 자동으로 업데이트됩니다. (권장)
    @Query("SELECT * FROM laundry_room_table ORDER BY room_name ASC")
    fun getAllLaundryRooms(): Flow<List<LaundryRoom>>

    @Query("SELECT * FROM washing_machine_table WHERE laundry_room_id = :roomId ORDER BY machine_name ASC")
    fun getWashingMachinesByRoom(roomId: Int): Flow<List<WashingMachine>>

    // --- LiveData를 사용하는 예시 (선택 가능) ---
    @Query("SELECT * FROM washing_machine_table WHERE laundry_room_id = :roomId ORDER BY machine_name ASC")
    fun getWashingMachinesByRoomAsLiveData(roomId: Int): LiveData<List<WashingMachine>>

    // --- ★★★ 웹소켓을 통한 상태 업데이트용 ★★★ ---
    @Query("UPDATE washing_machine_table SET status = :newStatus WHERE machine_id = :machineId")
    suspend fun updateMachineStatus(machineId: Int, newStatus: String)
}
