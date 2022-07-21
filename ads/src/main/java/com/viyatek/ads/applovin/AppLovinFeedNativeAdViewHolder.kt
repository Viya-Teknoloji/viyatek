package com.viyatek.ads.applovin

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.viyatek.ads.R
import com.viyatek.ads.databinding.AdmobStandaloneFeedDarkBinding


class AppLovinFeedNativeAdViewHolder(val theContext: Context, val binding: AdmobStandaloneFeedDarkBinding) : RecyclerView.ViewHolder(binding.root)
{
    init {

     /*   val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.native_custom_ad_view)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()

        val theMaxNativeAdview = MaxNativeAdView(binder,theContext)
*/
    }


}
