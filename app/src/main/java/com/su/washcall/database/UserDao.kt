package com.su.washcall.database

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
    @Query("SELECT * FROM user_table WHERE user_Snum = :user_snum AND user_password = :password LIMIT 1")
    fun loginUser(user_snum: Int, password: String): User?
}