package kr.co.wap.allyourstudy.logindata

data class LoginResponse (
    val email: String,
    val tokens: Token
        )
data class Token(
    val access: String,
    val refresh: String
)