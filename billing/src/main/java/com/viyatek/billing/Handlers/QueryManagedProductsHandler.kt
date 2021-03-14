package com.viyatek.billing.Handlers

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper


internal class QueryManagedProductsHandler(
    private val billingClient: BillingClient,
    private val context: Context
) {

    private val viyatekKotlinSharedPrefHelper by lazy {
        ViyatekKotlinSharedPrefHelper(
            context
        )
    }

    fun queryInAppProducts() {

        val inAppPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)

        val boughtManagedProducts = inAppPurchasesResult.purchasesList

        Log.d("Bill", "Queying managed products")
        if (boughtManagedProducts != null && boughtManagedProducts.size > 0) {
            for (purchase in boughtManagedProducts) {

                Log.d("Bill", "Consuming managed products")

                viyatekKotlinSharedPrefHelper.applyPrefs(ViyatekKotlinSharedPrefHelper.PREMIUM, 1)

                // managedProductListener.ManagedProductPurchaseSucceded(purchase)
                if(!purchase.isAcknowledged)
                AckHandler(billingClient).acknowledgePurchase(purchase)

                //consume(purchase)
            }
        }
        else
        {
            Log.d("Bill", "No Consuming managed products")
        }
    }

    fun consume(purchase : Purchase)
    {
        // When you call consumeAsync(),you shoud set developerPayload to distinguish consumed purchases.
        // When you call consumeAsync(),you shoud set developerPayload to distinguish consumed purchases.
        val consumeParams: ConsumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(consumeParams
        ) { result, purchaseToken ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("Bill", "Consumed")
                //do something
            } else {
                val errorMsg =
                    "Recovery consume failed: " + result.debugMessage + " Response code:" + result.responseCode
                Log.e("Bill", errorMsg)
                //do something
            }
        }
    }
}