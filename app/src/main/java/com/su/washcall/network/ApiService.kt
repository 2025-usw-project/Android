// 경로: app/src/main/java/com/su/washcall/network/ApiService.kt
package com.su.washcall.network

// ▼▼▼ [핵심 수정] Machine.java 클래스를 import 합니다. ▼▼▼
//import com.su.washcall.Machine

import com.su.washcall.network.user.AdminRegistrationRequest
import com.su.washcall.network.user.AdminRegistrationResponse
import com.su.washcall.network.user.LoginRequest
import com.su.washcall.network.user.LoginResponse
import com.su.washcall.network.user.RegisterRequest
import com.su.washcall.network.washmachinRequest.*
import com.su.washcall.network.washmachinResponse.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    // --- 1. 인증 API ---

    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void>

    /**
     * ✨ [핵심 수정] AdminRegisterActivity.java에서 호출할 관리자 회원가입 함수를 추가합니다.
     * 서버 명세에 따라 일반 회원가입과 동일한 "/register" 주소를 사용합니다.
     * 이 함수가 누락되어 있어 오류가 발생하고 있었습니다.
     */
    @POST("/register")
    fun registerAdmin(@Body registrationRequest: AdminRegistrationRequest): Call<AdminRegistrationResponse>


    // --- 2. 일반 사용자 API ---
    // 모든 @Header 파라미터를 제거하고, 코루틴 사용을 위해 suspend fun으로 통일합니다.

    @GET("/device_subscribe")
    suspend fun subscribeToRoom( // 또는 subscribeDevice
        @Query("room_name") roomName: String,
        @Query("user_snum") userSnum: String
    ): Response<MessageResponse>

    @POST("/load")
    suspend fun loadInitialData(
        @Body request: LoadDataRequest
    ): Response<List<LoadDataResponse>>

    @POST("/reserve")
    suspend fun reserveDevice(
        @Body body: ReserveRequest
    ): Response<MessageResponse>

    @POST("/notify_me")
    suspend fun notifyMe(
        @Body body: NotifyRequest
    ): Response<MessageResponse>


    // --- 3. 관리자 API ---
    // 모든 @Header 파라미터를 제거합니다.

    @POST("/admin/add_device") // 서버의 실제 엔드포인트 경로로 수정하세요
    suspend fun addDevice(
        @Body request: AdminAddDeviceRequest
    ): Response<AddDeviceResponse>

    @POST("/load")
    suspend fun getAdminMachineList(
        @Body request: AdminMachinListRequest
    ): Response<AdminMachinListResponse>

    @POST("/admin/add_room")
    suspend fun addLaundryRoom(
        @Body body: AddRoomRequest
    ): Response<AddRoomResponse>

}
