package com.viyatek.billing.SubscriptionHelpers

import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIBED
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TOKEN
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIPTION_TYPE

internal class CancelSubscription(val sharedPrefHelperViyatek: com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper) {

    fun CancelSubscribe() {

        com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.isSubscribed = false

        sharedPrefHelperViyatek.applyPrefs(SUBSCRIBED, 0)
        sharedPrefHelperViyatek.applyPrefs(SUBSCRIPTION_TYPE, "not_subscribed")
        sharedPrefHelperViyatek.applyPrefs(SUBSCRIPTION_TOKEN, "0")
    }
}