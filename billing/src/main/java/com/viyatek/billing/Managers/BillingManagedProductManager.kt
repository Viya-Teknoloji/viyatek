package com.viyatek.billing.Managers

import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Handlers.AckHandler
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Handlers.QueryManagedProductsSkuHandler
import com.viyatek.billing.Interface.IRestoreManagedProducts


class BillingManagedProductManager(
    val context: Context,
    private val managedProductListener: com.viyatek.billing.Interface.ManagedProductListener
) : com.viyatek.billing.BaseBillingClass(context) {

    private var managedProductSkuList = arrayListOf<String>()
    private var oneTimePurchaseSkuList = arrayListOf<String>()

    private var iRestoreListener : IRestoreManagedProducts? = null
    private val billingPrefHandlers by lazy { BillingPrefHandlers(context) }

    fun init(managedProductSkuList: ArrayList<String>) {
        this.managedProductSkuList = managedProductSkuList
        super.startProcess()
    }

    fun init(managedProductSkuList: ArrayList<String>, oneTimePurchaseSkuList : ArrayList<String>, iRestoreListener : IRestoreManagedProducts? = null) {
        this.managedProductSkuList = managedProductSkuList
        this.oneTimePurchaseSkuList = oneTimePurchaseSkuList
        this.iRestoreListener = iRestoreListener
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        QueryManagedProductsHandler(billingClient, context, iRestoreListener).queryInAppProducts()
        QueryManagedProductsSkuHandler(
            billingClient,
            managedProductSkuList,
            managedProductListener
        ).querySkuDetails()
    }

    override fun handlePurchase(purchase: Purchase) {

        val sku = purchase.skus[0]

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (managedProductSkuList.contains(sku)) {

                billingPrefHandlers.setPremium(true)
                // managedProductListener.ManagedProductPurchaseSucceded(purchase)
                AckHandler(billingClient).acknowledgePurchase(purchase)
            }
            else if(oneTimePurchaseSkuList.contains(sku))
            {
                managedProductListener.soldOneTimeProductsFetched(purchase)
                AckHandler(billingClient).acknowledgePurchase(purchase)
            }

        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            //handle pending state
            Toast.makeText(context, "You have a pending purchase", Toast.LENGTH_LONG).show()
        }

    }
}