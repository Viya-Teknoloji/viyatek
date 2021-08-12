package com.viyatek.helper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log

class OpenPlayStore(val activity: Activity) {

    fun Open(packageName: String) {

        Log.d("MESAJLARIM", packageName)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data =  Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            setPackage("com.android.vending")
        }

       try {
           Log.d("MESAJLARIM", packageName)
           activity.startActivity(intent)
       }
       catch (e: ActivityNotFoundException)
       {
           try {
               Intent(Intent.ACTION_VIEW).apply {
                   data =  Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
               }.apply {
                   activity.startActivity(this)
               }
           }
           catch (e: ActivityNotFoundException)
           {
               Log.d("MESAJLARIM", "Hard That Much")
           }

       }

    }
}
