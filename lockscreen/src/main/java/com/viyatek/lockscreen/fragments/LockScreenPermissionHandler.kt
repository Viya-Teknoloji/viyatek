package com.viyatek.lockscreen.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import com.viyatek.lockscreen.Statics.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE

class LockScreenPermissionHandler(val fragment: Fragment, val permissionListener: PermissionListener) {

    val LOG_TAG = "Permission"
    
    fun RequestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + fragment.requireContext().packageName)
            )
            fragment.startActivityForResult(
                intent,
                ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
            )
        }
    }


    fun PermissionResult() {
        Log.d(LOG_TAG, "Permission Result")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(fragment.requireContext())) {
                Log.d(LOG_TAG ,"Permission granted")
                permissionListener.PermissionResult(false)
            } else {
                Log.d(LOG_TAG, "Permission denied")
                permissionListener.PermissionResult(true)
            }
        } else {
            permissionListener.PermissionResult(true)
        }
    }


}