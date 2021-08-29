package kr.co.wap.allyourstudy.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
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

    private var lapTime = 0L
    private var isServiceStopped = false

    private lateinit var notificationManager: NotificationManagerCompat

    private var cctTimerNotificationBuilder : NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("누적 시간")

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
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
        val timeStarted = System.currentTimeMillis() - data * 1000  //(data,second) (millis = second *1000)
        CoroutineScope(Dispatchers.Main).launch{
            while(!isServiceStopped && timerEvent.value!! == TimerEvent.CumulativeTimerStart){
                lapTime = System.currentTimeMillis() - timeStarted
                cumulativeTimer.postValue(lapTime)
                delay(1000L)
            }
        }
    }
}