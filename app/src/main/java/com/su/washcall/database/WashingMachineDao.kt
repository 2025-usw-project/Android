// 경로: app/src/main/java/su/database/WashingMachineDao.kt
package su.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WashingMachineDao {

    // 세탁기 등록 (같은 ID면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(machine: WashingMachine)

    // 특정 세탁실의 모든 세탁기 조회
    @Query("SELECT * FROM washing_machine_table WHERE roomId = :roomId")
    fun getMachinesByRoom(roomId: String): Flow<List<WashingMachine>>

    // 세탁기 상태 업데이트
    @Query("UPDATE washing_machine_table SET status = :status, remainingTime = :remainingTime WHERE machineId = :machineId")
    suspend fun updateMachineStatus(machineId: Int, status: String, remainingTime: Int)

    // 모든 세탁기 조회 (관리자용)
    @Query("SELECT * FROM washing_machine_table")
    fun getAllMachines(): Flow<List<WashingMachine>>
}
