package com.viyatek.ads.interfaces

interface LoadMultipleAds {
        fun loadNativeAds(multipleAdsCount : Int = 0)
        fun loadNewAds()
        fun insertAdsInMenuItems()
}