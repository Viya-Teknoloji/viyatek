package com.viyatek.ads.mopub

import com.mopub.nativeads.NativeErrorCode

interface IMoPubFailListeners {
    fun failedWithInternet(e: NativeErrorCode?)
    fun failedWithoutInternet()
}