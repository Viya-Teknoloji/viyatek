package com.viyatek.billing.Interface

import android.util.Log
import com.android.billingclient.api.SkuDetails

interface IManagedProductsSkuDetails {
    fun ManagedProductsSkuFetched(skuDetailsList: List<SkuDetails>)
    fun ManagedProductsSkuFetchedError(billingResponseCode: Int) {
        Log.d(
            "Billing",
            "An error occured while fetching SKU data, Error Code : $billingResponseCode"
        )
    }

}