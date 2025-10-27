package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

/**
 * 사용자가 특정 '세탁실'의 빈자리 알림을 구독할 때 사용하는 데이터 모델
 */
data class RoomSubscribeRequest(
    // 서버 명세에 따라 room_id (Int)를 전송
    @SerializedName("room_name")
    val roomName: String,

    @SerializedName("user_snum")
    val userSnum: String

)
