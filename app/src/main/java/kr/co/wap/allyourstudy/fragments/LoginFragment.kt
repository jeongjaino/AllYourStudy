package kr.co.wap.allyourstudy.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.wap.allyourstudy.LoginActivity
import kr.co.wap.allyourstudy.databinding.FragmentLoginBinding


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

        return binding.root
    }
}