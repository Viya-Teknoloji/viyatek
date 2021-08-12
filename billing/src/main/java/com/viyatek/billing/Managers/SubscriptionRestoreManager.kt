package com.viyatek.billing.Managers

import android.content.Context
import com.viyatek.billing.Handlers.QuerySubscriptionHandler
import com.viyatek.billing.Interface.IRestoreSubscription

class SubscriptionRestoreManager(
    val context: Context,
    val subscriptionRestoreListener: IRestoreSubscription
) : com.viyatek.billing.BaseBillingClass(context) {

    fun init() { startProcess() }

    override fun connectedGooglePlay() {
        QuerySubscriptionHandler(
            billingClient,
            context,
            subscriptionRestoreListener
        ).querySubscriptions()
    }

}