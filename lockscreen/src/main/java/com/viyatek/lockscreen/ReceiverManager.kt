package com.viyatek.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class ReceiverManager(private val context: Context) {

    val LOG_TAG = "Receiver Manager"
    
    private val receivers: MutableList<BroadcastReceiver> = ArrayList()
    private var ref: ReceiverManager? = null

    fun init(): ReceiverManager? {
        if (ref == null) ref = ReceiverManager(context)
        return ref
    }

    fun registerReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter): Intent? {

        receivers.add(receiver)
        val intent = context.registerReceiver(receiver, intentFilter)
        Log.d(LOG_TAG, "registered receiver: $receiver  with filter: $intentFilter")
        Log.d(LOG_TAG, "receiver Intent: $intent")
        return intent
    }

    private fun isReceiverRegistered(receiver: BroadcastReceiver): Boolean {
        val registered = receivers.contains(receiver)
        Log.d(LOG_TAG, "is receiver $receiver registered? $registered")
        return registered
    }

    fun unregisterReceiver(receiver: BroadcastReceiver) {
        if (isReceiverRegistered(receiver)) {
            receivers.remove(receiver)
            try { context.unregisterReceiver(receiver)
                Log.d(LOG_TAG, "unregistered receiver: $receiver")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(LOG_TAG, "Already unregistered")
            }
        }
    }
}