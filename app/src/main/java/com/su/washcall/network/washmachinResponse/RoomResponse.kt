package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName

/**
 * 세탁실 추가 API의 응답을 위한 데이터 클래스
 */
data class RoomResponse(
    @SerializedName("room_id") // 서버에서 오는 JSON 키 이름이 "room_id"일 경우
    val roomId: Int
)
