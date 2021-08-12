package kr.co.wap.allyourstudy.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.TimerFragment
import kr.co.wap.allyourstudy.model.TimerEvent
import kr.co.wap.allyourstudy.utils.*
import java.util.concurrent.TimeUnit

class TimerService: LifecycleService() {

    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        val timerInMin = MutableLiveData<Long>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var isServiceStopped = false

    private var lapTime = 0L

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        initValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val any = when (it.action) {
                ACTION_TIMER_START -> {
                    Log.d("tag", "startService")
                    startForegroundService(it.action!!,0)
                }
                ACTION_TIMER_STOP -> {
                    Log.d("tag", "stopService")
                    stopService()
                }
                ACTION_DOWNTIMER_START ->{
                    startForegroundService(it.action!!,it.getLongExtra("data", -1))
                }
                ACTION_DOWNTIMER_STOP ->{
                    Log.d("tag", "stopService")
                    stopService()
                }
                else -> Log.d("Tag","Else")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initValues(){
        timerEvent.postValue(TimerEvent.END)
        timerInMillis.postValue(0L)
        timerInMin.postValue(0L)
    }

    private fun startForegroundService(action: String, data: Long) {
        timerEvent.postValue(TimerEvent.START)
        if(action == ACTION_TIMER_START) {
            startTimer()
        }
        else{
            startDownTimer(data)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, getNotificationBuilder().build())

        timerInMillis.observe(this, Observer {
            if(!isServiceStopped){
                val builder = getNotificationBuilder().setContentText(
                    TimerUtil.getFormattedSecondTime(it, false)
                )
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        })
        timerInMin.observe(this, Observer {
            if(!isServiceStopped){
                val builder = getNotificationBuilder().setContentText(
                    TimerUtil.getFormattedSecondTime(it, true)
                )
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotificationBuilder() :NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("AllYourStudy")
            .setContentText("00:00:00")
            .setContentIntent(getTimerFragmentPendingIntent())


    private fun getTimerFragmentPendingIntent() =
        PendingIntent.getActivity(
            this,
            420,
            Intent(this, TimerFragment::class.java).apply{
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun stopService(){
        isServiceStopped = true
        initValues()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }
    private fun startTimer(){
        val timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch{
            while(!isServiceStopped && timerEvent.value!! == TimerEvent.START){
                lapTime = System.currentTimeMillis() - timeStarted
                timerInMillis.postValue(lapTime)
                delay(1000L)
            }
        }
    }
    private fun startDownTimer(data: Long){
        var starting = data*1000
        CoroutineScope(Dispatchers.Main).launch {
            object : CountDownTimer(starting, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if(!isServiceStopped && timerEvent.value!! == TimerEvent.START) {
                        starting = millisUntilFinished
                        timerInMin.postValue(starting)
                    }
                }
                override fun onFinish() {
                    stopService()   
                }
            }.start()
        }
    }
}