package com.su.washcall.network.washmachinResponse

import com.google.gson.annotations.SerializedName

data class AddDeviceResponse(
    @SerializedName("message")
    val message: String
)
