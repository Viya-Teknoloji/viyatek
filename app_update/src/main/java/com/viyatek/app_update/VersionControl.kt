package com.viyatek.app_update

import android.content.Context
import android.content.pm.PackageManager


import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.viyatek.app_update.Statics.LOG_TAG
import com.viyatek.app_update.Statics.UPDATE_PREFS_NAME
import com.viyatek.app_update.Statics.VERSION_CODE
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class VersionControl(private val context: Context, private val defaultValue: Int) {

    val mSharedPreferences by lazy { ViyatekSharedPrefsHandler(context, UPDATE_PREFS_NAME) }
    val pInfo by lazy { context.packageManager.getPackageInfo(context.packageName, 0) }
    val version by lazy { pInfo.versionName }
    val verCode by lazy { PackageInfoCompat.getLongVersionCode(pInfo).toInt() }


    fun checkVersion(): Boolean {
        //Get App Version

        return try {
            // avoid huge version numbers and you will be ok
            Log.d(LOG_TAG, "Version: $version Version Code: $verCode")

            //Get old ver code
            var verCodeOld = mSharedPreferences.getIntegerValue(VERSION_CODE, defaultValue)
            Log.d(LOG_TAG, "Version Code Old: $verCodeOld")

            //Compare versions. If new, execute new version function
            if (verCodeOld == 0) {
                verCodeOld = verCode
                mSharedPreferences.applyPrefs(VERSION_CODE, verCode)
            }

            verCode > verCodeOld
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getOldVerCode() = mSharedPreferences.getIntegerValue(VERSION_CODE, defaultValue)
}