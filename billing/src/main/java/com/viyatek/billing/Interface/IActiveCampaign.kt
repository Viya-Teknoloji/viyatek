package com.viyatek.billing.Interface

import com.viyatek.billing.Campaign.CampaignType

interface IActiveCampaign {
    fun getActiveCampaign(campaignType: CampaignType)
}