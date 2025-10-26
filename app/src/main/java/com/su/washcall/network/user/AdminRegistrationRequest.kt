package com.su.washcall.network.user

import com.google.gson.annotations.SerializedName

/**
 * 관리자 회원가입 요청 시 서버로 보낼 데이터 모델.
 * 서버 명세에 따라 필드 이름을 @SerializedName으로 정확히 맞춰줍니다.
 */
data class AdminRegistrationRequest(
    @SerializedName("user_username")
    val userName: String,

    @SerializedName("user_password")
    val userPassword: String,

    @SerializedName("user_snum")
    val userSnum: Int
) {
    /**
     * ✨ [핵심 수정] userRole 필드를 생성자 밖으로 빼서,
     * 객체가 생성될 때 항상 자동으로 true 값을 갖도록 만듭니다.
     * 이렇게 하면 생성자에서 3개의 인자만 받아도 오류가 발생하지 않습니다.
     */
    @SerializedName("user_role")
    val userRole: Boolean = true
}

