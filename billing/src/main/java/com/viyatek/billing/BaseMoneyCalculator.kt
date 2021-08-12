package com.viyatek.billing

import android.view.View
import android.widget.TextView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import java.time.Period

object BaseMoneyCalculator {

    fun calculatePerDayCost(baseSku: SkuDetails?): Float {
        var baseMoneyPerDay: Float = -1f
        if (baseSku?.type == BillingClient.SkuType.SUBS) {

            val subscriptionPeriod = baseSku.subscriptionPeriod
            val thePeriod: Period?
            var thePeriodTime: Int
            if (subscriptionPeriod.isNotBlank()) {
                thePeriod = Period.parse(subscriptionPeriod)
                thePeriod.apply {
                    when {
                        years != 0 -> {
                            thePeriodTime = thePeriod.years
                            baseMoneyPerDay =
                                baseSku.priceAmountMicros.toFloat() / (thePeriodTime * 365)
                        }
                        months != 0 -> {
                            baseMoneyPerDay =
                                baseSku.priceAmountMicros.toFloat() / (thePeriod.months * 30)
                        }
                        else -> {
                            thePeriodTime = thePeriod.days
                            baseMoneyPerDay = baseSku.priceAmountMicros.toFloat() / (thePeriod.days)
                        }
                    }
                }
            }
        }

        return baseMoneyPerDay
    }
}