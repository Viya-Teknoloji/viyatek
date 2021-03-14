package com.viyatek.billing.Managers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TAG
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TRIAL_MODE_USED
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TYPE
import com.viyatek.billing.R
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionVerification


class BillingSubscribeManager(
    private val activity: Activity,
    private val subscriptionListener: com.viyatek.billing.Interface.SubscriptionListener,
    private val subscriptionVerificationDataFetched: com.viyatek.billing.Interface.SubscriptionVerificationDataFetched
) : com.viyatek.billing.BaseBillingClass(activity), com.viyatek.billing.Interface.ISubsSkuDetails {

    var subscriptionSkuList = arrayListOf<String>()
    private val skuDetailsList = arrayListOf<SkuDetails>()
    private val kotlinSharedPrefHelper by lazy {
        com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(
            activity
        )
    }

    fun init(subscriptionSkuList: ArrayList<String>) {
        this.subscriptionSkuList = subscriptionSkuList
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QuerySubscriptionHandler(
            billingClient,
            activity,
            subscriptionListener
        ).querySubscriptions()
        com.viyatek.billing.Handlers.QuerySubscriptionSkuHandler(
            billingClient,
            subscriptionSkuList,
            this
        ).querySkuDetails()
    }

    override fun handlePurchase(purchase: Purchase) {
        val sku = purchase.sku

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (subscriptionSkuList.contains(sku)) {
                kotlinSharedPrefHelper.applyPrefs(SUBSCRIPTION_TRIAL_MODE_USED, 1)
                Log.d(SUBSCRIPTION_TAG, "Handling Purchase")

                for (i in skuDetailsList.indices) {
                    if (sku == skuDetailsList[i].sku) {
                        Log.d(
                            SUBSCRIPTION_TAG,
                            "Subs Period " + skuDetailsList[i].subscriptionPeriod
                        )

                        when (skuDetailsList[i].subscriptionPeriod) {
                            "P1W" -> {
                                kotlinSharedPrefHelper.applyPrefs(
                                    SUBSCRIPTION_TYPE,
                                    activity.getString(R.string.weekly_subscription_type)
                                )
                            }
                            "P1M" -> {
                                kotlinSharedPrefHelper.applyPrefs(
                                    SUBSCRIPTION_TYPE,
                                    activity.getString(R.string.monthly_subscription_type)
                                )
                            }
                            else -> {
                                kotlinSharedPrefHelper.applyPrefs(
                                    SUBSCRIPTION_TYPE,
                                    activity.getString(R.string.yearly_subscription_type)
                                )
                            }
                        }
                    }
                }

                Log.d(
                    SUBSCRIPTION_TAG,
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
                com.viyatek.billing.Handlers.AckHandler(billingClient).acknowledgePurchase(purchase)
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