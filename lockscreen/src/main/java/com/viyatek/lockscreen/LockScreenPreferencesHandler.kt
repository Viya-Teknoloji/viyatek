package com.viyatek.lockscreen

import android.content.Context
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class LockScreenPreferencesHandler(val context: Context) {

    fun getPrefsManager(): ViyatekSharedPrefsHandler =
        ViyatekSharedPrefsHandler(context, Statics.LOCK_SCREEN_PREFS)
}