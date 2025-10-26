package com.su.washcall.database

import androidx.room.PrimaryKey

data class AccessToken(
    @PrimaryKey
    val user_username: String,
    val accessToken: String
)