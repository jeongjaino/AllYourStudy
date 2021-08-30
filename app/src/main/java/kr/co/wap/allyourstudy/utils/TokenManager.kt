package kr.co.wap.allyourstudy.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    fun saveAccessToken(token: String, context: Context){
        val prefs: SharedPreferences = context.getSharedPreferences(ACCESS_TOKEN,Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }
    fun getAccessToken(context: Context): String?{
        val prefs: SharedPreferences = context.getSharedPreferences(ACCESS_TOKEN,Context.MODE_PRIVATE)
        return prefs.getString(ACCESS_TOKEN, null)
    }
    fun saveRefreshToken(token: String, context: Context){
        val prefs: SharedPreferences = context.getSharedPreferences(REFRESH_TOKEN,Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }
    fun getRefreshToken(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(REFRESH_TOKEN,Context.MODE_PRIVATE)
        return prefs.getString(REFRESH_TOKEN, null)
    }
}
