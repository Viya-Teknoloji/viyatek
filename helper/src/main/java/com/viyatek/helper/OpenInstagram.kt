package com.viyatek.helper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class OpenInstagram(val context: Context) {

    fun OpenMyInstagramAccount(accountName: String) {

        val uri = Uri.parse("https://instagram.com/_u/$accountName")

        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.setPackage("com.instagram.android")
        try {
            context.startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$accountName/"))
            context.startActivity(intent)
        }
    }
}
