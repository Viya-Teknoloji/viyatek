package com.viyatek.realmhelper

import android.content.Context
import android.util.Base64
import android.util.Log
import io.realm.Realm
import java.io.File

class RealmInitializationHelper(private val context: Context, private val defaultKey : String?, private val defaultSchemaVersion : Long = 11L) {

    private val realmPrefManager by lazy { RealmPrefManager(context) }
    private val file by lazy { File(context.filesDir.toString() + "/default.realm")  }
    private val realmEncryption by lazy { RealmEncryption(context) }

    private val populatedRealmKey by lazy { realmEncryption.getPopulatedRealmKey().toByteArray() }
    private val populatedRealmConfiguration by lazy {
        HandleRealmInit.getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion)
         }
    private val populatedRealm by lazy { Realm.getInstance(populatedRealmConfiguration) }

    fun init(iRealmCreation: iRealmCreation? = null)
    {
        Realm.init(context)

        if(!realmPrefManager.isBundledRealmCreated())
        {
            createRealm(iRealmCreation)
            realmPrefManager.setBundleCreated()
        }
    }

    private fun createRealm(iRealmCreation: iRealmCreation?)
    {
        if(!file.exists()){

            val theKey = realmPrefManager.getRealmKey()!!
            realmPrefManager.setRealmKey(theKey)
            val key = Base64.decode(theKey, Base64.NO_PADDING)

            iRealmCreation?.beforeRealmCreated(populatedRealm)

            populatedRealm?.writeEncryptedCopyTo(File(context.filesDir, "default.realm"), key)
            populatedRealm?.close()

            Realm.deleteRealm(HandleRealmInit.getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion))
            Log.d("Realm", "Whatt Populated realm doysası kopyalanıp şifrelenerek default realm oluşması sağlandı $theKey")
        }
        else
        {
            Log.d("Realm", "Key is transferred to the new Pref system")

            defaultKey?.let { realmPrefManager.setRealmKey(it) } }
    }

}