package com.viyatek.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log

class OpenPlayStore(val activity: Activity) {

    fun Open(packageName: String) {

        Log.d("Paclage name", packageName)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(
            "https://play.google.com/store/apps/details?id=$packageName"
        )

        intent.setPackage("com.android.vending")
        activity.startActivity(intent)
    }
}
