package com.viyatek.billing.Managers

import android.content.Context

class SubscriptionRestoreManager(
    val context: Context,
    val subscriptionRestoreListener: com.viyatek.billing.Interface.IRestoreSubscription
) : com.viyatek.billing.BaseBillingClass(context) {

    fun init() {
        startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QuerySubscriptionHandler(
            billingClient,
            context,
            subscriptionRestoreListener
        ).querySubscriptions()
    }

}