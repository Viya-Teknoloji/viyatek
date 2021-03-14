package com.viyatek.firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class DynamicLinkHandler(val intent: Intent, val activity: Activity, private val dynamicLinkListener: DynamicLinkListener) {

    val LOG_TAG = "Dynamic Link"

     fun getDynamicLink() {

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(activity) {
                    pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri?

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    dynamicLinkListener.dynamicLinkFetched(deepLink)
                }
                Log.i(LOG_TAG, "Got dynamic Link")
                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(activity) { e ->
                dynamicLinkListener.dynamicLinkFetchError(e)
                Log.w(LOG_TAG, "getDynamicLink:onFailure", e)
            }
    }
}