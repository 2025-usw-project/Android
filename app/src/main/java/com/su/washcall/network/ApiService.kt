package com.su.washcall.network // 네 패키지 이름 확인

import com.su.washcall.network.model.LoginRequest
import com.su.washcall.network.model.LoginResponse
import retrofit2.Call // ◀ import retrofit2.Response 대신 Call을 사용
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // [수정] suspend fun 대신 fun,
    // [수정] Response<LoginResponse> 대신 Call<LoginResponse> 사용
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 여기에 다른 API 함수들(@POST("/register") 등) 추가 예정
}