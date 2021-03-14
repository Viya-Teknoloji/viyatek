package com.viyatek.billing.PrefHandlers

import android.content.Context
import android.util.Log

class ViyatekKotlinSharedPrefHelper(val context: Context) {

    private val sharedPrefs by lazy { context.getSharedPreferences(VIYATEK_BILLING_PREF, Context.MODE_PRIVATE) }
    private val editor by lazy { sharedPrefs.edit() }
    private val sharedPrefsEncryption by lazy { ViyatekSharedPrefEncKotlinHelper() }

    fun applyPrefs(tag: Array<String>, value: String) {
        editor.putString(tag[1], sharedPrefsEncryption.encrypt(value))
        editor.apply()
    }

    fun applyPrefs(tag: Array<String>, value: Int) {
        editor.putString(tag[1], sharedPrefsEncryption.encrypt(value.toString()))
        editor.apply()
    }

    fun getPref(tag: Array<String>): ViyatekRowDataKotlinHelper {

        val resolvedString =
            sharedPrefsEncryption.resolveEncrypt(sharedPrefs.getString(tag[1], null))
        return if (resolvedString == null || resolvedString == "") {
            ViyatekRowDataKotlinHelper(tag[0], "")
        } else ViyatekRowDataKotlinHelper(tag[0], resolvedString)
    }

    fun checkUserExists(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(VIYATEK_BILLING_PREF, Context.MODE_PRIVATE)
        return sharedPref.contains(SUBSCRIPTION_TOKEN[1])
    }

    fun createLocalAccount() {


        editor.putString(SUBSCRIPTION_TOKEN[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(SUBSCRIPTION_EXPIRATION_DATE[1], sharedPrefsEncryption.encrypt("0"))

        editor.putString(SUBSCRIBED[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(PREMIUM[1], sharedPrefsEncryption.encrypt("0"))

        editor.putString(SUBSCRIPTION_TRIAL_MODE_USED[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(SUBSCRIPTION_TYPE[1], sharedPrefsEncryption.encrypt("not_subscribed"))
        editor.putString(RESTORE_PURCHASE_ASYNC_CALL_MADE[1], sharedPrefsEncryption.encrypt("0"))

        editor.putString(REMOTE_CAMPAIGN_ACTIVE[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(SPECIAL_DAY_CAMPAIGN_ACTIVE[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(LOCAL_CAMPAIGN_ACTIVE[1], sharedPrefsEncryption.encrypt("0"))

        Log.d("Billing", "Viyatek prefs created")

        editor.putString(CAMPAIGN_START_TIME[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(COMMON_LOCAL_CAMPAIGN_MADE[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(LOCAL_CAMPAIGN_APPEARED_IN_HOME[1], sharedPrefsEncryption.encrypt("0"))
        editor.putString(
            SPECIAL_DAY_CAMPAIGN_APPEARED_IN_HOME[1],
            sharedPrefsEncryption.encrypt("0")
        )

        editor.apply()

        Log.d(
            com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity.billingLogs,
            "Account Created"
        )
    }

    companion object {
        //New User ID
        var isSubscribed = false
        var isPremium = false

        val SUBSCRIPTION_TAG = "Subscription"
        val VIYATEK_BILLING_PREF = "VIYATEK_BILLING_PREF"

        val SUBSCRIPTION_TOKEN = arrayOf("subscription_token", "vbjrvbjrbvwk")
        val SUBSCRIPTION_EXPIRATION_DATE = arrayOf("expiration_date", "wjbwkjrb")
        val SUBSCRIBED = arrayOf("subscribed", "wnrtnrtnr")
        val PREMIUM = arrayOf("premium", "betbbetbt")
        val SUBSCRIPTION_TYPE = arrayOf("subscription_status", "vejvnrınvıuvn")
        val SUBSCRIPTION_TRIAL_MODE_USED = arrayOf("trial_mode_used", "jwrwnfoıwenf")
        val RESTORE_PURCHASE_ASYNC_CALL_MADE = arrayOf("restore_purchase_async_call", "vnnfsjnfjkn")

        val REMOTE_CAMPAIGN_ACTIVE = arrayOf("remote_campaign_active", "sdfnlksnflsknf")
        val SPECIAL_DAY_CAMPAIGN_ACTIVE = arrayOf("special_day_campaign_active", "sdkfslknflsnf")
        val LOCAL_CAMPAIGN_ACTIVE = arrayOf("local_campaign_active", "flksdnflskdnfslknf")

        val CAMPAIGN_START_TIME = arrayOf("campaign_start_time", "fmsknfenfo")
        val COMMON_LOCAL_CAMPAIGN_MADE = arrayOf("local_campaign_no", "vsaldkvmskm")

        val LOCAL_CAMPAIGN_APPEARED_IN_HOME =
            arrayOf("local_campaign_appeared_in_home", "sdfskfekfmnafm")
        val LOCAL_CAMPAIGN_NO = arrayOf("local_campaign_no", "vkjbkjwbwfnwekf")

        val SPECIAL_DAY_CAMPAIGN_APPEARED_IN_HOME = arrayOf("special_day_campaign", "weptjawıtsese")
        val REMOTE_CAMPAIGN_APPEARED_IN_HOME = arrayOf("remote_campaign_at_home", "weırjwnfsand")

    }
}