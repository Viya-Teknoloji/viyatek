package com.viyatek.billing.Managers

import android.content.Context
import com.viyatek.billing.BaseBillingClass
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Handlers.QuerySubscriptionHandler
import com.viyatek.billing.Interface.ProductRestoreListener

class ProductRestoreManager(
    val context: Context,
    private val productRestoreListener: ProductRestoreListener
) : BaseBillingClass(context) {

    fun init() {
        startProcess()
    }

    override fun connectedGooglePlay() {
        QuerySubscriptionHandler(
            billingClient,
            context,
            productRestoreListener
        ).querySubscriptions()

        //QueryManagedProductsHandler(billingClient, context).queryInAppProducts()
    }

}