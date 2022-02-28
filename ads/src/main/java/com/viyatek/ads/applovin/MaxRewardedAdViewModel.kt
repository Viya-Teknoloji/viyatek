package com.viyatek.ultimatefacts.ViewModels

import android.app.Activity
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.viyatek.ultimatefacts.Interfaces.GiveReward
import java.util.concurrent.TimeUnit

class MaxRewardedAdViewModel : ViewModel(){

    var access = false
    var retryAttempt = 0
    val theRewardedAd = MutableLiveData<MaxRewardedAd>()

    fun setTheRewardedAd(adUnitId : String = "96e7fa3b1abeb546", theActivity : Activity)
    {
        if(theRewardedAd.value == null) { theRewardedAd.value = MaxRewardedAd.getInstance(adUnitId, theActivity) }

        theRewardedAd.value?.setListener( object : MaxRewardedAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

                // Reset retry attempt
                retryAttempt = 0;
            }

            override fun onAdDisplayed(ad: MaxAd?) {

            }

            override fun onAdHidden(ad: MaxAd?) {
                theRewardedAd.value?.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                // Rewarded ad failed to load
                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                // Rewarded ad failed to load
                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                retryAttempt++
                val delayMillis = TimeUnit.SECONDS.toMillis(
                    Math.pow(2.0, Math.min(6, retryAttempt).toDouble())
                        .toLong()
                )

                Handler().postDelayed({ theRewardedAd.value?.loadAd() }, delayMillis)
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                theRewardedAd.value?.loadAd()
            }

            override fun onRewardedVideoStarted(ad: MaxAd?) {}

            override fun onRewardedVideoCompleted(ad: MaxAd?) {}

            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {}


        });
    }

    fun loadRewardedAd(adUnitId : String = "96e7fa3b1abeb546", theActivity: Activity)
    {
        setTheRewardedAd(theActivity = theActivity)
        theRewardedAd.value?.loadAd()
    }

    fun showRewardedAd(giveReward: GiveReward?)
    {
        theRewardedAd.value?.setListener( object : MaxRewardedAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

                // Reset retry attempt
                retryAttempt = 0;
            }

            override fun onAdDisplayed(ad: MaxAd?) {

            }

            override fun onAdHidden(ad: MaxAd?) {
                theRewardedAd.value?.loadAd()

                if(access)
                {
                    giveReward?.userEarnedReward()
                    access = false
                }
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                // Rewarded ad failed to load
                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                // Rewarded ad failed to load
                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                retryAttempt++
                val delayMillis = TimeUnit.SECONDS.toMillis(
                    Math.pow(2.0, Math.min(6, retryAttempt).toDouble())
                        .toLong()
                )

                Handler().postDelayed({ theRewardedAd.value?.loadAd() }, delayMillis)
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                theRewardedAd.value?.loadAd()
            }

            override fun onRewardedVideoStarted(ad: MaxAd?) {}

            override fun onRewardedVideoCompleted(ad: MaxAd?) {}

            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                access = true
            }


        });
        theRewardedAd.value?.let { if(it.isReady) it.showAd() }

    }
}