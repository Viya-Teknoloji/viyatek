package com.viyatek.helper

import android.content.Context
import android.content.Intent
import android.net.Uri

class OpenTwitter(val context: Context) {

    fun OpenMyTwitterAccount(accountName: String) {
        var intent: Intent?
        try {
            context.packageManager.getPackageInfo("com.twitter.android", 0)
            intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=$accountName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$accountName"))
        }
        context.startActivity(intent)
    }

}