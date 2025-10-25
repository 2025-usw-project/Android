package com.su.washcall.network.washmachinResponse
import com.google.gson.annotations.SerializedName

// `subscribe ok`, `reserve ok` 등 공통 메시지 응답용
data class MessageResponse(
    @SerializedName("message") val message: String
)
