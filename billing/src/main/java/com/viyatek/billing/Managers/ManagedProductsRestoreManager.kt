package com.viyatek.billing.Managers

import android.content.Context
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BaseBillingClass
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Interface.IRestoreManagedProducts
import com.viyatek.billing.Interface.ManagedProductListener


class ManagedProductsRestoreManager(val context: Context,
                                    private val premiumSkuList : ArrayList<String>,
                                    private val oneTimeSkuList : ArrayList<String>? = null,
                                    private val iRestoreListener : IRestoreManagedProducts? = null) :
    BaseBillingClass(context) {

    val billingPrefHandlers by lazy { BillingPrefHandlers(context) }

    fun init() { startProcess() }

    override fun connectedGooglePlay() { QueryManagedProductsHandler(billingClient, context, iRestoreListener).queryInAppProducts() }
}