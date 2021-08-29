package kr.co.wap.allyourstudy.api

import kr.co.wap.allyourstudy.data.LoginRequest
import kr.co.wap.allyourstudy.data.LoginResponse
import kr.co.wap.allyourstudy.data.RegisterRequest
import kr.co.wap.allyourstudy.data.RegisterResponse
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