// 경로: C:/Users/eclipseuser/AndroidStudioProjects/washcall/app/src/main/java/com/su/washcall/MyApplication.kt
package com.su.washcall

import android.app.Application
import com.su.washcall.repository.LaundryRepository
// ▼▼▼▼▼ [핵심 수정 1] ▼▼▼▼▼
// 'WashCallDatabase'가 아닌, 실제 데이터베이스 클래스 이름인 'AppDatabase'를 import 합니다.
import com.su.washcall.database.AppDatabase
// ▲▲▲▲▲ [핵심 수정 1] ▲▲▲▲▲
import com.su.washcall.network.RetrofitClient
import com.su.washcall.EncryptedPrefs

class MyApplication : Application() {

    // ▼▼▼▼▼ [핵심 수정 2] ▼▼▼▼▼
    // 'WashCallDatabase'가 아닌, 실제 클래스 'AppDatabase'의 getDatabase()를 호출합니다.
    private val database by lazy { AppDatabase.getDatabase(this) }
    // ▲▲▲▲▲ [핵심 수정 2] ▲▲▲▲▲

    // 이 repository를 생성하기 위해 위의 모든 과정이 필요했습니다.
    val repository by lazy {
        // Repository에는 반드시 DAO 객체(database.laundryRoomDao())를 전달해야 합니다.
        LaundryRepository(RetrofitClient.instance, database.laundryRoomDao())
    }

    companion object {
        // --- ▼▼▼▼▼ [최종 수정] ▼▼▼▼▼ ---
        // @JvmStatic을 속성에 직접 적용하여 static getter를 자동으로 생성하게 합니다.
        @JvmStatic
        lateinit var prefs: EncryptedPrefs
            private set // 외부에서 prefs 변수 자체를 다른 객체로 교체하는 것을 방지 (선택사항이지만 권장)

        // 충돌을 일으키는 중복된 함수는 삭제합니다.
        /*
        @JvmStatic
        fun getPrefs(): EncryptedPrefs {
            return prefs
        }
        */
        // --- ▲▲▲▲▲ [최종 수정] ▲▲▲▲▲ ---
    }

    override fun onCreate() {
        super.onCreate()
        prefs = EncryptedPrefs(applicationContext)
    }
}
