package com.su.washcall.network.model // 패키지 경로 확인

// 로그인 시 서버로 보낼 JSON 형태 정의
data class LoginRequest(
    val user_snum: Int,
    val user_password: String
)