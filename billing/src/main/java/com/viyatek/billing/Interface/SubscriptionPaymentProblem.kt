package com.viyatek.billing.Interface

interface SubscriptionPaymentProblem {
    fun subscriptionInGracePeriod(purchaseId: String)
}