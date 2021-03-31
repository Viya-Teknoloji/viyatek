package com.viyatek.inappreward

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.viyatek.inappreward.databinding.BaseTopPrefBinding
import kotlin.random.Random

class InAppRewardHandler(private val context: Context, private val setSettingsHeader: setSettingsHeader? = null) {

    private val baseTopPrefBinding by lazy { BaseTopPrefBinding.inflate(LayoutInflater.from(context)) }
    private val inAppPrefsManager by lazy { InAppPrefsManager(context) }

    fun checkInAppReward()
    {
        if(!inAppPrefsManager.isRewardedShareMade())
        {
           setSettingsHeader?.setHeader(R.layout.base_top_pref,
                ContextCompat.getDrawable(context, R.drawable.share_girl)!!,
                    context.getString(R.string.invite_friends_text),
                    context.getString(R.string.invite_title),
                    HeaderType.REWARDED_SHARE_NOT_MADE)
        }
        else if(inAppPrefsManager.isRewardedShareMade() && !inAppPrefsManager.isShareRewarded())
        {
            setSettingsHeader?.setHeader(R.layout.base_top_pref,
                ContextCompat.getDrawable(context, R.drawable.share_girl)!!,
                context.getString(R.string.reward_friends_text),
                context.getString(R.string.reward_title),
                HeaderType.CLAIM_REWARD)
        }

        else
        {
            val mRandom = Random.nextFloat()

            if(mRandom < 0.5f)
            {
                setSettingsHeader?.setHeader(R.layout.base_top_pref,
                ContextCompat.getDrawable(context, R.drawable.share_girl)!!,
                context.getString(R.string.no_reward_title),
                context.getString(R.string.invite_friends_text),
                HeaderType.SHARING_CARING)
            }
            else
            {
                setSettingsHeader?.setHeader(R.layout.base_top_pref,
                ContextCompat.getDrawable(context, R.drawable.unlock_premium)!!,
                context.getString(R.string.unlock_all_title),
                context.getString(R.string.unlock_all_text),
                HeaderType.PREMIUM)
            }
        }
    }
}