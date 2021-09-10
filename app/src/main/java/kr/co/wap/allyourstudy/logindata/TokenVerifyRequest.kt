package kr.co.wap.allyourstudy.logindata

data class TokenVerifyRequest (
    val token: String
        )
data class RefreshToken(
    val refresh: String
)
