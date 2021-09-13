package kr.co.wap.allyourstudy.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface RoomCalendarInterface {
    @Query("select * from room_calendar")
    fun getAll(): List<RoomCalendar>

    @Insert(onConflict = REPLACE)
    fun insert(calendar: RoomCalendar)

    @Delete
    fun delete(calendar: RoomCalendar)

    @Query("select * from room_calendar WHERE :date")
    fun loadByDate(date: String): RoomCalendar
}