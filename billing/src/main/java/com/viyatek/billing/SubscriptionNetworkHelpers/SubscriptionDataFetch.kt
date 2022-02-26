package com.viyatek.billing.SubscriptionNetworkHelpers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.viyatek.billing.BillingHelperLibraryRequestQueue
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.Handlers.AckHandler
import com.viyatek.billing.Handlers.QueryManagedProductsHandler
import com.viyatek.billing.Interface.ProductRestoreListener
import com.viyatek.billing.Interface.SubscriptionPaymentProblem
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity
import com.viyatek.billing.Statics
import com.viyatek.billing.SubscriptionHelpers.SubscribeCheck
import org.json.JSONObject
import java.util.*


class SubscriptionDataFetch(
    val context: Context,
    private val billingClient: BillingClient
) {

    private var listener: SubscriptionPaymentProblem? = null
    private var managedProductsRestoreListener: ProductRestoreListener? = null
    private val billingPrefsHandler by lazy { BillingPrefHandlers(context) }

    constructor(
        billingClient: BillingClient,
        theContext: Context,
        productsRestoreListener: ProductRestoreListener?
    ) : this(theContext, billingClient) {
        this.managedProductsRestoreListener = productsRestoreListener
    }

    constructor(
        billingClient: BillingClient,
        theContext: Context,
        paymentProblem: SubscriptionPaymentProblem?
    ) : this(theContext, billingClient) {
        listener = paymentProblem
    }


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

        Log.d(ViyatekPremiumActivity.billingLogs, "Subs Data Fetch")

        val theSku =purchase.skus[0]

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

        DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        if(context.applicationContext.packageName == "com.viyatek.facefind") {

            val faceFindRequest =  JsonArrayRequest(
                Request.Method.POST, url.toString(), null,
                { response ->
                    val jsonPart: JSONObject = response.getJSONObject(0)
                    executeTheProcessWithJsonObject(jsonPart,theSku,purchase)
                },
                { error -> handleErrorProcess(error) }
            )


            faceFindRequest.setShouldCache(false)
            faceFindRequest.retryPolicy =
                DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

// Access the RequestQueue through your singleton class.
            BillingHelperLibraryRequestQueue.getInstance(context).addToRequestQueue(faceFindRequest)
        }
        else
        {
            val jsonArrayRequest = object : JsonObjectRequest(
                Method.POST, url.toString(), null,

                { response ->

                    executeTheProcessWithJsonObject(response, theSku, purchase)


                },
                { error ->
                    handleErrorProcess(error)

                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Content-Type"] = "application/form-data"
                    return headers
                }
            }

            jsonArrayRequest.setShouldCache(false)
            jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            // Access the RequestQueue through your singleton class.
            BillingHelperLibraryRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest)
        }


    }

    private fun handleErrorProcess(error: VolleyError) {
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
            if (error.message == null) {
            }
            Log.d(
                ViyatekPremiumActivity.billingLogs,
                "The error code  ${it.networkResponse?.data.toString()}"
            )
        }
    }

    private fun executeTheProcessWithJsonObject(
        response: JSONObject,
        theSku: String,
        purchase: Purchase?
    ) {
        Log.d(ViyatekPremiumActivity.billingLogs, "The Response : $response")

        var paymentStateint = 1
        val jsonPart: JSONObject = response

        val returnedToken = jsonPart.getString("purchase_token")
        val expiryTimeMillis = jsonPart.getLong("expiryTimeMillis")

        if (jsonPart.has("paymentState")) {
            val paymentState = jsonPart.getString("paymentState")
            paymentStateint = try { Integer.valueOf(paymentState)
            } catch (e: Exception) {
                1
            }
        }

        jsonPart.getString("subscription_id")
        //viyatekKotlinSharedPrefHelper.ApplyPrefs(ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TYPE, subscriptionId)
        Log.d(ViyatekPremiumActivity.billingLogs, "Checking subs")

        val isSubscribed = SubscribeCheck(context).checkSubscription(
            returnedToken,
            expiryTimeMillis,
            paymentStateint,
            theSku
        )

        if (isSubscribed) {
            if (isSubscribed) {
                Log.d(Statics.BILLING_LOGS, "Subscription is restored")
                //Toast.makeText(context, "Your subscription is restored", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(Statics.BILLING_LOGS, "Subscription not restored")
                // Toast.makeText(context, "Subscription is not valid", Toast.LENGTH_SHORT).show()
            }
        }



        QueryManagedProductsHandler(billingClient, context).queryInAppProducts()

        purchase?.let {
            if (!it.isAcknowledged) {
                AckHandler(billingClient).acknowledgePurchase(it)
            }
        }


        if (paymentStateint == 0) {
            listener?.subscriptionInGracePeriod(theSku)
        }
    }
}