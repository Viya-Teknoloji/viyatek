package com.viyatek.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri

/*
Created By Eren Tüfekçi
*/class EmailComposer(val activity: Activity) {

    fun composeEmail(subject: String?, addresses: Array<String>) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        }
    }
}