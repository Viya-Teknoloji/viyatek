package com.viyatek.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

abstract class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    val LOG_TAG = "FirebaseFCM"
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    abstract fun sendRegistrationToServer(token: String)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(LOG_TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(LOG_TAG, "Message data payload: " + remoteMessage.data)
            scheduleJob(remoteMessage)
        }


        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(LOG_TAG, "Message Notification Body: " + remoteMessage.notification?.body) }

        //HandleAlarms(applicationContext).SetAlarmManager()

        Log.d(LOG_TAG, "Yeni deneme")

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    abstract fun scheduleJob(data: RemoteMessage)

}