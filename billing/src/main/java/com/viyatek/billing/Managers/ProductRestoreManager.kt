package com.viyatek.billing.Managers

import android.content.Context
import android.util.Log
import com.viyatek.billing.BaseBillingClass
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Handlers.QuerySubscriptionHandler
import com.viyatek.billing.Interface.ProductRestoreListener

class ProductRestoreManager(
    val context: Context,
    private val productRestoreListener: ProductRestoreListener
) : BaseBillingClass(context) {

    fun init() {
        Log.d("Billing", "Starting Restore Process")
        startProcess()
    }

    override fun connectedGooglePlay() {
        QuerySubscriptionHandler(
            billingClient,
            context,
            productRestoreListener
        ).querySubscriptions()

    }

}