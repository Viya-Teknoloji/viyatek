package com.viyatek.billing.Campaign

import android.content.Context
import com.viyatek.billing.BillingPrefHandlers


class CampaignDialogHandler(
    val context: Context,
    val activeCampaign: CampaignType,
    private val remoteCampaignNo: Int,
    private val specialCampaignNo: Int,
    val isHome: Boolean
) {

    private val billingPrefsHandler by lazy { BillingPrefHandlers(context) }

    fun checkDialogShow(): Boolean {
        if (activeCampaign == CampaignType.NO_CAMPAIGN) return false

        if (activeCampaign == CampaignType.REMOTE_CAMPAIGN) {
            return if (isHome) {
                if (billingPrefsHandler.numberOfRemoteCampaignAppearedInHome() == remoteCampaignNo) {
                    false
                } else {
                    billingPrefsHandler.setRemoteCampaignAppearedInHome(remoteCampaignNo)
                    true
                }
            } else {
                true
            }
        }

        if (activeCampaign == CampaignType.SPECIAL_DAY_CAMPAIGN) {
            return if (isHome) {
                if (billingPrefsHandler.numberOfSpecialCampaignAppearedInHome() == specialCampaignNo) {
                    false
                } else {
                    billingPrefsHandler.setSpecialCampaignAppearedInHome(specialCampaignNo)
                    true
                }
            } else {
                true
            }
        }

        if (activeCampaign == CampaignType.LOCAL_CAMPAIGN) {
            return if (isHome) {

                if (billingPrefsHandler.numberOfLocalCampaignAppearedInHome() == billingPrefsHandler.getLocalCampaignNumber()) {
                    false
                } else {
                    billingPrefsHandler.setLocalCampaignAppearedInHome(
                        billingPrefsHandler.getLocalCampaignNumber()
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