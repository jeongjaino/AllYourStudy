package kr.co.wap.allyourstudy.fragments.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import retrofit2.Callback
import kr.co.wap.allyourstudy.LoginActivity
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import kr.co.wap.allyourstudy.logindata.LoginRequest
import kr.co.wap.allyourstudy.logindata.LoginResponse
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


        binding.goRegisterText.setOnClickListener {
            loginActivity.goRegister()
        }
        startLogin()
        return binding.root
    }
    private fun startLogin(){
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
                        Toast.makeText(loginActivity, "이메일이나 비밀번호가 올바르지 않습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(loginActivity,"인터넷에 연결이 필요합니다.",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}