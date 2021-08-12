package com.viyatek.ads.admob

import androidx.recyclerview.widget.RecyclerView
import com.viyatek.ads.databinding.SecondVersionAdmobStandloneFeedDarkBinding

class NativeAdSecondVersionDarkViewHolder(val binding: SecondVersionAdmobStandloneFeedDarkBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.nativeAdView.mediaView = binding.mediaView
        binding.nativeAdView.bodyView = binding.body
        binding.nativeAdView.headlineView = binding.primary
        binding.nativeAdView.callToActionView = binding.cta
        binding.nativeAdView.iconView = binding.icon
    }

}