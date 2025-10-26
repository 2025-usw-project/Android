package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName

/**
 * /device_subscribe API의 응답을 위한 데이터 클래스
 */
data class SubscribeResponse(
    @SerializedName("message")
    val message: String
)
