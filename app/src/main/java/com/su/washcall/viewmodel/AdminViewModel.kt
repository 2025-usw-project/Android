// 경로: app/src/main/java/com/su/washcall/viewmodel/AdminViewModel.kt
package com.su.washcall.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.su.washcall.network.ApiService
import com.su.washcall.network.RetrofitClient
import com.su.washcall.network.washmachinRequest.*
import kotlinx.coroutines.launch
import com.su.washcall.network.washmachinResponse.MachineInfo

// 서버 응답 결과를 표현하기 위한 클래스
sealed class RegisterResult {
    data class Success(val message: String) : RegisterResult()
    data class Failure(val message: String) : RegisterResult()
    object Loading : RegisterResult()
}

sealed class MachineListResult {
    data class Success(val machines: List<MachineInfo>) : MachineListResult()
    data class Failure(val message: String) : MachineListResult()
    object Loading : MachineListResult()
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient.instance

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _machineListResult = MutableLiveData<MachineListResult>()
    val machineListResult: LiveData<MachineListResult> = _machineListResult

    private val _addRoomResult = MutableLiveData<RegisterResult>()
    val addRoomResult: LiveData<RegisterResult> = _addRoomResult
    /**
     * 🔹 서버로 세탁기 정보를 전송하는 함수 (서버 명세에 맞게 수정)
     * roomId와 machineName 파라미터를 추가합니다.
     */
    fun registerNewMachine(roomId: Int, machineId: Int, machineName: String) {
        viewModelScope.launch {
            _registerResult.value = RegisterResult.Loading // 1. 로딩 상태 시작

            // 2. 토큰 가져오기 및 유효성 검사
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _registerResult.value = RegisterResult.Failure("로그인 토큰이 없습니다. 다시 로그인해주세요.")
                return@launch
            }

            try {
                // 3. 서버에 보낼 요청 객체 생성
                val request = AdminAddDeviceRequest(
                    roomId = roomId,
                    machineId = machineId,
                    machineName = machineName
                )

                // 4. 서버 API 호출 및 response 변수 선언
                val response = apiService.adminAddDevice("Bearer $token", request)

                // 5. 응답 결과 처리
                if (response.isSuccessful) {
                    _registerResult.value = RegisterResult.Success("세탁기가 성공적으로 등록되었습니다.")
                    // ★★★ 등록 성공 후, 해당 세탁실의 목록을 다시 불러옵니다. ★★★
                    loadMachineList(roomId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "등록 실패 (HTTP ${response.code()})"
                    _registerResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                // 6. 네트워크 오류 등 예외 처리
                _registerResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    /**
     * 🔹 [추가] 서버에서 세탁기 목록을 불러오는 함수
     */
    fun loadMachineList(roomId: Int) {
        viewModelScope.launch {
            _machineListResult.value = MachineListResult.Loading
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _machineListResult.value = MachineListResult.Failure("로그인이 필요합니다.")
                return@launch
            }

            try {
                val response = apiService.getMachineList("Bearer $token", roomId)
                if (response.isSuccessful) {
                    _machineListResult.value = MachineListResult.Success(response.body() ?: emptyList())
                } else {
                    val errorMsg = "목록 로딩 실패 (HTTP ${response.code()})"
                    _machineListResult.value = MachineListResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _machineListResult.value = MachineListResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    fun addNewLaundryRoom(roomName: String) { // 파라미터를 roomName 하나만 받도록 변경
        viewModelScope.launch {
            _addRoomResult.value = RegisterResult.Loading
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _addRoomResult.value = RegisterResult.Failure("로그인이 필요합니다.")
                return@launch
            }

            try {
                // ★★★ roomName만 사용하여 요청 객체 생성 ★★★
                val request = AddRoomRequest(roomName = roomName)
                val response = apiService.addLaundryRoom("Bearer $token", request)

                if (response.isSuccessful) {
                    _addRoomResult.value = RegisterResult.Success("새로운 세탁실이 등록되었습니다.")
                    // 참고: 여기서 사용자 화면의 세탁실 목록을 새로고침 하도록
                    // 전체 목록 조회 API를 다시 호출하는 로직을 추가하면 좋습니다.
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "세탁실 등록 실패"
                    _addRoomResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _addRoomResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "auth_prefs",
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString("jwt_token", null)
    }
}
