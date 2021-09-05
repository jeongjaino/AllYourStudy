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
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.api.RetrofitBuilder
import kr.co.wap.allyourstudy.data.RefreshToken
import kr.co.wap.allyourstudy.data.UnauthorizedResponse
import kr.co.wap.allyourstudy.databinding.FragmentHomeBinding
import kr.co.wap.allyourstudy.dialog.LogoutDialogFragment
import kr.co.wap.allyourstudy.utils.TokenManager
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {

    val binding by lazy{FragmentHomeBinding.inflate(layoutInflater)}

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

    ): View? {
        binding.logoutButton.setOnClickListener {
            logoutDialog()
        }
        return binding.root
    }
    private fun startLogout(){
        val refresh = RefreshToken(TokenManager.getRefreshToken(mainActivity)!!)
        RetrofitBuilder.userService.logout(refresh).enqueue(object: Callback<UnauthorizedResponse>{
            override fun onResponse(
                call: Call<UnauthorizedResponse>,
                response: Response<UnauthorizedResponse>
            ) {
                val responseCode = response.code()
                if(responseCode == 204){
                    mainActivity.goLogin()
                    TokenManager.deleteTokens(mainActivity)
                    Log.d("tag","logoutSuccess")
                }
            }
            override fun onFailure(call: Call<UnauthorizedResponse>, t: Throwable) {
                Toast.makeText(mainActivity,"네트워크를 확인해주세요",Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun logoutDialog(){
        val dialog = LogoutDialogFragment()
        dialog.setButtonClickListener(object: LogoutDialogFragment.OnButtonClickListener{
            override fun onButtonYesClicked() {
                startLogout()
            }
        })
        dialog.show(mainActivity.supportFragmentManager, "LogoutDialog")
    }
}