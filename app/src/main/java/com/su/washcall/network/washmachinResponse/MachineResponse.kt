// 경로: app/src/main/java/com/su/washcall/network/MachineResponse.kt
package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName/**
 * 서버로부터 세탁기 등록 결과를 응답받을 때 사용하는 데이터 클래스
 */
data class MachineResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("machine_id")
    val machineId: Int? // 서버 응답에 따라 null일 수도 있음을 고려
)
