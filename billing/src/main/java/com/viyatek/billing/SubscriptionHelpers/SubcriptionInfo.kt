package com.viyatek.billing.SubscriptionHelpers

import android.content.Context

internal class SubcriptionInfo(val context: Context) {

    fun isSubscribed(): Boolean {
        val prefHandler = com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(context)
        return prefHandler.getPref(com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIBED)
            ?.getIntegerValue() == 1
    }

    fun getSubscriptionToken(): String? {
        val prefHandler = com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(context)
        return prefHandler.getPref(com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TOKEN)
            ?.getStringValue()
    }

    fun getExpireTime(): Long? {
        val prefHandler = com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(context)
        return prefHandler.getPref(com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_EXPIRATION_DATE)
            ?.getStringValue()?.toLong()
    }

    fun getSubscriptionSku(): String? {
        val prefHandler = com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper(context)
        return prefHandler.getPref(com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TYPE)
            ?.getStringValue()
    }

}