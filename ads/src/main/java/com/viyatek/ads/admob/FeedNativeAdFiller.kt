package com.viyatek.ads.admob

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FeedNativeAdFiller {

        fun handle(nativeAd: NativeAd, adView: NativeAdView) {

            adView.headlineView?.apply { (this as TextView).text = nativeAd.headline }
            adView.bodyView?.apply {
                if (nativeAd.body != null) { (this as TextView).text = nativeAd.body }
                else { this.visibility = View.GONE }
            }
            adView.callToActionView?.apply {
                if(nativeAd.callToAction != null) {(this as Button).text = nativeAd.callToAction }
                else {this.visibility = View.GONE}
             }
            adView.storeView?.apply {
                if(nativeAd.store != null)
                {   (this as TextView).text = nativeAd.store }
                else
                { this.visibility = View.GONE}
             }
            adView.advertiserView?.apply {
                if(nativeAd.advertiser != null)
                {(this as TextView).text = nativeAd.advertiser}
                else
                { this.visibility = View.GONE }
                 }
            adView.priceView?.apply {
                if(nativeAd.price != null)
                {(this as TextView).text = nativeAd.price }
                else
                { this.visibility = View.GONE }
                }
            adView.iconView?.apply {
                if(nativeAd.icon != null)
                { (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable) }
                else
                {
                    this.visibility = View.GONE
                }
            }
            adView.starRatingView?.apply {
                if(nativeAd.starRating != null)
                {    (this as RatingBar ).rating = nativeAd.starRating!!.toFloat()}
                else
                {
                    this.visibility = View.GONE
                }
            }

            nativeAd.mediaContent?.apply { adView.mediaView?.setMediaContent(this)}
            adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

            adView.setNativeAd(nativeAd)
        }
}