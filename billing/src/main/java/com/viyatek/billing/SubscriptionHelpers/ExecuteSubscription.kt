package com.viyatek.billing.SubscriptionHelpers

internal class ExecuteSubscription(val sharedPrefsHelperViyatek: com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper) {

    fun SubscribeClient(token: String, expireTime: Long) {

        com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.isSubscribed = true
        sharedPrefsHelperViyatek.applyPrefs(
            com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIBED,
            1
        )
        sharedPrefsHelperViyatek.applyPrefs(
            com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_TOKEN,
            token
        )
        sharedPrefsHelperViyatek.applyPrefs(
            com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.SUBSCRIPTION_EXPIRATION_DATE,
            expireTime.toString()
        )
    }
}