// 경로: app/src/main/java/com/su/washcall/repository/LaundryRepository.kt
package com.su.washcall.repository

import com.su.washcall.database.LaundryDao
import com.su.washcall.database.LaundryRoom
import com.su.washcall.database.WashingMachine
import com.su.washcall.network.ApiService
import com.su.washcall.network.washmachinResponse.LoadDataResponse
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * 세탁실/세탁기 데이터의 출처(네트워크, 로컬DB)를 관리하는 클래스.
 * ViewModel은 이 Repository를 통해서만 데이터에 접근합니다.
 *
 * @param laundryDao 로컬 DB에 접근하기 위한 DAO
 * @param apiService 원격 서버에 접근하기 위한 Retrofit 서비스
 */
class LaundryRepository(
    private val laundryDao: LaundryDao,
    private val apiService: ApiService
) {

    // --- 1. DB에서 데이터 읽기 ---

    // DB에 저장된 모든 세탁실 목록을 Flow 형태로 가져옵니다.
    // UI는 이 Flow를 관찰(collect)하여 데이터 변경을 실시간으로 감지할 수 있습니다.
    val allLaundryRooms: Flow<List<LaundryRoom>> = laundryDao.getAllLaundryRooms()

    // 특정 세탁실에 속한 세탁기 목록을 Flow 형태로 가져옵니다.
    fun getWashingMachinesByRoom(roomId: Int): Flow<List<WashingMachine>> {
        return laundryDao.getWashingMachinesByRoom(roomId)
    }


    // --- 2. 네트워크에서 데이터 가져와서 DB에 저장하기 ---

    /**
     * 서버의 `/load` API를 호출하여 모든 최신 데이터를 가져온 후,
     * 로컬 DB를 완전히 새로운 데이터로 업데이트합니다.
     * @param accessToken 사용자의 인증 토큰
     */
    suspend fun refreshAllDataFromServer(accessToken: String) {
        try {
            val response = apiService.loadInitialData(accessToken)

            if (response.isSuccessful) {
                val serverData = response.body() ?: return // 서버 데이터가 없으면 종료

                // 서버에서 받은 데이터를 DB 형식에 맞게 변환
                val (laundryRooms, washingMachines) = convertServerDataToDbEntities(serverData)

                // 변환된 데이터를 DB에 저장 (기존 데이터는 덮어쓰기)
                laundryDao.insertLaundryRooms(laundryRooms)
                laundryDao.insertWashingMachines(washingMachines)
            } else {
                // API 호출은 성공했으나, 서버에서 에러 응답(4xx, 5xx)을 보낸 경우
                throw IOException("Server error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            // 네트워크 연결 실패 등 통신 자체에 오류가 발생한 경우
            // 여기서 로그를 남기거나 사용자에게 에러 메시지를 보여줄 수 있습니다.
            e.printStackTrace()
            throw e // 예외를 상위 호출자(ViewModel)에게 다시 던져서 처리하도록 함
        }
    }

    /**
     * 서버에서 받은 응답 데이터(LoadDataResponse) 리스트를
     * Room DB에 저장할 엔티티(LaundryRoom, WashingMachine) 리스트로 변환합니다.
     */
    private fun convertServerDataToDbEntities(serverData: List<LoadDataResponse>): Pair<List<LaundryRoom>, List<WashingMachine>> {
        // 세탁실 ID를 기준으로 그룹화하여 중복 없는 세탁실 목록 생성
        val laundryRooms = serverData
            .distinctBy { it.roomName } // roomName이 같은 것들은 하나로 취급
            .map { LaundryRoom(roomId = findRoomIdByName(serverData, it.roomName), roomName = it.roomName) }

        // 세탁기 목록 생성
        val washingMachines = serverData.map {
            WashingMachine(
                machineId = it.machineId,
                laundryRoomId = findRoomIdByName(serverData, it.roomName),
                machineName = it.machineName,
                status = it.status
            )
        }
        return Pair(laundryRooms, washingMachines)
    }

    // room_name으로 room_id를 찾는 보조 함수 (서버 응답에 room_id가 없어서 임시방편으로 추가)
    // ※ 중요: 서버 응답(`LoadDataResponse`)에 `room_id`가 포함되면 이 함수와 로직은 더 단순해집니다.
    private fun findRoomIdByName(serverData: List<LoadDataResponse>, roomName: String): Int {
        // 같은 roomName을 가진 데이터 중 아무거나 하나를 찾아 machineId의 앞 부분을 ID로 사용 (임시 규칙)
        // 예를 들어, machine_id가 20101, 20102이면 room_id를 201로 간주
        val machineId = serverData.firstOrNull { it.roomName == roomName }?.machineId ?: 0
        return machineId / 100 // 예시 규칙
    }
}
