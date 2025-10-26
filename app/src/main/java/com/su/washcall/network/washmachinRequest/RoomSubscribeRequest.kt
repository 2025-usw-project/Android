package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

data class RoomSubscribeRequest(
    val room_name: String,
    val user_snum: String
)
