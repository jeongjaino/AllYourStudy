package kr.co.wap.allyourstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.ActivityMainBinding
import kr.co.wap.allyourstudy.utils.TimerUtil
import kr.co.wap.allyourstudy.fragments.HomeFragment
import kr.co.wap.allyourstudy.fragments.TimerFragment
import kr.co.wap.allyourstudy.fragments.YoutubeFragment

class MainActivity : AppCompatActivity() {

    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    private val timerFragment = TimerFragment()
    private val youtubeFragment = YoutubeFragment()
    private val homeFragment = HomeFragment()

    private lateinit var currentTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObservers()
        initNavBar(binding.bottomBar)
    }
    private fun initNavBar(navbar: BottomNavigationView){
        navbar.run{
            setOnItemSelectedListener {
                when(it.itemId){
                    R.id.Timer -> { goTimer() }
                    R.id.Youtube -> { goYouTube() }
                    R.id.Home -> { goHome() }
                }
                true
            }
            selectedItemId = R.id.Home //기본 홈 설정
        }

    }
    private fun setObservers(){
        TimerService.timerEvent.observe(this){
        }
        TimerService.timerInMillis.observe(this) {
            currentTime = TimerUtil.getFormattedSecondTime(it, false)
            binding.shareTimer.text = currentTime //timer
        }
        TimerService.timerInMin.observe(this){
            val currentTime = TimerUtil.getFormattedSecondTime(it, true)
            binding.shareTimer.text = currentTime
        }
    }
    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer , fragment)
            transaction.commit()
        }
    }
    fun goTimer(){
        replaceFragment(timerFragment)
        timerVisibleToggle(timerFragment)
    }
    fun goYouTube(){
        replaceFragment(youtubeFragment)
        timerVisibleToggle(youtubeFragment)
    }
    fun goHome(){
        replaceFragment(homeFragment)
        timerVisibleToggle(homeFragment)
    }
    private fun timerVisibleToggle(fragment: Fragment){
        if(fragment == timerFragment) {
            binding.mainCardView.visibility = View.GONE
        }
        else{
            binding.mainCardView.visibility = View.VISIBLE
        }
    }
}