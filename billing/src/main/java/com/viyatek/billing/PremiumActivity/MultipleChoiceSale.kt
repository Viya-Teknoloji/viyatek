package com.viyatek.billing.PremiumActivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.google.android.material.card.MaterialCardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.R
import com.viyatek.billing.Statics
import com.viyatek.billing.databinding.FragmentMultipleChoiceSaleBinding

import java.time.Period
import kotlin.time.days

abstract class MultipleChoiceSale : Fragment() {

    val billingPrefsHanlder by lazy { BillingPrefHandlers(requireContext()) }
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }
    private var _binding: FragmentMultipleChoiceSaleBinding? = null
    private val binding get() = _binding!!

    var activeYearlySku: SkuDetails? = null
    var oldYearlySku: SkuDetails? = null
    var activeMonthlySku: SkuDetails? = null
    var oldMonthlySku: SkuDetails? = null
    var activeLifeTimeSku: SkuDetails? = null
    var oldLifeTimeSku: SkuDetails? = null

    var activeSku: SkuDetails? = null
    var cardList: ArrayList<MaterialCardView> = ArrayList()

    var isCloseButtonAnimationEnabled = false
    var closeButtonAnimationTime = 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMultipleChoiceSaleBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun bindData() {

        if (activeYearlySku != null || activeLifeTimeSku != null) {

            binding.monthlyPrice.text = activeMonthlySku?.price
            binding.yearlyPrice.text = activeYearlySku?.price
            binding.lifetimePrice.text = activeLifeTimeSku?.price

            binding.premiumSaleButtonGroup.viyatekOtherPlans.visibility = View.GONE
            binding.premiumSaleButtonGroup.viyatekRestorePurchaseButton.setOnClickListener {
                (requireActivity() as ViyatekPremiumActivity).queryPurchaseAsync(true)
            }

            if (oldLifeTimeSku != activeLifeTimeSku) {
                binding.lifetimeOldPrice.text = oldLifeTimeSku?.price
                binding.lifetimeOldPrice.paintFlags =
                    binding.lifetimeOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } 
            else { binding.lifetimeOldPrice.visibility = View.GONE }

            if (oldYearlySku != activeYearlySku) {
                binding.oldYearlyPrice.text = oldYearlySku?.price
                binding.oldYearlyPrice.paintFlags =
                    binding.oldYearlyPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.oldYearlyPrice.visibility = View.GONE
            }

            if (oldMonthlySku != activeMonthlySku) {
                binding.monthlyOldPrice.text = oldMonthlySku?.price
                binding.monthlyOldPrice.paintFlags =
                    binding.monthlyOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.monthlyOldPrice.visibility = View.GONE
            }

            handleIdentifiers(binding.lifetimeIdentifier, activeLifeTimeSku)
            handleIdentifiers(binding.yearlyIdentifier, activeYearlySku)
            handleIdentifiers(binding.monthlyIdentifier, activeMonthlySku)

            if (activeSku == null) activeSku = activeYearlySku

            if (activeSku != null) {
                handleBackGrounds()
                binding.selectYourPlanText3.visibility = View.VISIBLE
            } else {
                binding.selectYourPlanText3.visibility = View.INVISIBLE
            }

            calculateBargainAmounts()

            binding.multipleChoiceGroup.visibility = View.VISIBLE
            binding.loadingProgressbar.visibility = View.INVISIBLE
            binding.premiumSaleButtonGroup.viyatekPremiumTrialButton.isEnabled = true
            binding.premiumSaleButtonGroup.viyatekPremiumTrialButton.setOnClickListener {

                val oldPurchasedSkuId = billingPrefsHanlder.getSubscriptionType()!!
                val oldPurchaseSkuToken = billingPrefsHanlder.getSubscriptionToken()!!

                val flowParams =
                    if ((requireActivity() as ViyatekPremiumActivity).subSkuListHelper.getSkuList()
                            .contains(oldPurchasedSkuId)
                    ) {

                        Log.d(Statics.BILLING_LOGS, "Original Price ${activeSku?.originalPrice} normal price ${activeSku?.price}")

                        BillingFlowParams.newBuilder()
                            .setObfuscatedAccountId((requireActivity() as ViyatekPremiumActivity).appsFlyerUUID)
                            .setObfuscatedProfileId((requireActivity() as ViyatekPremiumActivity).gaid)
                            .setOldSku(oldPurchasedSkuId, oldPurchaseSkuToken)
                            .setSkuDetails(activeSku!!)
                            .build()
                    } else {
                        Log.d(Statics.BILLING_LOGS, "Original Price ${activeSku?.originalPrice} normal price ${activeSku?.price}")

                        BillingFlowParams.newBuilder()
                            .setObfuscatedAccountId((requireActivity() as ViyatekPremiumActivity).appsFlyerUUID)
                            .setObfuscatedProfileId((requireActivity() as ViyatekPremiumActivity).gaid)
                            .setSkuDetails(activeSku!!)
                            .build()
                    }

                (requireActivity() as ViyatekPremiumActivity).billingManager.billingClient.launchBillingFlow(
                    requireActivity(),
                    flowParams
                )
            }
        }
        else {
            binding.multipleChoiceGroup.visibility = View.INVISIBLE
            binding.loadingProgressbar.visibility = View.VISIBLE
            binding.premiumSaleButtonGroup.viyatekPremiumTrialButton.isEnabled = false
        }

      
        binding.monthlyClNew.setOnClickListener {
            Log.d("Billing", "Monthly Card Clicked")
            activeSku = activeMonthlySku
            handleBackGrounds()
        }

        binding.yearlyClNew.setOnClickListener {
            Log.d("Billing", "Yearly Card Clicked")
            activeSku = activeYearlySku
            handleBackGrounds()
        }
        binding.lifeTimeClNew.setOnClickListener {
            Log.d("Billing", "LifeTime Card Clicked")
            activeSku = activeLifeTimeSku
            handleBackGrounds()
        }

        val monthlyClickListener = View.OnClickListener {
            Log.d("Billing", "Monthly Card CL view Clicked")
            activeSku = activeMonthlySku
            handleBackGrounds()
        }

        binding.monthlyClNew.setOnClickListener (monthlyClickListener)
        binding.monthlyIdentifier.setOnClickListener (monthlyClickListener)
        binding.monthlyPrice.setOnClickListener (monthlyClickListener)
        binding.monthlyOldPrice.setOnClickListener (monthlyClickListener)
            
    }

    private fun calculateBargainAmounts() {
        handleBargain(activeMonthlySku, binding.monthlyPromoMotto)
        handleBargain(activeYearlySku, binding.yearlyPromoMotto)
        handleBargain(activeLifeTimeSku, binding.lifetimePromoMotto)
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

        val baseMoneyPerDay = calculatePerDayCost(baseSku)

        if (theSku != baseSku) {
            val basePrice = calculatePerDayCost(theSku)
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

    private fun calculatePerDayCost(baseSku: SkuDetails?): Float {
        var baseMoneyPerDay: Float = -1f

        if (baseSku?.type == BillingClient.SkuType.SUBS) {

            val subscriptionPeriod = baseSku.subscriptionPeriod
            val thePeriod: Period?
            var thePeriodTime: Int
            if (subscriptionPeriod.isNotBlank()) {
                thePeriod = Period.parse(subscriptionPeriod)
                thePeriod.apply {
                    when {
                        years != 0 -> {
                            thePeriodTime = thePeriod.years
                            baseMoneyPerDay =
                                baseSku.priceAmountMicros.toFloat() / (thePeriodTime * 365)
                        }
                        months != 0 -> {
                            baseMoneyPerDay =
                                baseSku.priceAmountMicros.toFloat() / (thePeriod.months * 30)
                        }
                        else -> {
                            thePeriodTime = if(thePeriod.days == 7) 1 else thePeriod.days
                            baseMoneyPerDay = baseSku.priceAmountMicros.toFloat() / (thePeriod.days)
                        }
                    }
                }
            }
        }

        return baseMoneyPerDay
    }

    @SuppressLint("RestrictedApi")
    private fun handleBackGrounds() {

        handleTexts(activeSku)

        val passiveBGColor = Color.argb(136, 255, 255, 255)
        
        for (view in cardList) {
            view.setCardBackgroundColor(passiveBGColor)
            view.strokeColor = passiveBGColor
            view.strokeWidth = 0
        }

        binding.yearlyPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        binding.monthlyPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        binding.lifetimePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.yearlyIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                10,
                16,
                1,
                TypedValue.COMPLEX_UNIT_SP
            )
            binding.lifetimeIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                10,
                16,
                1,
                TypedValue.COMPLEX_UNIT_SP
            )
            binding.monthlyIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                10,
                16,
                1,
                TypedValue.COMPLEX_UNIT_SP
            )
        } else {
            binding.yearlyIdentifier.textSize = resources.getDimension(R.dimen.premium_pack)
            binding.lifetimeIdentifier.textSize = resources.getDimension(R.dimen.premium_pack)
            binding.monthlyIdentifier.textSize = resources.getDimension(R.dimen.premium_pack)
        }

        when (activeSku) {
            activeYearlySku -> {
                binding.yearlyClNew.setCardBackgroundColor(Color.WHITE)
                binding.yearlyClNew.strokeColor = Color.BLACK

                binding.yearlyClNew.strokeWidth = DpPxConverter(requireContext()).dpToPx(3f)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.yearlyIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                        12,
                        20,
                        1,
                        TypedValue.COMPLEX_UNIT_SP
                    )
                } else {
                    binding.yearlyIdentifier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                }
                binding.yearlyPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

            }
            activeLifeTimeSku -> {
                binding.lifeTimeClNew.setCardBackgroundColor(Color.WHITE)
                binding.lifeTimeClNew.strokeColor = Color.BLACK
                binding.lifeTimeClNew.strokeWidth = DpPxConverter(requireContext())
                    .dpToPx(3f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.lifetimeIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                        12,
                        20,
                        1,
                        TypedValue.COMPLEX_UNIT_SP
                    )
                } else {
                    binding.lifetimeIdentifier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                }
                binding.lifetimePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }
            activeMonthlySku -> {
                binding.monthlyClNew.setCardBackgroundColor(Color.WHITE)
                binding.monthlyClNew.strokeColor = Color.BLACK
                binding.monthlyClNew.strokeWidth = DpPxConverter(requireContext()).dpToPx(3f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.monthlyIdentifier.setAutoSizeTextTypeUniformWithConfiguration(
                        12,
                        20,
                        1,
                        TypedValue.COMPLEX_UNIT_SP
                    )
                } else {
                    binding.monthlyIdentifier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                }
                binding.monthlyPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }
        }
    }

    private fun handleIdentifiers(appCompatTextView: AppCompatTextView, activeSku: SkuDetails?) {
        var thePeriodIdentifier = "Lifetime"
        var thePeriodTime = 1

        Log.d(Statics.BILLING_LOGS, "Active sku : ${activeSku?.sku}")

        if (activeSku?.type == BillingClient.SkuType.SUBS) {

            Log.d(Statics.BILLING_LOGS, "It is a subscription : ${activeSku?.sku}")

            val subscriptionPeriod = activeSku.subscriptionPeriod
            val thePeriod: Period?

            Log.d(Statics.BILLING_LOGS, "subscription  period is: ${activeSku?.sku}")

            if (subscriptionPeriod.isNotBlank()) {
                thePeriod = Period.parse(subscriptionPeriod)
                thePeriod.apply {
                    when {
                        years != 0 -> {
                            thePeriodIdentifier = "Yearly"
                            thePeriodTime = thePeriod.years
                        }
                        months != 0 -> {
                            thePeriodIdentifier = "Monthly"
                            thePeriodTime = thePeriod.months
                        }
                        else -> {
                            thePeriodTime = thePeriod.days

                            thePeriodIdentifier = if (thePeriodTime == 7) {
                                thePeriodTime = 1
                                "Weekly"
                            } else {
                                "Days"
                            }

                        }
                    }
                }
            }
            else{
                Log.d(Statics.BILLING_LOGS, "subscription  period is: blank")
            }

        }

        if (thePeriodIdentifier == "Days") {
            appCompatTextView.text = "$thePeriodTime $thePeriodIdentifier"
        } else {
            appCompatTextView.text = thePeriodIdentifier
        }


    }

    private fun handleTexts(activeSku: SkuDetails?) {
        if (activeSku?.type == BillingClient.SkuType.SUBS) {
            binding.cancelAnytimee.text = getString(R.string.cancel_anytime)
            binding.premiumSaleButtonGroup.viyatekPremiumTrialButton.text = getString(R.string.subscribe_now)

            val subscriptionPeriod = activeSku.subscriptionPeriod
            val thePeriod: Period?
            var thePeriodIdentifier = ""
            var thePeriodTime = 1

            if (subscriptionPeriod.isNotBlank()) {
                thePeriod = Period.parse(subscriptionPeriod)
                thePeriod.apply {
                    when {
                        years != 0 -> {
                            Log.d(Statics.BILLING_LOGS, "The Period time in year ${thePeriod.years}")
                            thePeriodIdentifier = "year"
                            thePeriodTime = thePeriod.years
                        }
                        months != 0 -> {
                            Log.d(Statics.BILLING_LOGS, "The Period time in year ${thePeriod.months}")
                            thePeriodIdentifier = "month"
                            thePeriodTime = thePeriod.months
                        }
                        else -> {
                            thePeriodTime = thePeriod.days

                            Log.d(Statics.BILLING_LOGS, "The Period time ${thePeriod.days}")
                            thePeriodIdentifier = if (thePeriodTime == 7) {
                                thePeriodTime = 1
                                "week"

                            } else {
                                "days"
                            }
                            Log.d(Statics.BILLING_LOGS, "The Period time after $thePeriodTime")

                        }
                    }
                }
            }

            if (activeSku.freeTrialPeriod.isNotBlank() && !billingPrefsHanlder.isSubscriptionTrialModeUsed()
            ) {
                val freeTrial = Period.parse(activeSku.freeTrialPeriod)
                binding.selectYourPlanText3.text = if (thePeriodTime != 1 && thePeriodTime != 7) {
                    getString(
                        R.string.plan_price_with_free_trial_custom_time,
                        freeTrial.days.toString(),
                        activeSku.price,
                        thePeriodTime,
                        thePeriodIdentifier
                    )
                } else {
                    getString(
                        R.string.plan_price_with_free_trial,
                        freeTrial.days.toString(),
                        activeSku.price,
                        thePeriodIdentifier
                    )
                }
            } else {


                Log.d(Statics.BILLING_LOGS, "The period time is $thePeriodTime")
                binding.selectYourPlanText3.text = if (thePeriodTime == 1) {
                    getString(
                        R.string.plan_price_without_free_trial,
                        activeSku.price,
                        thePeriodIdentifier
                    )
                } else {
                    getString(
                        R.string.plan_price_without_free_trial_custom_time,
                        activeSku.price,
                        thePeriodTime,
                        thePeriodIdentifier
                    )
                }
            }
        }
        else {

            binding.selectYourPlanText3.text = getString(R.string.life_time_plan_price, activeSku?.price)
            binding.cancelAnytimee.text = getString(R.string.lifetime_motto)
            binding.premiumSaleButtonGroup.viyatekPremiumTrialButton.text =
                getString(R.string.start_subscription_button)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeMonthlySku = (requireActivity() as ViyatekPremiumActivity).activeMonthlySku
        oldMonthlySku = (requireActivity() as ViyatekPremiumActivity).oldMonthlySku
        activeYearlySku = (requireActivity() as ViyatekPremiumActivity).activeYearlySku
        oldYearlySku = (requireActivity() as ViyatekPremiumActivity).oldYearlySku
        activeLifeTimeSku = (requireActivity() as ViyatekPremiumActivity).activeLifeTimeSku
        oldLifeTimeSku = (requireActivity() as ViyatekPremiumActivity).oldLifeTimeSku

        cardList.clear()
        cardList.add(binding.yearlyClNew)
        cardList.add(binding.monthlyClNew)
        cardList.add(binding.lifeTimeClNew)

        bindData()

        binding.yearlyClNew.isChecked = true
        binding.yearlyClNew.setCardBackgroundColor(Color.WHITE)

        binding.closeActivityButton2.apply {
            if(isCloseButtonAnimationEnabled)
            {
                alpha = 0f
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    animate().alpha(1f).setDuration(1000L).setListener(object  : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)

                            setOnClickListener {
                                ReportButonClick("closeButtonClicked")
                                requireActivity().onBackPressed()
                                // requireActivity().finish()
                            }
                        }
                    })
                }, closeButtonAnimationTime)

            }
            else
            {
                setOnClickListener {
                    ReportButonClick("closeButtonClicked")
                    requireActivity().onBackPressed()
                    // requireActivity().finish()
                }
            }

        }
        binding.premiumSaleButtonGroup.viyatekPrivacyPolicy.setOnClickListener {
            ReportButonClick("privacyPolicyClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }

        binding.premiumSaleButtonGroup.viyatekTermsOfUse.setOnClickListener {
            ReportButonClick("termsOfUseClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
            startActivity(browserIntent)
            //
        }

    }

    fun skuDetailsFetched(
        activeYearlySku: SkuDetails?,
        oldYearlySku: SkuDetails?,
        activeMonthlySku: SkuDetails?,
        oldMonthlySku: SkuDetails?,
        activeLifeTimeSku: SkuDetails?,
        oldLifeTimeSku: SkuDetails?
    ) {
        this.activeMonthlySku = activeMonthlySku
        this.oldMonthlySku = oldMonthlySku
        this.activeYearlySku = activeYearlySku
        this.oldYearlySku = oldYearlySku
        this.activeLifeTimeSku = activeLifeTimeSku
        this.oldLifeTimeSku = oldLifeTimeSku

        bindData()
    }

    private fun ReportButonClick(eventName: String) {
        val bundle = Bundle()
        bundle.putString("fragment", "MultipleChoiceSale")
        mFireBaseAnalytics.logEvent(eventName, bundle)
    }
}