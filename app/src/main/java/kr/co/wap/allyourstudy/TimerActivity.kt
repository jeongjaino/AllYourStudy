package kr.co.wap.allyourstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import kr.co.wap.allyourstudy.adapter.FragmentAdapter
import kr.co.wap.allyourstudy.databinding.ActivityTimerBinding
import kr.co.wap.allyourstudy.fragments.DownTimerFragment
import kr.co.wap.allyourstudy.fragments.PomodoroFragment
import kr.co.wap.allyourstudy.fragments.TimerFragment

class TimerActivity : AppCompatActivity() {

    private val binding by lazy{ActivityTimerBinding.inflate(layoutInflater)}

    private val timerFragment = TimerFragment()
    private val downTimerFragment = DownTimerFragment()
    private val pomodoroFragment = PomodoroFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var fragmentList = listOf(timerFragment, downTimerFragment, pomodoroFragment)
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        initNavBar(binding.bottomNavigationView)
        viewPagerMenu()
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
    /*private fun weekResetTime(){
    binding.cumulativeCycleTime.text = TimerUtil.getFormattedTime(cumulativeCycleTime)
                cumulativeCycleTime += 1
    private var cumulativeCycleTime: Long = 1
        val currentTime = Calendar.getInstance().time
        val weekdayFormat = SimpleDateFormat("EE", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val weekday = weekdayFormat.format(currentTime)
        val time = timeFormat.format(currentTime)

        if(weekday == "월" && time == "12:32:00"){
            cumulativeCycleTime = 0
        }
    }*/
}