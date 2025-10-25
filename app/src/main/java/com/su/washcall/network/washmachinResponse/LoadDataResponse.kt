// 경로: app/src/main/java/com/su/washcall/network/model/LoadDataResponse.kt
package com.su.washcall.network.washmachinResponse
import com.google.gson.annotations.SerializedName

// `/load` 응답 시 리스트의 각 아이템
data class LoadDataResponse(
    @SerializedName("machine_id") val machineId: Int,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("machine_name") val machineName: String,
    @SerializedName("status") val status: String
)
