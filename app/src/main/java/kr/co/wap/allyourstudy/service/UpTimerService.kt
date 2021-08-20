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
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.TimerActivity
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*

class UpTimerService: LifecycleService() {

    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val CCTEvent = MutableLiveData<TimerEvent>()
        val upTimer = MutableLiveData<Long>()
        val cumulativeTimer = MutableLiveData<Long>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var isServiceStopped = false

    private var lapTime = 0L

    private var upTimerNotificationBuilder :NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("스톱워치")
            .setContentText("00:00:00")

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val any = when (it.action) {
                ACTION_UP_TIMER_START -> {
                    startForegroundService(it.action!!,it.getLongExtra("data",-1))
                }
                ACTION_UP_TIMER_STOP -> {
                    stopService(false)
                }
                ACTION_UP_TIMER_PAUSE ->{
                   stopService(true)
                }
                ACTION_CUMULATIVE_TIMER_START ->{
                    CCTEvent.postValue(TimerEvent.CumulativeTimerStart)
                    startCumulativeTimer(it.getLongExtra("data",-1))
                }
                ACTION_CUMULATIVE_TIMER_STOP ->{
                    CCTEvent.postValue(TimerEvent.CumulativeTimerStop)
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun stopService(pause: Boolean){
        isServiceStopped = true
        timerEvent.postValue(TimerEvent.UpTimerStop)
        if(!pause) {
           upTimer.postValue(0L)
        }
        notificationManager.cancel(UP_TIMER_NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }
    private fun startForegroundService(action: String, data: Long) {
        timerEvent.postValue(TimerEvent.UpTimerStart)
        if(action == ACTION_UP_TIMER_START){
            startUpTimer(data)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startForeground(UP_TIMER_NOTIFICATION_ID, upTimerNotificationBuilder.build())

        upTimer.observe(this) {
            if (!isServiceStopped) {
                upTimerNotificationBuilder
                    .setContentIntent(getTimerActivityPendingIntent())
                    .setContentText(TimerUtil.getFormattedSecondTime(it, false)
                )
                notificationManager.notify(UP_TIMER_NOTIFICATION_ID, upTimerNotificationBuilder.build())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)
    }
    private fun getTimerActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            420,
            Intent(this, TimerActivity::class.java).apply{
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun startUpTimer(data: Long,){
        val timeStarted = System.currentTimeMillis() - data * 1000  //(data,second) (millis = second *1000)
        CoroutineScope(Dispatchers.Main).launch{
            while(!isServiceStopped && timerEvent.value!! == TimerEvent.UpTimerStart){
                lapTime = System.currentTimeMillis() - timeStarted
                upTimer.postValue(lapTime)
                delay(1000L)
            }
        }
    }
    private fun startCumulativeTimer(data: Long){
        val timeStarted = System.currentTimeMillis() - data * 1000  //(data,second) (millis = second *1000)
        CoroutineScope(Dispatchers.Main).launch{
            while(CCTEvent.value!! == TimerEvent.CumulativeTimerStart){
                lapTime = System.currentTimeMillis() - timeStarted
                cumulativeTimer.postValue(lapTime)
                delay(1000L)
            }
        }
    }
}