package com.viyatek.billing.Handlers

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams

class QuerySubscriptionSkuHandler(
    private val billingClient: BillingClient,
    private val subs_skuList: List<String>
) {

    private lateinit var listener: com.viyatek.billing.Interface.ISubsSkuDetails

    constructor(
        billingClient: BillingClient,
        subs_skuList: List<String>,
        inAppPurchaseListener: com.viyatek.billing.Interface.InAppPurchaseListener
    ) : this(billingClient, subs_skuList) {
        listener = inAppPurchaseListener
    }

    constructor(
        billingClient: BillingClient,
        subs_skuList: List<String>,
        subscriptionPurchaseListener: com.viyatek.billing.Interface.ISubsSkuDetails
    ) : this(billingClient, subs_skuList) {
        listener = subscriptionPurchaseListener

    }


    fun querySkuDetails() {

        Log.d("Subscription", "Sent Sku Names to Server " + subs_skuList.size)

        if(subs_skuList.isNotEmpty()) {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(subs_skuList).setType(BillingClient.SkuType.SUBS)

            billingClient.querySkuDetailsAsync(
                params.build()
            ) { billingResult, skuDetailsList ->
                // Process the result.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    listener.SubSkuFetched(skuDetailsList)
                } else {
                    listener.SubSkuFetchedError(billingResult.responseCode)
                }
            }
        }


    }
}