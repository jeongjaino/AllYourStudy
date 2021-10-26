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

    @Query("update room_calendar set checked = :checked where `no` =:no")
    fun modifyChecked(no : Long, checked: Boolean)
}
