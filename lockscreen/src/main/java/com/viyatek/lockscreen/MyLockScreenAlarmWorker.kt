package com.viyatek.lockscreen

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

abstract class MyLockScreenAlarmWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : Worker(context, workerParams) {
    val LOG_TAG = "MyLockScreenAlarmWorker"


    override fun doWork(): Result {
        Log.i(
            LOG_TAG,
            "Setting Alarm Manager in Workmanager"
        )

        setAlarm()
        return Result.success()
    }

    abstract fun setAlarm()

    companion object {
        val MYALARMMANAGER = "MyAlarmManager"
    }
}