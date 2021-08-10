package kr.co.wap.allyourstudy

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.BUTTON_POSITIVE
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.Service.TimerService
import kr.co.wap.allyourstudy.databinding.FragmentTimerBinding
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment: Fragment() {

    private var isTimerRunning = false

    val binding by lazy{ FragmentTimerBinding.inflate(layoutInflater)}

    var mainActivity: MainActivity? = null

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
            Log.d("Tag","0")
            toggleTimer()
        }
        setObservers()
        return binding.root
    }
    private fun setObservers(){
        TimerService.timerEvent.observe(viewLifecycleOwner){
            Log.d("Tag","1")
            updateUi(it)
        }

        TimerService.timerInMillis.observe(viewLifecycleOwner) {
            binding.timer.text =TimerUtil.getFormattedSecondTime(it, false) //timer
            Log.d("tag","212")
        }
        TimerService.timerInMin.observe(viewLifecycleOwner){
            binding.timer.text =TimerUtil.getFormattedSecondTime(it, true) //tick
            Log.d("tag","21")
        }
    }

    private fun toggleTimer(){
        if (!isTimerRunning) {
            Log.d("Tag","5")
            sendCommandToService(ACTION_TIMER_START,0)
            binding.downTimerFab.visibility = View.GONE
        } else {
            sendCommandToService(ACTION_TIMER_STOP,0)
            binding.downTimerFab.visibility = View.VISIBLE
        }
    }

    private fun toggleDownTimer(timeInSecond: Long){
        Log.d("tag","${isTimerRunning}")
        if(!isTimerRunning){
            sendCommandToService(ACTION_DOWNTIMER_START,timeInSecond)
            Log.d("Tag","${binding.timer.text}")
            binding.timerFab.visibility = View.GONE
        }
        else{
            sendCommandToService(ACTION_DOWNTIMER_STOP,0)
            binding.timerFab.visibility = View.VISIBLE
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
                Log.d("Tag","3")
                isTimerRunning = true
                binding.timerFab.setImageResource(R.drawable.ic_baseline_alarm_off_24)
            }
            is TimerEvent.END -> {
                Log.d("Tag","4")
                isTimerRunning = false
                binding.timerFab.setImageResource(R.drawable.ic_baseline_access_alarm_24)
            }
        }
    }
    private fun getTime(context: Context) {
        if(binding.timer.text == "00:00:00") {
            Log.d("tag","3212")
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                val timeInSecond = hour * 3600 + minute * 60
                binding.timer.text = SimpleDateFormat("HH:mm").format(cal.time)

                toggleDownTimer(timeInSecond.toLong())
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true).show()
        }
        else{
            Log.d("tag","321")
            toggleDownTimer(0)
        }
    }
}
