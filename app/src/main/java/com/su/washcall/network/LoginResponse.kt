// 경로: app/src/main/java/com/su/washcall/network/model/LoginResponse.kt
package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

// 서버가 토큰만 보내주므로, access_token 필드만 정의합니다.
data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String
)
