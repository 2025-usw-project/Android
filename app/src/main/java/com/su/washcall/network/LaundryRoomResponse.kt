// 경로: app/src/main/java/com/su/washcall/network/model/LaundryRoomResponse.kt
package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

/**
 * 서버로부터 받을 '세탁실' 정보에 대한 데이터 클래스
 * @param roomId 세탁실의 고유 ID
 * @param roomName 세탁실 이름 (예: "제1생활관 남성 세탁실")
 * @param washingMachineCount 이 세탁실에 속한 세탁기의 총 개수
 */
data class LaundryRoomResponse(
    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("room_name")
    val roomName: String,

    @SerializedName("WM_cnt")
    val washingMachineCount: Int
)
