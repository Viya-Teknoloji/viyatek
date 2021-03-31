package com.viyatek.app_update

import android.content.Context
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class UpdatePrefsHandler(val context: Context) {

    val sharedPrefsHandler by lazy { ViyatekSharedPrefsHandler(context, Statics.UPDATE_PREFS_NAME)}

    fun setVersionCode(verCode : Int) = sharedPrefsHandler.applyPrefs(Statics.VERSION_CODE, verCode)
    fun getVersionCode(defaultValue : Int = 0) : Int = sharedPrefsHandler.getIntegerValue(Statics.VERSION_CODE, defaultValue)

    fun isUpdateMessageMustShown() : Boolean = sharedPrefsHandler.getBooleanValue(Statics.UPDATE_MESSAGE_MUST_SHOWN , false)
    fun setUpdateMessageMustShown(mustShown : Boolean) = sharedPrefsHandler.applyPrefs(Statics.UPDATE_MESSAGE_MUST_SHOWN , mustShown)

    fun setUpdateRequireHandled(isHandled : Boolean) = sharedPrefsHandler.applyPrefs(Statics.IS_UPDATE_REQUIRE_HANDLED, isHandled)
    fun isUpdateRequireHandled() : Boolean = sharedPrefsHandler.getBooleanValue(Statics.IS_UPDATE_REQUIRE_HANDLED, false)


}