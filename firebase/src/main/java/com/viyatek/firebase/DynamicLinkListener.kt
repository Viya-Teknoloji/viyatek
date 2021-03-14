package com.viyatek.firebase

import android.net.Uri

interface DynamicLinkListener {
    fun dynamicLinkFetched(deepLink: Uri?)
    fun dynamicLinkFetchError(e: Exception)
}