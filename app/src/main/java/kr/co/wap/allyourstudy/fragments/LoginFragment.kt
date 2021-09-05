package kr.co.wap.allyourstudy.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import retrofit2.Callback
import kr.co.wap.allyourstudy.LoginActivity
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import kr.co.wap.allyourstudy.data.LoginRequest
import kr.co.wap.allyourstudy.data.LoginResponse
import kr.co.wap.allyourstudy.databinding.FragmentLoginBinding
import kr.co.wap.allyourstudy.utils.TokenManager
import retrofit2.Call
import retrofit2.Response


class LoginFragment : Fragment() {

    val binding by lazy{ FragmentLoginBinding.inflate(layoutInflater)}

    private lateinit var loginActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginActivity) loginActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding.goRegisterButton.setOnClickListener {
            loginActivity.goRegister()
        }
        binding.loginButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()

            val request = LoginRequest(email,password)

            RetrofitBuilder.userService.login(request).enqueue(object: Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val responseBody = response.body()
                    val responseCode = response.code()
                    if(responseCode == 200){
                        Toast.makeText(loginActivity, responseBody!!.email+" 반갑습니다!", Toast.LENGTH_LONG).show()
                        TokenManager.saveRefreshToken(responseBody.tokens.refresh,loginActivity)
                        TokenManager.saveAccessToken(responseBody.tokens.access,loginActivity)
                        loginActivity.goMain()
                    }
                    else{
                        Log.d("tag",responseCode.toString())
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("Tag",t.message.toString())
                    Toast.makeText(loginActivity,"인터넷에 연결이 필요합니다.",Toast.LENGTH_SHORT).show()
                }
            })
        }
        return binding.root
    }
}