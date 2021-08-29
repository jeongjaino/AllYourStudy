package kr.co.wap.allyourstudy

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.wap.allyourstudy.databinding.ActivityMainBinding
import kr.co.wap.allyourstudy.fragments.HomeFragment
import kr.co.wap.allyourstudy.fragments.YoutubeFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.DownTimerService
import kr.co.wap.allyourstudy.service.PomodoroService
import kr.co.wap.allyourstudy.service.UpTimerService


class MainActivity : AppCompatActivity(){

    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    private val youtubeFragment = YoutubeFragment()
    private val homeFragment = HomeFragment()

    private val gray = ColorStateList.valueOf(Color.rgb(181,181,181))
    private val red = ColorStateList.valueOf(Color.rgb(250,100,100))
    private val green = ColorStateList.valueOf(Color.rgb(100,250,100))
    private val blue = ColorStateList.valueOf(Color.rgb(100,100,250))

    var startX = 0f
    var startY = 0f

    var xl = 0f
    var yl = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObservers()
        dragAndDrop()
    }

    override fun onStart(){
        super.onStart()
        initNavBar(binding.bottomBar)
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
        UpTimerService.timerEvent.observe(this) {
            if (it == TimerEvent.UpTimerStop) {
                binding.stwFab.backgroundTintList = gray
            } else {
                binding.stwFab.backgroundTintList = green
            }
        }
        DownTimerService.timerEvent.observe(this){
            if(it == TimerEvent.DownTimerStop){
                binding.dtFab.backgroundTintList = gray
            } else{
                binding.dtFab.backgroundTintList = blue
            }
        }
        PomodoroService.timerEvent.observe(this){
            if(it == TimerEvent.PomodoroTimerStop){
                binding.pmFab.backgroundTintList = gray
            } else{
                binding.pmFab.backgroundTintList = red
            }
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
    private fun goYouTube(){
        replaceFragment(youtubeFragment)
    }
    private fun goHome(){
        replaceFragment(homeFragment)
    }
}