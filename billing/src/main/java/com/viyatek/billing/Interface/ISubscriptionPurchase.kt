package com.viyatek.billing.Interface

import com.android.billingclient.api.Purchase

interface ISubscriptionPurchase {
    fun subscriptionPurchaseSucceeded(purchase: Purchase)
}