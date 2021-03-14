package com.viyatek.billing.Interface

import com.android.billingclient.api.Purchase

interface IAcknowledge {
    fun purchaseNotAcknowledged(purchase: Purchase)
}