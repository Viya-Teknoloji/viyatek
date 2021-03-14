package com.viyatek.billing.DialogueFragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.viyatek.billing.databinding.BillingDialogueLayoutBinding


abstract class BaseBillingDialogueLayout : DialogFragment() {

    var _binding: BillingDialogueLayoutBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BillingDialogueLayoutBinding.inflate(inflater, container, false)
        dialog?.setCanceledOnTouchOutside(true)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val width: Int = GetScreenWidth().execute()
        dialog?.window?.setLayout(width * 6 / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogueCloseIcon.setOnClickListener { dismissAllowingStateLoss() }

        binding.actionButton.setOnClickListener {

            Log.d(
                com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity.billingLogs,
                "Action Button Clicked"
            )
            handleActionButtonClick()
            dismissAllowingStateLoss()
        }
    }

    abstract fun handleActionButtonClick()

    open fun bindData(drawableResourceId: Int, message: String, buttonString: String) {
        Glide.with(requireContext()).load(drawableResourceId).into(binding.dialogueImage)
        binding.dialogueText.text = message
        binding.actionButton.text = buttonString
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}