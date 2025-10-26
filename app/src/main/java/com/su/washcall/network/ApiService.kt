package com.su.washcall.network

import com.su.washcall.database.LaundryRoom
import com.su.washcall.network.washmachinRequest.*
import com.su.washcall.network.washmachinResponse.MachineInfo
import com.su.washcall.network.washmachinResponse.SubscribeResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // AdminViewModel에서 사용
    @POST("admin/devices")
    suspend fun adminAddDevice(
        @Header("Authorization") accessToken: String,
        @Body request: AdminAddDeviceRequest
    ): Response<Unit> // 반환값이 없는 성공/실패 응답

    @GET("admin/rooms/{roomId}/machines")
    suspend fun getMachineList(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<List<MachineInfo>>

    @POST("admin/rooms")
    suspend fun addLaundryRoom(
        @Header("Authorization") accessToken: String,
        @Body request: AddRoomRequest
    ): Response<Unit>

    // UserViewModel에서 사용
    @POST("user/subscribe")
    suspend fun subscribeToRoom(
        @Header("Authorization") accessToken: String,
        @Query("roomName") roomName: String,
        @Query("userSnum") userSnum: String
    ): Response<SubscribeResponse>

    // LaundryViewModel에서 사용 (세탁실 목록 조회)
    @GET("laundry/rooms")
    suspend fun getAllLaundryRooms(
        @Header("Authorization") accessToken: String
    ): Response<List<LaundryRoom>>
}

// subscribeToRoom의 응답 Body를 위한 데이터 클래스
data class SubscribeResponse(val message: String)

