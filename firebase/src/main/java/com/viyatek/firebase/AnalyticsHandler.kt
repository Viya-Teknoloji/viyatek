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


    /*

    class AnalyticsHandler(private val context: Context) {

    var theAnalyticsBuilder : AnalyticsBuilder? = null
    private var theEventName : String = ""
    val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    fun eventName(theName : String) : AnalyticsBuilder?
    {
        theEventName = theName
        theAnalyticsBuilder =  AnalyticsBuilder(theName,HashMap())
        return theAnalyticsBuilder
    }

    fun logEvent(eventName:String, eventBundle: Bundle?)
    { firebaseAnalytics.logEvent(eventName, eventBundle) }

    fun setUserProperty(key:String, value:String)
    { firebaseAnalytics.setUserProperty(key,value) }


    inner class AnalyticsBuilder(val theEventName:String, val theEvent : HashMap<String, Any> = HashMap())
    {
        inner class EmptyEventNameException(theMessage : String) : Exception(theMessage)

        fun addProperty(theKey:String, theValue:Any) : AnalyticsBuilder{
            theEvent[theKey] = theValue
            return this
        }
        fun logForAll()
        {
            logForFireBase()
            logForAmazon()
        }

        private fun logForAmazon() {
            val event = AnalyticsEvent.builder()
                .name(theEventName)

            theEvent.forEach {
                when (it.value) {
                    is String -> {
                        event.addProperty(it.key,it.value as String)
                    }
                    is Int -> {
                        event.addProperty(it.key,it.value as Int)
                    }
                    is Double -> {
                        event.addProperty(it.key,it.value as Double)
                    }
                    is Boolean -> {
                        event.addProperty(it.key,it.value as Boolean)
                    }
                }
            }
            Amplify.Analytics.recordEvent(event.build())
        }

        private fun logForFireBase() {
            val theBundle = Bundle()

            theEvent.forEach {
                when (it.value) {
                    is String -> {
                        theBundle.putString(it.key,it.value as String)
                    }
                    is Int -> {
                        theBundle.putInt(it.key,it.value as Int)
                    }
                    is Long -> {
                        theBundle.putLong(it.key,it.value as Long)
                    }
                    is Boolean -> {
                        theBundle.putBoolean(it.key,it.value as Boolean)
                    }
                }
            }

            firebaseAnalytics.logEvent(theEventName, theBundle)

        }
    }
}
     */
}
