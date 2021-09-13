package kr.co.wap.allyourstudy.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_calendar")
class RoomCalendar {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var no: Long? = null

    @ColumnInfo
    var date: String = ""

    @ColumnInfo
    var weekday: String = ""

    @ColumnInfo
    var check: String =""

    constructor(date: String, weekday: String, check: String){
        this.date = date
        this.weekday = weekday
        this.check = check
    }
}