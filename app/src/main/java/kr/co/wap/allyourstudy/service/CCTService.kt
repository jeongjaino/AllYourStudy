package kr.co.wap.allyourstudy.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.TimerActivity
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*

class CCTService: LifecycleService() {
    companion object{
        val cumulativeTimer = MutableLiveData<Long>()
        val timerEvent = MutableLiveData<TimerEvent>()
    }

    private var isServiceStopped = false

    override fun onCreate() {
        super.onCreate()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                ACTION_CUMULATIVE_TIMER_START ->{
                    timerEvent.postValue(TimerEvent.CumulativeTimerStart)
                    startCumulativeTimer(it.getLongExtra("data",-1))
                }
                ACTION_CUMULATIVE_TIMER_STOP ->{
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun stopService(){
        isServiceStopped = true
        timerEvent.postValue(TimerEvent.CumulativeTimerStop)
        stopSelf()
    }
    private fun startCumulativeTimer(data: Long){
        var timeStarted = data * 1000
        CoroutineScope(Dispatchers.Main).launch{
            while(!isServiceStopped && timerEvent.value!! == TimerEvent.CumulativeTimerStart){
                cumulativeTimer.postValue(timeStarted)
                timeStarted += 1000
                delay(993L)
            }
        }
    }
}