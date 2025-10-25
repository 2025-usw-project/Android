package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

// 패키지 경로 확인

// 로그인 시 서버로 보낼 JSON 형태 정의
data class LoginRequest(
    @SerializedName("user_snum")
    val studentNumber: Int, // 학번 (Int)

    @SerializedName("user_password")
    val password: String  // 비밀번호 (String)
)
