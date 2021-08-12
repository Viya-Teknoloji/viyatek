package com.viyatek.billing.Interface

import com.android.billingclient.api.Purchase


interface IManagedProductPurchase {
    fun ManagedProductPurchaseSucceded(purchase : Purchase)
}