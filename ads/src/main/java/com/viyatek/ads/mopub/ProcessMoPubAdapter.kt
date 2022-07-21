package com.viyatek.ads.mopub

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.mopub.nativeads.*
import com.mopub.nativeads.FacebookAdRenderer.FacebookViewBinder
import com.viyatek.ads.R

class ProcessMoPubAdapter(private val activity: Activity,
                          private val recyclerView: RecyclerView,
                          private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                          private val twitter_native_ad_unit_id : String,
                          private var isSwipe : Boolean = false,
                          private val isDark: Boolean =false,
                          private val designVersionNumber : Int = 1
) {

    val myMoPubAdapter = MoPubRecyclerAdapter(activity, adapter)

    fun getMoPubAdapter() : MoPubRecyclerAdapter{
        return myMoPubAdapter
    }

    fun init() {

             val myViewBinder = if(isSwipe) {
                ViewBinder.Builder(
                    if(isDark) { R.layout.tw_ad_card_layout_dark }
                    else { R.layout.tw_ad_card_layout })
                    .iconImageId(R.id.icon)
                    .sponsoredTextId(R.id.advertiser_name)
                    .titleId(R.id.ad_card_head_line)
                    .textId(R.id.ad_card_body_text)
                    .mainImageId(R.id.ad_card_image)
                    .callToActionId(R.id.ad_card_button)
                    .privacyInformationIconImageId(R.id.privacy)
                    .build()

            }
             else {
                when (designVersionNumber) {
                    2 -> {
                        ViewBinder.Builder(
                            if (isDark) {
                                R.layout.second_version_tw_ad_card_layout_feed_dark
                            } else {
                                R.layout.second_version_tw_ad_card_layout_feed
                            }
                        )
                            .iconImageId(R.id.icon)
                            .titleId(R.id.ad_card_head_line)
                            .textId(R.id.ad_card_body_text)
                            .mainImageId(R.id.ad_card_image)
                            .callToActionId(R.id.ad_card_button)
                            .privacyInformationIconImageId(R.id.privacy)
                            .build()
                    }
                    else -> {
                        ViewBinder.Builder(
                            if (isDark) {
                                R.layout.tw_ad_card_layout_feed_dark
                            } else {
                                R.layout.tw_ad_card_layout_feed
                            }
                        )
                            .titleId(R.id.ad_card_head_line)
                            .textId(R.id.ad_card_body_text)
                            .mainImageId(R.id.ad_card_image)
                            .callToActionId(R.id.ad_card_button)
                            .privacyInformationIconImageId(R.id.privacy)
                            .build()
                    }


                }
            }

            val myRenderer = MoPubStaticNativeAdRenderer(myViewBinder)


            val facebookAdRenderer = FacebookAdRenderer(
                if(isSwipe)
                {
                    FacebookViewBinder.Builder(
                            if(isDark)
                            { R.layout.fb_ad_card_layout_dark}
                            else
                            { R.layout.fb_ad_card_layout}
                        )
                        .titleId(R.id.ad_card_head_line)
                        .advertiserNameId(R.id.advertiser_name)
                        .adIconViewId(R.id.icon)
                        .textId(R.id.ad_card_body_text) // Binding to new layouts from Facebook 4.99.0+
                        .mediaViewId(R.id.ad_card_image)
                        .adChoicesRelativeLayoutId(R.id.privacy)
                        .callToActionId(R.id.ad_card_button)
                        .build()
                }
               else
                {
                    when(designVersionNumber)
                    {
                        2 -> {
                            FacebookViewBinder.Builder(

                                if(isDark)
                                { R.layout.second_version_fb_ad_card_layout_dark }
                                else
                                { R.layout.second_version_fb_ad_card_layout}

                            )
                                .titleId(R.id.ad_card_head_line)
                                .textId(R.id.ad_card_body_text) // Binding to new layouts from Facebook 4.99.0+
                                .mediaViewId(R.id.media_view)
                                .adChoicesRelativeLayoutId(R.id.privacy)
                                .callToActionId(R.id.ad_card_button)
                                .build()
                        }
                        else -> {
                            FacebookViewBinder.Builder(

                                if(isDark)
                                { R.layout.fb_ad_card_layout_feed_dark }
                                else
                                { R.layout.fb_ad_card_layout_feed}

                            )
                                .titleId(R.id.ad_card_head_line)
                                .textId(R.id.ad_card_body_text) // Binding to new layouts from Facebook 4.99.0+
                                .mediaViewId(R.id.ad_card_image)
                                .adChoicesRelativeLayoutId(R.id.privacy)
                                .callToActionId(R.id.ad_card_button)
                                .build()
                        }
                    }


                }
            )


        myMoPubAdapter.registerAdRenderer(facebookAdRenderer)
        myMoPubAdapter.registerAdRenderer(myRenderer)

        recyclerView.adapter = myMoPubAdapter
        myMoPubAdapter.loadAds(twitter_native_ad_unit_id)
    }
}