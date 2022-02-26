package com.viyatek.ads.applovin

import android.app.Activity
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.viyatek.ads.R

object AppLovingStandaloneNativeAdViewCreator {

    fun createNativeAdView(activity : Activity): MaxNativeAdView
    {
        val binder: MaxNativeAdViewBinder =

            MaxNativeAdViewBinder.Builder(R.layout.applovin_lock_screen_end_ad)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()

        return MaxNativeAdView(binder, activity)
    }
}