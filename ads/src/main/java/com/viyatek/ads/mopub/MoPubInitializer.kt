package com.viyatek.ads.mopub

import android.content.Context
import android.util.Log
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.facebook.ads.AdSettings
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.PangleAdapterConfiguration
import java.util.*

class MoPubInitializer(private val context: Context,
                       private val twitter_ad_unit_id:String,
                       private val mopubInitializer : IMoPubInit? = null,
                       private val isGDPRCheckEnabled : Boolean= false) {

    var pangleAppId : String? = null

    companion object{ val LOG_TAG : String = "MOPUB" }

    fun initializeMoPubSDK() {

        val testDevices: MutableList<String> = ArrayList()
        testDevices.add("FB9F1C3D53382E1666489F5407301E91")
        testDevices.add("CDD86067AF6113E56BF2B62A2D28F5DB")
        testDevices.add("8F7C1F9C3B6C783BB772DF15871E5636")

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()

        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(context)

       // AudienceNetworkAds.initialize(context);

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

            MoPub.initializeSdk(context, sdkConfiguration) {
            Log.d(LOG_TAG, "MoPub SDK initialized")

            mopubInitializer?.initMoPubCompleted()

            if(isGDPRCheckEnabled) {
                GdprHandler().checkForGDPR()}

            AdSettings.addTestDevice("c94498a3-6810-48db-921a-795f108e58e3");
            AdSettings.addTestDevice("4046b6af-565e-4535-8ed2-a274db570d19");
            AdSettings.addTestDevice("f442208d-12c1-471b-9381-305df8580264")
            AdSettings.addTestDevice("f3237402-e79a-4e5a-be81-b6c5b6441c28")
        }
    }

    fun  buildConfig() : TTAdConfig {
        return TTAdConfig.Builder()
            .appId("Your_App_Id")
            .supportMultiProcess(false)
            .coppa(0)
            .setGDPR(0)
            .build()
    }
}