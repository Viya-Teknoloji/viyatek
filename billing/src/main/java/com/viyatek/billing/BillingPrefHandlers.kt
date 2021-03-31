package com.viyatek.billing

import android.content.Context
import android.util.Log
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class BillingPrefHandlers(val context: Context) {


    val prefHandlers by lazy { getPrefManager() }

    private fun getPrefManager() = ViyatekSharedPrefsHandler(context, Statics.BILLING_PREF_NAME)

    fun numberOfRemoteCampaignAppearedInHome() : Int = prefHandlers.getIntegerValue(Statics.REMOTE_CAMPAIGN_APPEARED_IN_HOME, 0)
    fun setRemoteCampaignAppearedInHome(campaignNo : Int) = prefHandlers.applyPrefs(Statics.REMOTE_CAMPAIGN_APPEARED_IN_HOME, campaignNo)

    fun numberOfSpecialCampaignAppearedInHome() : Int = prefHandlers.getIntegerValue(Statics.SPECIAL_CAMPAIGN_APPEARED_IN_HOME, 0)
    fun setSpecialCampaignAppearedInHome(campaignNo : Int) = prefHandlers.applyPrefs(Statics.SPECIAL_CAMPAIGN_APPEARED_IN_HOME, campaignNo)

    fun numberOfLocalCampaignAppearedInHome() : Int = prefHandlers.getIntegerValue(Statics.LOCAL_CAMPAIGN_APPEARED_IN_HOME, 0)
    fun setLocalCampaignAppearedInHome(campaignNo : Int) = prefHandlers.applyPrefs(Statics.LOCAL_CAMPAIGN_APPEARED_IN_HOME, campaignNo)

    fun getLocalCampaignNumber() : Int = prefHandlers.getIntegerValue(Statics.LOCAL_CAMPAIGN_NO, 0)
    fun setLocalCampaignNumber(campaignNo : Int) = prefHandlers.applyPrefs(Statics.LOCAL_CAMPAIGN_NO, campaignNo)

    fun isPremium() : Boolean = prefHandlers.getBooleanValue(Statics.PREMIUM, false)
    fun setPremium(isPremium : Boolean) {
        Log.d("Bill", "Making Client Premium")
        prefHandlers.applyPrefs(Statics.PREMIUM, isPremium)
    }

    fun isSubscribed() : Boolean = prefHandlers.getBooleanValue(Statics.SUBSCRIBED, false)
    fun setSubscribed(isPremium : Boolean) = prefHandlers.applyPrefs(Statics.SUBSCRIBED, isPremium)

    fun getCampaignStartTime() : Long = prefHandlers.getLongVale(Statics.CAMPAIGN_START_TIME, 0L)
    fun setCampaignStartTime(campaignStartTime : Long) = prefHandlers.applyPrefs(Statics.CAMPAIGN_START_TIME, campaignStartTime)

    fun isRemoteCampaignActive() : Boolean = prefHandlers.getBooleanValue(Statics.REMOTE_CAMPAIGN_ACTIVE, false)
    fun setRemoteCampaignActive(isRemoteActive : Boolean) = prefHandlers.applyPrefs(Statics.REMOTE_CAMPAIGN_ACTIVE, isRemoteActive)

    fun isLocalCampaignActive() : Boolean = prefHandlers.getBooleanValue(Statics.LOCAL_CAMPAIGN_ACTIVE, false)
    fun setLocalCampaignActive(isLocalActive : Boolean) = prefHandlers.applyPrefs(Statics.LOCAL_CAMPAIGN_ACTIVE, isLocalActive)

    fun isSpecialDayCampaignActive() : Boolean = prefHandlers.getBooleanValue(Statics.SPECIAL_DAY_CAMPAIGN_ACTIVE, false)
    fun setSpecialDayCampaignActive(isSpecialActive : Boolean) = prefHandlers.applyPrefs(Statics.SPECIAL_DAY_CAMPAIGN_ACTIVE, isSpecialActive)

    fun getSubscriptionToken() : String? = prefHandlers.getStringValue(Statics.SUBSCRIPTION_TOKEN, "0")
    fun setSubscriptionToken(token : String)  = prefHandlers.applyPrefs(Statics.SUBSCRIPTION_TOKEN, token)

    fun getSubscriptionType() : String? = prefHandlers.getStringValue(Statics.SUBSCRIPTION_TYPE, "0")
    fun setSubscriptionType(sku : String)  = prefHandlers.applyPrefs(Statics.SUBSCRIPTION_TYPE, sku)

    fun getSubscriptionExpTime() : Long = prefHandlers.getLongVale(Statics.SUBSCRIPTION_EXP_DATE, 0L)
    fun setSubscriptionExpTime(date : Long)  = prefHandlers.applyPrefs(Statics.SUBSCRIPTION_EXP_DATE, date)

    fun isSubscriptionTrialModeUsed() : Boolean = prefHandlers.getBooleanValue(Statics.SUBSCRIPTION_TRIAL_MODE_USED, false)
    fun setSubscriptionTrialModeUsed(isUsed : Boolean) = prefHandlers.applyPrefs(Statics.SUBSCRIPTION_TRIAL_MODE_USED, isUsed)

    fun isRestorePurchaseAsyncCallMade() : Boolean = prefHandlers.getBooleanValue(Statics.RESTORE_PURCHASE_ASYNC_CALL_MADE, false)
    fun setRestorePurchaseAsyncCallMade(isUsed : Boolean) = prefHandlers.applyPrefs(Statics.RESTORE_PURCHASE_ASYNC_CALL_MADE, isUsed)

}