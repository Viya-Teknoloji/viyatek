package com.viyatek.realmhelper

import android.content.Context
import android.util.Log
import com.viyatek.preferences.ViyatekSharedPrefsHandler
import com.viyatek.realmhelper.RealmHelperStatics.REALM_VERSION
import io.realm.RealmConfiguration
import io.realm.RealmMigration

class RealmPrefManager(val context: Context) {

    private val mPrefManager by lazy { ViyatekSharedPrefsHandler(context, RealmHelperStatics.REALM_HELPER_PREFS) }
    private val realmEncryption by lazy { RealmEncryption(context) }

    fun isBundledRealmCreated() : Boolean = mPrefManager.getBooleanValue(RealmHelperStatics.BUNDLED_REALM_CREATION, false)
    fun setBundleCreated(isCreated : Boolean = true) = mPrefManager.applyPrefs(RealmHelperStatics.BUNDLED_REALM_CREATION, isCreated)

    fun getRealmKey() : String? = mPrefManager.getStringValue(RealmHelperStatics.REALM_KEY, realmEncryption.generateRealmKey())
    fun setRealmKey(encryptionKey : String) = mPrefManager.applyPrefs(RealmHelperStatics.REALM_KEY, encryptionKey)

    fun isRealmUpdateFromFarSourceAvailable() : Boolean = mPrefManager.getBooleanValue(RealmHelperStatics.REALM_UPDATE_FROM_FAR_SOURCE_AVAILABLE, false)
    fun setRealmUpdateFromFarSource(isAvailable : Boolean = true) = mPrefManager.applyPrefs(RealmHelperStatics.REALM_UPDATE_FROM_FAR_SOURCE_AVAILABLE, isAvailable)

    fun getLastRealmUpdateTime(defaultValue : Long = 1611262799000L) : Long = mPrefManager.getLongVale(RealmHelperStatics.REALM_LAST_UPDATE_DATE, defaultValue)
    fun setLastRealmUpdateTime(lastUpdateTime : Long) = mPrefManager.applyPrefs(RealmHelperStatics.REALM_LAST_UPDATE_DATE, lastUpdateTime)

    fun getRealmVersion() : Int = mPrefManager.getIntegerValue(REALM_VERSION, 1)
    fun setRealmVersion(version : Int) = mPrefManager.applyPrefs(REALM_VERSION, version)

}