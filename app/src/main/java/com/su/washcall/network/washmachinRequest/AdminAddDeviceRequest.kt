// 파일 경로: app/src/main/java/com/su/washcall/network/AddDeviceRequest.kt
package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

data class AdminAddDeviceRequest(
    @SerializedName("access_token") // 실제 서버 API가 받는 필드 이름으로 설정
    val access_token: String,
    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("machine_id")
    val machineId: Int,

    @SerializedName("machine_name")
    val machineName: String

    // --- ▼▼▼ [핵심 수정] 요청 본문에 토큰 필드를 추가합니다. ▼▼▼ ---

    // --- ▲▲▲ [핵심 수정] ▲▲▲ ---
)

