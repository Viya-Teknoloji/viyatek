package com.viyatek.lockscreen

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenBroadcastReceiver(private val activityBroadCastType: ActivityBroadCastType, private val activity: Class<out Activity>) : BroadcastReceiver() {
    
    val LOG_TAG = "Screen Broadcast"
    var appcontext: Context? = null
    var lockScreenIntent: Intent? = null

    override fun onReceive(context: Context, intent: Intent) {
        appcontext = context.applicationContext


        val scd = ScreenDisplayCoordinator(context)

        when {
            intent.action == Intent.ACTION_SCREEN_OFF && !isScreenOfCalled -> {
                Log.d(LOG_TAG, "In Method:  ACTION_SCREEN_OFF. Activity starts")
                Log.d(LOG_TAG, "Is screen off called before $isScreenOfCalled ")

                if (scd.checkIfDisplay() && activityBroadCastType == ActivityBroadCastType.SCREEN_OFF) {
                    Log.d(LOG_TAG, "Inside Action Screen Off for new User")
                    val i = Intent(appcontext, activity)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    i.putExtra("camefromScreenOff", true)
                    appcontext?.startActivity(i)
                    isScreenOfCalled = true
                }

            }
            intent.action == Intent.ACTION_SCREEN_ON -> {
                isScreenOfCalled = false
                Log.d(LOG_TAG, "In Method:  ACTION_SCREEN_ON")
            }

            intent.action == Intent.ACTION_USER_PRESENT -> {
                if (scd.checkIfDisplay() && activityBroadCastType == ActivityBroadCastType.USER_PRESENT) {
                    val i = Intent(appcontext, activity)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    i.putExtra("camefromScreenOff", false)
                    appcontext?.startActivity(i)
                    isScreenOfCalled = true
                }


            }


        }
    }

    companion object {
        const val SCREEN_BROADCAST_CALLBACK = "CALL_BACK_ULTIMATE_FACTS"

        @JvmField
        var isScreenOfCalled = false
        fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
    }
}