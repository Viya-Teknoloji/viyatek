package com.viyatek.app_update

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class UpdateProgressFragment : Fragment(R.layout.progress_fragment) {

    private var serviceConnection: ServiceConnection? = null
    lateinit var localUpdateService: Class<out UpdateService>

    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private var mShouldUnbind = false

    // To invoke the bound service, first make sure that this value
    // is not null.
    private var mBoundService: UpdateService? = null

    companion object {
        var isUpdateMade = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceConnection = object : ServiceConnection, UpdateServiceCallBack {

            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

                mBoundService = (iBinder as UpdateService.LocalBinder).service
                mBoundService?.setCallbacks(this)
                mBoundService?.handleUpdateTask()
                Log.d(Statics.LOG_TAG, "Database Syncronize service started")
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                Log.d(Statics.LOG_TAG, "Service Disconnected")
                mBoundService = null
                mShouldUnbind = false
            }

            override fun updateResult() {
                if (isAdded && !isDetached &&!requireActivity().isDestroyed && !requireActivity().isFinishing)  {

                    Log.d(Statics.LOG_TAG, "In Disconnect")

                    CoroutineScope(Dispatchers.Main).launch {
                        isUpdateMade = true
                        serviceConnection?.let { requireContext().unbindService(it) }
                        handleUpdateResult()
                    }

                }
            }
        }


        if (!mShouldUnbind) {
            doBindService()
        }
    }

    abstract fun handleUpdateResult()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(Statics.LOG_TAG, "Progress Fragment Created")


    }

    override fun onResume() {
        super.onResume()

        if (isUpdateMade) {
            updateMadeOnResume()
        }
    }

    abstract fun updateMadeOnResume()

    override fun onDestroy() {
        super.onDestroy()

        Log.d(Statics.LOG_TAG, "On Destroy called : $mShouldUnbind")
        try {
            doUnbindService();
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            unbindExceptionTrapped()

        }

    }

    abstract fun unbindExceptionTrapped()

    private fun doBindService() {

        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        // }

        serviceConnection?.let {
            if (requireActivity().bindService(
                    Intent(requireContext(), localUpdateService),
                    it,
                    AppCompatActivity.BIND_AUTO_CREATE
                )
            ) {
                Log.d(Statics.LOG_TAG, "ServiceBounded")
                mShouldUnbind = true;
            } else {
                Log.d(
                    Statics.LOG_TAG, "Error: The requested service doesn't " +
                            "exist, or this client isn't allowed access to it."
                )
            }

        }
    }

    private fun doUnbindService() {
        if (mShouldUnbind) {

            Log.d(Statics.LOG_TAG, "Unbinding Service do unbind func")
            // Release information about the service's state.
            serviceConnection?.let {

                Log.d(Statics.LOG_TAG, "service unbinded")
                mShouldUnbind = false

                requireActivity().unbindService(it)
                serviceConnection = null

            }
        }
    }
}