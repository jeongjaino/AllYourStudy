package kr.co.wap.allyourstudy.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.FragmentTimerBinding
import kr.co.wap.allyourstudy.dialog.DownTimerDialogFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment: Fragment() {

    private var isTimerRunning = false

    val binding by lazy{ FragmentTimerBinding.inflate(layoutInflater)}

    var mainActivity: MainActivity? = null

    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("HH:mm:ss")

    private var cumulativeCycleTime: Long = 1

    private var currentTime: String ="00:00:00"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.downTimerFab.setOnClickListener {
            getTime(mainActivity!!)
        }

        binding.timerFab.setOnClickListener {
            toggleTimer()
        }
        setObservers()
        return binding.root
    }
    private fun setObservers(){
        TimerService.timerEvent.observe(viewLifecycleOwner){
            updateUi(it)
        }

        TimerService.timerInMillis.observe(viewLifecycleOwner) {
            if(it > 1000) {
                binding.cumulativeCycleTime.text = TimerUtil.getFormattedTime(cumulativeCycleTime)
                cumulativeCycleTime += 1
            }
            currentTime = TimerUtil.getFormattedSecondTime(it, false)
            binding.timer.text = currentTime //timer
        }
        TimerService.timerInMin.observe(viewLifecycleOwner){
            if(it.toInt() > 900) {
                binding.timerFab.visibility = View.GONE
                binding.cumulativeCycleTime.text = TimerUtil.getFormattedTime(cumulativeCycleTime)
                cumulativeCycleTime += 1
            }
            else {
                binding.timerFab.visibility = View.VISIBLE //다운 타이머 종료시
            }
            val currentTime = TimerUtil.getFormattedSecondTime(it, true)
            binding.timer.text = currentTime
        }
    }

    private fun toggleTimer(){
        if (!isTimerRunning) {
            sendCommandToService(ACTION_TIMER_START,0)
            binding.downTimerFab.visibility = View.GONE

        } else {
            sendCommandToService(ACTION_TIMER_STOP,0)
            binding.downTimerFab.visibility = View.VISIBLE
        }
    }

    private fun toggleDownTimer(timeInSecond: Long){
        Log.d("tag","${isTimerRunning}:")
        if(!isTimerRunning) {
            sendCommandToService(ACTION_DOWNTIMER_START, timeInSecond)
        }
        else{
            sendCommandToService(ACTION_DOWNTIMER_STOP,0)
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        activity?.startService(Intent(activity, TimerService::class.java).apply {
            this.action = action
            this.putExtra("data",data)
        })
    }
    private fun updateUi(event: TimerEvent){
        when (event) {
            is TimerEvent.START -> {
                isTimerRunning = true
                binding.timerFab.setImageResource(R.drawable.ic_baseline_alarm_off_24)
            }
            is TimerEvent.END -> {
                isTimerRunning = false
                binding.timerFab.setImageResource(R.drawable.ic_baseline_access_alarm_24)
            }
        }
    }
    private fun getTime(context: Context) {
       if(binding.timer.text == "00:00:00") {
           val cal = Calendar.getInstance()
           val dialog = DownTimerDialogFragment()
           dialog.setButtonClickListener(object: DownTimerDialogFragment.OnButtonClickListener  {
               override fun onButtonYesClicked() {
                   val hour = dialog.binding.hourPicker.value
                   val minute = dialog.binding.minPicker.value
                   val second = dialog.binding.secondPicker.value
                   cal.set(Calendar.HOUR_OF_DAY, hour)
                   cal.set(Calendar.MINUTE, minute)
                   cal.set(Calendar.SECOND, second)
                   dialog.binding.minPicker.value
                   val timeInSecond = hour * 3600 + minute * 60 + second
                   binding.timer.text = sdf.format(cal.time)

                   toggleDownTimer(timeInSecond.toLong())
               }
           })
           dialog.show(mainActivity!!.supportFragmentManager, "InsertDialog")
       }
        else{
            toggleDownTimer(0)
        }
    }
}
