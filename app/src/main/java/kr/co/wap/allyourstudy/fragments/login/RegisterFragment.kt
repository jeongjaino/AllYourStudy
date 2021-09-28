package kr.co.wap.allyourstudy.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kr.co.wap.allyourstudy.LoginActivity
import retrofit2.Callback
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import kr.co.wap.allyourstudy.logindata.RegisterRequest
import kr.co.wap.allyourstudy.logindata.RegisterResponse
import kr.co.wap.allyourstudy.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Response

class RegisterFragment : Fragment() {

    val binding by lazy{FragmentRegisterBinding.inflate(layoutInflater)}

    private lateinit var loginActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginActivity) loginActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        startRegister()
        return binding.root
    }
    private fun startRegister(){
        binding.registerButton.setOnClickListener {
            val username = binding.registerNameText.text.toString()
            val password = binding.registerPasswordText.text.toString()
            val passwordVerify = binding.registerPasswordVerifyText.text.toString()

            if (passwordVerify == password) {
                val request = RegisterRequest(username, password)
                RetrofitBuilder.userService.register(request)
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            val responseBody = response.body()
                            val responseCode = response.code()
                            if (responseCode == 201) {
                                Toast.makeText(loginActivity, "회원가입 되었습니다!", Toast.LENGTH_LONG).show()
                                loginActivity.goLogin()
                            } else {
                                Log.d("Tag", responseCode.toString())
                                Toast.makeText(loginActivity, "이미 중복된 아이디가 있습니다.", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(loginActivity, "인터넷 연결이 불안정합니다.", Toast.LENGTH_LONG).show()
                        }
                    })
            }
            else{
                Toast.makeText(loginActivity, "비밀번호가 서로 다릅니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
