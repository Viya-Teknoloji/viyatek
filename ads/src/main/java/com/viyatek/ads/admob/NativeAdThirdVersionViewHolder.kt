package com.viyatek.ads.admob

import androidx.recyclerview.widget.RecyclerView
import com.viyatek.ads.databinding.ThirdVersionAdmobStandloneBinding

class NativeAdThirdVersionViewHolder(val binding: ThirdVersionAdmobStandloneBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.nativeAdView.headlineView = binding.theTitle
        binding.nativeAdView.mediaView = binding.mediaView
        binding.nativeAdView.bodyView = binding.body
        binding.nativeAdView.advertiserView = binding.primary
        binding.nativeAdView.callToActionView = binding.cta
        binding.nativeAdView.iconView = binding.icon
        binding.nativeAdView.starRatingView = binding.ratingBar
        binding.nativeAdView.priceView = binding.adPrice
        binding.nativeAdView.storeView = binding.adStore
    }

}