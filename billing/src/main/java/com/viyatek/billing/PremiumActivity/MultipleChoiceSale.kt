package com.viyatek.billing.PremiumActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TOKEN
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TRIAL_MODE_USED
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TYPE
import com.viyatek.billing.R
import com.viyatek.billing.databinding.FragmentMultipleChoiceSaleBinding

import java.time.Period

abstract class MultipleChoiceSale : Fragment() {

    val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(requireContext()) }
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


            binding.actionButtonGroup.otherSubscriptionPlans.visibility = View.GONE

            binding.actionButtonGroup.restorePurchasButton.setOnClickListener {
                (requireActivity() as ViyatekPremiumActivity).queryPurchaseAsync()
            }

            if (oldLifeTimeSku != activeLifeTimeSku) {
                binding.lifetimeOldPrice.text = oldLifeTimeSku?.price
                binding.lifetimeOldPrice.paintFlags =
                    binding.lifetimeOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.lifetimeOldPrice.visibility = View.GONE
            }

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
            binding.actionButtonGroup.premiumTrialButton2.isEnabled = true
            binding.actionButtonGroup.premiumTrialButton2.setOnClickListener {

                val oldPurchasedSkuId =
                    viyatekKotlinSharedPrefHelper.getPref(SUBSCRIPTION_TYPE)?.getStringValue()!!
                val oldPurchaseSkuToken =
                    viyatekKotlinSharedPrefHelper.getPref(SUBSCRIPTION_TOKEN)?.getStringValue()!!

                val flowParams =
                    if ((requireActivity() as ViyatekPremiumActivity).subSkuListHelper.getSkuList()
                            .contains(oldPurchasedSkuId)
                    ) {
                        BillingFlowParams.newBuilder()
                            .setOldSku(oldPurchasedSkuId, oldPurchaseSkuToken)
                            .setSkuDetails(activeSku!!)
                            .build()
                    } else {
                        BillingFlowParams.newBuilder()
                            .setSkuDetails(activeSku!!)
                            .build()
                    }

                (requireActivity() as ViyatekPremiumActivity).billingManager.billingClient.launchBillingFlow(
                    requireActivity(),
                    flowParams
                )
            }
        } else {
            binding.multipleChoiceGroup.visibility = View.INVISIBLE
            binding.loadingProgressbar.visibility = View.VISIBLE
            binding.actionButtonGroup.premiumTrialButton2.isEnabled = false
        }

        binding.yearlyClNew.setOnClickListener {
            activeSku = activeYearlySku
            handleBackGrounds()
        }
        binding.lifeTimeClNew.setOnClickListener {
            activeSku = activeLifeTimeSku
            handleBackGrounds()
        }
        binding.monthlyClNew.setOnClickListener {
            activeSku = activeMonthlySku
            handleBackGrounds()
        }
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
                            thePeriodTime = thePeriod.days
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

        if (activeSku?.type == BillingClient.SkuType.SUBS) {

            val subscriptionPeriod = activeSku.subscriptionPeriod
            val thePeriod: Period?


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
                                "Weekly"
                            } else {
                                "Days"
                            }

                        }
                    }
                }
            }

        }

        if (thePeriodIdentifier == "Days") {
            appCompatTextView.text = "${thePeriodTime} ${thePeriodIdentifier}"
        } else {
            appCompatTextView.text = thePeriodIdentifier
        }


    }

    private fun handleTexts(activeSku: SkuDetails?) {
        if (activeSku?.type == BillingClient.SkuType.SUBS) {
            binding.cancelAnytimee.text = getString(R.string.cancel_anytime)
            binding.actionButtonGroup.premiumTrialButton2.text = getString(R.string.subscribe_now)

            val subscriptionPeriod = activeSku.subscriptionPeriod
            val thePeriod: Period?
            var thePeriodIdentifier = ""
            var thePeriodTime = 1

            if (subscriptionPeriod.isNotBlank()) {
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
                            thePeriodTime = thePeriod.days

                            if (thePeriodTime == 7) {
                                thePeriodIdentifier = "week"
                            } else {
                                thePeriodIdentifier = "days"
                            }

                        }
                    }
                }
            }

            if (activeSku.freeTrialPeriod.isNotBlank() && viyatekKotlinSharedPrefHelper.getPref(
                    SUBSCRIPTION_TRIAL_MODE_USED
                )?.getIntegerValue() == 0
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
                binding.selectYourPlanText3.text = if (thePeriodTime == 1 && thePeriodTime != 7) {
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
        } else {
            binding.selectYourPlanText3.text =
                getString(R.string.life_time_plan_price, activeSku?.price)
            binding.cancelAnytimee.text = getString(R.string.lifetime_motto)
            binding.actionButtonGroup.premiumTrialButton2.text =
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

        binding.closeActivityButton2.setOnClickListener {
            ReportButonClick("closeButtonClicked")
            requireActivity().finish()
        }

        binding.actionButtonGroup.privacyPolicy.setOnClickListener {
            ReportButonClick("privacyPolicyClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }

        binding.actionButtonGroup.termsOfUse.setOnClickListener {
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