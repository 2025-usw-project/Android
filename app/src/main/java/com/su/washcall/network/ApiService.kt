// 경로: app/src/main/java/com/su/washcall/network/ApiService.kt
package com.su.washcall.network

import com.su.washcall.network.model.*
import com.su.washcall.network.user.LoginRequest // 정확한 경로로 수정
import com.su.washcall.network.user.LoginResponse // 정확한 경로로 수정
import com.su.washcall.network.user.RegisterRequest // 정확한 경로로 수정
import com.su.washcall.network.washmachinRequest.*
import com.su.washcall.network.washmachinResponse.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // --- 1. 인증 API (suspend 함수로 통일) ---
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void> // 성공 여부만 확인


    // --- 2. 기기 및 기능 API ---

    /**
     * 사용자가 특정 세탁실의 업데이트를 구독합니다.
     */
    @POST("/device_subscribe")
    fun subscribeDevice(
        @Header("access_token") accessToken: String,
        @Body body: DeviceRequest
    ): Response<MessageResponse>

    /**
     * 사용자가 로그인 시 세탁기/세탁실의 전체 최신 정보를 서버로부터 불러옵니다.
     */
    @POST("/load")
    fun loadInitialData(
        @Header("access_token") accessToken: String
    ): Response<List<LoadDataResponse>>

    /**
     * 사용자가 세탁기를 예약합니다.
     */
    @POST("/reserve")
    suspend fun reserveDevice(
        @Header("access_token") accessToken: String,
        @Body body: ReserveRequest
    ): Response<MessageResponse>

    /**
     * 특정 세탁기의 세탁 완료 알림을 요청합니다.
     */
    @POST("/notify_me")
    suspend fun notifyMe(
        @Header("access_token") accessToken: String,
        @Body body: NotifyRequest
    ): Response<MessageResponse>

    /**
     * (관리자) 새로운 세탁 기기를 서버에 등록합니다.
     */
    @POST("/admin_add_device")
    suspend fun adminAddDevice(
        @Header("access_token") accessToken: String,
        @Body body: AdminAddDeviceRequest
    ): Response<MessageResponse>
}
