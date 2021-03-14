package com.viyatek.helper

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat

class GetAppVersionCode(val mCtxt: Context) {

    fun GetCode(): Int {

        val verCode: Int
        val pInfo: PackageInfo

        try {
            pInfo = mCtxt.packageManager.getPackageInfo(mCtxt.packageName, 0)
            val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
            verCode = longVersionCode.toInt() // avoid huge version numbers and you will be ok
            return verCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }
}