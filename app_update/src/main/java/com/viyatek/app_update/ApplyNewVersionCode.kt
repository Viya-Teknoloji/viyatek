package com.viyatek.app_update

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class ApplyNewVersionCode(val context: Context) {

    val sharedPreferences by lazy { ViyatekSharedPrefsHandler(context, Statics.UPDATE_PREFS_NAME) }

    fun applySharedPrefNewVersionCode() {
        val verCode: Long
        var pInfo: PackageInfo? = null
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {

            e.printStackTrace()
        }
        val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo!!)
        verCode = longVersionCode  // avoid huge version numbers and you will be ok

        //Write new ver codex
        sharedPreferences.applyPrefs(Statics.VERSION_CODE, verCode.toInt())
    }
}