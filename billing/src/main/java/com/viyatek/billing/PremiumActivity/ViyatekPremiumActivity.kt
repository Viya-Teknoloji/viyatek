package com.viyatek.billing.PremiumActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails
import com.android.volley.VolleyError
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.Campaign.CampaignHandler
import com.viyatek.billing.Campaign.CampaignType
import com.viyatek.billing.Handlers.SkuListHelper
import com.viyatek.billing.Interface.InAppPurchaseListener
import com.viyatek.billing.Managers.BillingManager
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.RESTORE_PURCHASE_ASYNC_CALL_MADE
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TRIAL_MODE_USED
import com.viyatek.billing.R
import com.viyatek.billing.SubscriptionHelpers.SubscribeCheck
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionDataFetch
import com.viyatek.billing.databinding.ActivityViyatekPremiumBinding


abstract class ViyatekPremiumActivity : AppCompatActivity(), InAppPurchaseListener {


    var multiChoiceEnabled = false
    var multiChoiceFragmentId: Int = R.id.purchaseBaseFragment
    var standAloneSaleFragmentId = R.id.purchaseBaseFragment
    var activeSlot = 2L
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }


    var secondSlotSku: String = "premium"
    var secondSlotCampaignSku: String = "premium"
    var secondSlotLocalCampaignSku: String = "premium"

    var firstSlotSku: String = "premium"
    var firstSlotMonthlySku: String = "premium"
    var firstSlotLocalCampaignSku: String = "premium"

    var thirdSlotSku: String = "premium"
    var thirdSlotCampaignSku: String = "premium"
    var thirdSlotLifeTimeSku: String = "premium"

    val navInflater by lazy { navController.navInflater }
    var campaignHandler: CampaignHandler? = null

    private val theCampaignType: CampaignType by lazy { campaignHandler?.getActiveCampaign()!! }
    lateinit var graph: NavGraph
    val navController by lazy {
        NavHostFragment.findNavController(supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!)
    }
    private var isPremiumSkuFetched: Boolean = false
    private var isCameFromMainActivityDialog: Boolean = false
    private var isSubscriptionSkuFetched: Boolean = false

    private val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(this) }

    private lateinit var binding: ActivityViyatekPremiumBinding
    lateinit var billingManager: BillingManager

    val inAppSkuListHelper: SkuListHelper = SkuListHelper()
    val subSkuListHelper: SkuListHelper = SkuListHelper()

    val subscriptionSkuDetailsList: ArrayList<SkuDetails> = ArrayList()
    val managedProductSkuDetails: ArrayList<SkuDetails> = ArrayList()

    var activeMonthlySku: SkuDetails? = null
    var activeYearlySku: SkuDetails? = null
    var activeLifeTimeSku: SkuDetails? = null

    var oldMonthlySku: SkuDetails? = null
    var oldYearlySku: SkuDetails? = null
    var oldLifeTimeSku: SkuDetails? = null

    companion object {
        val billingLogs = "Billing"
        var isCampaignDialogOnShow = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViyatekPremiumBinding.inflate(layoutInflater)
        setPremiumActivityVariables(navInflater)

        val view = binding.root
        setContentView(view)

        if (multiChoiceEnabled) {
            graph.startDestination = multiChoiceFragmentId
        } else {
            graph.startDestination = standAloneSaleFragmentId
        }

        navController.graph = graph

        campaignHandler?.setCampaign()

        if (campaignHandler?.getActiveCampaign() == CampaignType.NO_CAMPAIGN) {
            setLocalCampaign()
        }

        if (intent != null) {
            isCameFromMainActivityDialog = intent.getBooleanExtra("cameFromBargainDialog", false)
        }

        billingManager = BillingManager(this, this)

        subSkuListHelper.addSku(secondSlotSku)
        subSkuListHelper.addSku(firstSlotSku)
        subSkuListHelper.addSku(secondSlotCampaignSku)
        subSkuListHelper.addSku(firstSlotMonthlySku)
        subSkuListHelper.addSku(firstSlotLocalCampaignSku)
        subSkuListHelper.addSku(secondSlotLocalCampaignSku)

        inAppSkuListHelper.addSku(thirdSlotSku)
        inAppSkuListHelper.addSku(thirdSlotCampaignSku)
        inAppSkuListHelper.addSku(thirdSlotLifeTimeSku)


        if (!viyatekKotlinSharedPrefHelper.checkUserExists(this)) {
            Log.d(billingLogs, "Create Prefs")
            viyatekKotlinSharedPrefHelper.createLocalAccount()
        } else {
            if (viyatekKotlinSharedPrefHelper.getPref(RESTORE_PURCHASE_ASYNC_CALL_MADE)
                    .getIntegerValue() == 0
            ) {
                Log.d(billingLogs, "Making call")
                queryPurchaseAsync()
            } else {
                Log.d(billingLogs, "call already made")
            }
        }

        billingManager.init(subSkuListHelper.getSkuList(), inAppSkuListHelper.getSkuList())
    }

    fun queryPurchaseAsync() {
        billingManager.billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, purchaseHistoryRecords ->
            if (purchaseHistoryRecords != null) {
                var activePurchaseRecord : PurchaseHistoryRecord? = null
                if (purchaseHistoryRecords.size > 0) {
                    for (purchaseHistoryRecord in purchaseHistoryRecords) {
                        Log.d(billingLogs, "Purchase History Record : $purchaseHistoryRecord")

                        if (inAppSkuListHelper.getSkuList().contains(purchaseHistoryRecord.sku)) {
                            viyatekKotlinSharedPrefHelper.applyPrefs(
                                ViyatekKotlinSharedPrefHelper.PREMIUM,
                                1
                            )

                            Toast.makeText(
                                this@ViyatekPremiumActivity,
                                getString(R.string.verification_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (subSkuListHelper.getSkuList().contains(purchaseHistoryRecord.sku)
                            ) {
                                viyatekKotlinSharedPrefHelper.applyPrefs(
                                    SUBSCRIPTION_TRIAL_MODE_USED,
                                    1
                                )

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

                    if (viyatekKotlinSharedPrefHelper.getPref(ViyatekKotlinSharedPrefHelper.PREMIUM).getIntegerValue() != 1
                    ) {

                        Toast.makeText(
                            this@ViyatekPremiumActivity,
                            "Subscription Purchases found, Checking validity...",
                            Toast.LENGTH_SHORT
                        ).show()

                        activePurchaseRecord?.let { SubscriptionDataFetch(
                                this@ViyatekPremiumActivity, this,
                                billingManager.billingClient
                            )
                                .executeNetWorkCall(
                                    getString(R.string.viyatek_subscription_check_endpoint),
                                    it.sku,
                                    it.purchaseToken
                                )
                        }
                    }
                }
                else {
                    Log.d(billingLogs, "Purchase History Record not found size 0") }

            }
            else {
                Toast.makeText(
                    this@ViyatekPremiumActivity,
                    "Purchase not found",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d(billingLogs, "Purchase History Record not found null")
            }

            viyatekKotlinSharedPrefHelper.applyPrefs(RESTORE_PURCHASE_ASYNC_CALL_MADE, 1)
        }
    }

    abstract fun setLocalCampaign()

    open fun setPremiumActivityVariables(navInflater: NavInflater) {}

    override fun SubSkuFetched(subsciptionSkuDetailsList: List<SkuDetails>) {
        subscriptionSkuDetailsList.clear()
        subscriptionSkuDetailsList.addAll(subsciptionSkuDetailsList)
        isSubscriptionSkuFetched = true
        //subsSkuDetails = list

        Log.d(billingLogs, "Skus fetched")

        for (skuDetails in subsciptionSkuDetailsList) {
            when (skuDetails.sku) {
                secondSlotSku -> {
                    activeYearlySku = skuDetails
                    oldYearlySku = skuDetails
                }

                firstSlotSku -> {
                    activeMonthlySku = skuDetails
                    oldMonthlySku = skuDetails
                }
            }
        }

        handleCampaign()
    }

    override fun SubscriptionVerificationFailed(error: VolleyError?) {}

    private fun handleCampaign() {

        Log.d(billingLogs, "The campaign type ${theCampaignType.name}")

        if (isPremiumSkuFetched && isSubscriptionSkuFetched) {
            handleActiveSku(theCampaignType)
            skuDetailsCallback()
        }

        if (isPremiumSkuFetched && isSubscriptionSkuFetched && !isDestroyed &&
            !isCampaignDialogOnShow && !isCameFromMainActivityDialog && !isFinishing && !isDestroyed
        ) {

            showCampaignDialog(theCampaignType)
        }

    }

    abstract fun showCampaignDialog(theCampaignType: CampaignType)

    private fun reportCampaign(campaignType: String) {
        val bundle = Bundle()
        bundle.putString("CampaignType", campaignType)
        mFireBaseAnalytics.logEvent("CampaignActive", bundle)
    }

    private fun handleActiveSku(theCampaignType: CampaignType?) {

        if (theCampaignType != null) {
            reportCampaign(theCampaignType.name)
        }

        val shouldBeYearlySku: String
        val shouldBeMonthlySku: String
        val shouldBeLifeTimeSku: String

        when (theCampaignType) {
            CampaignType.REMOTE_CAMPAIGN, CampaignType.SPECIAL_DAY_CAMPAIGN -> {

                shouldBeYearlySku = secondSlotCampaignSku
                shouldBeMonthlySku = firstSlotMonthlySku
                shouldBeLifeTimeSku = thirdSlotCampaignSku
            }

            CampaignType.LOCAL_CAMPAIGN -> {
                shouldBeMonthlySku = firstSlotLocalCampaignSku
                shouldBeYearlySku = secondSlotLocalCampaignSku
                shouldBeLifeTimeSku = thirdSlotLifeTimeSku
            }
            else -> {
                shouldBeYearlySku = secondSlotSku
                shouldBeMonthlySku = firstSlotSku
                shouldBeLifeTimeSku = thirdSlotSku
            }
        }

        for (skuDetail in managedProductSkuDetails) {
            when (skuDetail.sku) {
                shouldBeLifeTimeSku -> {
                    activeLifeTimeSku = skuDetail
                }
                shouldBeYearlySku -> {
                    activeYearlySku = skuDetail
                }
                shouldBeMonthlySku -> {
                    activeMonthlySku = skuDetail
                }
            }
        }

        for (skuDetail in subscriptionSkuDetailsList) {
            when (skuDetail.sku) {
                shouldBeYearlySku -> {
                    activeYearlySku = skuDetail
                }
                shouldBeMonthlySku -> {
                    activeMonthlySku = skuDetail
                }
                shouldBeLifeTimeSku -> {
                    activeLifeTimeSku = skuDetail
                }
            }
        }

        skuDetailsCallback()

    }

    private fun skuDetailsCallback() {
        Log.d(billingLogs, "Skus details")
        val fragment = getCurrentFragment()
        if (fragment != null && fragment.isAdded) {
            when (fragment) {
                is MultipleChoiceSale -> {
                    fragment.skuDetailsFetched(
                        activeYearlySku,
                        oldYearlySku,
                        activeMonthlySku,
                        oldMonthlySku,
                        activeLifeTimeSku,
                        oldLifeTimeSku
                    )//Will call the fragment function with sku}
                }
                is PurchaseStandAloneFragment -> {
                    val oldSkuDetail: SkuDetails?

                    val activeSkuDetail = when (activeSlot) {
                        0L -> {
                            oldSkuDetail = oldMonthlySku
                            activeMonthlySku
                        }
                        1L -> {
                            oldSkuDetail = oldYearlySku
                            activeYearlySku
                        }
                        2L -> {
                            oldSkuDetail = oldLifeTimeSku
                            activeLifeTimeSku
                        }
                        else -> {
                            oldSkuDetail = oldLifeTimeSku
                            activeYearlySku
                        }
                    }

                    fragment.skuDetailsFetched(activeSkuDetail, oldSkuDetail)
                }
            }
        }
    }

    override fun ManagedProductsSkuFetched(skuDetailsList: List<SkuDetails>) {
        managedProductSkuDetails.clear()
        managedProductSkuDetails.addAll(skuDetailsList)

        isPremiumSkuFetched = true
        //subsSkuDetails = list

        for (skuDetails in managedProductSkuDetails) {
            if (skuDetails.sku == thirdSlotSku) {
                activeLifeTimeSku = skuDetails
                oldLifeTimeSku = skuDetails
            }
        }

        handleCampaign()
    }

    override fun SubscriptionVerified() {}

    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment

        navHostFragment?.apply {
            return if (this.isAdded) {
                val manager = navHostFragment.childFragmentManager
                manager.fragments[0]
            } else {
                null
            }
        }

        return null

    }


}