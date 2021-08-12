package com.viyatek.firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfigHandler ()
{
    private val configSettings by lazy { getConfig() }
    val mFirebaseRemoteConfig by lazy { getConfigInstance() }

    fun setDefaultValues( xmlResourceId : Int) { mFirebaseRemoteConfig.setDefaultsAsync(xmlResourceId) }

    private fun getConfigInstance(): FirebaseRemoteConfig {

        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener {
        }

        return mFirebaseRemoteConfig
    }

    private fun getConfig(): FirebaseRemoteConfigSettings {
        return FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
    }

    fun getString(key : String) : String = mFirebaseRemoteConfig.getString(key)

    fun getBoolean(key:String) : Boolean = mFirebaseRemoteConfig.getBoolean(key)

    fun getLong(key:String):Long= mFirebaseRemoteConfig.getLong(key)



}