package com.su.washcall.network.model // 패키지 경로 확인

// 로그인 시 서버로 보낼 JSON 형태 정의
data class LoginRequest(
    val userId: String, // API 명세의 'user_username' 대신 'userId' 사용 (통일 필요)
    val password: String
)