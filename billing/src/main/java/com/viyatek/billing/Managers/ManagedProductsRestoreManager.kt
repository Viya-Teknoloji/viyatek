package com.viyatek.billing.Managers

import android.content.Context


class ManagedProductsRestoreManager(val context: Context) :
    com.viyatek.billing.BaseBillingClass(context) {

    fun init() {
        startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QueryManagedProductsHandler(billingClient, context)
            .queryInAppProducts()
    }

}