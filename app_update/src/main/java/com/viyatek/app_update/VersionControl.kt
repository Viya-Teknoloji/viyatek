package com.viyatek.app_update

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager


import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.viyatek.app_update.Statics.LOG_TAG

class VersionControl(private val context: Context, private val defaultValue: Int) {

    private val updatePrefsHandler by lazy { UpdatePrefsHandler(context) }
    private val pInfo: PackageInfo by lazy { context.packageManager.getPackageInfo(context.packageName, 0) }
    val version: String by lazy { pInfo.versionName }
    private val verCode by lazy { PackageInfoCompat.getLongVersionCode(pInfo).toInt() }

    fun checkVersion(iUserOpenedFirstTime : iFirstOpen? = null): Boolean {
        //Get App Version

        return try {
            // avoid huge version numbers and you will be ok
            Log.d(LOG_TAG, "Version: $version Version Code: $verCode")

            //Get old ver code
            var verCodeOld = getOldVerCode()
            Log.d(LOG_TAG, "Version Code Old: $verCodeOld")

            //Compare versions. If new, execute new version function
            if (verCodeOld == 0) {
                verCodeOld = verCode
                updatePrefsHandler.setVersionCode(verCode)
                iUserOpenedFirstTime?.userOpenedAppFirstTime()
            }

            verCode > verCodeOld
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getOldVerCode() = updatePrefsHandler.getVersionCode(defaultValue)
}