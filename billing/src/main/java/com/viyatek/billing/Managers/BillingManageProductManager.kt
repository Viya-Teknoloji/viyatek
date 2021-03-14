package com.viyatek.billing.Managers

import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.PREMIUM


class BillingManageProductManager(
    val context: Context,
    private val managedProductListener: com.viyatek.billing.Interface.ManagedProductListener
) : com.viyatek.billing.BaseBillingClass(context) {

    private var managedProductSkuList = arrayListOf<String>()
    private val viyatekKotlinSharedPrefHelper by lazy {
        com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(
            context
        )
    }

    fun init(managedProductSkuList: ArrayList<String>) {
        this.managedProductSkuList = managedProductSkuList
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        com.viyatek.billing.Handlers.QueryManagedProductsHandler(billingClient, context)
            .queryInAppProducts()
        com.viyatek.billing.Handlers.QueryManagedProductsSkuHandler(
            billingClient,
            managedProductSkuList,
            managedProductListener
        ).querySkuDetails()
    }

    override fun handlePurchase(purchase: Purchase) {

        val sku = purchase.sku

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (managedProductSkuList.contains(sku)) {
                viyatekKotlinSharedPrefHelper.applyPrefs(PREMIUM, 1)

                // managedProductListener.ManagedProductPurchaseSucceded(purchase)
                com.viyatek.billing.Handlers.AckHandler(billingClient).acknowledgePurchase(purchase)
            }

        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            //handle pending state
            Toast.makeText(context, "You have a pending purchase", Toast.LENGTH_LONG).show()
        }

    }
}