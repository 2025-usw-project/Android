// 경로: app/src/main/java/com/su/washcall/network/ApiService.kt

package com.su.washcall.network

import com.su.washcall.network.model.LoginRequest
import com.su.washcall.network.model.LoginResponse
import com.su.washcall.network.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * 로그인 API
     * @param loginRequest 학번(snum)과 비밀번호(password)를 포함하는 요청 객체
     * @return LoginResponse (access_token 포함)를 담은 Call 객체
     */
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    /**
     * 회원가입 API
     * @param registerRequest 이름, 비밀번호, 역할, 학번을 포함하는 요청 객체
     * @return 응답 본문이 없으므로 Void 타입을 담은 Call 객체
     */
    @POST("/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void>
}
