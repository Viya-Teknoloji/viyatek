package com.viyatek.ads.interfaces

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError

interface AdMobAdListener {
    fun adClicked()
    fun adFailedToLoad(error : LoadAdError?)
}