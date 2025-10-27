package com.su.washcall.network

import com.su.washcall.MyApplication
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 모든 네트워크 요청에 'Authorization' 헤더를 자동으로 추가하는 인터셉터.
 * 로그인 및 회원가입 요청은 헤더 추가 대상에서 제외됩니다.
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 로그인과 회원가입 요청에는 토큰을 추가하지 않음
        if (originalRequest.url.encodedPath.endsWith("/login") || originalRequest.url.encodedPath.endsWith("/register")) {
            return chain.proceed(originalRequest)
        }

        // MyApplication에 저장된 전역 prefs 객체에서 토큰을 직접 가져옴
        val token = MyApplication.prefs.accessToken

        // 토큰이 있는 경우에만 헤더에 추가
        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            // 토큰이 없으면 원본 요청을 그대로 보냄
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
