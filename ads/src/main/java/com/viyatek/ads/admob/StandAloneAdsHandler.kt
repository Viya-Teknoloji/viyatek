package com.viyatek.ads.admob

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.*

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

import com.viyatek.ads.databinding.ArticleAdBinding
import com.viyatek.ads.databinding.SecondVersionAdmobStandloneFeedBinding
import com.viyatek.ads.interfaces.AdLoaderInterface
import com.viyatek.ads.interfaces.AdMobAdListener

class StandAloneAdsHandler(
    private val theContext: Context,
    nativeAdID: String,
    private val container: FrameLayout,
    private val adMobAdListener: AdMobAdListener? = null,
    private val size : StandAloneAdSize = StandAloneAdSize.SMALL,
    private val showCTA : Boolean = true
) {

    private var adLoader: AdLoader? = null
    private var adloaded = false

    private val videoOptions: VideoOptions = VideoOptions.Builder()
        .setStartMuted(true)
        .build()

    private val adOptions: NativeAdOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .setRequestMultipleImages(true)
        .build()


    private val builder = AdLoader.Builder(theContext, nativeAdID)


    fun loadAd()
    {
        Log.d("Ads","AdMob Ad Loading" )

        adLoader =  builder.forNativeAd {
            // and if so, insert the ads into the list.
            Log.d("Ads","AdMob Ad Loaded" )
            if (!adLoader?.isLoading!!) {
                adloaded = true
                implementAd(container, it)
            }
            else
            {
                Log.d("Ads","AdMob Ad is Still Loading" )
            }

        }.withAdListener(object  : AdListener()
        {

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d("Ads","AdMob Ad is closed" )
            }


            override fun onAdOpened() {
                super.onAdOpened()
                Log.d("Ads","AdMob Ad is opened" )
            }

            override fun onAdLoaded() {
                Log.d("Ads","AdMob Ad is Loaded" )
                super.onAdLoaded()


                if(adMobAdListener!=null && adMobAdListener is AdLoaderInterface)
                {
                    adMobAdListener.onAdLoaded()
                }
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("Ads","AdMob Ad is Clicked" )
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d("Ads","AdMob Ad IImpression" )
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)

                Log.d("Ads","AdMob Ad Load Failed here ${adError.message}" )

                adMobAdListener?.adFailedToLoad(adError)
            }
        })
            .withNativeAdOptions(adOptions)
            .build()

        adLoader?.loadAd(AdRequest.Builder().build())

    }

    private fun implementAd(container: FrameLayout, nativeAd: NativeAd) {


        Log.d("Ads","AdMob Ad Implementing" )

        if(size == StandAloneAdSize.SMALL)
        {
            val articleAdView = ArticleAdBinding.inflate(LayoutInflater.from(theContext), container, false)
            populateUnifiedNativeAdView(nativeAd,  articleAdView)

            container.apply {
                removeAllViews()
                addView(articleAdView.root)
            }
        }
        else if (size == StandAloneAdSize.MIDDLE)
        {
            val secondVersionAdmobStandAlone = SecondVersionAdmobStandloneFeedBinding.inflate(LayoutInflater.from(theContext), container, false)
            populateUnifiedNativeAdView(nativeAd,  secondVersionAdmobStandAlone)

            container.apply {
                removeAllViews()
                addView(secondVersionAdmobStandAlone.root)
            }
        }

    }

    private fun populateUnifiedNativeAdView(
        nativeAd: NativeAd?,
        articleAdView : ArticleAdBinding
    ) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.

        nativeAd?.mediaContent?.let { articleAdView.lockScreenAdImage.setMediaContent(it) }
        articleAdView.lockScreenAdImage.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        articleAdView.articleAdLayout.mediaView = articleAdView.lockScreenAdImage

        // Set other ad assets.
        articleAdView.articleAdLayout.bodyView = articleAdView.lockScreenAdText
        articleAdView.articleAdLayout.callToActionView = articleAdView.lockScreenAdButton
        articleAdView.articleAdLayout.headlineView = articleAdView.lockScreenAdHeadline



        articleAdView.lockScreenAdHeadline.text = nativeAd?.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd?.body == null) {
            articleAdView.articleAdLayout.bodyView?.visibility = View.GONE
        } else {
            articleAdView.articleAdLayout.bodyView?.visibility = View.VISIBLE
            (articleAdView.articleAdLayout.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd?.callToAction == null) {
            articleAdView.articleAdLayout.callToActionView?.visibility = View.GONE
        } else {

            articleAdView.articleAdLayout.callToActionView?.visibility = View.VISIBLE

            (articleAdView.articleAdLayout.callToActionView as Button).text = nativeAd.callToAction
        }

        nativeAd?.let { articleAdView.articleAdLayout.setNativeAd(it) }

    }

    private fun populateUnifiedNativeAdView(
        nativeAd: NativeAd?,
        secondVersionAdmobStandAloneFeedBinding: SecondVersionAdmobStandloneFeedBinding
    ) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.

        nativeAd?.mediaContent?.let { secondVersionAdmobStandAloneFeedBinding.mediaView.setMediaContent(it) }
        secondVersionAdmobStandAloneFeedBinding.mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        secondVersionAdmobStandAloneFeedBinding.nativeAdView.mediaView = secondVersionAdmobStandAloneFeedBinding.mediaView

        // Set other ad assets.

        secondVersionAdmobStandAloneFeedBinding.nativeAdView.apply {
            bodyView = secondVersionAdmobStandAloneFeedBinding.body
            callToActionView = secondVersionAdmobStandAloneFeedBinding.cta
            headlineView = secondVersionAdmobStandAloneFeedBinding.primary
            iconView = secondVersionAdmobStandAloneFeedBinding.icon
        }


        secondVersionAdmobStandAloneFeedBinding.primary.text = nativeAd?.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd?.body == null) {
            secondVersionAdmobStandAloneFeedBinding.body.visibility = View.GONE
        } else {
            secondVersionAdmobStandAloneFeedBinding.body.visibility = View.VISIBLE
            secondVersionAdmobStandAloneFeedBinding.body.text = nativeAd.body
        }
        if (nativeAd?.callToAction == null) {
            secondVersionAdmobStandAloneFeedBinding.cta.visibility = View.GONE
        } else {
            if(showCTA) secondVersionAdmobStandAloneFeedBinding.cta.visibility = View.VISIBLE
            secondVersionAdmobStandAloneFeedBinding.cta.text = nativeAd.callToAction
        }

        nativeAd?.let { secondVersionAdmobStandAloneFeedBinding.nativeAdView.setNativeAd(it) }

    }


}