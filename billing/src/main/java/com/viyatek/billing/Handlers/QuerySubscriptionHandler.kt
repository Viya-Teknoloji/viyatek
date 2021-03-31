package com.viyatek.billing.Handlers

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Interface.ProductRestoreListener
import com.viyatek.billing.Interface.SubscriptionPaymentProblem
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity
import com.viyatek.billing.R
import com.viyatek.billing.SubscriptionHelpers.SubscribeCheck
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionDataFetch


internal class QuerySubscriptionHandler(
    private val theContext: Context,
    private val billingClient: BillingClient
) {

    private var listener: SubscriptionPaymentProblem? = null
    private var managedProductsRestoreListener: ProductRestoreListener? = null
    private val billingPrefsHandler by lazy { BillingPrefHandlers(theContext) }

    constructor(
        billingClient: BillingClient,
        theContext: Context,
        productsRestoreListener: ProductRestoreListener
    ) : this(theContext, billingClient) {
        this.managedProductsRestoreListener = productsRestoreListener
    }

    constructor(
        billingClient: BillingClient,
        theContext: Context,
        paymentProblem: SubscriptionPaymentProblem
    ) : this(theContext, billingClient) {
        listener = paymentProblem
    }



    fun querySubscriptions() {
        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)

        var activePurchase: Purchase

        val boughtPurchases = purchasesResult.purchasesList

        Log.d(ViyatekPremiumActivity.billingLogs, "querying subscriptions")

        if (boughtPurchases != null && boughtPurchases.size > 0) {

            activePurchase = boughtPurchases[0]

            for (i in boughtPurchases.indices) {
                if (boughtPurchases[i].purchaseTime > activePurchase.purchaseTime) {
                    activePurchase = boughtPurchases[i]
                }
            }

            //make interface call
            //Active Purchase Found

            Log.d(ViyatekPremiumActivity.billingLogs,
                "Package name ${theContext.applicationContext.applicationInfo.packageName}")


            SubscriptionDataFetch(billingClient, theContext, listener).executeNetWorkCall(
                theContext.getString(R.string.viyatek_subscription_check_endpoint),
                activePurchase
            )

        }
        else {
            Log.d(
                ViyatekPremiumActivity.billingLogs,
                "No subscription found"
            )

            SubscribeCheck(theContext).checkSubscription(
                billingPrefsHandler.getSubscriptionToken(),
                billingPrefsHandler.getSubscriptionExpTime(),
                paymentStatus = 0,
                billingPrefsHandler.getSubscriptionType()
            )


            if(listener == null && managedProductsRestoreListener != null) {
                QueryManagedProductsHandler(
                    billingClient,
                    theContext,
                    managedProductsRestoreListener
                ).queryInAppProducts()
            }
        }
    }
}