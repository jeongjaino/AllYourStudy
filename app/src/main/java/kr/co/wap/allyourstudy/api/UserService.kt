package kr.co.wap.allyourstudy.api

import kr.co.wap.allyourstudy.frienddata.SearchData
import kr.co.wap.allyourstudy.logindata.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService{
    @POST("/auth/register/")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @POST("/auth/login/")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @POST("/auth/token/verify/")
    fun verify(
        @Body tokenVerifyRequest: TokenVerifyRequest
    ): Call<UnauthorizedResponse>

    @POST("/auth/token/refresh/")
    fun refresh(
        @Body refreshToken: RefreshToken
    ):Call<TokenVerifyResponse>

    @POST("/auth/logout/")
    fun logout(
        @Body refreshToken: RefreshToken
    ):Call<UnauthorizedResponse>

    @GET("/auth/api/search/")
    fun getSearchUser(@Query("q") query: String):Call<SearchData>
}

object RetrofitBuilder{

    val userService: UserService

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.133.227")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }
}