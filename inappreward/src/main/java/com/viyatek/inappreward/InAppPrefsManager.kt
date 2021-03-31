package com.viyatek.inappreward

import android.content.Context
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class InAppPrefsManager(private val context: Context) {

    private val mPrefsManager by lazy { ViyatekSharedPrefsHandler(context, Statics.IN_APP_REWARD_PREFS)}

    fun isRewardedShareMade() : Boolean = mPrefsManager.getBooleanValue(Statics.IS_SHARE_MADE, false)
    fun setRewardedShareMade(isShared : Boolean) = mPrefsManager.applyPrefs(Statics.IS_SHARE_MADE, isShared)

    fun isShareRewarded() : Boolean = mPrefsManager.getBooleanValue(Statics.IS_SHARE_REWARDED, false)
    fun setShareRewarded(isRewarded : Boolean) = mPrefsManager.applyPrefs(Statics.IS_SHARE_REWARDED, isRewarded)

    fun isRewardedRateMade() : Boolean = mPrefsManager.getBooleanValue(Statics.IS_RATE_US_MADE, false)
    fun setRewardedRateMade(isRated:  Boolean) = mPrefsManager.applyPrefs(Statics.IS_RATE_US_MADE, isRated)

    fun isRateRewarded():Boolean = mPrefsManager.getBooleanValue(Statics.IS_RATE_REWARDED, false)
    fun setRateRewarded(isRewarded : Boolean) = mPrefsManager.applyPrefs(Statics.IS_RATE_REWARDED, isRewarded)

}