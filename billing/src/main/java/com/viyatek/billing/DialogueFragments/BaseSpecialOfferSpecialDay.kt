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
import com.bumptech.glide.Glide
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.R
import com.viyatek.billing.databinding.SpecialOfferDialogueBinding


/*
Created By Eren Tüfekçi
*/
abstract class BaseSpecialOfferSpecialDay : DialogFragment() {

    var _binding: SpecialOfferDialogueBinding? = null
    val binding get() = _binding!!


    private var countDownTimer: CountDownTimer? = null
    private var timerWorks = false

    private var specialDayCampaignNo: Int? = null
    private var special_offer_campaign_title = ""
    private var special_offer_text = ""
    private var specialOfferImageURL = ""
    private var isHome = false
    private var startDate: Long = 0L
    private var duration: Long = 0L


    val billingPrefHandlers by lazy { BillingPrefHandlers(requireContext()) }

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
        _binding = SpecialOfferDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set Required Variables Here

        val endDate = startDate + duration
        val hoursMax = convertMilisToHours(duration)
        binding.hoursBar.max = hoursMax
        binding.minsBar.max = 60
        binding.secsBar.max = 60

        val currentTime = System.currentTimeMillis()
        if (currentTime in (startDate + 1) until endDate) {
            countDownTimer = object : CountDownTimer(endDate - currentTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val totalSeconds = (millisUntilFinished.toInt() / 1000).toLong()
                    val hours = (totalSeconds / (60 * 60)).toInt()
                    val totalMins = ((totalSeconds - hours * 60 * 60) / 60).toInt()
                    val remainingSeconds = (totalSeconds - hours * 60 * 60 - totalMins * 60).toInt()
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
                    dismiss()
                }
            }.start()
            timerWorks = true
        }

        if (isHome) {
            specialDayCampaignNo?.let {
                billingPrefHandlers.setSpecialCampaignAppearedInHome(
                    it
                )
            }

        } else {
            binding.specialOfferActionButton.text = "OK"
        }



        binding.specialOfferTitle.text = special_offer_campaign_title
        binding.specialOfferText.text = special_offer_text

        Glide.with(requireContext())
            .load(specialOfferImageURL)
            .placeholder(R.drawable.general_special_offer)
            .error(R.drawable.general_special_offer)
            .into(binding.specialOfferImage)

        binding.closeIcon.setOnClickListener(View.OnClickListener { dismissAllowingStateLoss() })
        binding.specialOfferActionButton.setOnClickListener {

            if (isHome) {
                val intent = Intent(context, getPremiumActivityClass())
                intent.putExtra("cameFromBargainDialog", true)
                requireContext().startActivity(intent)
            }
            dismissAllowingStateLoss()
        }

    }

    fun setRequiredVariables(
        specialDayCampaignNo: Int,
        startDate: Long,
        duration: Long,
        isHome: Boolean,
        special_offer_campaign_title: String,
        special_offer_text: String,
        specialOfferImageURL: String
    ) {
        this.specialDayCampaignNo = specialDayCampaignNo
        this.startDate = startDate
        this.duration = duration
        this.isHome = isHome
        this.special_offer_campaign_title = special_offer_campaign_title
        this.special_offer_text = special_offer_text
        this.specialOfferImageURL = specialOfferImageURL
    }

    abstract fun getPremiumActivityClass(): Class<out Activity>

    private fun convertMilisToHours(duration: Long): Int {
        val totalSeconds = (duration.toInt() / 1000).toLong()
        return (totalSeconds / (60 * 60)).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        if (timerWorks) {
            countDownTimer!!.cancel()
        }
    }


}