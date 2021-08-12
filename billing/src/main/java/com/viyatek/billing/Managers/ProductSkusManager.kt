package com.viyatek.billing.Managers

import android.content.Context

class ProductSkusManager(
    context: Context,
    private val productSkuListener: com.viyatek.billing.Interface.ProductsSkuListener
) : com.viyatek.billing.BaseBillingClass(context) {

    private var subs_skuList: List<String> = ArrayList()
    private var managedProductsSkuList: List<String> = ArrayList()

    fun init(subscriptionSkuList: List<String>, managedProductsSkuList: List<String>) {
        this.subs_skuList = subscriptionSkuList
        this.managedProductsSkuList = managedProductsSkuList
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QuerySubscriptionSkuHandler(
            billingClient,
            subs_skuList,
            productSkuListener
        ).querySkuDetails()

        com.viyatek.billing.Handlers.QueryManagedProductsSkuHandler(
            billingClient,
            managedProductsSkuList,
            productSkuListener
        ).querySkuDetails()
    }


}