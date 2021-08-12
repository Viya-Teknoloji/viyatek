package com.viyatek.helper

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import androidx.core.content.pm.PackageInfoCompat


class OpenFbPage(val context: Context) {

    fun openFacebookPage(pageId: String) {
        val pageUrl = "https://www.facebook.com/$pageId"
        try {
            val applicationInfo: ApplicationInfo = context.packageManager.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                val versionCode: Long = PackageInfoCompat.getLongVersionCode(context.packageManager.getPackageInfo("com.facebook.katana", 0))

                val url: String = if (versionCode >= 3002850L) {
                    "fb://facewebmodal/f?href=$pageUrl"
                } else {
                    "fb://page/$pageId"
                }
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else {
                throw Exception("Facebook is disabled")
            }
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)))
        }
    }
}