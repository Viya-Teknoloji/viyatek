package com.viyatek.ads.admob

import com.viyatek.ads.databinding.SecondVersionAdmobStandloneFeedFlutterBinding
import com.viyatek.ads.databinding.ThirdVersionAdmobStandloneBinding

class BindAdViewsToLayout() {

    fun bindView(binding : ThirdVersionAdmobStandloneBinding)
    {
        binding.nativeAdView.apply {
            bodyView = binding.body
            callToActionView = binding.cta
            headlineView = binding.theTitle
            iconView = binding.icon
            advertiserView = binding.primary
            priceView = binding.adPrice
            starRatingView = binding.ratingBar
            mediaView = binding.mediaView
            storeView = binding.adStore
        }
    }

    fun bindView(binding : SecondVersionAdmobStandloneFeedFlutterBinding)
    {
        binding.nativeAdView.apply{
            bodyView = binding.body
            callToActionView = binding.cta
            headlineView = binding.primary
            iconView = binding.icon
            mediaView = binding.mediaView
        }
    }

}