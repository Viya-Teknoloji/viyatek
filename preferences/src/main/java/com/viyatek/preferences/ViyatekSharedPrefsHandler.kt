package com.viyatek.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class ViyatekSharedPrefsHandler(private val context: Context, private val prefName: String) {

    val LOG_TAG = "Preferences"

    // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC
    private val masterKey: MasterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()


    private val sharedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            prefName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val editor by lazy { sharedPrefs.edit() }

    fun applyPrefs(tag: String, value: String) {
        editor.putString(tag, value)
        editor.apply()
    }

    fun applyPrefs(tag: String, value: Int) {
        editor.putInt(tag, value)
        editor.apply()
    }

    fun applyPrefs(tag: String, value: Boolean) {
        editor.putBoolean(tag, value)
        editor.apply()
    }

    fun applyPrefs(tag: String, value: Long) {
        editor.putLong(tag, value)
        editor.apply()
    }

    fun applyPrefs(tag: String, value: Float) {
        editor.putFloat(tag, value)
        editor.apply()
    }

    fun getStringValue(tag: String, defaultValue : String?): String? {
        return if(sharedPrefs.contains(tag)) {
            sharedPrefs.getString(tag, defaultValue)
        } else {
            defaultValue?.let { applyPrefs(tag, it) }
            defaultValue
        }
    }

    fun getIntegerValue(tag: String, defaultValue : Int): Int {
        return if(sharedPrefs.contains(tag)) {
            sharedPrefs.getInt(tag, defaultValue)
        } else {
            applyPrefs(tag, defaultValue)
            defaultValue
        }
    }

    fun getBooleanValue(tag: String, defaultValue : Boolean): Boolean {

        return if(sharedPrefs.contains(tag)) {
            sharedPrefs.getBoolean(tag, defaultValue)
        } else {
            applyPrefs(tag, defaultValue)
            defaultValue
        }
    }

    fun getLongVale(tag: String, defaultValue : Long): Long {

        return if(sharedPrefs.contains(tag)) {
            sharedPrefs.getLong(tag, defaultValue)
        } else {
            applyPrefs(tag, defaultValue)
            defaultValue
        }
    }

    fun getFloatValue(tag: String, defaultValue : Float): Float {
        return if(sharedPrefs.contains(tag)) {
            sharedPrefs.getFloat(tag, defaultValue)
        } else {
            applyPrefs(tag, defaultValue)
            defaultValue
        }
    }
}