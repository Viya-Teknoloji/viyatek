package com.viyatek.billing.Interface

import com.android.billingclient.api.Purchase

interface IRestoreManagedProducts {
    fun soldOneTimeProductsFetched(purchase: Purchase)
}