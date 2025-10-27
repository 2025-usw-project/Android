// 파일 경로: app/src/main/java/com/su/washcall/viewmodel/LaundryViewModel.kt
package com.su.washcall.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope // ✅ 1. viewModelScope를 import 합니다.
import com.su.washcall.MyApplication
import com.su.washcall.database.AppDatabase
import com.su.washcall.database.LaundryRoom
import com.su.washcall.database.WashingMachine
import com.su.washcall.network.RetrofitClient
// ✅ 2. AdminAddDeviceRequest를 import 합니다.
import com.su.washcall.network.washmachinRequest.AdminAddDeviceRequest
import com.su.washcall.repository.LaundryRepository
import kotlinx.coroutines.launch // ✅ 3. launch를 import 합니다.
import java.lang.Exception

class LaundryViewModel(
    private val repository: LaundryRepository,
    private val application: Application
) : ViewModel() {

    // (기존 LiveData들은 그대로 유지)
    val allLaundryRooms: LiveData<List<LaundryRoom>> = repository.allLaundryRooms.asLiveData()
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage
    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    fun getWashingMachinesByRoom(roomId: Int): LiveData<List<WashingMachine>> {
        return repository.getWashingMachinesByRoom(roomId).asLiveData()
    }

    // --- ▼▼▼ [최종 수정] addDevice 함수 ▼▼▼ ---
    fun addDevice(roomId: Int, machineId: Int, machineName: String) {
        viewModelScope.launch { // 이제 이 부분은 오류가 없습니다.
            val token = MyApplication.prefs.accessToken
            if (token.isNullOrEmpty()) {
                _operationStatus.value = "❌ 추가 실패: 로그인 정보(토큰)가 없습니다."
                return@launch
            }

            try {
                _operationStatus.value = "요청 중..."

                // ✅ 4. 알려주신 필드 순서대로 요청 객체를 생성합니다.
                val request = AdminAddDeviceRequest(
                    access_token = token,
                    roomId = roomId,
                    machineId = machineId,
                    machineName = machineName
                )
                Log.d("LaundryViewModel", "API 요청 Body: $request")

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
                repository.refreshAllDataFromServer()
                _operationStatus.value = "데이터를 성공적으로 새로고침했습니다."
            } catch (e: Exception) {
                _operationStatus.value = "데이터 로딩 실패: ${e.message}"
            }
        }
    }
}

class LaundryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaundryViewModel::class.java)) {
            val apiService = RetrofitClient.instance
            val laundryRoomDao = AppDatabase.getDatabase(application).laundryRoomDao()
            val repository = LaundryRepository(apiService, laundryRoomDao)
            return LaundryViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
