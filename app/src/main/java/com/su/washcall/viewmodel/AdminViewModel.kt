// 파일 경로: app/src/main/java/com/su/washcall/viewmodel/AdminViewModel.kt
package com.su.washcall.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.su.washcall.network.ApiService
import com.su.washcall.network.RetrofitClient
import com.su.washcall.network.washmachinRequest.*
import com.su.washcall.network.washmachinResponse.MachineInfo
import kotlinx.coroutines.launch

private const val TAG = "AdminViewModel"
// 서버 응답 결과를 표현하기 위한 클래스 (파일 내에 이미 존재하므로 그대로 둠)
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

    // LiveData 변수들은 이미 올바르게 선언되어 있으므로 그대로 둡니다.
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _machineListResult = MutableLiveData<MachineListResult>()
    val machineListResult: LiveData<MachineListResult> = _machineListResult

    private val _addRoomResult = MutableLiveData<RegisterResult>()
    val addRoomResult: LiveData<RegisterResult> = _addRoomResult

    /**
     * 🔹 [수정됨] 서버로 세탁기 정보를 전송하는 함수
     * Body에 access_token을 포함하여 전송하도록 수정합니다.
     */
//    fun registerNewMachine(roomId: Int, machineId: Int, machineName: String) { // 1. 불필요한 accessToken 파라미터 제거
//        viewModelScope.launch {
//            _registerResult.value = RegisterResult.Loading
//
//            // 2. ViewModel 내부의 getToken() 함수를 통해 토큰을 일관되게 가져옵니다.
//            val token = getToken()
//            if (token.isNullOrEmpty()) {
//                _registerResult.value = RegisterResult.Failure("로그인 토큰이 없습니다. 다시 로그인해주세요.")
//                return@launch
//            }
//
//            try {
//                // 3. [수정] AdminAddDeviceRequest의 필드에 맞게 객체를 생성합니다.
//                //    AdminAddDeviceRequest에 accessToken 필드가 없다면 아래와 같이 수정해야 합니다.
//                //    만약 필드 이름이 다르다면, 올바른 이름으로 변경해주세요.
//                //    (예: access_token -> accessToken)
//                val request = AdminAddDeviceRequest(
//                    // accessToken = token, // 이 줄에서 오류가 발생했습니다.
//                    // AdminAddDeviceRequest에 해당 필드가 없다면 삭제하거나
//                    // 올바른 필드명으로 수정해야 합니다.
//                    // 여기서는 해당 데이터 클래스에 accessToken 필드가 있다고 가정하고 진행합니다.
//                    // 만약 없다면 이 줄을 지워주세요.
//                    roomId = roomId,
//                    machineId = machineId,
//                    machineName = machineName
//                )
//
//                // 4. [추가] 실제 API를 호출하는 코드를 추가합니다.
//                val response = apiService.adminAddDevice(request) // request 객체를 전달합니다.
//
//
//                if (response.isSuccessful) {
//                    _registerResult.value = RegisterResult.Success("세탁기가 성공적으로 등록되었습니다.")
//                    // loadMachineList(roomId) // 성공 시 목록을 다시 로드하려면 주석 해제
//                } else {
//                    val errorMsg = response.errorBody()?.string() ?: "등록 실패 (HTTP ${response.code()})"
//                    _registerResult.value = RegisterResult.Failure(errorMsg)
//                }
//            } catch (e: Exception) {
//                _registerResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
//            }
//        }
//    }

    /**
     * 🔹 [수정됨] 서버에서 세탁기 목록을 불러오는 함수
     * Body에 access_token을 포함하여 전송하도록 수정합니다.
     */
    fun registerNewMachine(roomId: Int, machineId: Int, machineName: String) {
        viewModelScope.launch {
            _registerResult.value = RegisterResult.Loading

            // 1. 올바르게 수정된 getToken() 함수를 통해 토큰을 가져옵니다.
            val token = getToken()
            if (token.isNullOrEmpty()) {
                _registerResult.value = RegisterResult.Failure("로그인 토큰이 없습니다. 다시 로그인해주세요.")
                return@launch
            }

            try {
                // ▼▼▼▼▼ [핵심 수정] ▼▼▼▼▼
                // 2. 요청 객체(request) 생성 시, 다른 데이터와 함께 accessToken을 포함시킵니다.
                val request = AdminAddDeviceRequest(
                    roomId = roomId,
                    machineId = machineId,
                    machineName = machineName,
                    accessToken = token // <-- 이 부분이 빠져있었습니다!
                )
                // ▲▲▲▲▲ [핵심 수정] ▲▲▲▲▲

                // 3. 토큰이 담긴 request 객체를 API로 전달합니다.
                val response = apiService.adminAddDevice(request)

                if (response.isSuccessful) {
                    _registerResult.value = RegisterResult.Success("세탁기가 성공적으로 등록되었습니다.")
                    // loadMachineList(roomId) // 이 함수는 현재 주석 처리되어 있습니다.
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "등록 실패 (HTTP ${response.code()})"
                    _registerResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    /**
     * 🔹 [수정됨] 새로운 세탁실을 서버에 등록하는 함수
     * Body에 access_token을 포함하여 전송하도록 수정합니다.
     */
    fun addNewLaundryRoom(roomName: String) {
        viewModelScope.launch {
            _addRoomResult.value = RegisterResult.Loading
            val token = getToken()
            android.util.Log.d(TAG, "getToken() result: $token")
            if (token.isNullOrEmpty()) {
                _addRoomResult.value = RegisterResult.Failure("로그인이 필요합니다.")
                return@launch
            }

            try {
                // [핵심 수정 5] 요청 객체 생성 시 accessToken을 포함합니다.
                val request = AddRoomRequest(
                    accessToken = token ,// <-- 토큰을 Body에 추가
                    roomName = roomName
                )

                // [핵심 수정 6] API 호출 시 헤더에서 토큰을 제거하고 Body만 전달합니다.
                val response = apiService.addLaundryRoom(request)

                if (response.isSuccessful) {
                    _addRoomResult.value = RegisterResult.Success("새로운 세탁실이 등록되었습니다.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "세탁실 등록 실패"
                    _addRoomResult.value = RegisterResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _addRoomResult.value = RegisterResult.Failure("서버 연결 실패: ${e.message}")
            }
        }
    }

    /**
     * 🔹 SharedPreferences에서 JWT 토큰을 가져오는 헬퍼 함수
     * (기존 코드와 동일, 수정 없음)
     */
    /**
     * 🔹 SharedPreferences에서 JWT 토큰을 가져오는 헬퍼 함수
     * [수정됨] LoginActivity와 동일하게 'EncryptedSharedPreferences'를 사용하여
     * 암호화된 금고에서 토큰을 읽어오도록 수정합니다.
     */
    private fun getToken(): String? {
        // ▼▼▼▼▼ [핵심 수정] EncryptedSharedPreferences 로직 전체 추가 ▼▼▼▼▼
        try {
            val context = getApplication<Application>().applicationContext

            // 1. LoginActivity와 동일한 방법으로 암호화 키를 생성합니다.
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            // 2. 암호화된 SharedPreferences 객체를 생성합니다.
            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "auth_prefs", // LoginActivity와 동일한 금고 이름
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            // 3. 이제 암호화된 금고에서 토큰을 가져옵니다.
            val token = sharedPreferences.getString("access_token", null)
            Log.d(TAG, "[ViewModel] Encrypted getToken() called. Found token: $token")
            return token

        } catch (e: Exception) {
            // GeneralSecurityException 또는 IOException 발생 시
            Log.e(TAG, "암호화된 토큰을 읽는 중 오류 발생", e)
            return null
        }
        // ▲▲▲▲▲ [핵심 수정] ▲▲▲▲▲
    }

}
