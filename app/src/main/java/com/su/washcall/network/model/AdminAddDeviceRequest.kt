package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

// `/device_subscribe` 요청 시 사용
data class DeviceRequest(
    @SerializedName("room_id") val roomId: Int
)