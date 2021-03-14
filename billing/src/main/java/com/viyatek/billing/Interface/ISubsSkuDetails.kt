package com.viyatek.billing.Interface

import android.util.Log
import com.android.billingclient.api.SkuDetails

interface ISubsSkuDetails {

    fun SubSkuFetched(subsciptionSkuDetailsList: List<SkuDetails>)
    fun SubSkuFetchedError(billingResponseCode: Int) {

        Log.d(
            "Subscription",
            "An error occured while fetching SKU data, Error Code : $billingResponseCode"
        )
    }
}