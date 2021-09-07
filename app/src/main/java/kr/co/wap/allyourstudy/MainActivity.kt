package kr.co.wap.allyourstudy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.wap.allyourstudy.databinding.ActivityMainBinding
import kr.co.wap.allyourstudy.fragments.HomeFragment
import kr.co.wap.allyourstudy.fragments.YoutubeFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.DownTimerService
import kr.co.wap.allyourstudy.service.PomodoroService
import kr.co.wap.allyourstudy.service.UpTimerService
import kr.co.wap.allyourstudy.fragments.YoutubePlayerFragment


class MainActivity : AppCompatActivity(){

    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val homeFragment = HomeFragment()

    var startX = 0f
    var startY = 0f

    var xl = 0f
    var yl = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObservers()
        dragAndDrop()
        disabledButton()
    }

    override fun onStart(){
        super.onStart()
        initNavBar(binding.bottomBar)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //가로 방형
            binding.bottomBar.visibility = View.GONE
            binding.cardView.visibility = View.GONE
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 세로 방향
            binding.bottomBar.visibility = View.VISIBLE
            binding.cardView.visibility = View.VISIBLE
        }
    }
    private fun initNavBar(navbar: BottomNavigationView){
        navbar.run{
            setOnItemSelectedListener {
                when(it.itemId){
                    R.id.pomodoroTimer -> { goTimer() }
                    R.id.Youtube -> { goYouTube() }
                    R.id.Home -> { goHome() }
                }
                true
            }
            selectedItemId = R.id.Home //기본 홈 설정
        }
    }
    private fun disabledButton(){
        binding.pomodoroToggleButton.isEnabled = false
        binding.UpTimerToggleButton.isEnabled = false
        binding.DownTimerToggleButton.isEnabled = false
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun dragAndDrop(){
        binding.cardView.setOnTouchListener{v, event ->

            val height = binding.fragmentContainer.height - binding.cardView.height
            val width = binding.fragmentContainer.width - binding.cardView.width
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE ->{
                    val movedX: Float = event.x - startX
                    val movedY: Float = event.y - startY

                    v.x = v.x + movedX
                    v.y = v.y + movedY

                    xl = if(v.x <= width/2){ 0f }
                    else { width.toFloat() }

                    yl = when {
                        v.y < 0 -> { 0f }
                        v.y > height -> { height.toFloat() }
                        else -> { v.y }
                    }
                    v.animate()
                        .x(xl)
                        .y(yl)
                }
            }
            true
        }
    }
    private fun setObservers(){
        PomodoroService.timerEvent.observe(this){
            binding.pomodoroToggleButton.isEnabled = true
            binding.pomodoroToggleButton.isChecked = it != TimerEvent.PomodoroTimerStop
            disabledButton()
        }
        UpTimerService.timerEvent.observe(this) {
            binding.UpTimerToggleButton.isEnabled = true
            binding.UpTimerToggleButton.isChecked = it != TimerEvent.UpTimerStop
            disabledButton()
        }
        DownTimerService.timerEvent.observe(this){
            binding.DownTimerToggleButton.isEnabled = true
            binding.DownTimerToggleButton.isChecked = it != TimerEvent.DownTimerStop
            disabledButton()
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer , fragment)
        transaction.commit()
    }
    private fun goTimer(){
        val intent = Intent(this, TimerActivity::class.java)
        startActivity(intent)
    }
    fun goYouTube(){
        replaceFragment(YoutubeFragment())
    }
    private fun goHome(){
        replaceFragment(homeFragment)
    }
    fun goLogin(){
        val intent = Intent(this, LoginActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
    fun goYoutubePlayer(){
        replaceFragment(YoutubePlayerFragment())
    }
}