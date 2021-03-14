package com.viyatek.billing.SubscriptionHelpers

import android.content.Context
import android.util.Log
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity

import java.util.*

internal class SubscribeCheck(val mCtxt: Context) {

    private var calendar: Calendar = Calendar.getInstance()


    fun checkSubscription(
        sharedPrefsHelperViyatek: ViyatekKotlinSharedPrefHelper,
        token: String,
        expirationTime: Long,
        paymentStatus: Int,
        SubscriptionType: String
    ): Boolean {


        Log.d(ViyatekPremiumActivity.billingLogs, "Checking Subs ")

        sharedPrefsHelperViyatek.applyPrefs(
            ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TYPE,
            SubscriptionType
        )

        //IF RECOVERED WE WILL REGISTER PERSON WİTH NEW ID
        return if (expirationTime < calendar.timeInMillis) {
            Log.d(ViyatekPremiumActivity.billingLogs, "Cancelling Subs ")
            CancelSubscription(sharedPrefsHelperViyatek).CancelSubscribe()
            false
        } else if (expirationTime > calendar.timeInMillis && paymentStatus == 0) {
            Log.d(ViyatekPremiumActivity.billingLogs, "Execute Subs ")
            ExecuteSubscription(sharedPrefsHelperViyatek).SubscribeClient(token, expirationTime)
            true
        } else {
            Log.d(ViyatekPremiumActivity.billingLogs, "Execute Subs ")
            ExecuteSubscription(sharedPrefsHelperViyatek).SubscribeClient(token, expirationTime)
            true
        }
    }
}