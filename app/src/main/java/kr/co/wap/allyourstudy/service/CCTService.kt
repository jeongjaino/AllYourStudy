package kr.co.wap.allyourstudy.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.PowerManager
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

class CCTService: LifecycleService() {
    companion object{
        val cumulativeTimer = MutableLiveData<Long>()
        val timerEvent = MutableLiveData<TimerEvent>()
    }

    private var isServiceStopped = false
    private lateinit var notificationManager: NotificationManagerCompat

    private val wakeLock: PowerManager.WakeLock by lazy {
        (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)

        wakeLock.acquire()

        val ccTimerNotificationBuilder :NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setContentIntent(getTimerActivityPendingIntent())
                .setSmallIcon(R.drawable.main_cloud)
                .setColor(ContextCompat.getColor(baseContext, R.color.up_timer_green))
                .setContentTitle("AllYourStudy")
                .setGroup(ALL_YOUR_STUDY)
                .setGroupSummary(true)

        startForeground(CCTIMER_NOTIFICATION_ID, ccTimerNotificationBuilder.build())
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                ACTION_CUMULATIVE_TIMER_START ->{
                   startService(it.getLongExtra("data",-1))
                }
                ACTION_CUMULATIVE_TIMER_STOP ->{
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startService(data: Long){
        timerEvent.postValue(TimerEvent.CumulativeTimerStart)
        startCumulativeTimer(data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }
    private fun stopService(){
        isServiceStopped = true
        timerEvent.postValue(TimerEvent.CumulativeTimerStop)
        stopSelf()
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
    private fun startCumulativeTimer(data: Long){
        var timeStarted = data * 1000
        CoroutineScope(Dispatchers.Main).launch{
            while(!isServiceStopped && timerEvent.value!! == TimerEvent.CumulativeTimerStart){
                cumulativeTimer.postValue(timeStarted)
                timeStarted += 1000
                delay(997L)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()
    }
}