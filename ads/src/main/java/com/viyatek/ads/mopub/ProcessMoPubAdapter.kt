package com.viyatek.ads.mopub

import android.app.Activity
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.mopub.nativeads.*
import com.mopub.nativeads.FacebookAdRenderer.FacebookViewBinder
import com.viyatek.ads.R

class ProcessMoPubAdapter(private val activity: Activity,
                          private val recyclerView: RecyclerView,
                          private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                          private val twitter_native_ad_unit_id : String,
                          private var isSwipe : Boolean = false,
                          private val isDark: Boolean =false
) {

    fun init() {

        val myMoPubAdapter = MoPubRecyclerAdapter(activity, adapter)

            val myViewBinder = ViewBinder.Builder(
                if(isSwipe){
                   if(isDark)
                   { R.layout.tw_ad_card_layout_dark}
                    else
                   { R.layout.tw_ad_card_layout}
                }
                else{
                    if(isDark)
                    { R.layout.tw_ad_card_layout_feed_dark }
                    else
                    { R.layout.tw_ad_card_layout_feed }

                })
                .titleId(R.id.ad_card_head_line)
                .textId(R.id.ad_card_body_text)
                .mainImageId(R.id.ad_card_image)
                .callToActionId(R.id.ad_card_button)
                .privacyInformationIconImageId(R.id.privacy)
                .build()


            val myRenderer = MoPubStaticNativeAdRenderer(myViewBinder)

            val googlePlayServicesAdRenderer = GooglePlayServicesAdRenderer(
                GooglePlayServicesViewBinder.Builder(
                    if(isSwipe){
                        if(isDark)
                        { R.layout.admob_ad_card_layout_dark}
                        else
                        { R.layout.admob_ad_card_layout}
                    }
                    else{
                        if(isDark)
                        { R.layout.admob_ad_card_layout_feed_dark }
                        else
                        { R.layout.admob_ad_card_layout_feed}

                    })

                    .mediaLayoutId(R.id.ad_card_image) // bind to your `com.mopub.nativeads.MediaLayout` element
                    .titleId(R.id.ad_card_head_line)
                    .textId(R.id.ad_card_body_text)
                    .callToActionId(R.id.ad_card_button)
                    .privacyInformationIconImageId(R.id.privacy)
                    .addExtra(
                        GooglePlayServicesAdRenderer.VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER,
                        R.id.native_ad_choices_icon_container
                    )
                    .build()
            )

            val facebookAdRenderer = FacebookAdRenderer(
                FacebookViewBinder.Builder(
                    if(isSwipe){
                        if(isDark)
                        { R.layout.fb_ad_card_layout_dark}
                        else
                        { R.layout.fb_ad_card_layout}
                    }
                    else{
                        if(isDark)
                        { R.layout.fb_ad_card_layout_feed_dark }
                        else
                        { R.layout.fb_ad_card_layout_feed}

                    })
                    .titleId(R.id.ad_card_head_line)
                    .textId(R.id.ad_card_body_text) // Binding to new layouts from Facebook 4.99.0+
                    .mediaViewId(R.id.ad_card_image)
                    .adChoicesRelativeLayoutId(R.id.privacy)
                    .callToActionId(R.id.ad_card_button)
                    .build()
            )


            val pangleAdRenderer = PangleAdRenderer(
                PangleAdViewBinder.Builder(
                    if(isSwipe){
                        if(isDark)
                        { R.layout.pangle_ad_card_layout_dark}
                        else
                        { R.layout.pangle_ad_card_layout}
                    }
                    else{
                        if(isDark)
                        { R.layout.pangle_ad_card_layout_feed_dark}
                        else
                        { R.layout.pangle_ad_card_layout_feed}

                    })
                    .callToActionId(R.id.ad_card_button)
                    .decriptionTextId(R.id.ad_card_body_text) //.iconImageId(R.id.native_icon_image)
                    .titleId(R.id.ad_card_head_line)
                    .mediaViewIdId(R.id.ad_card_image) // Bind to <com.bytedance.sdk.openadsdk.adapter.MediaView /> in XML
                    .build()
            )



        myMoPubAdapter.registerAdRenderer(googlePlayServicesAdRenderer)
        myMoPubAdapter.registerAdRenderer(facebookAdRenderer)
        myMoPubAdapter.registerAdRenderer(myRenderer)
        myMoPubAdapter.registerAdRenderer(pangleAdRenderer)

        recyclerView.adapter = myMoPubAdapter
        myMoPubAdapter.loadAds(twitter_native_ad_unit_id)
    }
}