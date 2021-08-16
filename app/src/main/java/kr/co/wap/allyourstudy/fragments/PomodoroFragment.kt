package kr.co.wap.allyourstudy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.FragmentPomodoroBinding
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.ACTION_POMODORO_TIMER_START
import kr.co.wap.allyourstudy.utils.ACTION_POMODORO_TIMER_STOP
import kr.co.wap.allyourstudy.utils.ACTION_TIMER_PAUSE
import kr.co.wap.allyourstudy.utils.TimerUtil


class PomodoroFragment : Fragment() {

    val binding by lazy{FragmentPomodoroBinding.inflate(layoutInflater)}

    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.pomodoroProgress.max = 25 * 60
        binding.pomodoroStartButton.setOnClickListener{
            toggleTimer()
        }
        binding.pomodoroResetButton.setOnClickListener{
            resetTimer()
        }
        setObservers()
        return binding.root
    }
    private fun setObservers(){
        TimerService.timerEvent.observe(viewLifecycleOwner){
            updateUi(it)
        }
        TimerService.timerPomodoro.observe(viewLifecycleOwner){
            binding.pomodoroTimer.text = TimerUtil.getFormattedSecondTime(it, true)
            binding.pomodoroProgress.progress = (it/1000).toInt()
        }
    }
    private fun toggleTimer(){
        val time = binding.pomodoroTimer.text
        if(!isTimerRunning){
            sendCommandToService(ACTION_POMODORO_TIMER_START,TimerUtil.getLongTimer(time as String))
        }
        else{
            sendCommandToService(ACTION_TIMER_PAUSE,0)
        }
    }
    private fun resetTimer(){
        sendCommandToService(ACTION_POMODORO_TIMER_STOP,0)
        binding.pomodoroProgress.progress = 0
    }
    private fun updateUi(event: TimerEvent) {
        when (event) {
            is TimerEvent.START -> {
                isTimerRunning = true
                binding.pomodoroStartButton.text = "PAUSE"
            }
            is TimerEvent.END -> {
                isTimerRunning = false
                binding.pomodoroStartButton.text = "START"
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