package kr.co.wap.allyourstudy.model

sealed class TimerEvent{
    object UpTimerStart : TimerEvent()
    object UpTimerStop : TimerEvent()

    object DownTimerStart: TimerEvent()
    object DownTimerStop: TimerEvent()

    object PomodoroTimerStart: TimerEvent()
    object PomodoroTimerStop: TimerEvent()

    object PomodoroRestTimerStart: TimerEvent()
    object PomodoroRestTimerStop: TimerEvent()

    object CumulativeTimerStart: TimerEvent()
    object CumulativeTimerStop: TimerEvent()
}
