// 경로: app/src/main/java/com/su/washcall/EncryptedPrefs.kt
package com.su.washcall

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * 🔹 EncryptedSharedPreferences를 사용하여 데이터를 안전하게 관리하는 클래스.
 * 토큰과 같은 민감한 정보를 암호화하여 저장합니다.
 */
class EncryptedPrefs(context: Context) {

    // MasterKey는 암호화 키를 관리합니다.
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 암호화된 SharedPreferences 인스턴스를 생성합니다.
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs_name", // SharedPreferences 파일 이름
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    var token: String?
        get() = prefs.getString("AUTH_TOKEN", null)
        set(value) {
            prefs.edit().putString("AUTH_TOKEN", value).apply()
        }

    // SharedPreferences 값을 가져오거나 설정하는 프로퍼티
    var accessToken: String?
        get() = prefs.getString("ACCESS_TOKEN", null)
        set(value) {
            prefs.edit().putString("ACCESS_TOKEN", value).apply()
        }

    var refreshToken: String?
        get() = prefs.getString("REFRESH_TOKEN", null)
        set(value) {
            prefs.edit().putString("REFRESH_TOKEN", value).apply()
        }

    // 필요에 따라 다른 데이터도 추가할 수 있습니다.
    // var userEmail: String?
    //     get() = prefs.getString("USER_EMAIL", null)
    //     set(value) {
    //         prefs.edit().putString("USER_EMAIL", value).apply()
    //     }

    /**
     * SharedPreferences에 저장된 모든 데이터를 삭제하는 함수 (예: 로그아웃 시 사용)
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
