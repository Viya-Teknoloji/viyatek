package com.viyatek.billing.Managers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Handlers.AckHandler
import com.viyatek.billing.Handlers.QuerySubscriptionHandler
import com.viyatek.billing.Handlers.QuerySubscriptionSkuHandler

import com.viyatek.billing.R
import com.viyatek.billing.Statics.BILLING_LOGS
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionVerification


class BillingSubscribeManager(
    private val activity: Activity,
    private val subscriptionListener: com.viyatek.billing.Interface.SubscriptionListener,
    private val subscriptionVerificationDataFetched: com.viyatek.billing.Interface.SubscriptionVerificationDataFetched
) : com.viyatek.billing.BaseBillingClass(activity), com.viyatek.billing.Interface.ISubsSkuDetails {

    var subscriptionSkuList = arrayListOf<String>()
    private val skuDetailsList = arrayListOf<SkuDetails>()
    private val billingPrefsHandler by lazy { BillingPrefHandlers(activity) }

    fun init(subscriptionSkuList: ArrayList<String>) {
        this.subscriptionSkuList = subscriptionSkuList
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        QuerySubscriptionHandler(
            billingClient,
            activity,
            subscriptionListener
        ).querySubscriptions()

        QuerySubscriptionSkuHandler(
            billingClient,
            subscriptionSkuList,
            this
        ).querySkuDetails()
    }

    override fun handlePurchase(purchase: Purchase) {
        val sku = purchase.sku

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (subscriptionSkuList.contains(sku)) {
                billingPrefsHandler.setSubscriptionTrialModeUsed(true)

                Log.d(BILLING_LOGS, "Handling Purchase")

                for (i in skuDetailsList.indices) {
                    if (sku == skuDetailsList[i].sku) {
                        Log.d(
                            BILLING_LOGS,
                            "Subs Period " + skuDetailsList[i].subscriptionPeriod
                        )

                        when (skuDetailsList[i].subscriptionPeriod) {
                            "P1W" -> {
                                billingPrefsHandler.setSubscriptionType("weekly")
                            }
                            "P1M" -> {
                                billingPrefsHandler.setSubscriptionType("monthly")
                            }
                            else -> {
                                billingPrefsHandler.setSubscriptionType("yearly")
                            }
                        }
                    }
                }

                Log.d(
                    BILLING_LOGS,
                    "Package info : ${activity.applicationContext.applicationInfo.packageName}"
                )
                SubscriptionVerification(activity, subscriptionVerificationDataFetched)
                    .executeNetworkCall(
                        activity.getString(
                            R.string.viyatek_subscription_validation,
                            purchase.purchaseToken,
                            purchase.sku,
                            activity.applicationContext.applicationInfo.packageName
                        ),
                        purchase
                    )

                // subscriptionListener.SubscriptionPurchaseSucceded(purchase)
                AckHandler(billingClient).acknowledgePurchase(purchase)
            }

        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            //handle pending state
            Toast.makeText(activity, "You have a pending purchase", Toast.LENGTH_LONG).show()
        }
    }

    override fun SubSkuFetched(subsciptionSkuDetailsList: List<SkuDetails>) {
        subscriptionListener.SubSkuFetched(subsciptionSkuDetailsList)
        skuDetailsList.clear()
        skuDetailsList.addAll(subsciptionSkuDetailsList)
    }


}