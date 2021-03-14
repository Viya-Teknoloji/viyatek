package com.viyatek.lockscreen.fragments

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.viyatek.lockscreen.databinding.FragmentLockScreenInfoBinding

abstract class LockScreenInfoFragment : Fragment() {

    private var _binding: FragmentLockScreenInfoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTheInfoText(binding.theQuestion3)
        loadRequiredPhoto(binding.lockScreenInfoImg)

        binding.continueButton.theContinue.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(context)) {
                setActionWhenPermissionNeedToRequested()
            } else {
                setActionWhenNoNeedUserPermission()
            }
        }
    }

    abstract fun setTheInfoText(theTextView: TextView)

    abstract fun loadRequiredPhoto(lockScreenInfoImg: ImageView)

    abstract fun setActionWhenNoNeedUserPermission()

    abstract fun setActionWhenPermissionNeedToRequested()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLockScreenInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}