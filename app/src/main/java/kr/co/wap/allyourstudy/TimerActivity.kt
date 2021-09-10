package kr.co.wap.allyourstudy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.wap.allyourstudy.service.UpTimerService
import kr.co.wap.allyourstudy.adapter.FragmentAdapter
import kr.co.wap.allyourstudy.databinding.ActivityTimerBinding
import kr.co.wap.allyourstudy.fragments.DownTimerFragment
import kr.co.wap.allyourstudy.fragments.PomodoroFragment
import kr.co.wap.allyourstudy.fragments.TimerFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.CCTService
import kr.co.wap.allyourstudy.service.DownTimerService
import kr.co.wap.allyourstudy.service.PomodoroService
import kr.co.wap.allyourstudy.utils.ACCESS_TOKEN
import kr.co.wap.allyourstudy.utils.ACTION_CUMULATIVE_TIMER_START
import kr.co.wap.allyourstudy.utils.ACTION_CUMULATIVE_TIMER_STOP
import kr.co.wap.allyourstudy.utils.TimerUtil
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class TimerActivity : AppCompatActivity()/*, AdapterView.OnItemSelectedListener*/ {

    private val binding by lazy{ActivityTimerBinding.inflate(layoutInflater)}

    private val timerFragment = TimerFragment()
    private val downTimerFragment = DownTimerFragment()
    private val pomodoroFragment = PomodoroFragment()

    private var UserInteraction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTimerTheme()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fragmentList = listOf(pomodoroFragment, timerFragment, downTimerFragment)
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        initNavBar(binding.bottomNavigationView)
        viewPagerMenu()
        setSpinner()
        setEventObservers()
        setObservers()
        //weekResetTime()
    }
    private fun activityRestart() {
        this.recreate()
    }
    private fun initNavBar(navbar: BottomNavigationView){
        navbar.run{
            setOnItemSelectedListener {
                when(it.itemId){
                    R.id.PomodoroTimer -> { binding.viewPager.currentItem = 0  }
                    R.id.UpTimer -> { binding.viewPager.currentItem = 1 }
                    R.id.DownTimer -> { binding.viewPager.currentItem = 2  }
                }
                true
            }
        }
    }
    private fun viewPagerMenu(){
        binding.viewPager.registerOnPageChangeCallback(
            object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                   binding.bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )
    }
    private fun setTimerTheme(){
        //db에서 숫자 꺼내서 테마 설정
        val prefs: SharedPreferences = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val position = prefs.getInt("theme",-1)
        if(position == 2){
            setTheme(R.style.SplashScreenTheme)
        }
        else if(position == 1){
            setTheme(R.style.Theme_AllYourStudy)
        }
    }
    private fun setSpinner(){
        val spinner = binding.timerThemeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.timer_theme_array,
            android.R.layout.simple_spinner_item
        ).also{
                adapter -> adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val prefs: SharedPreferences = getSharedPreferences("theme", Context.MODE_PRIVATE)
                if(UserInteraction && position != 0) {
                    val editor = prefs.edit()
                    editor.clear()
                    editor.putInt("theme", position)
                    editor.apply()
                    activityRestart()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        UserInteraction = true
    }
    private fun setObservers(){
        CCTService.cumulativeTimer.observe(this){
            binding.cumulativeCycleTimer.text = TimerUtil.getFormattedSecondTime(it, false)
            //weekResetTime()
        }
    }
    private fun setEventObservers() {
        DownTimerService.timerEvent.observe(this){
            cumulativeCycleTimer()
        }
        UpTimerService.timerEvent.observe(this){
            cumulativeCycleTimer()
        }
        PomodoroService.timerEvent.observe(this){
            cumulativeCycleTimer()
        }
    }

    private fun cumulativeCycleTimer() {
        if (UpTimerService.timerEvent.value != TimerEvent.UpTimerStart &&
            DownTimerService.timerEvent.value != TimerEvent.DownTimerStart &&
            PomodoroService.timerEvent.value != TimerEvent.PomodoroTimerStart) {
                if(PomodoroService.timerEvent.value != TimerEvent.PomodoroRestTimerStart) {
                    sendCommandToService(ACTION_CUMULATIVE_TIMER_STOP, 0)
                }
        }
        else {
            if(CCTService.timerEvent.value != TimerEvent.CumulativeTimerStart) {
                Log.d("Tag","start")
                val timer = binding.cumulativeCycleTimer.text.toString()
                sendCommandToService(ACTION_CUMULATIVE_TIMER_START, TimerUtil.getLongTimer(timer))
            }
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, CCTService::class.java).apply {
                this.action = action
                this.putExtra("data", data)
            })
        }
        else{
            this.startService(Intent(this, CCTService::class.java).apply {
                this.action = action
                this.putExtra("data", data)
            })
        }
    }
    private fun weekResetTime(){
        val currentTime = Calendar.getInstance().time
        val weekdayFormat = SimpleDateFormat("EE", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val weekday = weekdayFormat.format(currentTime)
        val time = timeFormat.format(currentTime)

        if(weekday == "월" && time == "07:00:00"){
            binding.cumulativeCycleTimer.text = "00:00:00"
        }
    }
    private fun goHome(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}