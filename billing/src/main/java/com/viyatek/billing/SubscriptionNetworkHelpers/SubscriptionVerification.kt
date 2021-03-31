package com.viyatek.billing.SubscriptionNetworkHelpers

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.billingclient.api.Purchase
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.viyatek.billing.BillingHelperLibraryRequestQueue
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.DialogueFragments.ProgressDialog
import com.viyatek.billing.Interface.SubscriptionVerificationDataFetched

import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity
import com.viyatek.billing.SubscriptionHelpers.SubscribeCheck
import org.json.JSONObject


class SubscriptionVerification(
    val activity: Activity,
    private val listener: SubscriptionVerificationDataFetched
) {

    private val billingPrefHandlers by lazy {
        BillingPrefHandlers(
            activity
        )
    }

    private val progressialog = ProgressDialog()

    fun executeNetworkCall(endpoint: String, purchase: Purchase) {

        if (!activity.isFinishing || !activity.isDestroyed) {
            showDialog(progressialog)
        }

        //will get url
        val url = Uri.parse(endpoint)
            .buildUpon()
            .appendQueryParameter("token", purchase.purchaseToken)
            .appendQueryParameter("subscriptionId", purchase.sku)
            .appendQueryParameter("packageName", activity.applicationContext.packageName)
            .build()

        // Formulate the request and handle the response.

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url.toString(), null,
            { response ->


                val jsonPart: JSONObject = response

                val returnedToken = jsonPart.getString("purchase_token")
                val expiryTimeMillis = jsonPart.getLong("expiryTimeMillis")

                if (purchase.purchaseToken == returnedToken) {
                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "Bravo tokens are same"
                    )
                } else {
                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "Bravo tokens are different"
                    )
                }

                if (progressialog.isAdded || progressialog.isVisible) {
                    progressialog.dismissAllowingStateLoss()
                }


                val isSubscribed = SubscribeCheck(activity).checkSubscription(
                            returnedToken,
                            expiryTimeMillis,
                            1,
                            purchase.sku)

                if (isSubscribed) {
                    Log.d(ViyatekPremiumActivity.billingLogs,
                        "Subscription successfull")

                    billingPrefHandlers.apply {
                        setSubscribed(true)
                        setSubscriptionType(purchase.sku)
                    }

                    listener.SubscriptionVerified()

                } else {
                    listener.SubscriptionVerificationFailed(null)
                }

            },
            { error ->


                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "Failed with error msg:\t" + error.message
                )
                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "Error StackTrace: \t" + error.stackTrace
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

                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "The error code  ${error.networkResponse?.data.toString()}"
                )


                if (progressialog.isAdded || progressialog.isVisible) {
                    progressialog.dismissAllowingStateLoss()
                }

                listener.SubscriptionVerificationFailed(error)
            }
        )

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy =
            DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

// Access the RequestQueue through your singleton class.
        BillingHelperLibraryRequestQueue.getInstance(activity).addToRequestQueue(jsonObjectRequest)

    }

    fun showDialog(fragment: DialogFragment?) {

        if (!activity.isFinishing || !activity.isDestroyed) {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            val ft = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag("verification")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            ft.add(fragment!!, "verification")
            ft.commitAllowingStateLoss()
            Log.d(
                ViyatekPremiumActivity.billingLogs,
                "Show Dialogue Called"
            )
        }

    }
}