package kr.co.wap.allyourstudy.room

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(RoomCalendar::class), version = 1, exportSchema = false)
abstract class RoomHelper: RoomDatabase() {
    abstract fun roomCalendarDao(): RoomCalendarInterface

    companion object{
        private var instance: RoomHelper?= null


        @Synchronized
        fun getInstance(context: Context): RoomHelper? {
            if (instance == null) {
                synchronized(RoomHelper::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomHelper::class.java,
                        "room-helper"
                    ).build()
                }
            }
            return instance
        }
    }
}