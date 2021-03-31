package com.viyatek.lockscreen

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.viyatek.app_update.VersionControl

abstract class LockScreenActivity : AppCompatActivity() {

    val defaultVersionValue = 0
    val isTransparentBackground = false

    private val versionControl by lazy { VersionControl(this, defaultVersionValue) }
    private val myKM by lazy { getSystemService(android.content.Context.KEYGUARD_SERVICE) as KeyguardManager }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(versionControl.checkVersion())
        {
            handleUpdateInLockScreen()
            finish()
        }
        else
        {
            adjustLockScreenFlags()

            //android O fix bug orientation
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            if(isTransparentBackground)
            {
                val w = window

                w.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                setTransparentView()
            }
            else
            {
                setOpaqueLayout()
            }

            logIfLockScreenOpenedFromNotification()

            increaseTheSeenNumber()

            setCampaign()

        }
    }

    abstract fun setCampaign()

    abstract fun increaseTheSeenNumber()

    abstract fun setOpaqueLayout()

    abstract fun setTransparentView()

    private fun adjustLockScreenFlags() {

        //Bring to front at lockScreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
    }

    private fun logIfLockScreenOpenedFromNotification() {
        intent?.let {
            it.getStringExtra("fromNotification")?.let { notificationExtra->
                if(notificationExtra=="yes")
                {
                    lockScreenOpenedFromNotification()
                }
            }
        }
    }

    abstract fun lockScreenOpenedFromNotification()

    abstract fun handleUpdateInLockScreen()
}