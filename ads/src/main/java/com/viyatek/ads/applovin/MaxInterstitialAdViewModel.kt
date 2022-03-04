package com.viyatek.ultimatefacts.ViewModels

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.viyatek.ultimatefacts.Interfaces.OnInterstitialAdHidden

import java.util.concurrent.TimeUnit
import kotlin.math.pow

class MaxInterstitialAdViewModel : ViewModel(){

    var retryAttempt = 0
    val theInterstitialAd = MutableLiveData<MaxInterstitialAd>()

    fun setTheInterstitialAd(adUnitId : String = "91a7a38265d70a40", theActivity : Activity)
    {
        if(theInterstitialAd.value == null) { theInterstitialAd.value = MaxInterstitialAd(adUnitId, theActivity) }

        theInterstitialAd.value?.setListener( object : MaxAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttempt = 0;
            }

            override fun onAdDisplayed(ad: MaxAd?) {}

            override fun onAdHidden(ad: MaxAd?) {
                theInterstitialAd.value?.loadAd();
            }

            override fun onAdClicked(ad: MaxAd?) {}

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                // Interstitial ad failed to load
                // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                // Interstitial ad failed to load
                // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                retryAttempt++
                val delayMillis: Long = TimeUnit.SECONDS.toMillis(
                    2.0.pow(6.coerceAtMost(retryAttempt).toDouble())
                        .toLong()
                )

                Handler(Looper.getMainLooper()).postDelayed(Runnable {theInterstitialAd.value?.loadAd() }, delayMillis)
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
                theInterstitialAd.value?.loadAd();
            }

        });
    }

    fun loadInterstitialAd(adUnitId : String = "91a7a38265d70a40", theActivity: Activity)
    {
        setTheInterstitialAd(theActivity = theActivity)
        theInterstitialAd.value?.loadAd()
    }

    fun showInterstitialAd(onInterstitialAdHidden: OnInterstitialAdHidden?)
    {
        theInterstitialAd.value?.setListener( object : MaxAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttempt = 0;
                Log.d("onAdLoaded_Applovin","$ad")
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                Log.d("onAdDisplayed_Applovin","$ad")
            }

            override fun onAdHidden(ad: MaxAd?) {
                theInterstitialAd.value?.loadAd();
                onInterstitialAdHidden?.onInterstitialDismissed()
                Log.d("onAdHidden_Applovin","$ad")
            }

            override fun onAdClicked(ad: MaxAd?) {
                Log.d("onAdClicked_Applovin","$ad")
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                // Interstitial ad failed to load
                // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                // Interstitial ad failed to load
                // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                retryAttempt++
                val delayMillis: Long = TimeUnit.SECONDS.toMillis(
                    2.0.pow(6.coerceAtMost(retryAttempt).toDouble())
                        .toLong()
                )

                Handler(Looper.getMainLooper()).postDelayed(Runnable {theInterstitialAd.value?.loadAd() }, delayMillis)
                Log.d("onAdLoadFailed_Applovin","$error")
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
                theInterstitialAd.value?.loadAd();
                Log.d("onAdDisplayFailed_Applo","$error")
            }

        });
        theInterstitialAd.value?.let { if(it.isReady) it.showAd() }

    }
}