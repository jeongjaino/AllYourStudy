package kr.co.wap.allyourstudy.logindata

data class LoginResponse (
    val tokens: Tokens
    )
data class Tokens(
    val access: String,
    val refresh: String
)