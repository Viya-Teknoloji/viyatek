package com.viyatek.lockscreen

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import com.viyatek.preferences.ViyatekSharedPrefsHandler

abstract class LockScreenService : Service(){

    lateinit var theClass: Class<out Activity>
    var broadCastType: ActivityBroadCastType = ActivityBroadCastType.SCREEN_OFF
    var reduceLatencyMode = false

    val LOG_TAG = "LockScreenService"

    val NEW_CHANNEL_ID = "AndroidForegroundServiceChannel"

    var callBackReceived = false

    val sharedPrefsHandler by lazy { ViyatekSharedPrefsHandler(this, Statics.LOCK_SCREEN_PREFS) }
    val isLockScreenOK by lazy {
        sharedPrefsHandler.getBooleanValue(
            Statics.IS_LOCK_SCREEN_OK,
            true
        )
    }
    val isLockScreenNotificationOK by lazy {
        sharedPrefsHandler.getBooleanValue(
            Statics.IS_LOCK_SCREEN_NOTIFICATION_OK,
            true
        )
    }

    val receiverManager: ReceiverManager? by lazy { ReceiverManager(applicationContext).init() }

    override fun onCreate() {
        super.onCreate()

        setRequiredVariables()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    abstract fun setRequiredVariables()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "On start command")
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                when {
                    Settings.canDrawOverlays(this) && isLockScreenOK -> {
                        val notification = showNotificationFor10Plus(NEW_CHANNEL_ID)
                        handleService()
                        Log.d(LOG_TAG, "Receiver created")
                        startForeground(2, notification)
             
                    }
                    else -> {
                        val notification = showNotificationFor10Plus(NEW_CHANNEL_ID)
                        startForeground(2, notification)
                    }
                }
            }
            else -> {
                when {
                    isLockScreenOK -> {

                        val notification = showNotificationFor10Below(NEW_CHANNEL_ID)

                        handleService()
                        startForeground(1, notification)
                    }
                    else -> {

                        val notification = showNotificationFor10Below(NEW_CHANNEL_ID)
                        startForeground(1, notification)
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun handleService() {
        //Intent Filter
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)


        mScreenBroadcastReceiver?.let {

            try {
                receiverManager?.unregisterReceiver(it)
                receiverManager?.registerReceiver(it, filter)
                Log.d(LOG_TAG, "Receiver unregistered")
            } catch (e: IllegalArgumentException) {
                Log.d(LOG_TAG, "Broadcast is already unregistered")
            }
        }

        Log.d(LOG_TAG, "What the hell unregistered")

        mScreenBroadcastReceiver = ScreenBroadcastReceiver(ActivityBroadCastType.SCREEN_OFF, theClass).apply {
                Log.d(this@LockScreenService.LOG_TAG, "Receiver registered")
                try{
                    receiverManager?.registerReceiver(this, filter)
                }
                catch (e:Exception)
                {
                    Log.d(this@LockScreenService.LOG_TAG, "Receiver register Error")
                }

            }

        if (reduceLatencyMode) {
            toReduceLatencyOpenActivity()
        }

    }

    abstract fun showNotificationFor10Below(CHANNEL_ID: String) : Notification

    fun toReduceLatencyOpenActivity() {
        val pm: PowerManager? = getSystemService(Context.POWER_SERVICE) as PowerManager?
        if (pm != null && !pm.isInteractive) {
            createLockScreenAndMoveItBack()
        }
    }

    fun createLockScreenAndMoveItBack() {
        val lockScreenIntent: Intent = Intent(this, theClass)
            lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            lockScreenIntent.putExtra("camefromScreenOff", true)
            startActivity(lockScreenIntent)
            ScreenBroadcastReceiver.isScreenOfCalled = true
    }

    abstract fun showNotificationFor10Plus(CHANNEL_ID: String)  : Notification

    private fun createNotificationChannel() {

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this) -> {
                createNotificationChannelOPlus(NotificationManager.IMPORTANCE_MAX)

            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isLockScreenOK && isLockScreenNotificationOK -> {
                createNotificationChannelOPlus(NotificationManager.IMPORTANCE_HIGH)
            }
            else-> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createNotificationChannelOPlus(
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                }
            }
        }
    }

    private fun createNotificationChannelOPlus(
        importanceDefault: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NEW_CHANNEL_ID,
                "Quotes Foreground Service Channel",
                importanceDefault
            )
            serviceChannel.setSound(null, null)
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        var mScreenBroadcastReceiver: BroadcastReceiver? = null
    }

}
