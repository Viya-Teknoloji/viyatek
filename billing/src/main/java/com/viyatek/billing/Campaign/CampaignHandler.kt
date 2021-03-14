package com.viyatek.billing.Campaign
/*
Created By Eren Tüfekçi
*/
import android.content.Context
import android.util.Log
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.CAMPAIGN_START_TIME
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.LOCAL_CAMPAIGN_ACTIVE
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.PREMIUM
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.REMOTE_CAMPAIGN_ACTIVE
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SPECIAL_DAY_CAMPAIGN_ACTIVE
import com.viyatek.billing.PrefHandlers.ViyatekKotlinSharedPrefHelper.Companion.SUBSCRIBED

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
    private val viyatekSharedPrefsHandler by lazy { ViyatekKotlinSharedPrefHelper(context) }
    private val isPremium by lazy {
        viyatekSharedPrefsHandler.getPref(PREMIUM)
            .getIntegerValue() == 1 || viyatekSharedPrefsHandler.getPref(SUBSCRIBED)
            .getIntegerValue() == 1
    }

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

        setActiveCampaign(0, 0, 0)

        if (isPremium) {
            Log.d("Bargain", "No Campaign is set Because the user is premium")
            return
        }

        campaignType = if (isRemoteCampaignEnabled) {

            if (checkCampaignActive(startDate, duration)) {
                setActiveCampaign(1, 0, 0)
                CampaignType.REMOTE_CAMPAIGN
            } else {
                setActiveCampaign(0, 0, 0)
                CampaignType.NO_CAMPAIGN
            }
        } else {
            Log.d(ViyatekPremiumActivity.billingLogs, "Remote Campaign is off because of remote")
            CampaignType.NO_CAMPAIGN
        }

        if (campaignType == CampaignType.NO_CAMPAIGN) {
            if (isSpecialCampaignEnabled) {
                if (checkCampaignActive(startDate, duration)) {
                    setActiveCampaign(0, 1, 0)
                    campaignType = CampaignType.SPECIAL_DAY_CAMPAIGN
                    Log.d(ViyatekPremiumActivity.billingLogs, "Special Day Campaign is Active ")
                } else {
                    setActiveCampaign(0, 0, 0)
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
        Log.d(ViyatekPremiumActivity.billingLogs, "Set Campaign ${isLocalCampaignEnabled}")


        if (campaignType == CampaignType.NO_CAMPAIGN) {
            if (isLocalCampaignEnabled) {

                Log.d(
                    ViyatekPremiumActivity.billingLogs,
                    "Active Campaign in Local ${
                        viyatekSharedPrefsHandler.getPref(CAMPAIGN_START_TIME).getStringValue()
                    }"
                )
                campaignType =
                    if (checkCampaignActive(
                            viyatekSharedPrefsHandler.getPref(CAMPAIGN_START_TIME).getStringValue()
                                .toLong(), localCampaignDuration
                        )
                    ) {
                        Log.d("Local_Promotion", "Local Campaign is active")
                        setActiveCampaign(0, 0, 1)
                        CampaignType.LOCAL_CAMPAIGN
                    } else {
                        setActiveCampaign(0, 0, 0)
                        CampaignType.NO_CAMPAIGN
                    }
            }
        }

    }

    private fun setActiveCampaign(
        isRemoteCampaignActive: Int,
        isSpecialDayCampaignActive: Int,
        isLocalCampaignActive: Int
    ) {
        viyatekSharedPrefsHandler.applyPrefs(REMOTE_CAMPAIGN_ACTIVE, isRemoteCampaignActive)
        viyatekSharedPrefsHandler.applyPrefs(
            SPECIAL_DAY_CAMPAIGN_ACTIVE,
            isSpecialDayCampaignActive
        )
        viyatekSharedPrefsHandler.applyPrefs(LOCAL_CAMPAIGN_ACTIVE, isLocalCampaignActive)
    }


    fun getActiveCampaign(): CampaignType {

        return when {
            viyatekSharedPrefsHandler.getPref(REMOTE_CAMPAIGN_ACTIVE).getIntegerValue() == 1 -> {
                CampaignType.REMOTE_CAMPAIGN
            }
            viyatekSharedPrefsHandler.getPref(SPECIAL_DAY_CAMPAIGN_ACTIVE)
                .getIntegerValue() == 1 -> {
                CampaignType.SPECIAL_DAY_CAMPAIGN
            }
            viyatekSharedPrefsHandler.getPref(LOCAL_CAMPAIGN_ACTIVE).getIntegerValue() == 1 -> {
                CampaignType.LOCAL_CAMPAIGN
            }
            else -> CampaignType.NO_CAMPAIGN
        }
    }

    fun startLocalCampaign() {
        Log.d(
            "Billing",
            "Starting a local campaign ${System.currentTimeMillis()} and local campaign no : ${
                viyatekSharedPrefsHandler.getPref(ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_NO)
                    .getIntegerValue()
            } "
        )

        viyatekSharedPrefsHandler.applyPrefs(
            CAMPAIGN_START_TIME,
            System.currentTimeMillis().toString()
        )

        Log.d(
            "Billing",
            "Campaign Start Time ${
                viyatekSharedPrefsHandler.getPref(ViyatekKotlinSharedPrefHelper.CAMPAIGN_START_TIME)
                    .getStringValue()
            }"
        )
        viyatekSharedPrefsHandler.applyPrefs(
            ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_NO,
            viyatekSharedPrefsHandler.getPref(ViyatekKotlinSharedPrefHelper.LOCAL_CAMPAIGN_NO)
                .getIntegerValue() + 1
        )

        setActiveCampaign(0, 0, 1)
    }

}