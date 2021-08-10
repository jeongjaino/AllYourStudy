package kr.co.wap.allyourstudy.utils

import java.util.concurrent.TimeUnit

object TimerUtil {
    fun getFormattedSecondTime(time: Long, divide: Boolean): String{

        if(divide) {
            var seconds = time / 1000

            val hours = TimeUnit.SECONDS.toHours(seconds)
            seconds -= TimeUnit.HOURS.toSeconds(hours)
            val minutes = TimeUnit.SECONDS.toMinutes(seconds)
            seconds -= TimeUnit.MINUTES.toSeconds(minutes)
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        else {
            var milliseconds = time
            val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
            milliseconds -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
    }
}