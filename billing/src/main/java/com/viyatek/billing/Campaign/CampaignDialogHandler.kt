package com.viyatek.billing.Campaign

import android.content.Context
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.LOCAL_CAMPAIGN_APPEARED_IN_HOME

class CampaignDialogHandler(
    val context: Context,
    val activeCampaign: CampaignType,
    private val remoteCampaignNo: Int,
    private val specialCampaignNo: Int,
    val isHome: Boolean
) {

    private val viyatekKotlinSharedPrefHelper by lazy { ViyatekKotlinSharedPrefHelper(context) }

    fun checkDialogShow(): Boolean {
        if (activeCampaign == CampaignType.NO_CAMPAIGN) return false

        if (activeCampaign == CampaignType.REMOTE_CAMPAIGN) {
            return if (isHome) {
                if (viyatekKotlinSharedPrefHelper.getPref(ViyatekKotlinSharedPrefHelper.REMOTE_CAMPAIGN_APPEARED_IN_HOME)
                        .getIntegerValue() == remoteCampaignNo
                ) {
                    false
                } else {
                    viyatekKotlinSharedPrefHelper.applyPrefs(
                        ViyatekKotlinSharedPrefHelper.REMOTE_CAMPAIGN_APPEARED_IN_HOME,
                        remoteCampaignNo
                    )
                    true
                }
            } else {
                true
            }
        }

        if (activeCampaign == CampaignType.SPECIAL_DAY_CAMPAIGN) {
            return if (isHome) {
                if (viyatekKotlinSharedPrefHelper.getPref(ViyatekKotlinSharedPrefHelper.SPECIAL_DAY_CAMPAIGN_APPEARED_IN_HOME)
                        .getIntegerValue() == specialCampaignNo
                ) {
                    false
                } else {
                    viyatekKotlinSharedPrefHelper.applyPrefs(
                        ViyatekKotlinSharedPrefHelper.SPECIAL_DAY_CAMPAIGN_APPEARED_IN_HOME,
                        specialCampaignNo
                    )
                    true
                }
            } else {
                true
            }
        }

        if (activeCampaign == CampaignType.LOCAL_CAMPAIGN) {
            return if (isHome) {

                if (viyatekKotlinSharedPrefHelper.getPref(LOCAL_CAMPAIGN_APPEARED_IN_HOME)
                        .getIntegerValue() ==
                    viyatekKotlinSharedPrefHelper.getPref(ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_NO)
                        .getIntegerValue()
                ) {
                    false
                } else {
                    viyatekKotlinSharedPrefHelper.applyPrefs(
                        ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_APPEARED_IN_HOME,
                        viyatekKotlinSharedPrefHelper.getPref(ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_NO)
                            .getIntegerValue()
                    )

                    true
                }
            } else {
                true
            }
        }

        return false
    }
}