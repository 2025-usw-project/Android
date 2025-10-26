package com.su.washcall.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.su.washcall.network.ApiService
import com.su.washcall.network.RetrofitClient
import kotlinx.coroutines.launch

// API 호출 결과를 UI에 전달하기 위한 클래스
sealed class ApiResult {
    data class Success(val message: String) : ApiResult()
    data class Failure(val message: String) : ApiResult()
    object Loading : ApiResult()
}

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient.instance

    // 구독 요청 결과를 UI에 알리기 위한 LiveData
    private val _subscribeResult = MutableLiveData<ApiResult>()
    val subscribeResult: LiveData<ApiResult> = _subscribeResult

    /**
     * 🔹 사용자가 선택한 세탁실을 구독하는 함수
     */
    fun subscribeToLaundryRoom(roomName: String) {
        viewModelScope.launch {
            _subscribeResult.value = ApiResult.Loading

            val token = getToken()
            val userSnum = getStudentNumber() // SharedPreferences 등에서 학번 가져오기

            if (token.isNullOrEmpty() || userSnum.isNullOrEmpty()) {
                _subscribeResult.value = ApiResult.Failure("로그인 정보가 필요합니다.")
                return@launch
            }

            try {
                val response = apiService.subscribeToRoom(
                    accessToken = "Bearer $token",
                    roomName = roomName,
                    userSnum = userSnum
                )

                if (response.isSuccessful && response.body() != null) {
                    _subscribeResult.value = ApiResult.Success(response.body()!!.message)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "구독 요청 실패"
                    _subscribeResult.value = ApiResult.Failure(errorMsg)
                }
            } catch (e: Exception) {
                _subscribeResult.value = ApiResult.Failure("서버 연결에 실패했습니다: ${e.message}")
            }
        }
    }

    // 로그인 시 저장했던 토큰을 가져오는 함수
    private fun getToken(): String? {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "auth_prefs",
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString("jwt_token", null)
    }

    // 로그인 시 저장했던 학번을 가져오는 함수
    private fun getStudentNumber(): String? {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "auth_prefs",
            Context.MODE_PRIVATE
        )
        // "user_snum" 이라는 키로 학번을 저장했다고 가정
        return sharedPreferences.getString("user_snum", null)
    }
}
