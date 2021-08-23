package kr.co.wap.allyourstudy.data

data class RegisterResponse (
    val statusCode: Int,
    val refreshToken: String,
    val accessToken: String
        )