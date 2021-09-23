package kr.co.wap.allyourstudy.service
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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

class PomodoroService : LifecycleService(){

    companion object{
        val timerEvent = MutableLiveData<TimerEvent>()
        val pomodoroTimer = MutableLiveData<Long>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var isServiceStopped = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)

        val pomodoroTimerNotificationBuilder : NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setGroup(ALL_YOUR_STUDY)
                .setColor(ContextCompat.getColor(baseContext, R.color.pomodoro_red))
                .setSmallIcon(R.drawable.pomodoro_timer)
                .setContentTitle("뽀모도로 타이머")
                .setContentText("00:00:00")

        startForeground(POMODORO_TIMER_NOTIFICATION_ID, pomodoroTimerNotificationBuilder.build())

        pomodoroTimer.observe(this) {
            if (!isServiceStopped) {
                pomodoroTimerNotificationBuilder
                    .setContentTitle("뽀모도로 타이머")
                if(timerEvent.value == TimerEvent.PomodoroRestTimerStart){
                    pomodoroTimerNotificationBuilder
                        .setContentTitle("휴식시간")
                }
                pomodoroTimerNotificationBuilder
                    .setContentIntent(getTimerActivityPendingIntent())
                    .setContentText(TimerUtil.getFormattedSecondTime(it, true))
                notificationManager.notify(POMODORO_TIMER_NOTIFICATION_ID, pomodoroTimerNotificationBuilder.build())
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent.let{
            when(it?.action){
                ACTION_POMODORO_TIMER_START -> {
                    startForegroundService(it.action!!, it.getLongExtra("data",-1)  )}

                ACTION_POMODORO_TIMER_STOP ->{
                    stopService(false) }

                ACTION_POMODORO_TIMER_PAUSE ->{
                    stopService(true)}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun stopService(pause: Boolean){
        isServiceStopped = true
        timerEvent.postValue(TimerEvent.PomodoroTimerStop)
        if(!pause) {
            pomodoroTimer.postValue(25*1000*60L)
        }
        notificationManager.cancel(POMODORO_TIMER_NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }
    private fun startForegroundService(action: String, data: Long) {
        timerEvent.postValue(TimerEvent.PomodoroTimerStart)
        if(action == ACTION_POMODORO_TIMER_START){
            startPomodoroTimer(data)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
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

    private fun getTimerActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            421,
            Intent(this, TimerActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun startPomodoroTimer(data: Long){
        var starting: Long = data * 1000
        CoroutineScope(Dispatchers.Main).launch {
            while (!isServiceStopped && timerEvent.value!! == TimerEvent.PomodoroTimerStart) {
                pomodoroTimer.postValue(starting)
                if (starting == 0L) {
                    delay(100) //누적시간이 따라오는시간
                    timerEvent.postValue(TimerEvent.PomodoroRestTimerStart)
                    startRestTimer()
                    break
                }
                starting -= 1000
                delay(997L)
            }
        }
    }
    private fun startRestTimer(){
        var starting: Long = 5 * 60 * 1000
        CoroutineScope(Dispatchers.Main).launch {
            while (!isServiceStopped && timerEvent.value!! == TimerEvent.PomodoroRestTimerStart) {
                pomodoroTimer.postValue(starting)
                if (starting == 0L) {
                    timerEvent.postValue(TimerEvent.PomodoroTimerStart)
                    startPomodoroTimer(25*60L)
                    break
                }
                starting -= 1000
                delay(997L)
            }
        }
    }
}