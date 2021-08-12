package com.viyatek.ads.mopub

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.mopub.nativeads.*
import com.viyatek.ads.R

class NativeAdGeneration {

    class HandleNativeReminderMoPubAdGeneration(private val localExtras: MutableMap<String, Any>, private val activity: Activity, private val adUnitId: String) {

        private var moPubNativeAd: NativeAd? = null
        private var adapterHelper: AdapterHelper? = null

        fun createMoPubNative(frameLayout: FrameLayout) {
            localExtras[GooglePlayServicesNative.KEY_EXTRA_AD_CHOICES_PLACEMENT] = NativeAdOptions.ADCHOICES_BOTTOM_RIGHT
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
            val moPubNativeNetworkListener: MoPubNative.MoPubNativeNetworkListener = object : MoPubNative.MoPubNativeNetworkListener {
                override fun onNativeLoad(nativeAd: NativeAd?) {
                    Log.d(MoPubInitializer.LOG_TAG, "Native ad has loaded.")
                    if (moPubNativeAd != null) { moPubNativeAd!!.destroy() }
                    moPubNativeAd = nativeAd

                    // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                    val v = adapterHelper?.getAdView(null, null, moPubNativeAd, viewBinder)
                    // Set the native event listeners (onImpression, and onClick).
                    moPubNativeAd?.setMoPubNativeEventListener(object : NativeAd.MoPubNativeEventListener {
                        override fun onImpression(view: View?) {}
                        override fun onClick(view: View?) {}
                    })


                    // Add the ad view to our view hierarchy
                    frameLayout.addView(v)
                }

                override fun onNativeFail(errorCode: NativeErrorCode?) {

                    if (errorCode != NativeErrorCode.CONNECTION_ERROR) { nativeAdFailedWithInternet() }
                    else { nativeAdFailedWithoutInternet() }
                }
            }
            val moPubNative = MoPubNative(activity, adUnitId, moPubNativeNetworkListener)
            moPubNative.setLocalExtras(localExtras)

            val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(viewBinder)

            val googlePlayServicesAdRenderer = GooglePlayServicesAdRenderer(
                GooglePlayServicesViewBinder.Builder(R.layout.admob_lock_screen_end_ad)
                    .mediaLayoutId(R.id.admob_media_layout) // bind to your `com.mopub.nativeads.MediaLayout` element
                    .titleId(R.id.tw_lock_screen_ad_headline)
                    .textId(R.id.tw_lock_screen_ad_text)
                    .callToActionId(R.id.tw_lock_screen_ad_button)
                    .privacyInformationIconImageId(R.id.ad_choices_overlay)
                    .build())

            moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer)
            moPubNative.registerAdRenderer(googlePlayServicesAdRenderer)
            moPubNative.makeRequest()

            adapterHelper = AdapterHelper(activity, 0, 5)
        }

        private fun nativeAdFailedWithoutInternet() {}
        private fun nativeAdFailedWithInternet() {}
    }
}