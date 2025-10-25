package su.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val  user_snum: Int, //고유 ID이자 로그인 ID
    val user_username: String,
    val user_password: String,
    val user_role: Boolean // 역할 구분 (true: 관리자)

)