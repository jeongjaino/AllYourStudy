package kr.co.wap.allyourstudy.api

import kr.co.wap.allyourstudy.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

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
        @Body refreshVerifyRequest: RefreshVerifyRequest
    ):Call<TokenVerifyResponse>
}

object RetrofitBuilder{

    val userService: UserService

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://allstudy.run.goorm.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }
}