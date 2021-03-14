package com.viyatek.billing.Handlers

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams

class QueryManagedProductsSkuHandler(
    private val billingClient: BillingClient,
    private val managedProductsSkuList: List<String>
) {

    private lateinit var listener: com.viyatek.billing.Interface.IManagedProductsSkuDetails

    constructor(
        billingClient: BillingClient,
        managedProductsSkuList: List<String>,
        inAppPurchaseListener: com.viyatek.billing.Interface.InAppPurchaseListener
    ) : this(billingClient, managedProductsSkuList) {
        listener = inAppPurchaseListener
    }

    constructor(
        billingClient: BillingClient,
        managedProductsSkuList: List<String>,
        subscriptionPurchaseListener: com.viyatek.billing.Interface.IManagedProductsSkuDetails
    ) : this(billingClient, managedProductsSkuList) {
        listener = subscriptionPurchaseListener
    }

    fun querySkuDetails() {

        Log.d("Subscription", "Sent Sku Names to Server " + managedProductsSkuList.size)

        val skuParams = SkuDetailsParams.newBuilder()

        skuParams.setSkusList(managedProductsSkuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(skuParams.build()) { billingResult, skuDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                listener.ManagedProductsSkuFetched(skuDetailsList)
            }
        }
    }
}