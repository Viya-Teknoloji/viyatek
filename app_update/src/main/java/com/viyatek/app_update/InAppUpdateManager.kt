package com.viyatek.app_update

import android.app.Activity
import android.content.IntentSender
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class InAppUpdateManager(
    private val activity: Activity,
    private val inAppUpdateType: Int,
    private val IInAppUpdate: IInAppUpdate,
    private val completeUpdate: Boolean
) {

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(activity) }
    private val upDatePrefsHandler by lazy {
        UpdatePrefsHandler(activity)
    }

    fun handleInAppUpdate() {
// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                when {
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) && inAppUpdateType == 1 -> {
                        try {
                            appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                                activity,  // Include a request code to later monitor this update request.
                                Statics.UPDATE_REQUEST_CODE
                            )

                            upDatePrefsHandler.setUpdateRequireHandled(true)

                            IInAppUpdate.inAppUpdateFlowStarted()

                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) && inAppUpdateType == 0 -> {

                        // Create a listener to track request state updates.
                        val listener = InstallStateUpdatedListener { state ->
                            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                // After the update is downloaded, show a notification
                                // and request user confirmation to restart the app.
                                popUpSnackBarForCompleteUpdate(appUpdateManager!!)
                            }
                            // Show module progress, log state, or install the update.
                        }

                        // Before starting an update, register a listener for updates.
                        appUpdateManager?.registerListener(listener)

                        // Start an update.
                        try {
                            appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                                activity,  // Include a request code to later monitor this update request.
                                Statics.UPDATE_REQUEST_CODE
                            )
                            upDatePrefsHandler.setUpdateRequireHandled(true)

                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }

                        // When status updates are no longer needed, unregister the listener.
                        appUpdateManager?.unregisterListener(listener)
                    }
                    else -> {
                        try {
                            appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                                activity,  // Include a request code to later monitor this update request.
                                Statics.UPDATE_REQUEST_CODE
                            )
                            upDatePrefsHandler.setUpdateRequireHandled(true)

                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            // Request the update.
        }

    }

    fun handleInAppUpdateOnResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED && completeUpdate) {
                    popUpSnackBarForCompleteUpdate(appUpdateManager)
                }
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity,
                            Statics.UPDATE_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /* Displays the snackbar notification and call to action. */
    private fun popUpSnackBarForCompleteUpdate(appUpdateManager: AppUpdateManager) {
        upDatePrefsHandler.setUpdateRequireHandled(false)

        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackbar.setActionTextColor(ThemeColorHelper(activity).get(R.attr.colorPrimary))
        snackbar.show()
    }
}