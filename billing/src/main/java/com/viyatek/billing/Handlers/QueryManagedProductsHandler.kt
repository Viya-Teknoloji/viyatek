package com.viyatek.billing.Handlers

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Interface.IRestoreManagedProducts
import com.viyatek.billing.Interface.ManagedProductListener
import com.viyatek.billing.Interface.ProductsSkuListener


internal class QueryManagedProductsHandler(
    private val billingClient: BillingClient,
    private val context: Context,
    private val managedProductsListener: IRestoreManagedProducts? = null
) {

    private val billingPrefsHandler by lazy { BillingPrefHandlers(context) }

    fun queryInAppProducts() {

        val inAppPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)

        val boughtManagedProducts = inAppPurchasesResult.purchasesList

        Log.d("Bill", "Querying managed products")
        if (boughtManagedProducts != null && boughtManagedProducts.size > 0) {
            for (purchase in boughtManagedProducts) {

                Log.d("Bill", "Consuming managed products what purchase ${purchase.skus[0]}")

                if(managedProductsListener == null)
                {
                    billingPrefsHandler.setPremium(true)
                    //consume(purchase)
                }
                else
                {
                    managedProductsListener.soldOneTimeProductsFetched(purchase)
                }


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