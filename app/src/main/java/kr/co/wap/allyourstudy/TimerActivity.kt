package kr.co.wap.allyourstudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.SharingCommand
import kr.co.wap.allyourstudy.service.UpTimerService
import kr.co.wap.allyourstudy.adapter.FragmentAdapter
import kr.co.wap.allyourstudy.databinding.ActivityTimerBinding
import kr.co.wap.allyourstudy.fragments.DownTimerFragment
import kr.co.wap.allyourstudy.fragments.PomodoroFragment
import kr.co.wap.allyourstudy.fragments.TimerFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.DownTimerService
import kr.co.wap.allyourstudy.service.PomodoroService
import kr.co.wap.allyourstudy.utils.ACTION_CUMULATIVE_TIMER_START
import kr.co.wap.allyourstudy.utils.ACTION_CUMULATIVE_TIMER_STOP
import kr.co.wap.allyourstudy.utils.TimerUtil
import java.text.SimpleDateFormat
import java.util.*

class TimerActivity : AppCompatActivity() {

    private val binding by lazy{ActivityTimerBinding.inflate(layoutInflater)}

    private val timerFragment = TimerFragment()
    private val downTimerFragment = DownTimerFragment()
    private val pomodoroFragment = PomodoroFragment()

    private var upTimerEvent: TimerEvent = TimerEvent.UpTimerStop
    private var downTimerEvent: TimerEvent = TimerEvent.DownTimerStop
    private var pomodoroTimerEvent: TimerEvent = TimerEvent.PomodoroTimerStop

    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentList = listOf(timerFragment, downTimerFragment, pomodoroFragment)
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        initNavBar(binding.bottomNavigationView)
        viewPagerMenu()
        setObservers()
        setEventObservers()
        weekResetTime()
    }
    private fun initNavBar(navbar: BottomNavigationView){
        navbar.run{
            setOnItemSelectedListener {
                when(it.itemId){
                    R.id.UpTimer -> { binding.viewPager.currentItem = 0 }
                    R.id.DownTimer -> { binding.viewPager.currentItem = 1  }
                    R.id.PomodoroTimer -> { binding.viewPager.currentItem = 2  }
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
    private fun setEventObservers() {
        UpTimerService.timerEvent.observe(this){
            upTimerEvent = it
            cumulativeCycleTimer()
        }
        DownTimerService.timerEvent.observe(this){
            downTimerEvent = it
            cumulativeCycleTimer()
        }
        PomodoroService.timerEvent.observe(this){
            pomodoroTimerEvent = it
            cumulativeCycleTimer()
        }
    }
    private fun setObservers(){
        UpTimerService.cumulativeTimer.observe(this){
            binding.cumulativeCycleTimer.text = TimerUtil.getFormattedSecondTime(it, false)
            weekResetTime()
            Log.d("atm",it.toString())
        }
        UpTimerService.CCTEvent.observe(this){
            isTimerRunning = it == TimerEvent.CumulativeTimerStart
        }
    }
    private fun cumulativeCycleTimer() {
        if (upTimerEvent == TimerEvent.UpTimerStop && downTimerEvent == TimerEvent.DownTimerStop
            && pomodoroTimerEvent != TimerEvent.PomodoroTimerStart
        ) {
            sendCommandToService(ACTION_CUMULATIVE_TIMER_STOP, 0)
        } else {
            if(!isTimerRunning) {
                val timer = binding.cumulativeCycleTimer.text.toString()
                sendCommandToService(ACTION_CUMULATIVE_TIMER_START, TimerUtil.getLongTimer(timer))
            }
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        this.startService(Intent(this, UpTimerService::class.java).apply {
            this.action = action
            this.putExtra("data",data)
        })
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
}