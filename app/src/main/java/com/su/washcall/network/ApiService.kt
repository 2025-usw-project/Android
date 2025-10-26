// 경로: app/src/main/java/com/su/washcall/network/ApiService.kt
package com.su.washcall.network

// user 패키지의 모든 클래스를 사용하므로 * 로 변경해도 무방합니다.
import com.su.washcall.network.user.*
import com.su.washcall.network.washmachinRequest.*
import com.su.washcall.network.washmachinResponse.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- 1. 인증 API ---
    @POST("/login") // 로그인 API 주소는 /login 입니다.
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register") // 일반 사용자 회원가입 API 주소는 /register 입니다.
    fun register(@Body registerRequest: RegisterRequest): Call<Void>

    /**
     * ✨ [핵심 수정] 관리자 회원가입도 일반 회원가입과 동일한 "/register" 주소를 사용하도록 변경합니다.
     * 서버는 Request Body에 포함된 "user_role: true" 값을 보고 관리자로 인식하게 됩니다.
     * 이전 코드의 "/admins/register" 주소가 오류의 원인이었습니다.
     */
    @POST("/register") // 여기를 "/admins/register"에서 "/register"로 수정했습니다.
    fun registerAdmin(@Body registrationRequest: AdminRegistrationRequest): Call<AdminRegistrationResponse>


    // --- 2. 일반 사용자 API --- (이하 모든 코드는 기존과 동일하며, 수정할 필요 없습니다)
    @POST("/device_subscribe")
    suspend fun subscribeDevice(
        @Body body: RoomSubscribeRequest
    ): Response<MessageResponse>

    @POST("/load")
    suspend fun loadInitialData(): Response<List<LoadDataResponse>>

    @POST("/reserve")
    suspend fun reserveDevice(
        @Body body: ReserveRequest
    ): Response<MessageResponse>

    @POST("/notify_me")
    suspend fun notifyMe(
        @Body body: NotifyRequest
    ): Response<MessageResponse>

    @GET("/device_subscribe")
    suspend fun subscribeToRoom(
        @Query("room_name") roomName: String,
        @Query("user_snum") userSnum: String
    ): Response<SubscribeResponse>


    // --- 3. 관리자 API ---
    @POST("/admin/add_device")
    suspend fun adminAddDevice(
        @Body body: AdminAddDeviceRequest
    ): Response<MessageResponse>

    @GET("/admin/machines")
    suspend fun getMachineList(
        @Query("room_id") roomId: Int
    ): Response<List<MachineInfo>>

    @POST("/admin/add_room")
    suspend fun addLaundryRoom(
        @Body body: AddRoomRequest
    ): Response<AddRoomResponse>
}

