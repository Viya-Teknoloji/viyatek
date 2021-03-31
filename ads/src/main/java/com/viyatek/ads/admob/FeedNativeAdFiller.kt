package com.viyatek.ads.admob

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FeedNativeAdFiller {
        fun handle(nativeAd: NativeAd, adView: NativeAdView) {
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body
            (adView.callToActionView as Button).text = nativeAd.callToAction

            adView.mediaView.setMediaContent(nativeAd.mediaContent)
            adView.mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

            adView.setNativeAd(nativeAd)
        }
}