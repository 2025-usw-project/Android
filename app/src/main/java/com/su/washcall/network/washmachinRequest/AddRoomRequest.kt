package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

/**
 * [수정] 신규 세탁실 등록 요청 시, 서버 명세에 따라 room_name만 전송합니다.
 */
data class AddRoomRequest(
    @SerializedName("room_name") val roomName: String
)
