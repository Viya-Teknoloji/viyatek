package com.viyatek.helper

import android.net.Uri
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

class UrlToUri {

    fun ExecuteChange(myUrlStr: String): Uri? {
        val url: URL
        val uri: Uri
        try {
            url = URL(myUrlStr)
            uri = Uri.parse(url.toURI().toString())
            return uri
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return null
    }
}