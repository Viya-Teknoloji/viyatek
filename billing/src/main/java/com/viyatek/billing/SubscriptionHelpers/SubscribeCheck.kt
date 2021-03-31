package com.viyatek.billing.SubscriptionHelpers

import android.content.Context
import android.util.Log
import com.viyatek.billing.BillingPrefHandlers
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity

import java.util.*

internal class SubscribeCheck(val mCtxt: Context) {

    private var calendar: Calendar = Calendar.getInstance()

    val billingPrefHandlers by lazy { BillingPrefHandlers(mCtxt) }

    fun checkSubscription(
        token: String?,
        expirationTime: Long,
        paymentStatus: Int,
        SubscriptionType: String?
    ): Boolean {


        Log.d(ViyatekPremiumActivity.billingLogs, "Checking Subs ")

        billingPrefHandlers.setSubscriptionType(SubscriptionType!!)


        //IF RECOVERED WE WILL REGISTER PERSON WİTH NEW ID
        return if (expirationTime < calendar.timeInMillis) {
            Log.d(ViyatekPremiumActivity.billingLogs, "Cancelling Subs ")

            billingPrefHandlers.apply {
                setSubscribed(false)
                setSubscriptionType("not_subscribed")
                setSubscriptionToken("0")
            }

            false
        } else  {
            Log.d(ViyatekPremiumActivity.billingLogs, "Execute Subs ")

            billingPrefHandlers.apply {
                setSubscribed(true)
                setSubscriptionExpTime(expirationTime)
                setSubscriptionToken(token!!)
            }

            true
        }
    }
}