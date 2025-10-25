package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

// `/notify_me` 요청 시 사용
data class NotifyRequest(
    @SerializedName("machine_id") val machineId: Int
)
