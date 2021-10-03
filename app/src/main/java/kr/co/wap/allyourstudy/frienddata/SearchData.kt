package kr.co.wap.allyourstudy.frienddata

data class SearchData(
    val accounts: List<Account>
)
data class Account(
    val profile: String,
    val status: Int,
    val username: String
)