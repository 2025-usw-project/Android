package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName

/**
 * 서버에서 받아오는 개별 세탁기 정보를 담는 데이터 클래스
 */
data class MachineInfo(
    @SerializedName("machine_id")
    val machineId: Int,

    @SerializedName("machine_name")
    val machineName: String,

    @SerializedName("status")
    val status: String, // 예: "available", "running", "error" 등
)
