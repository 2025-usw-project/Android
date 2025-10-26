package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

/**
 * 서버로 세탁기 정보를 등록/요청할 때 사용하는 데이터 클래스
 */
data class MachineRequest(
    @SerializedName("machine_id") // JSON에서 실제 사용할 이름
    val machineId: Int,

    @SerializedName("initial_value")
    val initialValue: Int
)