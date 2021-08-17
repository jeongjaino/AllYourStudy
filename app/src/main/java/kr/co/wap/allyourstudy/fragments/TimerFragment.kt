package kr.co.wap.allyourstudy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.FragmentTimerBinding
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*

class TimerFragment: Fragment() {

    private var isTimerRunning = false

    private val binding by lazy{ FragmentTimerBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.upTimerStartButton.setOnClickListener {
            if(binding.upTimer.text == "00:00:00") {
                toggleTimer(0)
            }
            else{
                toggleTimer(TimerUtil.getLongTimer(binding.upTimer.text.toString()))
            }
        }
        binding.upTimerResetButton.setOnClickListener{
            upTimerReset()
        }
        setObservers()
        return binding.root
    }
    private fun setObservers(){
        TimerService.timerEvent.observe(viewLifecycleOwner){
            Log.d("event",it.toString())
            updateUi(it)
        }
        TimerService.timerInMillis.observe(viewLifecycleOwner) {
            Log.d("tag",it.toString())
            binding.upTimer.text = TimerUtil.getFormattedSecondTime(it, false)
        }
    }
    private fun toggleTimer(data: Long){
        if (!isTimerRunning) {
            sendCommandToService(ACTION_TIMER_START, data)
        } else {
            sendCommandToService(ACTION_TIMER_PAUSE, data)
        }
    }
    private fun upTimerReset(){
        sendCommandToService(ACTION_TIMER_STOP, 0)
    }
    private fun updateUi(event: TimerEvent){
        when (event) {
            is TimerEvent.START -> {
                isTimerRunning = true
                binding.upTimerStartButton.text = "PAUSE"
            }
            is TimerEvent.END -> {
                isTimerRunning = false
                binding.upTimerStartButton.text = "START"
            }
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        activity?.startService(Intent(activity, TimerService::class.java).apply {
            this.action = action
            this.putExtra("data",data)
        })
    }
}
