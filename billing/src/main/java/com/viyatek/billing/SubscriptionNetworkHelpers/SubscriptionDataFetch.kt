package com.viyatek.billing.SubscriptionNetworkHelpers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.viyatek.billing.BillingHelperLibraryRequestQueue
import com.viyatek.billing.Handlers.AckHandler
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Interface.SubscriptionPaymentProblem
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity
import com.viyatek.billing.SubscriptionHelpers.SubscribeCheck
import org.json.JSONObject
import java.util.*


class SubscriptionDataFetch(
    val context: Context,
    private val listener: SubscriptionPaymentProblem,
    private val billingClient: BillingClient
) {

    private val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(context)}

    fun executeNetWorkCall(endpoint: String, sku : String, token : String )
    {

        val url = Uri.parse(endpoint)
            .buildUpon()
            .appendQueryParameter("token", token)
            .appendQueryParameter("subscriptionId", sku)
            .appendQueryParameter("packageName", context.applicationContext.packageName)
            .build()


        handleRestoreProcess(url, sku, null)
    }

    fun executeNetWorkCall(endpoint: String, purchase: Purchase) {

        Log.d(
            ViyatekPremiumActivity.billingLogs,
            "Subs Data Fetch"
        )

        val theSku =purchase.sku

        val url = Uri.parse(endpoint)
            .buildUpon()
            .appendQueryParameter("token", purchase.purchaseToken)
            .appendQueryParameter("subscriptionId", theSku)
            .appendQueryParameter("packageName", context.applicationContext.packageName)
            .build()

// Formulate the request and handle the response.

        handleRestoreProcess(url, theSku, purchase)

    }

    private fun handleRestoreProcess(url: Uri, theSku: String, purchase: Purchase?) {
        // val myUrl = context.getString(R.string.viyatek_subscription_validation, purchase.purchaseToken, purchase.sku,context.applicationContext.packageName)
        Log.d(ViyatekPremiumActivity.billingLogs, url.toString())

        var paymentStateint = 1

        //will get url
        //will get url

        val jsonArrayRequest = object : JsonObjectRequest(
            Method.POST, url.toString(), null,

            { response ->

                Log.d(ViyatekPremiumActivity.billingLogs, "The Response : $response")

                val jsonPart: JSONObject = response

                val returnedToken = jsonPart.getString("purchase_token")
                val expiryTimeMillis = jsonPart.getLong("expiryTimeMillis")

                if (jsonPart.has("paymentState")) {
                    val paymentState = jsonPart.getString("paymentState")
                    paymentStateint = try {
                        Integer.valueOf(paymentState)
                    } catch (e: Exception) {
                        1
                    }
                }

                jsonPart.getString("subscription_id")
                //viyatekKotlinSharedPrefHelper.ApplyPrefs(ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TYPE, subscriptionId)
                Log.d(ViyatekPremiumActivity.billingLogs, "Checking subs")

                val isSubscribed = SubscribeCheck(context).checkSubscription(
                    viyatekKotlinSharedPrefHelper,
                    returnedToken,
                    expiryTimeMillis,
                    paymentStateint,
                    theSku)

                if(purchase == null)
                    {
                        if(isSubscribed)
                        { Toast.makeText(context, "Your subscription is restored", Toast.LENGTH_SHORT).show() }
                        else
                        { Toast.makeText(context, "Subscription is not valid", Toast.LENGTH_SHORT).show() }
                    }



                QueryManagedProductsHandler(billingClient, context).queryInAppProducts()

                purchase?.let { if (!it.isAcknowledged) { AckHandler(billingClient).acknowledgePurchase(it) } }


                if (paymentStateint == 0) {
                    listener.subscriptionInGracePeriod(theSku)
                }


            },
            { error ->
                error?.let {

                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "Failed with error msg:\t" + it.message
                    )
                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "Error StackTrace: \t" + it.stackTrace
                    )
                    // edited here
                    // edited here
                    try {
                        val htmlBodyBytes = error.networkResponse.data
                        Log.e(
                            ViyatekPremiumActivity.billingLogs,
                            String(htmlBodyBytes),
                            error
                        )
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                    if (error.message == null) { }
                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "The error code  ${it.networkResponse?.data.toString()}"
                    )
                }

            }

        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/form-data"
                return headers
            }
        }

        DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        jsonArrayRequest.setShouldCache(false)
        jsonArrayRequest.retryPolicy =
            DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Access the RequestQueue through your singleton class.
        BillingHelperLibraryRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
}