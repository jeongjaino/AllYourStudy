package kr.co.wap.allyourstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.databinding.ActivityLoginBinding
import kr.co.wap.allyourstudy.fragments.RegisterFragment

class LoginActivity : AppCompatActivity() {

    private val registerFragment = RegisterFragment()

    private val binding by lazy{ ActivityLoginBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer , fragment)
        transaction.commit()
    }

    fun goRegister(){
        replaceFragment(registerFragment)
    }
}