package kr.co.wap.allyourstudy.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.FragmentPomodoroBinding
import kr.co.wap.allyourstudy.dialog.DownTimerDialogFragment
import kr.co.wap.allyourstudy.dialog.PomodoroRestDialogFragment
import kr.co.wap.allyourstudy.dialog.ResetDialogFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*
import java.text.SimpleDateFormat
import java.util.*


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
        resetDialog()
    }
    private fun updateUi(event: TimerEvent) {
        when (event) {
            is TimerEvent.START -> {
                isTimerRunning = true
                binding.pomodoroStartButton.text = "PAUSE"
            }
            is TimerEvent.END -> {
                isTimerRunning = false
                binding.pomodoroResetButton.visibility = View.VISIBLE
                binding.pomodoroProgress.setProgressStartColor(Color.RED)
                binding.pomodoroProgress.setProgressEndColor(Color.RED)
                binding.pomodoroStartButton.text = "START"
            }
            is TimerEvent.POMODORO_END ->{
                isTimerRunning = false
                pomdoroRestDialog()
            }
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        activity?.startService(Intent(activity, TimerService::class.java).apply {
            this.action = action
            this.putExtra("data",data)
        })
    }
    private fun setPomodoroRestTimer(){
        binding.pomodoroProgress.max = 5 * 60
        binding.pomodoroProgress.setProgressStartColor(Color.BLUE)
        binding.pomodoroProgress.setProgressEndColor(Color.BLUE)
        binding.pomodoroStartButton.visibility = View.GONE
    }
    private fun pomdoroRestDialog(){
        val dialog = PomodoroRestDialogFragment()
        dialog.setButtonClickListener(object : PomodoroRestDialogFragment.OnButtonClickListener {
            override fun onButtonYesClicked() {
                val time = binding.pomodoroTimer.text
                setPomodoroRestTimer()
                sendCommandToService(ACTION_POMODORO_REST_TIMER_START,TimerUtil.getLongTimer(time as String))
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "RestDialog")
    }
    private fun resetDialog(){
        val dialog = ResetDialogFragment()
        dialog.setButtonClickListener(object : ResetDialogFragment.OnButtonClickListener{
            override fun onButtonYesClicked() {
                sendCommandToService(ACTION_POMODORO_TIMER_STOP,0)
                binding.pomodoroProgress.progress = 0
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "resetDialog")
    }
}