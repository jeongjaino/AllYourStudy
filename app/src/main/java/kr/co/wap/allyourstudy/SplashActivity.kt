package kr.co.wap.allyourstudy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Callback
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import kr.co.wap.allyourstudy.data.RefreshVerifyRequest
import kr.co.wap.allyourstudy.data.TokenVerifyRequest
import kr.co.wap.allyourstudy.data.TokenVerifyResponse
import kr.co.wap.allyourstudy.data.UnauthorizedResponse
import kr.co.wap.allyourstudy.utils.TokenManager
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessTokenVerify()
    }
    private fun accessTokenVerify(){
        if(TokenManager.getAccessToken(this) == null){
            loadSplashScreen(LoginActivity())
            Log.d("tag","null")
        }
        else{
            val token = TokenVerifyRequest(TokenManager.getAccessToken(this)!!)
            RetrofitBuilder.userService.verify(token).enqueue(object: Callback<UnauthorizedResponse>{
                override fun onResponse(
                    call: Call<UnauthorizedResponse>,
                    response: Response<UnauthorizedResponse>
                ) {
                    val responseCode = response.code()
                    if(responseCode == 200){
                        Log.d("tag","access success")
                        loadSplashScreen(MainActivity())
                    }
                    else{
                        Log.d("tag",responseCode.toString())
                        refreshTokenVerify()
                    }
                }
                override fun onFailure(call: Call<UnauthorizedResponse>, t: Throwable) {
                    Log.d("Tag","fail")
                    loadSplashScreen(LoginActivity())
                }
            })
        }
    }
    private fun refreshTokenVerify(){

        val token = RefreshVerifyRequest(TokenManager.getRefreshToken(this)!!)
        RetrofitBuilder.userService.refresh(token).enqueue(object: Callback<TokenVerifyResponse>{
            override fun onResponse(
                call: Call<TokenVerifyResponse>,
                response: Response<TokenVerifyResponse>
            ) {
                val responseCode = response.code()
                if(responseCode == 200){
                    Log.d("tag","refresh success")
                    val responseAccessToken = response.body()?.access
                    TokenManager.saveAccessToken(responseAccessToken.toString(),this@SplashActivity)
                    accessTokenVerify()
                }
                else{
                    Log.d("tag",responseCode.toString())
                    loadSplashScreen(LoginActivity())
                }
            }
            override fun onFailure(call: Call<TokenVerifyResponse>, t: Throwable) {
                Log.d("Tag",t.message.toString())
                loadSplashScreen(LoginActivity())
            }
        })
    }
    private fun loadSplashScreen(activity: Activity){
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,activity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        },2000)
    }
}