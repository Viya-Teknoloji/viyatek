package com.viyatek.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHandler(private val context: Context) {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    fun logEvent(eventName:String, eventBundle: Bundle?)
    { firebaseAnalytics.logEvent(eventName, eventBundle) }

    fun setUserProperty(key:String, value:String)
    { firebaseAnalytics.setUserProperty(key,value) }

}