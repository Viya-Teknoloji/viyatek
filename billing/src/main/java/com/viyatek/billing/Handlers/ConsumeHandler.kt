package com.viyatek.billing.Handlers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BaseBillingClass
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Interface.IRestoreManagedProducts

class ConsumeHandler (
    private val context: Context
) : BaseBillingClass(context) {

    private val billingPrefsHandler by lazy { BillingPrefHandlers(context) }

    fun init()
    {
        if(getConnectionStatus())
        { queryInAppProducts() }
        else{ startProcess() }
    }
    fun queryInAppProducts() {

        val inAppPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)

        val boughtManagedProducts = inAppPurchasesResult.purchasesList

        Log.d("Bill", "Querying managed products")
        if (boughtManagedProducts != null && boughtManagedProducts.size > 0) {
            for (purchase in boughtManagedProducts) {

                consume(purchase)

                if(billingPrefsHandler.isPremium())
                {
                    billingPrefsHandler.setPremium(false)
                }
            }
        }
        else
        {
            Toast.makeText(context,"No Consuming managed products", Toast.LENGTH_SHORT).show()
        }
    }

    fun consume(purchase : Purchase)
    {

            // When you call consumeAsync(),you shoud set developerPayload to distinguish consumed purchases.
            val consumeParams: ConsumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            billingClient.consumeAsync(consumeParams
            ) { result, purchaseToken ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(context,"Purchase is consumed ", Toast.LENGTH_SHORT).show()
                    Log.d("Bill", "Consumed")
                    //do something
                } else {
                    Toast.makeText(context,"Purchase can't consumed ", Toast.LENGTH_SHORT).show()
                    val errorMsg =
                        "Recovery consume failed: " + result.debugMessage + " Response code:" + result.responseCode
                    Log.e("Bill", errorMsg)
                    //do something
                }
            }
        }

    override fun connectedGooglePlay() {
        queryInAppProducts()
    }
}