package com.su.washcall

import android.app.Application
import com.su.washcall.network.ApiService
import com.su.washcall.database.AppDatabase
import com.su.washcall.network.RetrofitClient
import com.su.washcall.database.LaundryRoomDao
import com.su.washcall.repository.LaundryRepository

/**
 * 🔹 앱 전체의 생명주기 동안 유지되는 공용 클래스.
 * 앱의 핵심 컴포넌트(DB, API, Repository)를 한 번만 생성하여 관리하는 창고 역할을 합니다.
 */
class LaundryApplication : Application() {

    // 데이터베이스 인스턴스를 지연 초기화합니다. 앱에서 처음 필요할 때 한 번만 생성됩니다.
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Retrofit API 서비스 인스턴스를 지연 초기화합니다.
    private val apiService by lazy {
        RetrofitClient.instance
    }

    // 데이터베이스 DAO와 API 서비스를 사용하는 Repository 인스턴스를 지연 초기화합니다.
    // 이 repository 인스턴스를 여러 ViewModel에서 공유하게 됩니다.
    val repository by lazy { LaundryRepository(apiService, database.laundryDao()) }
}
