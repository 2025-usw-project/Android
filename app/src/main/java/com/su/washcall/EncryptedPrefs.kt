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
    private val prefs: SharedPreferences by lazy {
        // 1. 암호화 키를 생성합니다.
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // 2. 암호화된 SharedPreferences 인스턴스를 생성합니다.
        EncryptedSharedPreferences.create(
            context,
            "auth_prefs", // ◀️ LoginActivity에서 사용하던 파일 이름과 반드시 동일해야 합니다.
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) {
            prefs.edit().putString("access_token", value).apply()
        }
}
