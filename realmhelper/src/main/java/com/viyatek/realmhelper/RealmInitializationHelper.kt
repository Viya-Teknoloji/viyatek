package com.viyatek.realmhelper

import android.content.Context
import android.util.Base64
import android.util.Log
import io.realm.Realm
import java.io.File

class RealmInitializationHelper(private val context: Context,
                                private val existedRealmKey : String?,
                                private val populatedName : String = "populated.realm",
                                private val destinationName : String = "default.realm",
                                private val encPopulatedRealmKey : String = RealmHelperStatics.EncRealmKey,
                                val defaultSchemaVersion : Long = 11L) {

    private val realmPrefManager by lazy { RealmPrefManager(context) }

    private val file by lazy { File(context.filesDir, destinationName)  }

    private val realmEncryption by lazy { RealmEncryption(context, encPopulatedRealmKey) }

    private val populatedRealmKey by lazy { realmEncryption.getPopulatedRealmKey().toByteArray() }
    private val populatedRealmConfiguration by lazy {HandleRealmInit.getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion,populatedName) }
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

            populatedRealm?.writeEncryptedCopyTo(file, key)
            populatedRealm?.close()

            Realm.deleteRealm(HandleRealmInit.getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion))
            Log.d("Realm", "Whatt Populated realm doysası kopyalanıp şifrelenerek default realm oluşması sağlandı $theKey")
        }
        else
        {
            Log.d("Realm", "Key is transferred to the new Pref system")

            existedRealmKey?.let { realmPrefManager.setRealmKey(it) }
        }
    }

}