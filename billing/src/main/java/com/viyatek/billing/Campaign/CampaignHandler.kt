package com.viyatek.billing.Campaign
/*
Created By Eren Tüfekçi
*/
import android.content.Context
import android.util.Log
import com.viyatek.billing.BillingPrefHandlers


import com.viyatek.billing.PremiumActivity.ViyatekPremiumActivity


class CampaignHandler(
    private val context: Context,
    private val isRemoteCampaignEnabled: Boolean = true,
    private val isSpecialCampaignEnabled: Boolean = true,
    private val isLocalCampaignEnabled: Boolean = true,
    private val startDate: Long = 0L,
    private val duration: Long = 0L,
    private val localCampaignDuration: Long = 0L
) {

    private var campaignType: CampaignType = CampaignType.NO_CAMPAIGN
    private val billingPrefHandlers by lazy { BillingPrefHandlers(context) }
    private val isPremium by lazy { billingPrefHandlers.isPremium() || billingPrefHandlers.isSubscribed() }

    private fun checkCampaignActive(startDate: Long, duration: Long): Boolean {

        val endDate = startDate + duration
        Log.d("Bargain", "end date : $endDate")
        Log.d("Bargain", "dtart date : $startDate")

        val currentTime = System.currentTimeMillis()

        if (currentTime < startDate) {
            Log.d("Bargain", "False Because start date is greater then current date")
            return false
        }

        if (endDate < currentTime) Log.d(
            "Bargain",
            "False Because end date is smaller then current date"
        )

        return endDate > currentTime
    }

    fun setCampaign() {

        Log.d("Bargain", "Setting Active Campaign")

        setActiveCampaign(
            isRemoteCampaignActive = false,
            isSpecialDayCampaignActive = false,
            isLocalCampaignActive = false
        )

        if (isPremium) {
            Log.d("Bargain", "No Campaign is set Because the user is premium")
            return
        }

        campaignType = if (isRemoteCampaignEnabled) {

            if (checkCampaignActive(startDate, duration)) {
                setActiveCampaign(
                    isRemoteCampaignActive = true,
                    isSpecialDayCampaignActive = false,
                    isLocalCampaignActive = false
                )
                CampaignType.REMOTE_CAMPAIGN
            } else {
                setActiveCampaign(
                    isRemoteCampaignActive = false,
                    isSpecialDayCampaignActive = false,
                    isLocalCampaignActive = false
                )
                CampaignType.NO_CAMPAIGN
            }
        } else {
            Log.d(ViyatekPremiumActivity.billingLogs, "Remote Campaign is off because of remote")
            CampaignType.NO_CAMPAIGN
        }

        if (campaignType == CampaignType.NO_CAMPAIGN) {
            if (isSpecialCampaignEnabled) {
                if (checkCampaignActive(startDate, duration)) {
                    setActiveCampaign(
                        isRemoteCampaignActive = false,
                        isSpecialDayCampaignActive = true,
                        isLocalCampaignActive = false
                    )
                    campaignType = CampaignType.SPECIAL_DAY_CAMPAIGN
                    Log.d(ViyatekPremiumActivity.billingLogs, "Special Day Campaign is Active ")
                } else {
                    setActiveCampaign(
                        isRemoteCampaignActive = false,
                        isSpecialDayCampaignActive = false,
                        isLocalCampaignActive = false
                    )
                    Log.d(
                        ViyatekPremiumActivity.billingLogs,
                        "Special Day Campaign off out of time limit "
                    )
                    campaignType = CampaignType.NO_CAMPAIGN
                }
            } else {
                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "Special Day Campaign is off because of remote"
                )
            }
        }
        Log.d(ViyatekPremiumActivity.billingLogs, "Set Campaign $isLocalCampaignEnabled")


        if (campaignType == CampaignType.NO_CAMPAIGN) {
            if (isLocalCampaignEnabled) {

                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "Active Campaign in Local ${billingPrefHandlers.getCampaignStartTime()}"
                )
                campaignType =
                    if (checkCampaignActive(
                       billingPrefHandlers.getCampaignStartTime(), localCampaignDuration
                        )
                    ) {
                        Log.d("Local_Promotion", "Local Campaign is active")
                        setActiveCampaign(
                            isRemoteCampaignActive = false,
                            isSpecialDayCampaignActive = false,
                            isLocalCampaignActive = true
                        )
                        CampaignType.LOCAL_CAMPAIGN
                    } else {
                        setActiveCampaign(
                            isRemoteCampaignActive = false,
                            isSpecialDayCampaignActive = false,
                            isLocalCampaignActive = false
                        )
                        CampaignType.NO_CAMPAIGN
                    }
            }
        }

    }

    private fun setActiveCampaign(
        isRemoteCampaignActive: Boolean,
        isSpecialDayCampaignActive: Boolean,
        isLocalCampaignActive: Boolean
    ) {
       billingPrefHandlers.setRemoteCampaignActive(isRemoteCampaignActive)
        billingPrefHandlers.setSpecialDayCampaignActive(isSpecialDayCampaignActive)
        billingPrefHandlers.setLocalCampaignActive(isLocalCampaignActive)
    }


    fun getActiveCampaign(): CampaignType {

        return when {
            billingPrefHandlers.isRemoteCampaignActive() -> { CampaignType.REMOTE_CAMPAIGN }
            billingPrefHandlers.isSpecialDayCampaignActive()-> { CampaignType.SPECIAL_DAY_CAMPAIGN }
            billingPrefHandlers.isLocalCampaignActive() -> { CampaignType.LOCAL_CAMPAIGN }
            else -> CampaignType.NO_CAMPAIGN
        }
    }

    fun startLocalCampaign() {
        Log.d(
            "Billing",
            "Starting a local campaign ${System.currentTimeMillis()} and local campaign no : ${
                billingPrefHandlers.getLocalCampaignNumber()
            } "
        )

        billingPrefHandlers.setCampaignStartTime(System.currentTimeMillis())


        Log.d(
            "Billing",
            "Campaign Start Time ${
             billingPrefHandlers.getCampaignStartTime()
            }"
        )
        billingPrefHandlers.setLocalCampaignNumber(billingPrefHandlers.getLocalCampaignNumber() + 1)


        setActiveCampaign(
            isRemoteCampaignActive = false,
            isSpecialDayCampaignActive = false,
            isLocalCampaignActive = true
        )
    }

}