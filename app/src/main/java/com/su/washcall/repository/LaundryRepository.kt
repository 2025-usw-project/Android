// 파일 경로: app/src/main/java/com/su/washcall/repository/LaundryRepository.kt
package com.su.washcall.repository

import android.util.Log
import android.view.PixelCopy.request
import com.su.washcall.database.LaundryRoom
import com.su.washcall.database.LaundryRoomDao
import com.su.washcall.database.WashingMachine
import com.su.washcall.network.ApiService
import com.su.washcall.network.washmachinRequest.AdminAddDeviceRequest
import com.su.washcall.network.washmachinRequest.LoadDataRequest
import com.su.washcall.network.washmachinResponse.LoadDataResponse
import kotlinx.coroutines.flow.Flow
import java.io.IOException


class LaundryRepository(
    private val apiService: ApiService,
    private val laundryRoomDao: LaundryRoomDao
) {

    // [로그 추가] MainActivity가 처음 생성될 때 이 데이터를 관찰(Observe) 시작
    val allLaundryRooms: Flow<List<LaundryRoom>> = laundryRoomDao.getAllLaundryRooms()

    // [로그 추가] MainActivity에서 특정 세탁실을 클릭하여 WashingMachineActivity로 넘어갈 때 호출
    fun getWashingMachinesByRoom(roomId: Int): Flow<List<WashingMachine>> {
        Log.d("LaundryRepository", "[호출] getWashingMachinesByRoom(roomId: $roomId)")
        return laundryRoomDao.getWashingMachinesByRoom(roomId)
    }

    // [로그 추가] MainActivity에서 데이터를 새로고침할 때 호출
    suspend fun refreshAllDataFromServer(request: LoadDataRequest) { // ✅ 파라미터를 LoadDataRequest로 변경
        Log.d("LaundryRepository", "[호출] refreshAllDataFromServer - /load API 요청 시작")
        try {
            // ✅ ViewModel에서 받은 request 객체를 그대로 전달
            val response = apiService.loadInitialData(request)

            if (response.isSuccessful) {
                // ... (성공 로직은 동일)
            } else {
                // ... (실패 로직은 동일)
            }
        } catch (e: Exception) {
            // ... (예외 로직은 동일)
        }
    }

    private fun convertServerDataToDbEntities(serverData: List<LoadDataResponse>): Pair<List<LaundryRoom>, List<WashingMachine>> {
        // ... 이 함수는 내부 로직이므로 별도 로그는 생략 ...
        val laundryRooms = serverData.map { roomData ->
            LaundryRoom(roomId = roomData.roomId, roomName = roomData.roomName)
        }

        val washingMachines = serverData.flatMap { roomData ->
            roomData.machines.map { machine ->
                WashingMachine(
                    machineId = machine.machineId,
                    laundryRoomId = roomData.roomId,
                    machineName = machine.machineName,
                    status = machine.status
                )
            }
        }
        return Pair(laundryRooms, washingMachines)
    }

    // [로그 추가] 관리자 화면에서 '세탁기 추가' 버튼을 눌렀을 때 호출
    suspend fun addDevice(request: AdminAddDeviceRequest) { // ✅ 파라미터를 request 객체 하나만 받도록 수정
        Log.d("LaundryRepository", "[호출] addDevice - /admin/add_device API 요청 시작, 요청 데이터: $request")
        try {
            // ✅ ViewModel에서 받은 request 객체를 그대로 전달
            val response = apiService.addDevice(request)
            if (response.isSuccessful) {
                Log.d("LaundryRepository", "[성공] addDevice API 응답 성공 (코드: ${response.code()})")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Log.e("LaundryRepository", "[실패] addDevice API 응답 실패 (코드: ${response.code()}) - 메시지: $errorMsg")
                throw IOException("세탁기 추가 실패: ${response.code()} $errorMsg")
            }
        } catch (e: Exception) {
            Log.e("LaundryRepository", "[오류] addDevice 함수 실행 중 예외 발생", e)
            throw e
        }
    }

    // [로그 추가] 사용자 화면에서 '세탁실 구독' 버튼을 눌렀을 때 호출
    suspend fun subscribeToRoom(roomName: String, userSnum: String) {
        Log.d("LaundryRepository", "[호출] subscribeToRoom - /device_subscribe API 요청 시작, 파라미터: roomName=$roomName, userSnum=$userSnum")
        try {
            val response = apiService.subscribeToRoom(roomName, userSnum)
            if (response.isSuccessful) {
                Log.d("LaundryRepository", "[성공] subscribeToRoom API 응답 성공 (코드: ${response.code()})")
            } else {
                val errorBody = response.errorBody()?.string() ?: "알 수 없는 오류"
                Log.e("LaundryRepository", "[실패] subscribeToRoom API 응답 실패 (코드: ${response.code()}) - 메시지: $errorBody")
                throw Exception("API 호출 실패(코드: ${response.code()}): $errorBody")
            }
        } catch (e: Exception) {
            Log.e("LaundryRepository", "[오류] subscribeToRoom 함수 실행 중 예외 발생", e)
            throw e
        }
    }
}
