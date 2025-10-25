// C:/Users/eclipseuser/AndroidStudioProjects/washcall/app/src/main/java/com/su/washcall/network/model/RegisterRequest.kt

package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

// 회원가입 시 앱이 서버로 보낼 데이터 모델
data class RegisterRequest(
    @SerializedName("user_username")
    val username: String,

    @SerializedName("user_password")
    val password: String,

    @SerializedName("user_role")
    val isAdmin: Boolean,

    @SerializedName("user_snum")
    val studentNumber: Int
)
