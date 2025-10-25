// 경로: app/src/main/java/com/su/washcall/network/model/WashingMachineResponse.kt
package com.su.washcall.network.model

import com.google.gson.annotations.SerializedName

/**
 * 서버로부터 받을 '개별 세탁기'의 상태 정보에 대한 데이터 클래스
 * @param machineId 세탁기의 고유 ID
 * @param roomId 이 세탁기가 소속된 세탁실의 ID
 * @param machineName 세탁기 이름 (예: "1번 세탁기")
 * @param status 세탁기 현재 상태 (예: "AVAILABLE", "RUNNING", "OUT_OF_ORDER")
 */
data class WashingMachineResponse(
    @SerializedName("machine_id")
    val machineId: Int,

    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("machine_name")
    val machineName: String,

    @SerializedName("status")
    val status: String
)
