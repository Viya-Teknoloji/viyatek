package com.viyatek.billing.Managers

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.Purchase
import com.viyatek.billing.BaseBillingClass
import com.viyatek.billing.Handlers.*
import com.viyatek.billing.Interface.InAppPurchaseListener
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.R
import com.viyatek.billing.SubscriptionNetworkHelpers.SubscriptionVerification


class BillingManager(
    private val activity: FragmentActivity,
    private val inAppPurchaseListener: InAppPurchaseListener
) : BaseBillingClass(activity) {


    private var isConnected: Boolean = false
    private var subs_skuList: List<String> = ArrayList()
    private var managedProductsSkuList: List<String> = ArrayList()
    private val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(activity)
    }

    fun init(subscriptionSkuList: List<String>, managedProductsSkuList: List<String>) {
        this.subs_skuList = subscriptionSkuList
        this.managedProductsSkuList = managedProductsSkuList
        super.startProcess()
    }

    override fun connectedGooglePlay() {
        queryPurchases()
        querySkuDetails()
    }

    override fun handlePurchase(purchase: Purchase) {
        val sku = purchase.sku

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (managedProductsSkuList.contains(sku)) {

                viyatekKotlinSharedPrefHelper.applyPrefs(ViyatekKotlinSharedPrefHelper.PREMIUM, 1)
                ViyatekKotlinSharedPrefHelper.isPremium = true
                AckHandler(billingClient).acknowledgePurchase(purchase)
                inAppPurchaseListener.ManagedProductPurchaseSucceded(purchase.sku)

            } else {

                SubscriptionVerification(activity, inAppPurchaseListener).executeNetworkCall(
                    activity.getString(R.string.viyatek_subscription_validation_end_point),
                    purchase
                )

                AckHandler(billingClient).acknowledgePurchase(purchase)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            //handle pending state
            Toast.makeText(activity, "You have a pending purchase", Toast.LENGTH_LONG).show()
        }
    }

    private fun queryPurchases() {
        QuerySubscriptionHandler(billingClient, activity, inAppPurchaseListener).querySubscriptions()
        QueryManagedProductsHandler(billingClient, activity).queryInAppProducts()
    }

    private fun querySkuDetails() {

        Log.d("Subscription", "Sent Sku Names to Server " + subs_skuList.size)

        QuerySubscriptionSkuHandler(
            billingClient,
            subs_skuList,
            inAppPurchaseListener
        ).querySkuDetails()
        QueryManagedProductsSkuHandler(billingClient, managedProductsSkuList, inAppPurchaseListener).querySkuDetails()
    }


}