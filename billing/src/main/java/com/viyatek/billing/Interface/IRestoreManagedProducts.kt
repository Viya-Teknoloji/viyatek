package com.viyatek.billing.Interface

import com.android.billingclient.api.Purchase

interface IRestoreManagedProducts {
    fun soldManagedProductsFetched(purchase: Purchase)
}