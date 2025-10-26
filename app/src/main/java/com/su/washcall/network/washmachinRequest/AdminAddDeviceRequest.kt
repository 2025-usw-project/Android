package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

// `/admin_add_device` 요청 시 사용
data class AdminAddDeviceRequest(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("machine_id") val machineId: Int,
    @SerializedName("machine_name") val machineName: String
)