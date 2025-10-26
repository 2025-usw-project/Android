package com.su.washcall.viewmodel

import androidx.lifecycle.*
import com.su.washcall.database.LaundryRoom
import com.su.washcall.repository.LaundryRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LaundryViewModel(private val repository: LaundryRepository) : ViewModel() {

    val allLaundryRooms: LiveData<List<LaundryRoom>> = repository.allLaundryRooms.asLiveData()

    // 데이터 로딩 상태를 외부로 알리기 위한 LiveData
    private val _dataLoadingStatus = MutableLiveData<String>()
    // [수정] 오타 수정: _dataLoading-Status -> _dataLoadingStatus
    val dataLoadingStatus: LiveData<String> = _dataLoadingStatus

    /**
     * 🔹 서버에서 최신 데이터를 가져와 Room DB를 업데이트하도록 Repository에 요청합니다.
     */
    fun refreshData(token: String) {
        viewModelScope.launch {
            try {
                _dataLoadingStatus.value = "데이터 새로고침 중..."
                // [수정] 올바른 함수 호출: refreshLaundryData -> refreshAllDataFromServer
                repository.refreshAllDataFromServer(token)
                _dataLoadingStatus.value = "새로고침 완료"
            } catch (e: Exception) {
                _dataLoadingStatus.value = "오류 발생: ${e.message}"
            }
        }
    }
}

// ViewModel에 파라미터(repository)를 전달하기 위한 Factory 클래스
class LaundryViewModelFactory(private val repository: LaundryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaundryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LaundryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
