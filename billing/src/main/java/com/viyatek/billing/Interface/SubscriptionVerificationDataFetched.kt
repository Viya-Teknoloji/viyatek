package com.viyatek.billing.Interface

import com.android.volley.VolleyError

interface SubscriptionVerificationDataFetched {
    fun SubscriptionVerified()
    fun SubscriptionVerificationFailed(error: VolleyError?)
}