package com.su.washcall.repository

import com.su.washcall.database.LaundryRoomDao
import com.su.washcall.network.ApiService
import kotlinx.coroutines.flow.Flow
import com.su.washcall.database.LaundryRoom

/**
 * 데이터 소스(로컬 DB, 원격 서버)를 관리하고 조정하는 클래스입니다.
 * ViewModel은 이 Repository를 통해서만 데이터에 접근합니다.
 */
class LaundryRepository(
    private val laundryRoomDao: LaundryRoomDao,
    private val apiService: ApiService
) {

    // Room DB에 저장된 모든 세탁실 목록을 Flow 형태로 외부(ViewModel)에 노출합니다.
    val allLaundryRooms: Flow<List<LaundryRoom>> = laundryRoomDao.getAllLaundryRooms()

    /**
     * 서버에서 최신 세탁실 목록 데이터를 가져와 로컬 DB(Room)를 업데이트합니다.
     * @param token 인증을 위한 Bearer 토큰
     */
    suspend fun refreshAllDataFromServer(token: String) {
        try {
            // API를 호출하여 서버로부터 최신 세탁실 목록을 가져옵니다.
            val response = apiService.getAllLaundryRooms(token)

            if (response.isSuccessful) {
                // 응답이 성공적이면, 응답 본문(body)의 데이터를 가져옵니다.
                val laundryRoomsFromServer = response.body()
                if (!laundryRoomsFromServer.isNullOrEmpty()) {
                    // 기존의 로컬 데이터를 모두 삭제합니다.
                    laundryRoomDao.deleteAll()
                    // 서버에서 받은 새로운 데이터로 로컬 DB를 갱신합니다.
                    laundryRoomDao.insertAll(laundryRoomsFromServer)
                }
            } else {
                // API 호출이 실패한 경우 (예: 4xx, 5xx 에러)
                throw Exception("서버 응답 실패: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            // 네트워크 오류 등 API 호출 중 예외가 발생한 경우
            throw Exception("데이터 새로고침 실패: ${e.message}")
        }
    }
}
