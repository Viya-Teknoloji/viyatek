package com.viyatek.billing.DialogueFragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.databinding.SpecialOfferDialogueLocalBinding


/*
Created By Eren Tüfekçi
*/
abstract class BaseSpecialOfferRemote : DialogFragment() {

    var _binding: SpecialOfferDialogueLocalBinding? = null
    val binding get() = _binding!!

    var startDate: Long = 0
    var duration: Long = 0
    var endDate: Long = 0
    var campaign_no: Int = 0
    var isHome = false
    var remoteCampaignTitle: String = ""
    var promotionAmount: String = ""

    val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(requireContext()) }

    private var countDownTimer: CountDownTimer? = null
    private var timerWorks = false
    private var isLocal = false

    override fun onResume() {
        super.onResume()
        val width: Int = GetScreenWidth().execute()
        dialog?.window?.setLayout(width * 6 / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SpecialOfferDialogueLocalBinding.inflate(inflater, container, false)
        dialog?.setCanceledOnTouchOutside(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInputs()

        try {
            if (isHome) {
                if (isLocal) {
                    viyatekKotlinSharedPrefHelper.applyPrefs(
                        ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_APPEARED_IN_HOME,
                        campaign_no
                    )
                } else {
                    viyatekKotlinSharedPrefHelper.applyPrefs(
                        ViyatekKotlinSharedPrefHelper.REMOTE_CAMPAIGN_APPEARED_IN_HOME,
                        campaign_no
                    )
                }
            } else {
                binding.specialOfferActionButton.text = "OK"
            }

            binding.specialOfferText.text = remoteCampaignTitle
            binding.specialOfferPromotionAmount.text = promotionAmount

            endDate = startDate + duration
            val currentTimeinMilis = System.currentTimeMillis()
            val hoursMax = ConvertMilisToHours(duration)

            binding.hoursBar.max = hoursMax
            binding.minsBar.max = 60
            binding.secsBar.max = 60
            if (currentTimeinMilis in (startDate + 1) until endDate) {
                countDownTimer = object : CountDownTimer(endDate - currentTimeinMilis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val totalSeconds = (millisUntilFinished.toInt() / 1000).toLong()
                        val hours = (totalSeconds / (60 * 60)).toInt()
                        val totalMins = ((totalSeconds - hours * 60 * 60) / 60).toInt()
                        val remainingSeconds =
                            (totalSeconds - hours * 60 * 60 - totalMins * 60).toInt()

                        binding.hoursBar.progress = hours
                        binding.minsBar.progress = totalMins
                        binding.secsBar.progress = remainingSeconds
                        binding.countDownHours.text = hours.toString()
                        binding.countDownMins.text = totalMins.toString()
                        binding.countDownSecs.text = remainingSeconds.toString()
                    }

                    override fun onFinish() {
                        binding.countDownHours.text = "00"
                        binding.countDownMins.text = "00"
                        binding.countDownSecs.text = "00"
                        dismissAllowingStateLoss()
                    }
                }.start()
                timerWorks = true
            }

            binding.closeIcon.setOnClickListener(View.OnClickListener { dismissAllowingStateLoss() })
            binding.specialOfferActionButton.setOnClickListener(
                View.OnClickListener {
                    if (isHome) {
                        val intent = Intent(context, getPremiumActivityClass())
                        intent.putExtra("cameFromBargainDialog", true)
                        requireContext().startActivity(intent)
                    }
                    dismissAllowingStateLoss()
                }
            )
        } catch (e: NullPointerException) {
            throw RequiredVariablesNotSet(
                "Be sure you set all required variables with" +
                        " setRequiredVariables() method before the super method inside OnViewCreated"
            )
        }

    }

    abstract fun getPremiumActivityClass(): Class<out Activity>
    abstract fun setInputs()

    private fun ConvertMilisToHours(duration: Long): Int {
        val totalSeconds = (duration.toInt() / 1000).toLong()
        return (totalSeconds / (60 * 60)).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity.isCampaignDialogOnShow = false
        if (timerWorks) {
            countDownTimer?.cancel()
        }
    }

    fun setRequiredVariables(
        campaignNo: Int,
        startDate: Long,
        duration: Long,
        isHome: Boolean,
        promotionAmount: String,
        campaign_title: String,
    ) {
        this.campaign_no = campaignNo
        this.startDate = startDate
        this.duration = duration
        this.isHome = isHome
        this.promotionAmount = promotionAmount
        this.remoteCampaignTitle = campaign_title
    }

    inner class RequiredVariablesNotSet(message: String) : Exception(message)

}