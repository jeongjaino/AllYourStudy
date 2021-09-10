package kr.co.wap.allyourstudy.logindata

data class RegisterResponse(
    val data: Data
)
data class Data(
    val email: String,
    val username: String
)