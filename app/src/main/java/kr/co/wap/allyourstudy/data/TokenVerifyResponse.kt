package kr.co.wap.allyourstudy.data

data class TokenVerifyResponse(
    val access: String
)
data class UnauthorizedResponse(
    val detail: String,
    val code: String
)