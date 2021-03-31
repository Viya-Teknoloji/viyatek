package com.viyatek.ads.admob

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.viyatek.ads.databinding.AdmobAdCardLayoutDarkBinding
import com.viyatek.ads.databinding.AdmobAdCardLayoutFeedBinding
import com.viyatek.ads.databinding.AdmobAdCardLayoutFeedDarkBinding
import com.viyatek.ads.databinding.AdmobStandaloneSwipeDarkBinding

class NativeAdDarkSwipeViewHolder(val binding: AdmobStandaloneSwipeDarkBinding) : RecyclerView.ViewHolder(binding.root)
{init {
    // If the app is using a MediaView, it should be
    // instantiated and passed to setMediaView. This view is a little different
    // in that the asset is populated automatically, so there's one less step.

    // If the app is using a MediaView, it should be
    // instantiated and passed to setMediaView. This view is a little different
    // in that the asset is populated automatically, so there's one less step.

    binding.adView.mediaView = binding.adCardImage

    // Register the view used for each individual asset.

    // Register the view used for each individual asset.
    binding.adView.headlineView = binding.adCardHeadLine
    binding.adView.bodyView = binding.adCardBodyText
    binding.adView.callToActionView = binding.adCardButton

}
}
