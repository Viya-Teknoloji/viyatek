package com.viyatek.firebase

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class GetFCMToken {

    val LOG_TAG = "FCM"

    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            if (task.result != null) {
                // Get new Instance ID token
                val token = task.result

                // Log and toast
                val msg = "Token id granted : $token"
                Log.d(LOG_TAG, msg)
            }
        })
    }
}