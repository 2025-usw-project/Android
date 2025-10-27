package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName

/**
 * 서버의 '/load' API 응답의 최상위 구조. 세탁실 목록을 담고 있습니다.
 * 이것이 List<LoadDataResponse> 형태로 서버에서 내려옵니다.
 */
data class LoadDataResponse(
    @SerializedName("id")
    val roomId: Int, // 세탁실 ID

    @SerializedName("room_name")
    val roomName: String, // 세탁실 이름

    // --- ▼▼▼ [가장 중요] 이 부분이 있어야 합니다. ▼▼▼ ---
    @SerializedName("machines")
    val machines: List<MachineInfo> // 이 세탁실에 속한 세탁기 목록
    // --- ▲▲▲ [가장 중요] ▲▲▲ ---
)