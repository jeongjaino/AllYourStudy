package kr.co.wap.allyourstudy.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private var pomodoroTimerNotificationBuilder : NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("뽀모도로 타이머")
            .setContentText("00:00:00")



    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent.let{
            when(it?.action){
                ACTION_POMODORO_TIMER_START -> {
                    startForegroundService(it.action!!, it.getLongExtra("data",-1)  )}

                ACTION_POMODORO_TIMER_STOP ->{
                    stopService(false)}

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
        startForeground(POMODORO_TIMER_NOTIFICATION_ID, pomodoroTimerNotificationBuilder.build())

        pomodoroTimer.observe(this) {
            if (!isServiceStopped) {
                pomodoroTimerNotificationBuilder
                    .setContentIntent(getTimerActivityPendingIntent())
                    .setContentText(TimerUtil.getFormattedSecondTime(it, true))
                if(timerEvent.value == TimerEvent.PomodoroRestTimerStart){
                    pomodoroTimerNotificationBuilder
                        .setContentTitle("휴식시간")
                }
                notificationManager.notify(POMODORO_TIMER_NOTIFICATION_ID, pomodoroTimerNotificationBuilder.build())
            }
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
        var starting: Long = data * 1000 + 50
        CoroutineScope(Dispatchers.Main).launch {
            object  : CountDownTimer(starting, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    if(!isServiceStopped && timerEvent.value!! == TimerEvent.PomodoroTimerStart) {
                        starting = millisUntilFinished
                        pomodoroTimer.postValue(starting)
                    }
                }
                override fun onFinish() {
                    timerEvent.postValue(TimerEvent.PomodoroRestTimerStart)
                    startRestTimer()
                }
            }.start()
        }
    }
    private fun startRestTimer(){
        var starting: Long = 5 * 60 * 1000
        CoroutineScope(Dispatchers.Main).launch {
            object: CountDownTimer(starting, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    if(timerEvent.value == TimerEvent.PomodoroRestTimerStart){
                        starting = millisUntilFinished
                        pomodoroTimer.postValue(starting)
                    }
                }
                override fun onFinish() {
                    pomodoroTimer.postValue(25*1000*60L)
                    timerEvent.postValue(TimerEvent.PomodoroRestTimerStop)
                }
            }.start()
        }
    }
}