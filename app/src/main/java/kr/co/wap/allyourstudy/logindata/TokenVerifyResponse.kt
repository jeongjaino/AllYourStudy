package kr.co.wap.allyourstudy.logindata

data class TokenVerifyResponse(
    val access: String
)
data class UnauthorizedResponse(
    val detail: String,
    val code: String
)