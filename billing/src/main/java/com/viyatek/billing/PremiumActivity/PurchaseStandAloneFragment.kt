package com.viyatek.billing.PremiumActivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Campaign.ActiveSku
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity.Companion.billingLogs
import com.viyatek.billing.R
import com.viyatek.billing.databinding.FragmentStandAloneSaleBinding
import java.time.Period


abstract class PurchaseStandAloneFragment : Fragment() {

    private var _binding: FragmentStandAloneSaleBinding? = null
    private val binding get() = _binding!!

    var activeSkuType = ActiveSku.YEARLY
    var otherSubscriptionPlansAvailable = false
    private var activeSkuDetail: SkuDetails? = null
    private var oldSkuDetail: SkuDetails? = null
    val billingPrefHandlers by lazy { BillingPrefHandlers(requireContext()) }
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }

    private val theActivity by lazy { (requireActivity() as ViyatekPremiumActivity) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandAloneSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeSkuDetail = when (activeSkuType) {
            ActiveSku.MONTHLY -> { theActivity.activeMonthlySku }
            ActiveSku.YEARLY -> { theActivity.activeYearlySku }
            ActiveSku.LIFETIME -> { theActivity.activeLifeTimeSku }
        }

        bindData()

        binding.closeActivityButton.setOnClickListener {
            ReportButonClick("closeButtonClicked")
            requireActivity().onBackPressed()
           // requireActivity().finish()
        }

        binding.saleButtonGroup.viyatekPrivacyPolicy.setOnClickListener {
            ReportButonClick("privacyPolicyClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }
        binding.saleButtonGroup.viyatekTermsOfUse.setOnClickListener {
            ReportButonClick("termsOfUseClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
            startActivity(browserIntent)
            //
        }

        binding.saleButtonGroup.viyatekRestorePurchaseButton.setOnClickListener {
            theActivity.queryPurchaseAsync(true)
        }

        binding.saleButtonGroup.viyatekOtherPlans.apply {
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

        bindConditions(binding.premiumConditions2, binding.premiumConditions3, binding.premiumConditions4, binding.premiumConditions5)
    }

    abstract fun bindConditions(
        first_row: AppCompatTextView,
        second_row: AppCompatTextView,
        third_row: AppCompatTextView,
        fourth_row: AppCompatTextView
    )

    open fun navigateToMultiChoiceFragment() {}

    private fun ReportButonClick(eventName: String) {
        val bundle = Bundle()
        bundle.putString("fragment", "StandAloneSale")
        mFireBaseAnalytics.logEvent(eventName, bundle)
    }

    private fun bindData() = if (activeSkuDetail != null) {

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
            Log.d(billingLogs, "Free Trial ${activeSkuDetail?.freeTrialPeriod!!}")
            Log.d(billingLogs, "Subscription Period ${thePeriod?.years} ")


            if (activeSkuDetail?.freeTrialPeriod != null
                && activeSkuDetail?.freeTrialPeriod?.isNotBlank()!!
                && !billingPrefHandlers.isSubscriptionTrialModeUsed()
            ) {
                val freeTrial = Period.parse(activeSkuDetail?.freeTrialPeriod)
                binding.premiumConditions.text = "${freeTrial.days} Days Free Trial"

                if (activeSkuDetail != oldSkuDetail && oldSkuDetail != null) {
                    val discountAmount = calculateDiscount()

                    binding.planPrice.text = if (thePeriodTime != 1) {
                        val desiredText = SpannableStringBuilder(
                            "${
                                getString(
                                    R.string.save_50,
                                    "%$discountAmount"
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
                        )

                        getString(
                            R.string.stand_alone_save_string_custom_time,
                            "%$discountAmount",
                            oldSkuDetail?.price,
                            activeSkuDetail?.price,
                            thePeriodTime,
                            thePeriodIdentifier
                        )


                    } else {
                        getString(
                            R.string.stand_alone_save_string,
                            "%$discountAmount",
                            oldSkuDetail?.price,
                            activeSkuDetail?.price,
                            thePeriodIdentifier
                        )
                    }

                    drawTheLineOnOldPrice(binding.planPrice)

                } else {
                    binding.planPrice.text = if (thePeriodTime != 1) {
                        getString(
                            R.string.plan_price_with_free_trial_custom_time,
                            freeTrial.days.toString(),
                            activeSkuDetail?.price,
                            thePeriodTime,
                            thePeriodIdentifier
                        )
                    } else {
                        getString(
                            R.string.plan_price_with_free_trial,
                            freeTrial.days.toString(),
                            activeSkuDetail?.price,
                            thePeriodIdentifier
                        )
                    }

                }


            }
            else {
                binding.premiumConditions.text = getString(R.string.access_to_all_features)
                if (activeSkuDetail != oldSkuDetail && oldSkuDetail != null) {
                    val calculateDiscount = calculateDiscount()
                    binding.planPrice.text = if (thePeriodTime == 1) {
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
                    drawTheLineOnOldPrice(binding.planPrice)
                } else {
                    binding.planPrice.text = if (thePeriodTime == 1) {
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
            if (oldSkuDetail != activeSkuDetail && oldSkuDetail != null) {
                val discountAmount = calculateDiscount()
                binding.planPrice.text = getString(
                    R.string.stand_alone_save_string_pay_once,
                    "½$discountAmount", oldSkuDetail?.price, activeSkuDetail?.price
                )
                drawTheLineOnOldPrice(binding.planPrice)
            } else {
                binding.planPrice.text =
                    getString(R.string.life_time_plan_price, activeSkuDetail?.price)
            }

            binding.cancelAnytime.text = getString(R.string.lifetime_motto)
            binding.saleButtonGroup.viyatekPremiumTrialButton.text =
                getString(R.string.start_subscription_button)
        }

        val theIndex = binding.planPrice.text.indexOf("just", 0, true)
        Log.d(billingLogs, "The index in ${binding.planPrice.text} $theIndex")

        binding.planPrice.visibility = View.VISIBLE
        binding.cancelAnytime.visibility = View.VISIBLE
        binding.saleButtonGroup.viyatekPremiumTrialButton.isEnabled = true
        binding.saleButtonGroup.viyatekPremiumTrialButton.setOnClickListener {

            Log.d(billingLogs, "Started billing flow ${(requireActivity() as ViyatekPremiumActivity).appsFlyerUUID}")

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
                        .setOldSku(oldPurchasedSkuId, oldPurchaseSkuToken)
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
        binding.planPrice.visibility = View.INVISIBLE
        binding.cancelAnytime.visibility = View.INVISIBLE
        binding.saleButtonGroup.viyatekPremiumTrialButton.isEnabled = false
    }

    private fun calculateDiscount(): Int {
        return ((((oldSkuDetail?.priceAmountMicros?.toFloat())!! - (activeSkuDetail?.priceAmountMicros?.toFloat())!!) / (oldSkuDetail?.priceAmountMicros?.toFloat()!!))
            .times(100)).toInt()
    }

    fun skuDetailsFetched(theSkuDetail: SkuDetails?, oldSkuDetail: SkuDetails?) {
        Log.d(billingLogs, "Where the hell is this $theSkuDetail")
        activeSkuDetail = theSkuDetail
        this.oldSkuDetail = oldSkuDetail
        bindData()
    }

    private fun drawTheLineOnOldPrice(myText: TextView) {
        val theText = myText.text
        val theSpannable = SpannableString(theText)
        val theStartIndex = theText.indexOf("just", 0, true) + "just ".length
        val endIndex = theStartIndex + oldSkuDetail?.price?.length!!
        val strikethroughSpan = StrikethroughSpan()

        val textAppereanceSpan =
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
                textAppereanceSpan,
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