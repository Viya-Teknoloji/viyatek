package com.viyatek.lockscreen

import android.content.Context
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class LockScreenPreferencesHandler(val context: Context) {

    private val lockPrefsManager by lazy{getPrefsManager()}

    fun getPrefsManager(): ViyatekSharedPrefsHandler = ViyatekSharedPrefsHandler(context, Statics.LOCK_SCREEN_PREFS)

    fun isLockScreenOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_LOCK_SCREEN_OK, true)
    fun setLockScreenOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_LOCK_SCREEN_OK, isOk)

    fun isLockScreenNotificationOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_LOCK_SCREEN_NOTIFICATION_OK, true)
    fun setLockScreenNotificationOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_LOCK_SCREEN_NOTIFICATION_OK, isOk)

    fun isNightOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_NIGHT_OK, true)
    fun setNightOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_NIGHT_OK, isOk)

    fun isMorningOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_MORNING_OK, true)
    fun setMorningOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_MORNING_OK, isOk)

    fun isAfternoonOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_AFTERNOON_OK, true)
    fun setAfterNoonOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_AFTERNOON_OK, isOk)

    fun isEveningOk():Boolean = lockPrefsManager.getBooleanValue(Statics.IS_EVENING_OK, true)
    fun setEveningOk(isOk : Boolean) = lockPrefsManager.applyPrefs(Statics.IS_EVENING_OK, isOk)

    fun getMustShowFactCount() : Int = lockPrefsManager.getIntegerValue(Statics.SHOW_FACT_COUNT, 15)
    fun setMustShowFactCount(factCount : Int) = lockPrefsManager.applyPrefs(Statics.SHOW_FACT_COUNT, factCount)

    fun getSeenFactCount() : Int = lockPrefsManager.getIntegerValue(Statics.SEEN_FACTS_SUM_SO_FAR, 0)
    fun setSeenFactCount(factCount : Int) = lockPrefsManager.applyPrefs(Statics.SEEN_FACTS_SUM_SO_FAR, factCount)

    fun getLastDayOpened() : Int = lockPrefsManager.getIntegerValue(Statics.LAST_DAY_OPENED, 0)
    fun setLastDayOpened(dayCount : Int) = lockPrefsManager.applyPrefs(Statics.LAST_DAY_OPENED, dayCount)

    fun getShowTime() : Long = lockPrefsManager.getLongVale(Statics.SHOW_TIME, 0L)
    fun setShowTime(showTime : Long) = lockPrefsManager.applyPrefs(Statics.SHOW_TIME, showTime)

}