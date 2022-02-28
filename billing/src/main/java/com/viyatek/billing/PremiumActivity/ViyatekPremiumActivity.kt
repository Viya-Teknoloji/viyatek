package com.viyatek.billing.PremiumActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails
import com.android.volley.VolleyError
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Campaign.CampaignHandler
import com.viyatek.billing.Campaign.CampaignType
import com.viyatek.billing.Handlers.SkuListHelper
import com.viyatek.billing.Interface.InAppPurchaseListener
import com.viyatek.billing.Interface.ProductRestoreListener
import com.viyatek.billing.Managers.BillingManager
import com.viyatek.billing.R
import com.viyatek.billing.Statics
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionDataFetch
import com.viyatek.billing.databinding.ActivityViyatekPremiumBinding
import java.lang.Exception


abstract class ViyatekPremiumActivity : AppCompatActivity(), InAppPurchaseListener, ProductRestoreListener {


    var multiChoiceEnabled = false
    var multiChoiceFragmentId: Int = R.id.purchaseBaseFragment
    var standAloneSaleFragmentId = R.id.purchaseBaseFragment
    var activeSlot = 2L
    val mFireBaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }
    var appsFlyerUUID = ""
    var gaid =""


    var standAloneSku:String = "premium"
    var standAloneCampaignSku: String = "premium"
    var standAloneLocalCampaignSku: String = "premium"

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

    private val billingPrefsHandler by lazy { BillingPrefHandlers(this) }

    private lateinit var binding: ActivityViyatekPremiumBinding
    lateinit var billingManager: BillingManager

    val oneTimePurchasesSkuHelper : SkuListHelper = SkuListHelper()
    val premiumSkuHelper: SkuListHelper = SkuListHelper()
    val subSkuListHelper: SkuListHelper = SkuListHelper()

    val subscriptionSkuDetailsList: ArrayList<SkuDetails> = ArrayList()
    val managedProductSkuDetails: ArrayList<SkuDetails> = ArrayList()

    var activeMonthlySku: SkuDetails? = null
    var activeYearlySku: SkuDetails? = null
    var activeLifeTimeSku: SkuDetails? = null
    var activeStandAloneSku : SkuDetails? = null

    var oldMonthlySku: SkuDetails? = null
    var oldYearlySku: SkuDetails? = null
    var oldLifeTimeSku: SkuDetails? = null
    var oldStandAloneSku : SkuDetails? = null



    companion object {
        val billingLogs = "Billing"
        var isCampaignDialogOnShow = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViyatekPremiumBinding.inflate(layoutInflater)
        setPremiumActivityVariables(navInflater)
        setBackgroundImage(binding.root)

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

        subSkuListHelper.addSku(thirdSlotSku)
        subSkuListHelper.addSku(thirdSlotCampaignSku)
        subSkuListHelper.addSku(thirdSlotLifeTimeSku)
        subSkuListHelper.addSku(standAloneSku)

        subSkuListHelper.addSku(secondSlotSku)
        subSkuListHelper.addSku(firstSlotSku)
        subSkuListHelper.addSku(secondSlotCampaignSku)
        subSkuListHelper.addSku(firstSlotMonthlySku)
        subSkuListHelper.addSku(firstSlotLocalCampaignSku)
        subSkuListHelper.addSku(secondSlotLocalCampaignSku)
        subSkuListHelper.addSku(standAloneCampaignSku)
        subSkuListHelper.addSku(standAloneLocalCampaignSku)

        premiumSkuHelper.addSku(thirdSlotSku)
        premiumSkuHelper.addSku(thirdSlotCampaignSku)
        premiumSkuHelper.addSku(thirdSlotLifeTimeSku)


        if (!billingPrefsHandler.isRestorePurchaseAsyncCallMade()) {
                Log.d(billingLogs, "Making call")
                queryPurchaseAsync(false)
            } else {
                Log.d(billingLogs, "call already made")
            }

        val adInfo : AdvertisingIdClient.Info? = try {
            AdvertisingIdClient.getAdvertisingIdInfo(this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        adInfo?.let { it.id?.let { gaid = it  }  }

        Log.d(Statics.BILLING_LOGS, "Gaid $gaid")

       // billingManager.init(subSkuListHelper.getSkuList(), subSkuListHelper.getSkuList())

        billingManager.init(subSkuListHelper.getSkuList(), premiumSkuHelper.getSkuList())
    }

    abstract fun setBackgroundImage(rootLayout: ConstraintLayout)

    fun queryPurchaseAsync(showToast : Boolean = false) {

        billingManager.billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, purchaseHistoryRecords ->
            if (purchaseHistoryRecords != null) {
                var activePurchaseRecord : PurchaseHistoryRecord? = null

                if (purchaseHistoryRecords.size > 0) {
                    for (purchaseHistoryRecord in purchaseHistoryRecords) {
                        Log.d(billingLogs, "Purchase History Record : $purchaseHistoryRecord")

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

                        if(showToast) {
                            Toast.makeText(
                                this@ViyatekPremiumActivity,
                                "Subscription Purchases found, Checking validity...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        activePurchaseRecord?.let { SubscriptionDataFetch(
                            billingManager.billingClient,
                                this@ViyatekPremiumActivity, productsRestoreListener = this@ViyatekPremiumActivity
                            )
                                .executeNetWorkCall(
                                    getString(R.string.viyatek_subscription_check_endpoint),
                                    it.skus[0],
                                    it.purchaseToken
                                )
                        }
                    }
                }
                else {
                    billingManager.billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { billingResult, purchaseHistoryRecords->
                        if (purchaseHistoryRecords != null) {
                            if (purchaseHistoryRecords.size > 0) {
                                for (purchaseHistoryRecord in purchaseHistoryRecords) {
                                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                                        if (premiumSkuHelper.getSkuList().contains(purchaseHistoryRecord.skus[0]))
                                        {

                                            Log.d("Billing", "Made Premium in Premium Activity async restore func")
                                            billingPrefsHandler.setPremium(true)
                                        }

                                    }
                                }
                            }
                        }
                    }


                    Log.d(billingLogs, "Purchase History Record not found size 0") }

            }
            else {
                if(billingPrefsHandler.isRestorePurchaseAsyncCallMade() && showToast) {
                    Toast.makeText(
                        this@ViyatekPremiumActivity,
                        "Purchase not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d(billingLogs, "Purchase History Record not found null")
            }

            billingPrefsHandler.setRestorePurchaseAsyncCallMade(true)
        }
    }

    abstract fun setLocalCampaign()

    open fun setPremiumActivityVariables(navInflater: NavInflater) {}

    override fun SubSkuFetched(subsciptionSkuDetailsList: List<SkuDetails>) {

        subscriptionSkuDetailsList.clear()
        subscriptionSkuDetailsList.addAll(subsciptionSkuDetailsList)
        isSubscriptionSkuFetched = true
        //subsSkuDetails = list

        Log.d(billingLogs, "Skus fetched ")

        for (skuDetails in subsciptionSkuDetailsList) {

            Log.d(billingLogs, "Incoming Skus $skuDetails")

            when (skuDetails.sku) {
                thirdSlotSku -> {
                    activeLifeTimeSku = skuDetails
                    oldLifeTimeSku = skuDetails
                }
                secondSlotSku -> {
                    activeYearlySku = skuDetails
                    oldYearlySku = skuDetails
                }

                firstSlotSku -> {
                    activeMonthlySku = skuDetails
                    oldMonthlySku = skuDetails
                }
                standAloneSku -> {
                    activeStandAloneSku = skuDetails
                    oldStandAloneSku = skuDetails
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

        if (theCampaignType != null) { reportCampaign(theCampaignType.name) }

        val shouldBeYearlySku: String
        val shouldBeMonthlySku: String
        val shouldBeLifeTimeSku: String
        val shouldBeStandAloneSku : String

        when (theCampaignType) {
            CampaignType.REMOTE_CAMPAIGN, CampaignType.SPECIAL_DAY_CAMPAIGN -> {

                shouldBeYearlySku = secondSlotCampaignSku
                shouldBeMonthlySku = firstSlotMonthlySku
                shouldBeLifeTimeSku = thirdSlotCampaignSku
                shouldBeStandAloneSku = standAloneCampaignSku
            }

            CampaignType.LOCAL_CAMPAIGN -> {
                shouldBeMonthlySku = firstSlotLocalCampaignSku
                shouldBeYearlySku = secondSlotLocalCampaignSku
                shouldBeLifeTimeSku = thirdSlotLifeTimeSku
                shouldBeStandAloneSku = standAloneLocalCampaignSku
            }
            else -> {
                shouldBeYearlySku = secondSlotSku
                shouldBeMonthlySku = firstSlotSku
                shouldBeLifeTimeSku = thirdSlotSku
                shouldBeStandAloneSku = standAloneSku
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
                shouldBeStandAloneSku -> {
                    activeStandAloneSku = skuDetail
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
                shouldBeStandAloneSku -> {
                    activeStandAloneSku = skuDetail
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
                is FacieTypeMultipleChoiceSale -> {
                    fragment.skuDetailsFetched(
                        activeYearlySku,
                        oldYearlySku,
                        activeMonthlySku,
                        oldMonthlySku,
                        activeLifeTimeSku,
                        oldLifeTimeSku
                    )//Will call the fragment function with sku}
                }
                is FacieTypePurchaseStandAloneFragment -> {
                    val (oldSkuDetail: SkuDetails?, activeSkuDetail) = handleSkuDetails()
                    fragment.skuDetailsFetched(activeSkuDetail, oldSkuDetail)
                }
                is PurchaseStandAloneFragment -> {
                    val (oldSkuDetail: SkuDetails?, activeSkuDetail) = handleSkuDetails()
                    fragment.skuDetailsFetched(activeStandAloneSku, oldStandAloneSku)
                }

            }
        }
    }

    private fun handleSkuDetails(): Pair<SkuDetails?, SkuDetails?> {
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
        return Pair(oldSkuDetail, activeSkuDetail)
    }

    override fun ManagedProductsSkuFetched(skuDetailsList: List<SkuDetails>) {
        managedProductSkuDetails.clear()
        managedProductSkuDetails.addAll(skuDetailsList)

        isPremiumSkuFetched = true
        //subsSkuDetails = list

        managedProductSkuDetails.forEach { skuDetails->

            when(skuDetails.sku)
            {
                thirdSlotSku -> {
                    activeLifeTimeSku = skuDetails
                    oldLifeTimeSku = skuDetails
                }
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