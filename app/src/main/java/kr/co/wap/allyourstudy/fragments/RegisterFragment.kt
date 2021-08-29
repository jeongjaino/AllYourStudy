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
import kr.co.wap.allyourstudy.data.RegisterRequest
import kr.co.wap.allyourstudy.data.RegisterResponse
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

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailText.text.toString()
            val username = binding.registerNameText.text.toString()
            val password = binding.registerPasswordText.text.toString()

            val request = RegisterRequest(email, username, password)

            RetrofitBuilder.userService.register(request).enqueue(object: Callback<RegisterResponse>{
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val responseBody = response.body()
                    val responseCode = response.code()
                    if(responseCode == 201){
                        Toast.makeText(loginActivity,"이메일을 보냈습니다. ", Toast.LENGTH_LONG).show()
                        loginActivity.goLogin()
                    }
                    else{
                        Log.d("Tag",responseCode.toString())
                    }
                }
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d("Tag",t.message.toString())
                }
            })
        }
        return binding.root
    }
}
