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
    var text: String = ""

    @ColumnInfo
    var level: String =""

    @ColumnInfo
    var checked: Boolean = false

    constructor(date: String, text: String, level: String, checked: Boolean){
        this.date = date
        this.text = text
        this.level = level
        this.checked = checked
    }
}