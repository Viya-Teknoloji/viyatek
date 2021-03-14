package com.viyatek.billing.Managers

import android.content.Context

class ManagedProductsSkuDetailsManager(
    context: Context,
    val managedProductsSkuDetailsListener: com.viyatek.billing.Interface.IManagedProductsSkuDetails
) : com.viyatek.billing.BaseBillingClass(context) {

    private var managedProducts_skuList = arrayListOf<String>()

    fun init(managedProducts_skuList: ArrayList<String>) {
        this.managedProducts_skuList = managedProducts_skuList
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QueryManagedProductsSkuHandler(
            billingClient,
            managedProducts_skuList,
            managedProductsSkuDetailsListener
        ).querySkuDetails()
    }

}