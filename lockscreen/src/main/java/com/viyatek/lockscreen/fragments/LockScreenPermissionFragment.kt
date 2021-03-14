package com.viyatek.lockscreen.fragments

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.viyatek.lockscreen.R
import com.viyatek.lockscreen.Statics
import com.viyatek.lockscreen.databinding.FragmentPermissionBinding
import com.viyatek.preferences.ViyatekSharedPrefsHandler

abstract class LockScreenPermissionFragment : Fragment(), PermissionListener {

    val LOG_TAG = "Permission Fragment"
    private lateinit var lockScreenPermissionHandler: LockScreenPermissionHandler

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefsHandler by lazy {
        ViyatekSharedPrefsHandler(
            requireContext(),
            Statics.LOCK_SCREEN_PREFS
        )
    }

    var appStartCompleted = false
    var permissionDeniedText = ""
    var theText = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionDeniedText = requireActivity().resources.getString(R.string.permission_denied_text)
        theText = getString(R.string.permission_required_text)

        setRequiredVariables()

        binding.textView.text = theText
        lockScreenPermissionHandler = LockScreenPermissionHandler(this, this)

        Log.d(LOG_TAG, "Android Version : " + Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(LOG_TAG, "Android Version above R ")


            var theDrawable: Int = R.drawable.permission_android_r_day

            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> theDrawable = R.drawable.permission_android_r
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.permission_android_r_day
                Configuration.UI_MODE_NIGHT_UNDEFINED -> R.drawable.permission_android_r_day
            }

            Glide.with(requireContext())
                .asGif()
                .load(theDrawable)
                .into(binding.permissionGif)

        }
        else
        {
            Log.d(LOG_TAG, "Android Version below R ")

            var theDrawable : Int = R.drawable.permission_android_q_day

            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> theDrawable = R.drawable.permission_android_q
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.permission_android_q_day
                Configuration.UI_MODE_NIGHT_UNDEFINED -> R.drawable.permission_android_q_day
            }

            Glide.with(requireContext())
                .asGif()
                .load(theDrawable)
                .into(binding.permissionGif)
        }

        changeDefaultGif(binding.permissionGif)

        binding.continueButton.theContinue.setOnClickListener {
            lockScreenPermissionHandler.RequestPermission()
        }

    }

    abstract fun setRequiredVariables()

    abstract fun changeDefaultGif(permissionGif: ImageView)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(LOG_TAG, "On Activity result")
        if (requestCode == Statics.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            Log.d(LOG_TAG, "Settings Overlay result")
            lockScreenPermissionHandler.PermissionResult()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun PermissionResult(result: Boolean) {
        if(!result)
        {
            sharedPrefsHandler.applyPrefs(Statics.IS_LOCK_SCREEN_OK, false)


            if (!appStartCompleted) { sharedPrefsHandler.applyPrefs(Statics.IS_LOCK_SCREEN_NOTIFICATION_OK, true) }

            // You don't have permission
            AlertDialog.Builder(requireActivity())
                .setTitle(requireActivity().resources.getString(R.string.permission_denied_title))
                .setMessage(permissionDeniedText)
                .setPositiveButton(requireActivity().getString(R.string.ok)) { _: DialogInterface?, _: Int ->

                 actionWhenPermissionDenied()

                }.show()
        }
        else
        {


            sharedPrefsHandler.applyPrefs(Statics.IS_LOCK_SCREEN_OK, true)

            actionWhenPermissionGranted()
        }
    }

    abstract fun actionWhenPermissionGranted()

    abstract fun actionWhenPermissionDenied()
}