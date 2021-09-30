package kr.co.wap.allyourstudy.fragments.timer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.wap.allyourstudy.databinding.FragmentDownTimerBinding
import kr.co.wap.allyourstudy.dialog.DownTimerDialogFragment
import kr.co.wap.allyourstudy.dialog.ResetDialogFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.DownTimerService
import kr.co.wap.allyourstudy.utils.*
import java.text.SimpleDateFormat
import java.util.*

class DownTimerFragment : Fragment() {

    private val binding by lazy{ FragmentDownTimerBinding.inflate(layoutInflater)}

    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.downTimerStartButton.setOnClickListener{
            if(binding.downTimer.text == "00:00:00")
            timeDialog()
            else{
                toggleDownTimer(TimerUtil.getLongTimer(binding.downTimer.text.toString()))
            }
        }
        binding.downTimerResetButton.setOnClickListener{
            resetDialog()
        }
        setObservers()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(DownTimerService.timerMax.value != null ) {
            if(DownTimerService.timerMax.value!! > 0 ) {
                binding.downTimerProgress.max = DownTimerService.timerMax.value!!
            }
        }
    }
    private fun setObservers(){
        DownTimerService.timerEvent.observe(viewLifecycleOwner){
            updateUi(it)
        }
        DownTimerService.downTimer.observe(viewLifecycleOwner){
            binding.downTimer.text = TimerUtil.getFormattedSecondTime(it, true)
            binding.downTimerProgress.progress = (it/1000).toInt()
        }
    }
    private fun toggleDownTimer(setTime: Long){
        if(!isTimerRunning){
            sendCommandToService(ACTION_DOWN_TIMER_START,setTime)
        }
        else{
            sendCommandToService(ACTION_DOWN_TIMER_PAUSE,0)
        }
    }
    private fun updateUi(event: TimerEvent){
        when (event) {
            is TimerEvent.DownTimerStart -> {
                isTimerRunning = true
                binding.downTimerStartButton.text = "PAUSE"
            }
            is TimerEvent.DownTimerStop -> {
                isTimerRunning = false
                binding.downTimerStartButton.text = "START"
            }
        }
    }
    private fun timeDialog() {
        val cal = Calendar.getInstance()
        val dialog = DownTimerDialogFragment()
        dialog.setButtonClickListener(object : DownTimerDialogFragment.OnButtonClickListener {
            override fun onButtonYesClicked() {
                val hour = dialog.binding.hourPicker.value
                val minute = dialog.binding.minPicker.value
                val second = dialog.binding.secondPicker.value
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, second)
                dialog.binding.minPicker.value
                val timeInSecond = hour * 3600 + minute * 60 + second
                binding.downTimer.text = SimpleDateFormat("HH:mm:ss").format(cal.time)
                binding.downTimerProgress.max = timeInSecond
                Log.d("timeInSecond",timeInSecond.toString())
                toggleDownTimer(timeInSecond.toLong())
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "InsertDialog")
    }
    private fun sendCommandToService(action: String, data: Long) {
        activity?.startForegroundService(Intent(activity, DownTimerService::class.java).apply {
            this.action = action
            this.putExtra("data", data)
        })
    }
    private fun resetDialog(){
        val dialog = ResetDialogFragment()
        dialog.setButtonClickListener(object : ResetDialogFragment.OnButtonClickListener{
            override fun onButtonYesClicked() {
                sendCommandToService(ACTION_DOWN_TIMER_STOP,0)
                binding.downTimerProgress.progress = 0
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "resetDialog")
    }
}