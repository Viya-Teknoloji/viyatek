package com.viyatek.app_update

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.viyatek.preferences.ViyatekSharedPrefsHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class UpdateService : Service() {

    private val binder: IBinder = LocalBinder()
    private val ACTION_STOP_SERVICE = "STOP"
    private var serviceCallbacks: UpdateServiceCallBack? = null

    private val pendingNotificationIntent by lazy {
        PendingIntent.getActivity(
            this, 1022,
            null,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private val applyNewVersionCode by lazy { ApplyNewVersionCode(this) }

    private val stopSelf by lazy { Intent(this, UpdateService::class.java) }
    private val pStopSelf by lazy {
        PendingIntent.getService(
            this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private val sharedPrefsHandler by lazy {
        ViyatekSharedPrefsHandler(
            this,
            Statics.UPDATE_PREFS_NAME
        )
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (ACTION_STOP_SERVICE == intent.action) {
            Log.d(Statics.LOG_TAG, "called to cancel service")
            stopSelf()
        }

        Log.d(Statics.LOG_TAG, "On Start Command, Update Service")

        Statics.isUpdateServiceRunning = true

        stopSelf.action = ACTION_STOP_SERVICE //TODO RETHİNK


        val notification = createNotification(Statics.UPDATE_CHANNEL_ID)

        startForeground(21, notification)

        handleUpdateTask()

        return START_NOT_STICKY
    }

    abstract fun createNotification(updateChannelId: String): Notification?

    fun handleUpdateTask() {
        CoroutineScope(Dispatchers.IO).launch {

            Log.d(Statics.LOG_TAG, "Called here")

            longRunningUpdateTask()

            applyNewVersionCode.applySharedPrefNewVersionCode()

            Statics.isUpdateServiceRunning = false
            sharedPrefsHandler.applyPrefs(Statics.UPDATE_MESSAGE_MUST_SHOWN, true)
            serviceCallbacks?.updateResult()

            stopSelf()
        }
    }

    abstract fun longRunningUpdateTask()

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: UpdateService
            get() =// Return this instance of MyService so clients can call public methods
                this@UpdateService
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var serviceChannel: NotificationChannel? = null
            serviceChannel = NotificationChannel(
                Statics.UPDATE_CHANNEL_ID,
                "Ultimate Quotes Update Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.setSound(null, null)
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun setCallbacks(callbacks: UpdateServiceCallBack?) {
        Log.i(Statics.LOG_TAG, "Callbacks setted Service")
        serviceCallbacks = callbacks
    }
}