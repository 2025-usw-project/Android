// 경로: app/src/main/java/com/su/washcall/network/ApiService.kt
package com.su.washcall.network

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
    // 이 함수들은 인터셉터에서 헤더 추가를 제외하므로, 파라미터가 필요 없습니다.
    // LoginActivity가 Java로 되어 있어 Call<> 타입을 사용합니다.
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void>


    // --- 2. 일반 사용자 API ---
    // 모든 @Header 파라미터를 제거하고, 코루틴 사용을 위해 suspend fun으로 통일합니다.

    @GET("/device_subscribe")
    suspend fun subscribeToRoom( // 또는 subscribeDevice
        @Query("room_name") roomName: String,
        @Query("user_snum") userSnum: String
    ): Response<MessageResponse>

    @POST("/load")
    suspend fun loadInitialData(
        @Body request: LoadDataRequest // ◀️ AdminMachinListRequest 에서 LoadDataRequest 로 수정
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

    @POST("/admin/machines")
    suspend fun getAdminMachineList(
        @Body request: AdminMachinListRequest
    ): Response<AdminMachinListResponse>

    @POST("/admin/add_room")
    suspend fun addLaundryRoom(
        @Body body: AddRoomRequest
    ): Response<AddRoomResponse>
}
