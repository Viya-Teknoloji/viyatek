package com.viyatek.lockscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_NOTIFICATION_OK
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_OK
import com.viyatek.lockscreen.Statics.LAST_DAY_OPENED
import com.viyatek.lockscreen.Statics.NOTIFICATION_CHANNEL_ID
import com.viyatek.lockscreen.Statics.SEEN_FACTS_SUM_SO_FAR
import com.viyatek.preferences.ViyatekSharedPrefsHandler
import java.util.*

abstract class LockScreenAlarmBroadcast : BroadcastReceiver() {

    val LOG_TAG = "Alarm Broadcast"
    
    lateinit var context: Context
    var intent: Intent? = null

    private val lockScreenPrefsHandler by lazy { LockScreenPreferencesHandler(context) }
    private val sharedPrefsHandler by lazy { ViyatekSharedPrefsHandler(context, Statics.LOCK_SCREEN_PREFS) }
    private val lastDayOpened: Int by lazy { sharedPrefsHandler.getIntegerValue(LAST_DAY_OPENED, 0)  }

      private val c: Calendar by lazy { Calendar.getInstance()  }
    private val today by lazy { c[Calendar.DAY_OF_MONTH] }

    private val screenDisplayCoordinator by lazy { ScreenDisplayCoordinator(context) }

    override fun onReceive(context: Context, intent: Intent?) {

        this.context = context

            Log.d(LOG_TAG, "Alarm Received New ${intent?.action} ")

            this.intent = intent

            val versionControl = versionControl(context)

            if (versionControl) {
                handleUpdate()
                setNextAlarm()
            }
            else {
                if(lockScreenPrefsHandler.isLockScreenOk() || lockScreenPrefsHandler.isLockScreenNotificationOk()) {

                    setSpareAlarm()
                    checkLastDayOpened()

                    if (lockScreenPrefsHandler.isLockScreenOk() && screenDisplayCoordinator.checkIfDisplay()) {
                        Log.d(LOG_TAG, "Starting Daily Quote Service")
                        startLockScreenService()
                    } else if (lockScreenPrefsHandler.isLockScreenNotificationOk() && screenDisplayCoordinator.checkIfDisplay()) {
                        createNotification()
                    } else  {
                        setNextAlarm()
                    }
                }
            }

    }

    abstract fun startLockScreenService()

    abstract fun setSpareAlarm()

    abstract fun handleUpdate()

    abstract fun versionControl(context: Context): Boolean

    private fun createNotification() {
        if (isEligibleToShow()) {

            createNotificationChannel()

            val notification = createDesiredNotification(context, NOTIFICATION_CHANNEL_ID)

            setNextAlarm()

            notification?.let {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(
                    Statics.NOTIFICATION_ID,
                    it
                )
            }

        } else {
            Log.d(LOG_TAG, "Notificatxion is Not Eligible to show,  won't Start")
        }
    }

    abstract fun createDesiredNotification(context: Context, NOTIFICATION_CHANNEL_ID: String): Notification?

    private fun setNextAlarm() {
        screenDisplayCoordinator.updateFutureTime()
        setNextReminderAlarm()
    }

    abstract fun setNextReminderAlarm()

    private fun createNotificationChannel() {
        val notificationChannel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.setSound(null, null)
            val manager = context.getSystemService(NotificationManager::class.java)

            manager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun isEligibleToShow(): Boolean = screenDisplayCoordinator.checkIfDisplay()


    private fun checkLastDayOpened() {
        //Check if new day
        if (today != lastDayOpened) {
            Log.d(LOG_TAG, "Making knowledge_education amount zero because it is different day")
            sharedPrefsHandler.applyPrefs(SEEN_FACTS_SUM_SO_FAR, 0)
        }
        
        sharedPrefsHandler.applyPrefs(LAST_DAY_OPENED, today)
        Log.d(LOG_TAG, "Last day 's today now")
    }


}