// 파일 경로: app/src/main/java/com/su/washcall/viewmodel/LaundryViewModel.kt
package com.su.washcall.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope // ✅ 1. viewModelScope를 import 합니다.
import com.auth0.android.jwt.JWT
import com.su.washcall.MyApplication
import com.su.washcall.database.AppDatabase
import com.su.washcall.database.LaundryRoom
import com.su.washcall.database.WashingMachine
import com.su.washcall.network.RetrofitClient
// ✅ 2. AdminAddDeviceRequest를 import 합니다.
import com.su.washcall.network.washmachinRequest.AdminAddDeviceRequest
import com.su.washcall.network.washmachinRequest.LoadDataRequest
import com.su.washcall.repository.LaundryRepository
import kotlinx.coroutines.launch // ✅ 3. launch를 import 합니다.
import java.lang.Exception

class LaundryViewModel(
    private val repository: LaundryRepository,
    application: Application // 생성자에서 private val 제거
) : AndroidViewModel(application) {

// (기존 LiveData들은 그대로 유지)
    val allLaundryRooms: LiveData<List<LaundryRoom>> = repository.allLaundryRooms.asLiveData()
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage
    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    private val _subscribeResult = MutableLiveData<Result<String>>()
    val subscribeResult: LiveData<Result<String>> get() = _subscribeResult

    fun getWashingMachinesByRoom(roomId: Int): LiveData<List<WashingMachine>> {
        return repository.getWashingMachinesByRoom(roomId).asLiveData()
    }

    // --- ▼▼▼ [최종 수정] addDevice 함수 ▼▼▼ ---
    fun addDevice(roomId: Int, machineId: Int, machineName: String) {
        viewModelScope.launch {
            val token = MyApplication.prefs.accessToken
            if (token.isNullOrEmpty()) {
                _operationStatus.value = "❌ 추가 실패: 로그인 정보(토큰)가 없습니다."
                return@launch
            }

            try {
                _operationStatus.value = "요청 중..."

                // 요청 객체 생성
                val request = AdminAddDeviceRequest(
                    access_token = token,
                    roomId = roomId,
                    machineId = machineId,
                    machineName = machineName
                )
                Log.d("LaundryViewModel", "API 요청 Body: $request")

                // [핵심 수정] repository.addDevice 호출 시 request 객체 하나만 전달합니다.
                repository.addDevice(request)

                _operationStatus.value = "✅ 세탁기가 성공적으로 추가되었습니다."
            } catch (e: Exception) {
                _operationStatus.value = "❌ 추가 실패: ${e.message}"
            }
        }
    }
    // --- ▲▲▲ [최종 수정] addDevice 함수 ▲▲▲ ---


    fun refreshData() {
        viewModelScope.launch {
            val token = MyApplication.prefs.accessToken
            if (token.isNullOrEmpty()) {
                _operationStatus.value = "데이터 로딩 실패: 로그인 정보(토큰)가 없습니다."
                return@launch
            }

            try {
                // 1. 토큰으로 LoadDataRequest 객체를 생성합니다.
                val request = LoadDataRequest(accessToken = token)
                Log.d("LaundryViewModel", "/load API 요청 Body: $request")

                // 2. 생성한 request 객체를 Repository로 전달합니다.
                repository.refreshAllDataFromServer(request)

                _operationStatus.value = "데이터를 성공적으로 새로고침했습니다."
            } catch (e: Exception) {
                _operationStatus.value = "데이터 로딩 실패: ${e.message}"
                Log.e("LaundryViewModel", "refreshData 실패", e)
            }
        }
    }

    fun subscribeToRoom(roomName: String) {
        viewModelScope.launch {
            try {
                // 1. SharedPreferences에서 'access_token'을 가져옵니다.
                val token = MyApplication.prefs.accessToken
                if (token.isNullOrEmpty()) {
                    throw IllegalStateException("로그인 정보(토큰)가 없습니다.")
                }

                // 2. 토큰을 디코딩하여 'user_snum'(String)을 추출합니다.
                val jwt = JWT(token)
                val userSnumString = jwt.getClaim("snum").asString()
                if (userSnumString.isNullOrEmpty()) {
                    throw IllegalStateException("토큰이 유효하지 않거나 학번 정보가 없습니다.")
                }


                Log.d("ViewModel", "구독 API 요청 파라미터: roomName=$roomName, userSnum=$userSnumString")

                // --- ▼▼▼ [핵심 수정] 이 부분을 아래 코드로 교체해주세요. ▼▼▼ ---
                // 4. Repository의 수정된 함수를 호출합니다. (변수 이름을 userSnumString으로 수정)
                repository.subscribeToRoom(roomName, userSnumString)
                // --- ▲▲▲ [핵심 수정] 여기까지 교체 ▲▲▲ ---

                // 5. 성공 결과를 LiveData에 전달합니다.
                _subscribeResult.value = Result.success(roomName)

            } catch (e: Exception) {
                Log.e("ViewModel", "subscribeToRoom failed", e)
                _subscribeResult.value = Result.failure(Exception("구독 요청 실패: ${e.message}", e))
            }
        }
    }
}

class LaundryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaundryViewModel::class.java)) {
            // ✅ 1. MyApplication에서 이미 만들어진 repository를 가져옵니다.
            val repository = (application as MyApplication).repository

            // ✅ 3. 가져온 repository와 application을 ViewModel에 전달합니다.
            return LaundryViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
