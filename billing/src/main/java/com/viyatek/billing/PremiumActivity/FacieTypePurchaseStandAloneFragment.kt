package com.viyatek.billing.PremiumActivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.BaseMoneyCalculator
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Campaign.ActiveSku
import com.viyatek.billing.R
import com.viyatek.billing.databinding.FragmentPurchaseStandAloneCardNewBinding
import kotlinx.coroutines.Dispatchers
import java.time.Period

abstract class FacieTypePurchaseStandAloneFragment : Fragment() {

    private var _binding: FragmentPurchaseStandAloneCardNewBinding? = null
    private val binding get() = _binding!!

    var activeSkuType = ActiveSku.YEARLY
    var otherSubscriptionPlansAvailable = false
    private var activeSkuDetail: SkuDetails? = null
    private var oldSkuDetail: SkuDetails? = null
    val billingPrefHandlers by lazy { BillingPrefHandlers(requireContext()) }
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }

    var activeYearlySku: SkuDetails? = null
    var oldYearlySku: SkuDetails? = null
    var activeMonthlySku: SkuDetails? = null
    var oldMonthlySku: SkuDetails? = null
    var activeLifeTimeSku: SkuDetails? = null
    var oldLifeTimeSku: SkuDetails? = null

    private val theActivity by lazy { (requireActivity() as ViyatekPremiumActivity) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseStandAloneCardNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeMonthlySku = (requireActivity() as ViyatekPremiumActivity).activeMonthlySku
        oldMonthlySku = (requireActivity() as ViyatekPremiumActivity).oldMonthlySku
        activeYearlySku = (requireActivity() as ViyatekPremiumActivity).activeYearlySku
        oldYearlySku = (requireActivity() as ViyatekPremiumActivity).oldYearlySku
        activeLifeTimeSku = (requireActivity() as ViyatekPremiumActivity).activeLifeTimeSku
        oldLifeTimeSku = (requireActivity() as ViyatekPremiumActivity).oldLifeTimeSku

        activeSkuDetail = when (activeSkuType) {
            ActiveSku.MONTHLY -> { theActivity.activeMonthlySku }
            ActiveSku.YEARLY -> { theActivity.activeYearlySku }
            ActiveSku.LIFETIME -> { theActivity.activeLifeTimeSku }
        }

        bindData()

        binding.closeButton.setOnClickListener {
            ReportButonClick("closeButtonClicked")
           closeButtonClicked()
        }
        binding.viyatekPrivacyPolicy.setOnClickListener {
            ReportButonClick("privacyPolicyClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }
        binding.viyatekTermsOfUse.setOnClickListener {
            ReportButonClick("termsOfUseClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
            startActivity(browserIntent)
            //
        }
        binding.viyatekOtherPlans.apply {
            if (otherSubscriptionPlansAvailable) {
                visibility = View.VISIBLE
                setOnClickListener {
                    ReportButonClick("otherPlansClicked")

                    navigateToMultiChoiceFragment()
                }
            } else {
                visibility = View.GONE
            }
        }

      }

    open fun navigateToMultiChoiceFragment() {}

    open fun closeButtonClicked() {

        requireActivity().finish()
    }

    private fun ReportButonClick(eventName: String) {
        val bundle = Bundle()
        bundle.putString("fragment", "StandAloneSale")
        mFireBaseAnalytics.logEvent(eventName, bundle)
    }

    private fun bindData() {
        requireActivity().runOnUiThread {
            if (activeSkuDetail != null) {

                Log.d("MESAJ", "Active sku is $activeSkuDetail")



                if (activeSkuDetail?.type == BillingClient.SkuType.SUBS) {


                    val subscriptionPeriod = activeSkuDetail?.subscriptionPeriod
                    var thePeriod: Period? = null
                    var thePeriodIdentifier = ""
                    var thePeriodTime = 1

                    if (activeSkuDetail?.subscriptionPeriod != null && activeSkuDetail?.subscriptionPeriod!!.isNotBlank()) {
                        thePeriod = Period.parse(subscriptionPeriod)
                        thePeriod.apply {
                            when {
                                years != 0 -> {
                                    thePeriodIdentifier = "year"
                                    thePeriodTime = thePeriod.years
                                }
                                months != 0 -> {
                                    thePeriodIdentifier = "month"
                                    thePeriodTime = thePeriod.months
                                }
                                else -> {
                                    thePeriodIdentifier = "days"
                                    thePeriodTime = thePeriod.days
                                }
                            }
                        }
                    }

                    // Log.d(Statics.BILLING_LOGS,"Subscription Period ${}" )
                    Log.d(ViyatekPremiumActivity.billingLogs, "Free Trial ${activeSkuDetail?.freeTrialPeriod!!}")
                    Log.d(ViyatekPremiumActivity.billingLogs, "Subscription Period ${thePeriod?.years} ")

                    if(thePeriodIdentifier == "year" || (thePeriodIdentifier == "month" && thePeriodTime != 1))
                    {
                        Log.d(ViyatekPremiumActivity.billingLogs,"First if")

                        binding.priceMonthlyInfo.visibility = View.VISIBLE
                        val bargainAmount = calculateDiscountAmount(activeSkuDetail)
                        Log.d(ViyatekPremiumActivity.billingLogs, "Bargain amount $bargainAmount")
                        try {

                            when (thePeriodIdentifier) {
                                "year" -> {
                                    Log.d(ViyatekPremiumActivity.billingLogs, "First if year")
                                    binding.priceMonthlyInfo.text = getString(
                                        R.string.monthly_price_info,
                                        12,
                                        "${activeSkuDetail?.priceCurrencyCode}${
                                            String.format(
                                                "%.2f",
                                                (BaseMoneyCalculator.calculatePerDayCost(activeSkuDetail) * 30) / 1000000
                                            )
                                        }",
                                        "%${bargainAmount}"
                                    )
                                }
                                "month" -> {
                                    binding.priceMonthlyInfo.text = getString(
                                        R.string.monthly_price_info,
                                        thePeriodTime,
                                        "${activeSkuDetail?.priceCurrencyCode}${
                                            String.format(
                                                "%.2f",
                                                (BaseMoneyCalculator.calculatePerDayCost(activeSkuDetail) * 30) / 1000000
                                            )
                                        }",
                                        "%${bargainAmount}"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(ViyatekPremiumActivity.billingLogs, "First if problem ${e.message}")

                            e.printStackTrace()
                        }



                        Log.d(ViyatekPremiumActivity.billingLogs,"First if end $thePeriodIdentifier")
                    }
                    else
                    {
                        Log.d(ViyatekPremiumActivity.billingLogs,"First else")

                        binding.priceMonthlyInfo.visibility = View.GONE
                    }



                    if (activeSkuDetail?.freeTrialPeriod != null
                        && activeSkuDetail?.freeTrialPeriod?.isNotBlank()!!
                        && !billingPrefHandlers.isSubscriptionTrialModeUsed()
                    ) {

                        Log.d(ViyatekPremiumActivity.billingLogs,"Second if")

                        val freeTrial = Period.parse(activeSkuDetail?.freeTrialPeriod)
                        binding.freeTrialDays.text = getString(R.string.try_3_days_for_free,freeTrial.days)
                        binding.freeTrialDays.visibility = View.VISIBLE

                        if (activeSkuDetail != oldSkuDetail && oldSkuDetail != null) {

                            /*                      val desiredText = SpannableStringBuilder(
                                  "${
                                      getString(
                                          R.string.save_50,
                                          "%$bargainAmount"
                                      )
                                  } just"
                              )
                              val strikethroughSpan = StrikethroughSpan()

                              desiredText.append(oldSkuDetail?.price)
                              desiredText.setSpan(
                                  strikethroughSpan,
                                  desiredText.length - oldSkuDetail?.price?.length!!,
                                  desiredText.length,
                                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                              )*/
                            binding.priceInfo.text = if (thePeriodTime != 1) {

                                getString(
                                    R.string.then_price_campaign_if_not_canceled_custom_time,
                                    oldSkuDetail?.price,
                                    activeSkuDetail?.price,
                                    thePeriodTime,
                                    thePeriodIdentifier
                                )


                            }
                            else {
                                getString(
                                    R.string.then_price_campaign_if_not_canceled,
                                    oldSkuDetail?.price,
                                    activeSkuDetail?.price,
                                    thePeriodIdentifier
                                )
                            }

                            drawTheLineOnOldPrice(binding.priceInfo)

                            Log.d(ViyatekPremiumActivity.billingLogs,"Third if end")

                        }
                        else {

                            Log.d(ViyatekPremiumActivity.billingLogs,"Second else start")

                            binding.priceInfo.text = if (thePeriodTime != 1) {
                                getString(
                                    R.string.then_price_if_not_canceled_custom_time,
                                    activeSkuDetail?.price,
                                    thePeriodTime,
                                    thePeriodIdentifier
                                )
                            } else {
                                getString(
                                    R.string.then_price_if_not_canceled,
                                    activeSkuDetail?.price,
                                    thePeriodIdentifier
                                )
                            }

                            Log.d(ViyatekPremiumActivity.billingLogs,"Second else end")

                        }
                    }
                    else {

                        Log.d(ViyatekPremiumActivity.billingLogs,"Third else start")

                        binding.freeTrialDays.visibility = View.GONE

                        if (activeSkuDetail != oldSkuDetail && oldSkuDetail != null) {

                            Log.d(ViyatekPremiumActivity.billingLogs,"Fourth if start")

                            val calculateDiscount = calculateDiscount()
                            binding.priceInfo.text = if (thePeriodTime == 1) {
                                getString(
                                    R.string.stand_alone_save_string,
                                    "%$calculateDiscount",
                                    oldSkuDetail?.price,
                                    activeSkuDetail?.price,
                                    thePeriodIdentifier
                                )

                            } else {
                                getString(
                                    R.string.stand_alone_save_string_custom_time,
                                    "%$calculateDiscount",
                                    oldSkuDetail?.price,
                                    activeSkuDetail?.price,
                                    thePeriodTime,
                                    thePeriodIdentifier
                                )
                            }
                            drawTheLineOnOldPrice(binding.priceInfo)

                            Log.d(ViyatekPremiumActivity.billingLogs,"Fourth if end")
                        }
                        else {
                            binding.priceInfo.text = if (thePeriodTime == 1) {
                                getString(
                                    R.string.plan_price_without_free_trial,
                                    activeSkuDetail?.price,
                                    thePeriodIdentifier
                                )
                            } else {
                                getString(
                                    R.string.plan_price_without_free_trial_custom_time,
                                    activeSkuDetail?.price,
                                    thePeriodTime,
                                    thePeriodIdentifier
                                )
                            }

                        }
                    }


                }
                else {

                    Log.d(ViyatekPremiumActivity.billingLogs,"Last else start")

                    if (oldSkuDetail != activeSkuDetail && oldSkuDetail != null) {
                        val discountAmount = calculateDiscount()
                        binding.priceInfo.text = getString(
                            R.string.stand_alone_save_string_pay_once,
                            "½$discountAmount", oldSkuDetail?.price, activeSkuDetail?.price
                        )
                        drawTheLineOnOldPrice(binding.priceInfo)
                    } else {
                        binding.priceInfo.text =
                            getString(R.string.life_time_plan_price, activeSkuDetail?.price)
                    }

                    binding.priceMonthlyInfo.text = getString(R.string.lifetime_motto)
                    binding.subscribeButton.text = getString(R.string.start_subscription_button)

                    Log.d(ViyatekPremiumActivity.billingLogs,"Last else end")
                }

                val theIndex = binding.priceInfo.text.indexOf("just", 0, true)
                Log.d(ViyatekPremiumActivity.billingLogs, "The index in ${binding.priceInfo.text} $theIndex")

                binding.loadingProgressbar.visibility = View.GONE
                binding.viyatekOtherPlans.visibility = View.VISIBLE
                binding.priceInfo.visibility = View.VISIBLE
                binding.priceMonthlyInfo.visibility = View.VISIBLE
                binding.subscribeButton.isEnabled = true
                binding.subscribeButton.setOnClickListener {

                    Log.d(ViyatekPremiumActivity.billingLogs, "Started billing flow ${(requireActivity() as ViyatekPremiumActivity).appsFlyerUUID}")

                    ReportButonClick("subscribeButtonClick")

                    val oldPurchasedSkuId = billingPrefHandlers.getSubscriptionType()!!
                    val oldPurchaseSkuToken = billingPrefHandlers.getSubscriptionToken()!!

                    val flowParams =
                        if (theActivity.subSkuListHelper.getSkuList()
                                .contains(oldPurchasedSkuId)
                        ) {
                            BillingFlowParams.newBuilder()
                                .setObfuscatedAccountId((requireActivity() as ViyatekPremiumActivity).appsFlyerUUID)
                                .setObfuscatedProfileId((requireActivity() as ViyatekPremiumActivity).gaid)
                                .setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldSkuPurchaseToken(oldPurchaseSkuToken).build())
                                .setSkuDetails(activeSkuDetail!!)
                                .build()
                        } else {
                            BillingFlowParams.newBuilder()
                                .setObfuscatedAccountId((requireActivity() as ViyatekPremiumActivity).appsFlyerUUID)
                                .setObfuscatedProfileId((requireActivity() as ViyatekPremiumActivity).gaid)
                                .setSkuDetails(activeSkuDetail!!)
                                .build()
                        }


                    theActivity.billingManager.billingClient.launchBillingFlow(
                        requireActivity(),
                        flowParams
                    )
                }
            } else {

                Log.d("MESAJ", "Active sku is null")

                binding.priceInfo.visibility = View.INVISIBLE
                binding.priceMonthlyInfo.visibility = View.INVISIBLE
                binding.loadingProgressbar.visibility = View.VISIBLE
                binding.viyatekOtherPlans.visibility = View.GONE
                binding.subscribeButton.isEnabled = false
            }
        }

    }

    private fun calculateDiscountAmount(theSku: SkuDetails?): Int{
        val baseSku = oldMonthlySku
        val baseMoneyPerDay = BaseMoneyCalculator.calculatePerDayCost(baseSku)

        if (theSku != baseSku) {
            val basePrice = BaseMoneyCalculator.calculatePerDayCost(theSku)
            return (((baseMoneyPerDay - basePrice) / baseMoneyPerDay).times(100)).toInt()
        }

        return -1
    }

    private fun calculateDiscount(): Int {
        return ((((oldSkuDetail?.priceAmountMicros?.toFloat())!! - (activeSkuDetail?.priceAmountMicros?.toFloat())!!) / (oldSkuDetail?.priceAmountMicros?.toFloat()!!))
            .times(100)).toInt()
    }

    private fun handleBargain(theSku: SkuDetails?, textView: TextView) {

        var baseSku = oldMonthlySku

        if (oldLifeTimeSku != null) {
            if (oldYearlySku?.priceAmountMicros!! < baseSku?.priceAmountMicros!!) {
                baseSku = oldYearlySku
            } else if (oldLifeTimeSku?.priceAmountMicros!! < baseSku.priceAmountMicros) {
                baseSku = oldLifeTimeSku
            }
        }

        val baseMoneyPerDay = BaseMoneyCalculator.calculatePerDayCost(baseSku)

        if (theSku != baseSku) {
            val basePrice = BaseMoneyCalculator.calculatePerDayCost(theSku)
            textView.visibility = View.VISIBLE
            if (basePrice != -1f) {
                val percentage =
                    (((baseMoneyPerDay - basePrice) / baseMoneyPerDay).times(100)).toInt()
                textView.text = getString(R.string.save_50, "%$percentage")
            } else {
                textView.text = getString(R.string.pay_once)
            }
        } else {
            textView.visibility = View.GONE
        }
    }

    fun skuDetailsFetched(theSkuDetail: SkuDetails?, oldSkuDetail: SkuDetails?) {

        oldMonthlySku = (requireActivity() as ViyatekPremiumActivity).oldMonthlySku

        Log.d(ViyatekPremiumActivity.billingLogs, "Where the hell is this $theSkuDetail")
        activeSkuDetail = theSkuDetail
        this.oldSkuDetail = oldSkuDetail
        bindData()
    }

    private fun drawTheLineOnOldPrice(myText: TextView) {
        val theText = myText.text
        val theSpannable = SpannableString(theText)
        val theStartIndex = theText.indexOf("then", 0, true) + "then ".length
        val endIndex = theStartIndex + oldSkuDetail?.price?.length!!
        val strikethroughSpan = StrikethroughSpan()

        val textApperaenceSpan =
            TextAppearanceSpan(requireContext(), android.R.style.TextAppearance_Small)
        ResourcesCompat.getFont(requireContext(), R.font.proximanovaregular)
        theSpannable.apply {

            setSpan(
                strikethroughSpan,
                theStartIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(
                textApperaenceSpan,
                theStartIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(
                ForegroundColorSpan(Color.RED),
                theStartIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        myText.setText(theSpannable, TextView.BufferType.EDITABLE)
    }
}