package com.viyatek.billing.PremiumActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.billingclient.api.*
import com.android.volley.VolleyError
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Campaign.ActiveSku
import com.viyatek.billing.Campaign.CampaignHandler
import com.viyatek.billing.Campaign.CampaignType
import com.viyatek.billing.Handlers.SkuListHelper
import com.viyatek.billing.Interface.SubscriptionListener
import com.viyatek.billing.Managers.BillingSubscribeManager
import com.viyatek.billing.R
import com.viyatek.billing.Statics
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionDataFetch
import com.viyatek.billing.databinding.FragmentPurchaseStandAloneCardBinding
import java.time.Period

abstract class PurchaseStandAloneCardFragment : Fragment(), SubscriptionListener {

    var activeSkuType = ActiveSku.YEARLY
    val subSkuListHelper: SkuListHelper = SkuListHelper()

    var activeSku: String = "premium"
    var activeCampaignSku: String = "premium"
    var activeLocalCampaignSku: String = "premium"
    var appsFlyerUUID = ""

    private val billingPrefsHandler by lazy { BillingPrefHandlers(requireContext()) }

    val subscriptionSkuDetailsList: ArrayList<SkuDetails> = ArrayList()

    var campaignHandler: CampaignHandler? = null
    private val theCampaignType: CampaignType by lazy { campaignHandler?.getActiveCampaign()!! }

    private var activeSkuDetail: SkuDetails? = null
    private var oldSkuDetail: SkuDetails? = null
    val billingPrefHandlers by lazy { BillingPrefHandlers(requireContext()) }
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }

    lateinit var billingManager: BillingSubscribeManager

    private var _binding: FragmentPurchaseStandAloneCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseStandAloneCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            closeButtonClicked()
        }
        campaignHandler?.setCampaign()

        billingManager = BillingSubscribeManager(requireActivity(), this, this)

        subSkuListHelper.addSku(activeSku)
        subSkuListHelper.addSku(activeCampaignSku)
        subSkuListHelper.addSku(activeLocalCampaignSku)

        if (!billingPrefsHandler.isRestorePurchaseAsyncCallMade()) {
            Log.d(ViyatekPremiumActivity.billingLogs, "Making call")
            queryPurchaseAsync()
        } else {
            Log.d(ViyatekPremiumActivity.billingLogs, "call already made")
        }

        billingManager.init(subSkuListHelper.getSkuList())

        binding.viyatekPrivacyPolicy.setOnClickListener {
            ReportButonClick("privacyPolicyClicked")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }

        binding.viyatekTermsOfUse.setOnClickListener {
            ReportButonClick("termsOfUseClicked")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
            startActivity(browserIntent)
            //
        }


    }

    abstract fun closeButtonClicked()

    fun queryPurchaseAsync() {

        billingManager.billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, purchaseHistoryRecords ->
            if (purchaseHistoryRecords != null) {
                var activePurchaseRecord : PurchaseHistoryRecord? = null

                if (purchaseHistoryRecords.size > 0) {
                    for (purchaseHistoryRecord in purchaseHistoryRecords) {
                        Log.d(ViyatekPremiumActivity.billingLogs, "Purchase History Record : $purchaseHistoryRecord")

                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (subSkuListHelper.getSkuList().contains(purchaseHistoryRecord.skus[0])
                            ) {
                                billingPrefsHandler.setSubscriptionTrialModeUsed(true)

                                if (activePurchaseRecord == null) {
                                    activePurchaseRecord = purchaseHistoryRecord
                                } else {
                                    if (purchaseHistoryRecord.purchaseTime > activePurchaseRecord.purchaseTime) {
                                        activePurchaseRecord = purchaseHistoryRecord
                                    }
                                }

                            }
                        }

                    }

                    if (!billingPrefsHandler.isPremium()) {

                        Toast.makeText(
                            requireContext(),
                            "Subscription Purchases found, Checking validity...",
                            Toast.LENGTH_SHORT
                        ).show()

                        activePurchaseRecord?.let { SubscriptionDataFetch(
                            billingManager.billingClient,
                            requireContext(),
                            this
                        )
                            .executeNetWorkCall(
                                getString(R.string.viyatek_subscription_check_endpoint),
                                it.skus[0],
                                it.purchaseToken
                            )
                        }
                    }
                }


            }
            else {
                if(billingPrefsHandler.isRestorePurchaseAsyncCallMade()) {
                    Toast.makeText(
                        requireContext(),
                        "Purchase not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d(ViyatekPremiumActivity.billingLogs, "Purchase History Record not found null")
            }

            billingPrefsHandler.setRestorePurchaseAsyncCallMade(true)
        }
    }

    override fun SubSkuFetched(subsciptionSkuDetailsList: List<SkuDetails>) {
        subscriptionSkuDetailsList.clear()
        subscriptionSkuDetailsList.addAll(subsciptionSkuDetailsList)
        //subsSkuDetails = list

        Log.d(Statics.BILLING_LOGS, "Skus fetched")

        for (skuDetails in subsciptionSkuDetailsList) {
            when (skuDetails.sku) {
                activeSku -> {
                    activeSkuDetail = skuDetails
                    oldSkuDetail = skuDetails
                }
            }
        }

        handleCampaign()
    }

    private fun handleCampaign() {

        if(campaignHandler != null) {
            // Log.d(ViyatekPremiumActivity.billingLogs, "The campaign type ${theCampaignType.name}")
            handleActiveSku(theCampaignType)
        }
        else
        {
            bindData()
        }
    }

    private fun handleActiveSku(theCampaignType: CampaignType?) {


        val shouldBeSku: String = when (theCampaignType) {
            CampaignType.REMOTE_CAMPAIGN, CampaignType.SPECIAL_DAY_CAMPAIGN -> {
                activeCampaignSku
            }

            CampaignType.LOCAL_CAMPAIGN -> {
                activeLocalCampaignSku

            }
            else -> {
                activeSku
            }
        }

        for (skuDetail in subscriptionSkuDetailsList) { when (skuDetail.sku) {shouldBeSku -> { activeSkuDetail = skuDetail } } }


        bindData()


    }

    private fun bindData() = if (activeSkuDetail != null) {

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
        Log.d(
            ViyatekPremiumActivity.billingLogs,
            "Free Trial ${activeSkuDetail?.freeTrialPeriod!!}"
        )
        Log.d(ViyatekPremiumActivity.billingLogs, "Subscription Period ${thePeriod?.years} ")


        if (!activeSkuDetail?.freeTrialPeriod?.isBlank()!! && !billingPrefHandlers.isSubscriptionTrialModeUsed()
        ) {
            val freeTrial = Period.parse(activeSkuDetail?.freeTrialPeriod)
            binding.freeTrialDays.text = getString(R.string.try_3_days_for_free, freeTrial.days)
            binding.freeTrialDays.visibility = View.VISIBLE
        }

        binding.priceMonthlyInfo.text = getString(R.string.monthly_price_info,
            12,
            "TRY 14.17",
            "%55"
        )

        binding.priceInfo.text = if (thePeriodTime == 1) {
            getString(
                R.string.then_price_if_not_canceled,
                activeSkuDetail?.price,
                thePeriodIdentifier
            )
        } else {
            getString(
                R.string.then_price_if_not_canceled_custom_time,
                activeSkuDetail?.price,
                thePeriodTime,
                thePeriodIdentifier
            )
        }

        binding.priceInfo.visibility = View.VISIBLE
        binding.priceMonthlyInfo.visibility = View.VISIBLE
        binding.freeTrialDays.visibility = View.VISIBLE
        binding.loadingProgressbar.visibility = View.GONE
        binding.subscribeButton.isEnabled = true
        binding.subscribeButton.setOnClickListener {

            Log.d(ViyatekPremiumActivity.billingLogs, "Selam Aleküm")
            ReportButonClick("subscribeButtonClick")

            val oldPurchasedSkuId = billingPrefHandlers.getSubscriptionType()!!
            val oldPurchaseSkuToken = billingPrefHandlers.getSubscriptionToken()!!

            val flowParams =
                if (subSkuListHelper.getSkuList().contains(oldPurchasedSkuId) && !oldPurchaseSkuToken.isEmpty()
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
                        .setObfuscatedAccountId(appsFlyerUUID)
                        .build()
                }


            billingManager.billingClient.launchBillingFlow(
                requireActivity(),
                flowParams
            )
        }

    }
        else {
            binding.priceInfo.visibility = View.INVISIBLE
            binding.priceMonthlyInfo.visibility = View.INVISIBLE
            binding.freeTrialDays.visibility = View.INVISIBLE
        binding.loadingProgressbar.visibility = View.VISIBLE
            binding.subscribeButton.isEnabled = false
        }

    private fun calculateDiscount(): Int {
        return ((((oldSkuDetail?.priceAmountMicros?.toFloat())!! - (activeSkuDetail?.priceAmountMicros?.toFloat())!!) / (oldSkuDetail?.priceAmountMicros?.toFloat()!!))
            .times(100)).toInt()
    }

    override fun SubscriptionVerified() {}

    override fun SubscriptionVerificationFailed(error: VolleyError?) {}

    override fun subscriptionInGracePeriod(purchaseId: String) {}

    abstract fun ReportButonClick(eventName : String)
}