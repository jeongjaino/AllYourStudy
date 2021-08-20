package kr.co.wap.allyourstudy.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.databinding.FragmentPomodoroBinding
import kr.co.wap.allyourstudy.dialog.ResetDialogFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.service.PomodoroService
import kr.co.wap.allyourstudy.utils.*


class PomodoroFragment : Fragment() {

    val binding by lazy{FragmentPomodoroBinding.inflate(layoutInflater)}

    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        initValues()

        binding.pomodoroStartButton.setOnClickListener{
            toggleTimer()
        }
        binding.pomodoroResetButton.setOnClickListener{
            resetDialog()
        }
        setObservers()
        return binding.root
    }
    private fun setObservers(){
        PomodoroService.timerEvent.observe(viewLifecycleOwner){
            updateUi(it)
        }
        PomodoroService.pomodoroTimer.observe(viewLifecycleOwner){
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
            sendCommandToService(ACTION_POMODORO_TIMER_PAUSE,0)
        }
    }
    private fun updateUi(event: TimerEvent) {
        when (event) {
            is TimerEvent.PomodoroTimerStart -> {
                isTimerRunning = true
                binding.pomodoroStartButton.text = "PAUSE"
            }
            is TimerEvent.PomodoroTimerStop -> {
                isTimerRunning = false
                binding.pomodoroStartButton.text = "START"
                initValues()
            }
            is TimerEvent.PomodoroRestTimerStart ->{
                isTimerRunning = false
                restTimerValues()
            }
        }
    }
    private fun sendCommandToService(action: String, data: Long) {
        activity?.startService(Intent(activity, PomodoroService::class.java).apply {
            this.action = action
            this.putExtra("data",data)
        })
    }
    private fun initValues(){
        binding.pomodoroStartButton.visibility = View.VISIBLE
        binding.pomodoroStartButton.text = "START"
        binding.pomodoroResetButton.text = "RESET"
        binding.pomodoroProgress.max = 25 * 60
        binding.pomodoroProgress.setProgressStartColor(Color.RED)
        binding.pomodoroProgress.setProgressEndColor(Color.RED)
    }
    private fun restTimerValues(){
        binding.pomodoroStartButton.visibility = View.GONE
        binding.pomodoroResetButton.text = "SKIP"
        binding.pomodoroProgress.max = 5 * 60
        binding.pomodoroProgress.setProgressStartColor(Color.BLUE)
        binding.pomodoroProgress.setProgressEndColor(Color.BLUE)
    }
    /*private fun setPomodoroRestTimer(){
        binding.pomodoroProgress.max = 5 * 60
        binding.pomodoroProgress.setProgressStartColor(Color.BLUE)
        binding.pomodoroProgress.setProgressEndColor(Color.BLUE)
        binding.pomodoroStartButton.visibility = View.GONE
        binding.pomodoroResetButton.visibility = View.GONE
    }
    private fun pomdoroRestDialog(){
        val dialog = PomodoroRestDialogFragment()
        dialog.setButtonClickListener(object : PomodoroRestDialogFragment.OnButtonClickListener {
            override fun onButtonYesClicked() {
                setPomodoroRestTimer()
                sendCommandToService(ACTION_POMODORO_REST_TIMER_START,0)
                Log.d("tag2","click")
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "RestDialog")
    }*/
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