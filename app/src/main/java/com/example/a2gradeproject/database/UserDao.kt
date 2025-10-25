package com.example.a2gradeproject.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // 회원가입 (이메일이 같으면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerUser(user: User)

    // 로그인 (이메일과 비밀번호로 사용자 조회)
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    fun loginUser(email: String, password: String): User?
}