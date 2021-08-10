package kr.co.wap.allyourstudy.model

sealed class TimerEvent{
    object START : TimerEvent()
    object END : TimerEvent()
}