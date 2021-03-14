package com.viyatek.billing.Managers

import android.content.Context

class SubscriptionSkuDetailsManager(
    context: Context,
    val subscriptionSkuDetailsListener: com.viyatek.billing.Interface.ISubsSkuDetails
) : com.viyatek.billing.BaseBillingClass(context) {

    private var subs_skuList = arrayListOf<String>()

    fun init(subs_skuList: ArrayList<String>) {
        this.subs_skuList = subs_skuList
        startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QuerySubscriptionSkuHandler(
            billingClient,
            subs_skuList,
            subscriptionSkuDetailsListener
        ).querySkuDetails()
    }
}