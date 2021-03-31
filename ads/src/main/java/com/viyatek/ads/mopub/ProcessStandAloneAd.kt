package com.viyatek.ads.mopub

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout

import com.mopub.nativeads.*
import com.mopub.nativeads.FacebookAdRenderer.FacebookViewBinder
import com.mopub.nativeads.MoPubNative.MoPubNativeNetworkListener
import com.mopub.nativeads.NativeAd.MoPubNativeEventListener
import com.viyatek.ads.R

class ProcessStandAloneAd(
    val context: Context,
    val adUnitId: String,
    val iMoPubFailListeners: IMoPubFailListeners
) {

    var moPubNativeAd: NativeAd? = null
    var adapterHelper: AdapterHelper? = null

    fun createMoPubNative(frameLayout: FrameLayout) {

        val viewBinder = ViewBinder.Builder(R.layout.tw_lock_screen_end_ad)
            .mainImageId(R.id.tw_lock_screen_ad_image)
            .titleId(R.id.tw_lock_screen_ad_headline)
            .textId(R.id.tw_lock_screen_ad_text)
            .callToActionId(R.id.tw_lock_screen_ad_button)
            .privacyInformationIconImageId(R.id.ad_choices_overlay)
            .build()

        // Retrieve the pre-built ad view that AdapterHelper prepared for us.
        // Set the native event listeners (onImpression, and onClick).
        // ((LockScreenActivity) mCtxt).RequestToOpenLockScreen();
        // Add the ad view to our view hierarchy
        val moPubNativeNetworkListener: MoPubNativeNetworkListener =
            object : MoPubNativeNetworkListener {
                override fun onNativeLoad(nativeAd: NativeAd) {
                    Log.d("MoPub", "Native ad has loaded.")
                    if (moPubNativeAd != null) { moPubNativeAd?.destroy()}
                    moPubNativeAd = nativeAd

                    // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                    val v: View? = adapterHelper?.getAdView(null, null, moPubNativeAd, viewBinder)
                    // Set the native event listeners (onImpression, and onClick).

                    moPubNativeAd?.setMoPubNativeEventListener(object : MoPubNativeEventListener {
                        override fun onImpression(view: View?) {}

                        override fun onClick(view: View?) {}
                    })

                    // Add the ad view to our view hierarchy
                    frameLayout.addView(v)
                }

                override fun onNativeFail(errorCode: NativeErrorCode) {

                    if (errorCode != NativeErrorCode.CONNECTION_ERROR) {
                        iMoPubFailListeners.failedWithInternet(errorCode)
                    } else {
                        iMoPubFailListeners.failedWithoutInternet()
                    }
                }
            }

        val moPubNative = MoPubNative(context, adUnitId, moPubNativeNetworkListener)

        val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(viewBinder)

        Log.d("Ads", "Handling the ads")

        val googlePlayServicesAdRenderer = GooglePlayServicesAdRenderer(
            GooglePlayServicesViewBinder.Builder(R.layout.admob_lock_screen_end_ad)
                .mediaLayoutId(R.id.admob_media_layout) // bind to your `com.mopub.nativeads.MediaLayout` element
                .titleId(R.id.tw_lock_screen_ad_headline)
                .textId(R.id.tw_lock_screen_ad_text)
                .callToActionId(R.id.tw_lock_screen_ad_button)
                .privacyInformationIconImageId(R.id.ad_choices_overlay)
                .build())

        val facebookAdRenderer = FacebookAdRenderer(
            FacebookViewBinder.Builder(R.layout.facebook_lock_screen_end_ad)
                .titleId(R.id.tw_lock_screen_ad_headline)
                .textId(R.id.tw_lock_screen_ad_text) // Binding to new layouts from Facebook 4.99.0+
                .mediaViewId(R.id.facebook_media_layout)
                .adChoicesRelativeLayoutId(R.id.ad_choices_overlay) // End of binding to new layouts
                .callToActionId(R.id.tw_lock_screen_ad_button)
                .build()
        )

        val pangleAdRenderer = PangleAdRenderer(
            PangleAdViewBinder.Builder(R.layout.pangle_lock_screen_end_ad)
                .callToActionId(R.id.tw_lock_screen_ad_button)
                .decriptionTextId(R.id.tw_lock_screen_ad_text) //.iconImageId(R.id.native_icon_image)
                .titleId(R.id.tw_lock_screen_ad_headline)
                .mediaViewIdId(R.id.pangle_media) // Bind to <com.bytedance.sdk.openadsdk.adapter.MediaView /> in XML
                .build()
        )

        moPubNative.registerAdRenderer(facebookAdRenderer)
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer)
        moPubNative.registerAdRenderer(googlePlayServicesAdRenderer)
        moPubNative.registerAdRenderer(pangleAdRenderer)
        moPubNative.makeRequest()

        adapterHelper = AdapterHelper(context, 0, 5)
    }

}