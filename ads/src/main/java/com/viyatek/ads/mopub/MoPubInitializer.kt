package com.viyatek.ads.mopub

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinSdk
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.facebook.ads.AdSettings
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.PangleAdapterConfiguration


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

            AdSettings.addTestDevice("c94498a3-6810-48db-921a-795f108e58e3");
            AdSettings.addTestDevice("4046b6af-565e-4535-8ed2-a274db570d19");
            AdSettings.addTestDevice("f442208d-12c1-471b-9381-305df8580264")
            AdSettings.addTestDevice("f3237402-e79a-4e5a-be81-b6c5b6441c28")
        })

        Log.d(LOG_TAG, "MoPub SDK initializiation Called")

        val pangleConfig: MutableMap<String, String> = HashMap()
        pangleAppId?.let { pangleConfig["app_id"] = it }

        val configBuilder = SdkConfiguration.Builder(twitter_ad_unit_id)
        configBuilder.withLogLevel(MoPubLog.LogLevel.DEBUG)

        val sdkConfiguration = configBuilder
            .withAdditionalNetwork(PangleAdapterConfiguration::class.java.name)
            .withMediatedNetworkConfiguration(PangleAdapterConfiguration::class.java.name, pangleConfig)
            .withLegitimateInterestAllowed(true)
            .build()

            MoPub.initializeSdk(context, sdkConfiguration) {}
    }

    fun  buildConfig() : TTAdConfig {
        return TTAdConfig.Builder()
            .appId(pangleAppId)
            .supportMultiProcess(false)
            .coppa(0)
            .setGDPR(0)
            .build()
    }
}