package com.viyatek.inappads

import android.content.Context
import com.viyatek.inappads.ViyatekInAppStatics.BASKET_BUSTERS_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.BOTTLE_CAP_CHALLANGE_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.COLOR_UP_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.FACE_FIND_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.FACTS_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.MOTILIFE_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.QUOTES_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.ULTIMATE_FACTS_TWITTER_AD_CLICKED
import com.viyatek.inappads.ViyatekInAppStatics.ULTIMATE_QUOTES_INSTAGRAM_AD_CLICKED
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class ViyatekInAppPrefHandlers(private val context: Context) {

    private val viyatekInAppPrefHandlers by lazy { ViyatekSharedPrefsHandler(context, ViyatekInAppStatics.InAppAdsPrefs) }

    fun isBasketBustersAdClicked() = viyatekInAppPrefHandlers.getBooleanValue(BASKET_BUSTERS_AD_CLICKED, false)
    fun setBasketBustersAdClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(BASKET_BUSTERS_AD_CLICKED, isClicked)

    fun isColorUpAdClicked() = viyatekInAppPrefHandlers.getBooleanValue(COLOR_UP_AD_CLICKED, false)
    fun setColorUpAdClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(COLOR_UP_AD_CLICKED, isClicked)

    fun isBottleCapChallangeAdClicked() = viyatekInAppPrefHandlers.getBooleanValue(BOTTLE_CAP_CHALLANGE_AD_CLICKED, false)
    fun setBottleCapChallangeAdClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(BOTTLE_CAP_CHALLANGE_AD_CLICKED, isClicked)

    fun isUqInstagramClicked() = viyatekInAppPrefHandlers.getBooleanValue(ULTIMATE_QUOTES_INSTAGRAM_AD_CLICKED, false)
    fun setUqInstagramClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(ULTIMATE_QUOTES_INSTAGRAM_AD_CLICKED, isClicked)

    fun isUqClicked() = viyatekInAppPrefHandlers.getBooleanValue(QUOTES_AD_CLICKED, false)
    fun setUqClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(QUOTES_AD_CLICKED, isClicked)

    fun isFactClicked() = viyatekInAppPrefHandlers.getBooleanValue(FACTS_AD_CLICKED, false)
    fun setFactClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(FACTS_AD_CLICKED, isClicked)

    fun isMotilifeClicked() = viyatekInAppPrefHandlers.getBooleanValue(MOTILIFE_AD_CLICKED, false)
    fun setMotilifeClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(MOTILIFE_AD_CLICKED, isClicked)

    fun faceFindClicked() = viyatekInAppPrefHandlers.getBooleanValue(FACE_FIND_CLICKED, false)
    fun setFaceFindClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(FACE_FIND_CLICKED, isClicked)

    fun isUfInstagramClicked() = viyatekInAppPrefHandlers.getBooleanValue(ULTIMATE_QUOTES_INSTAGRAM_AD_CLICKED, false)
    fun setUfInstagramClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(ULTIMATE_QUOTES_INSTAGRAM_AD_CLICKED, isClicked)

    fun isUfTwitterClicked() = viyatekInAppPrefHandlers.getBooleanValue(ULTIMATE_FACTS_TWITTER_AD_CLICKED, false)
    fun setUfTwitterClicked(isClicked : Boolean) = viyatekInAppPrefHandlers.applyPrefs(ULTIMATE_FACTS_TWITTER_AD_CLICKED, isClicked)

}