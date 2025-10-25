package com.example.a2gradeproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val email: String, // 고유 ID이자 로그인 ID
    val password: String,
    val isAdmin: Boolean // 역할 구분 (true: 관리자)
)