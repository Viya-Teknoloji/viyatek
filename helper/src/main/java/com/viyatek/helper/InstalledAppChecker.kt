package com.viyatek.helper

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException

class InstalledAppChecker(val context: Context) {
    fun isAppInstalled(packageName : String) : Boolean{
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: NameNotFoundException) {
            false
        }
    }
}