// 파일 경로: app/src/main/java/com/su/washcall/viewmodel/AdminViewModel.kt
package com.su.washcall.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.su.washcall.database.AppDatabase
import com.su.washcall.database.LaundryRoom
import com.su.washcall.network.ApiService
import com.su.washcall.network.RetrofitClient
import com.su.washcall.network.washmachinRequest.AddRoomRequest
import com.su.washcall.network.washmachinRequest.AdminAddDeviceRequest
import com.su.washcall.network.washmachinRequest.AdminMachinListRequest
import com.su.washcall.network.washmachinResponse.MachineInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// RegisterResult를 클래스 밖, 파일 최상단 레벨로 이동
sealed class RegisterResult {
    data class Success(val message: String) : RegisterResult()
    data class Failure(val message: String) : RegisterResult()
    object Loading : RegisterResult()
}

// MachineListResult를 클래스 밖, 파일 최상단 레벨로 이동
sealed class MachineListResult {
    data class Success(val machines: List<MachineInfo>) : MachineListResult()
    data class Failure(val message: String) : MachineListResult()
    object Loading : MachineListResult()
}

private const val TAG = "AdminViewModel_LOG"

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val laundryRoomDao = db.laundryRoomDao()
    private val apiService: ApiService = RetrofitClient.instance

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _machineListResult = MutableLiveData<MachineListResult>()
    val machineListResult: LiveData<MachineListResult> = _machineListResult

    private val _addRoomResult = MutableLiveData<RegisterResult>()
    val addRoomResult: LiveData<RegisterResult> = _addRoomResult

    // --- 세탁기 등록 함수 ---
    fun registerNewMachine(roomId: Int, machineId: Int, machineName: String) {
        viewModelScope.launch {
            _registerResult.value = RegisterResult.Loading

            val token = getToken()
            if (token.isNullOrEmpty()) {
                _registerResult.value = RegisterResult.Failure("로그인 토큰이 없습니다. 다시 로그인해주세요.")
                return@launch
            }

            try {
                // ✅ [수정 완료] AdminAddDeviceRequest의 토큰 필드 이름은 access_token이 맞습니다.
                val request = AdminAddDeviceRequest(
                    access_token = token,
                    roomId = roomId,
                    machineId = machineId,
                    machineName = machineName
                )

                val response = apiService.addDevice(request)

                if (response.isSuccessful) {
                    _registerResult.value = RegisterResult.Success("세탁기가 성공적으로 등록되었습니다.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "등록 실패 (HTTP ${response.code()})"
                    _registerResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    // --- 세탁실 추가 함수 ---
    fun addNewLaundryRoom(roomName: String) {
        viewModelScope.launch {
            _addRoomResult.value = RegisterResult.Loading
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _addRoomResult.value = RegisterResult.Failure("로그인이 필요합니다.")
                return@launch
            }

            try {
                // ▼▼▼ [핵심 수정] AddRoomRequest 생성 시, 토큰 필드 이름을 서버와 약속된 'access_token'으로 사용합니다. ▼▼▼
                val request = AddRoomRequest(access_token = token, roomName = roomName)
                // ▲▲▲ [핵심 수정] ▲▲▲

                val response = apiService.addLaundryRoom(request)

                if (response.isSuccessful && response.body() != null) {
                    val receivedRoomId = response.body()!!.roomId
                    val newLaundryRoom = LaundryRoom(roomId = receivedRoomId, roomName = roomName)

                    withContext(Dispatchers.IO) {
                        laundryRoomDao.insert(newLaundryRoom)
                    }

                    _addRoomResult.value = RegisterResult.Success("새로운 세탁실 등록 성공! (ID: $receivedRoomId, 이름: $roomName)")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "세탁실 등록 실패 (Code: ${response.code()})"
                    _addRoomResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _addRoomResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    // --- 관리자용 세탁기 목록 조회 함수 ---
    fun loadMachineList(roomId: Int) {
        viewModelScope.launch {
            _machineListResult.value = MachineListResult.Loading
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _machineListResult.value = MachineListResult.Failure("로그인이 필요합니다.")
                return@launch
            }

            try {
                // ✅ [수정 완료] AdminMachinListRequest의 토큰 필드 이름은 access_token이 맞습니다.
                val request = AdminMachinListRequest(access_token = token, room_id = roomId)
                val response = apiService.getAdminMachineList(request)

                if (response.isSuccessful && response.body() != null) {
                    _machineListResult.value = MachineListResult.Success(response.body()!!.machine_list)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "세탁기 목록을 불러오는데 실패했습니다."
                    _machineListResult.value = MachineListResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _machineListResult.value = MachineListResult.Failure("서버 연결에 실패했습니다: ${e.message}")
            }
        }
    }

    // --- 토큰을 가져오는 함수 (모든 API 호출 전에 사용) ---
    private fun getToken(): String? {
        try {
            val context = getApplication<Application>().applicationContext
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            // LoginActivity에서 저장한 곳과 '파일 이름'과 '키 이름'이 완전히 동일합니다.
            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "auth_prefs", // 파일 이름
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            // "access_token" 키로 토큰을 읽어옵니다.
            return sharedPreferences.getString("access_token", null)
        } catch (e: Exception) {
            Log.e(TAG, "[getToken] 암호화된 토큰 읽기 오류", e)
            return null
        }
    }
}
