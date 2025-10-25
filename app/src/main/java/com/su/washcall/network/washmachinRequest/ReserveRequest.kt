package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

// `/reserve` 요청 시 사용
data class ReserveRequest(
@SerializedName("room_id") val roomId: Int,
@SerializedName("isreserved") val isReserved: Int // 0 또는 1
)
