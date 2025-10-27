package com.su.washcall.network.washmachinRequest

import com.google.gson.annotations.SerializedName

data class AdminMachinListRequest (
    val access_token: String,
    val room_id: Int
)