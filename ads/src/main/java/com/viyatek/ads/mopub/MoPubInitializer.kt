package com.viyatek.ads.mopub

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinSdk
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog


class MoPubInitializer(private val context: Context,
                       private val twitter_ad_unit_id:String,
                       private val mopubInitializer : IMoPubInit? = null,
                       private val isGDPRCheckEnabled : Boolean= false) {

    var pangleAppId : String? = null

    companion object{ val LOG_TAG : String = "MOPUB" }

    fun initializeMoPubSDK() {

        // Please make sure to set the mediation provider value to "max" to ensure proper functionality
        // Please make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(context).mediationProvider = "max"
        AppLovinSdk.initializeSdk(context, AppLovinSdk.SdkInitializationListener {
            // AppLovin SDK is initialized, start loading ads
            Log.d(LOG_TAG, "Applovin SDK initialized")
            mopubInitializer?.initMoPubCompleted()

            if(isGDPRCheckEnabled) { GdprHandler().checkForGDPR() }

        })

        Log.d(LOG_TAG, "MoPub SDK initializiation Called")

        val pangleConfig: MutableMap<String, String> = HashMap()
        pangleAppId?.let { pangleConfig["app_id"] = it }

        val configBuilder = SdkConfiguration.Builder(twitter_ad_unit_id)
        configBuilder.withLogLevel(MoPubLog.LogLevel.DEBUG)

        val sdkConfiguration = configBuilder
            .withLegitimateInterestAllowed(true)
            .build()

            MoPub.initializeSdk(context, sdkConfiguration) {}
    }

}