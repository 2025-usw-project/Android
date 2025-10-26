package com.su.washcall

import android.app.Application
import com.su.washcall.database.LaundryRoomDatabase
import com.su.washcall.network.RetrofitClient
import com.su.washcall.repository.LaundryRepository

class LaundryApplication : Application() {
    // 데이터베이스 인스턴스를 lazy를 통해 앱 생명주기 동안 한 번만 생성
    private val database by lazy { LaundryRoomDatabase.getDatabase(this) }

    // ApiService 인스턴스
    private val apiService by lazy { RetrofitClient.instance }

    // Repository 인스턴스를 lazy를 통해 생성하고, Dao와 ApiService를 주입
    // 이 repository 인스턴스는 앱 전체에서 공유됩니다.
    val repository by lazy { LaundryRepository(database.laundryRoomDao(), apiService) }
}
