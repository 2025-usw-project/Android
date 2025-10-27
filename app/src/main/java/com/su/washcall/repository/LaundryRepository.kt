// 파일 경로: app/src/main/java/com/su/washcall/repository/LaundryRepository.kt
package com.su.washcall.repository

import android.util.Log
import com.su.washcall.database.LaundryRoom
import com.su.washcall.database.LaundryRoomDao
import com.su.washcall.database.WashingMachine
import com.su.washcall.network.ApiService
import com.su.washcall.network.washmachinRequest.AdminAddDeviceRequest
import com.su.washcall.network.washmachinResponse.LoadDataResponse
import kotlinx.coroutines.flow.Flow
import java.io.IOException


class LaundryRepository(
    private val apiService: ApiService,
    private val laundryRoomDao: LaundryRoomDao
) {

    val allLaundryRooms: Flow<List<LaundryRoom>> = laundryRoomDao.getAllLaundryRooms()

    fun getWashingMachinesByRoom(roomId: Int): Flow<List<WashingMachine>> {
        return laundryRoomDao.getWashingMachinesByRoom(roomId)
    }

    // /load API는 토큰이 필요 없는 공개 API로 간주하고, 파라미터 없이 호출합니다.
    suspend fun refreshAllDataFromServer() {
        try {
            // ApiService의 loadInitialData() 정의에 따라 파라미터 없이 호출
            val response = apiService.loadInitialData()

            if (response.isSuccessful) {
                val serverData = response.body() ?: return
                if (serverData.isNotEmpty()) {
                    // 올바른 데이터 변환 함수를 호출합니다.
                    val (laundryRooms, washingMachines) = convertServerDataToDbEntities(serverData)
                    Log.d("LaundryRepository", "DB에 저장할 데이터: ${laundryRooms.size}개의 세탁실, ${washingMachines.size}개의 세탁기")
                    laundryRoomDao.insertLaundryRooms(laundryRooms)
                    laundryRoomDao.insertWashingMachines(washingMachines)
                }
            } else {
                throw IOException("Server error on /load: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("LaundryRepository", "refreshAllDataFromServer 실패", e)
            throw e
        }
    }

    // 데이터 변환 로직을 서버 응답 구조에 맞게 수정
    private fun convertServerDataToDbEntities(serverData: List<LoadDataResponse>): Pair<List<LaundryRoom>, List<WashingMachine>> {
        // 1. 세탁실 목록 만들기
        val laundryRooms = serverData.map { roomData -> // ✅ 이제 Kotlin 기본 map으로 올바르게 동작
            LaundryRoom(roomId = roomData.roomId, roomName = roomData.roomName)
        }

        // 2. 모든 세탁기 목록 만들기
        val washingMachines = serverData.flatMap { roomData -> // ✅ 이제 Kotlin 기본 flatMap으로 올바르게 동작
            roomData.machines.map { machine ->
                WashingMachine(
                    machineId = machine.machineId,
                    laundryRoomId = roomData.roomId, // 세탁기가 속한 세탁실의 ID를 정확히 사용
                    machineName = machine.machineName,
                    status = machine.status
                )
            }
        }
        return Pair(laundryRooms, washingMachines)
    }

    // ViewModel에서 AdminAddDeviceRequest 객체를 만들어 토큰을 담아 전달하면 됩니다.
    suspend fun addDevice(request: AdminAddDeviceRequest) {
        try {
            val response = apiService.addDevice(request)
            if (!response.isSuccessful) {
                throw IOException("세탁기 추가 실패: ${response.code()} ${response.message()}")
            }
            Log.d("LaundryRepository", "addDevice 성공.")
        } catch (e: Exception) {
            Log.e("LaundryRepository", "addDevice 실패", e)
            throw e
        }
    }
}
