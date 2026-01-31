package com.example.cup2.utils
import android.content.Context
object SheetPrefs {
    private const val PREF_NAME="sheet_prefs"
    private const val KEY_COUNT="last_count"
    fun saveLastCount(context:Context,count:Int){
        val prefs=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_COUNT,count).apply()
    }
    fun getLastCount(context:Context):Int{
        val prefs=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        return prefs.getInt(KEY_COUNT,0)

    }
}