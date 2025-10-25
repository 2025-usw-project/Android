package com.su.washcall.network.model

// 로그인 성공 시 서버로부터 받을 JSON 형태 정의
data class LoginResponse(
    val access_token: String,
    val user_role: String // "USER" 또는 "ADMIN"
)