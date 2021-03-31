package com.viyatek.ads.admob

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.ArrayList

class AdMobInitializer(private val context: Context)  {

        fun init() {
            val testDevices: MutableList<String> = ArrayList()
            testDevices.add("FB9F1C3D53382E1666489F5407301E91")
            testDevices.add("CDD86067AF6113E56BF2B62A2D28F5DB")
            testDevices.add("8F7C1F9C3B6C783BB772DF15871E5636")

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)

            MobileAds.initialize(context) { initializationStatus ->

                val statusMap = initializationStatus.adapterStatusMap

                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d("MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status!!.description, status.latency))
                }

                isAdmobInitialized = true
                // Start loading ads here...
            }
        }

    companion object
    {
        var isAdmobInitialized = false
    }
}