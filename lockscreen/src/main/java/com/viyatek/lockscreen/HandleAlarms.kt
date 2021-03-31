package com.viyatek.lockscreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_NOTIFICATION_OK
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_OK
import com.viyatek.lockscreen.Statics.SHOW_TIME
import com.viyatek.preferences.ViyatekSharedPrefsHandler
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HandleAlarms(private val context: Context, private val intent: Intent) {

    val LOG_TAG = "Handle Alarms"

    val sdc by lazy { ScreenDisplayCoordinator(context) }
    val lockScreenPrefsHandler by lazy { LockScreenPreferencesHandler(context) }
    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    val sharedPrefsHandler by lazy { ViyatekSharedPrefsHandler(context, Statics.LOCK_SCREEN_PREFS) }
    private val alarmTime by lazy {
        lockScreenPrefsHandler.getShowTime().let {
            if (it > now.timeInMillis) { it
            } else {
                now.timeInMillis + 15 * 1000
            }
        }
    }
    val now: Calendar by lazy { Calendar.getInstance() }


    val pendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            Statics.pendingIntentRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun updateAlarmTime()
    { sdc.updateFutureTime() }

    fun setAlarmManager() {
        if (lockScreenPrefsHandler.isLockScreenOk() || lockScreenPrefsHandler.isLockScreenNotificationOk()
        ) {

            showAlarmTimeinLog(alarmTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, alarmTime, pendingIntent)
            }
            else {
                alarmManager.setRepeating(
                    AlarmManager.RTC,
                    alarmTime,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent
                )
            }
        }
    }

    private fun showAlarmTimeinLog(alarmTime: Long) {
        val dateFormat = "dd/MM/yyyy hh:mm:ss.SSS"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val alarmToRing = formatter.format(alarmTime)
        Log.i(
            LOG_TAG,
            "Alarm time $alarmToRing"
        )
        Log.i(
            LOG_TAG,
            "Alarm Manager Set $alarmToRing"
        )
    }

    fun setAlarmWorkManager(clazz: Class<out MyLockScreenAlarmWorker>) {
        val alarmWorker: WorkManager = WorkManager.getInstance(context)
        Log.i(
            LOG_TAG,
            "Work manager for alarming settled"
        )
        val requestBuilder: PeriodicWorkRequest.Builder =
            PeriodicWorkRequest.Builder(clazz, 3 * 60 * 60 * 1000, TimeUnit.MILLISECONDS)
        Log.i(
            LOG_TAG,
            "Periodic Work request created " + AlarmManager.INTERVAL_HALF_HOUR + " mili sn aralığı ile"
        )
        val myWork: PeriodicWorkRequest = requestBuilder.build()

        alarmWorker.enqueueUniquePeriodicWork(
            MyLockScreenAlarmWorker.MYALARMMANAGER,
            ExistingPeriodicWorkPolicy.KEEP,
            myWork
        )

    }

    fun cancelAlarm() {

        if (pendingIntent != null) { alarmManager.cancel(pendingIntent)
            Log.i(LOG_TAG, "Alarm cancelled")
        }
        val alarmWorker: WorkManager = WorkManager.getInstance(context)
        Log.i(LOG_TAG, "Work Manager cancelled")
        alarmWorker.cancelUniqueWork(MyLockScreenAlarmWorker.MYALARMMANAGER)
    }

    fun setSpareAlarm() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }
    }

    fun setDeveloperAlarm() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lockScreenPrefsHandler.setShowTime(System.currentTimeMillis())
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }
    }
}