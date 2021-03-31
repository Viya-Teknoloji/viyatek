package com.viyatek.realmhelper

import android.content.Context
import android.util.Base64
import android.util.Log
import com.viyatek.preferences.ViyatekSharedPrefsHandler
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.exceptions.RealmFileException
import io.realm.exceptions.RealmMigrationNeededException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

abstract class HandleRealmInit(private val context: Context,
                               private val migration : RealmMigration,
                                private val defaultSchemaVersion : Long) {

    private val realmTag = "Realm"
    private val realmPrefManager by lazy {RealmPrefManager(context) }
    private val realmEncryption by lazy { RealmEncryption(context) }

    //private val mFirebaseAnalytics by lazy { AnalyticsHandler(context) }

    private val populatedRealmConfiguration by lazy { getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion)}
    private val defaultRealmConfiguration by lazy { getDefaultRealmConfig(key, migration, defaultSchemaVersion) }

    private val backgroundRealm by lazy { Realm.getInstance(defaultRealmConfiguration) }
    private val populatedRealm by lazy { Realm.getInstance(populatedRealmConfiguration) }

    var realm: Realm? = null

    val key: ByteArray by lazy { Base64.decode(realmPrefManager.getRealmKey(), Base64.NO_PADDING) }
    val populatedRealmKey by lazy { realmEncryption.getPopulatedRealmKey().toByteArray() }


    private val file by lazy { File(context.filesDir.toString() + "/default.realm")  }

    companion object{

        @Volatile
        private var defaultRealmConfigInstance: RealmConfiguration? = null

        @Volatile
        private var populatedRealmConfigInstance: RealmConfiguration? = null

        fun getDefaultRealmConfig(key: ByteArray, migrationClass : RealmMigration, schemaVersion: Long= 11L) : RealmConfiguration {

            return when {
                defaultRealmConfigInstance != null -> defaultRealmConfigInstance!!

                else -> {
                    defaultRealmConfigInstance= RealmConfiguration.Builder()
                        .schemaVersion(schemaVersion)
                        .migration(migrationClass)
                        .compactOnLaunch()
                        .encryptionKey(key)
                        .allowQueriesOnUiThread(true)
                        .allowWritesOnUiThread(true)
                        .build()

                    return defaultRealmConfigInstance!!
                }
            }
        }

        fun getPopulatedRealmConfig(populatedKey: ByteArray, schemaVersion: Long = 11L) : RealmConfiguration {
            return when {
                populatedRealmConfigInstance != null -> populatedRealmConfigInstance!!

                else -> {
                    populatedRealmConfigInstance = RealmConfiguration.Builder()
                        .schemaVersion(schemaVersion)
                        .compactOnLaunch()
                        .encryptionKey(populatedKey)
                        .assetFile("populated.realm")
                        .name("populated.realm")
                        .allowQueriesOnUiThread(true)
                        .allowWritesOnUiThread(true)
                        .build()

                    return populatedRealmConfigInstance!!
                }
            }


        }
    }

    private fun setDefault() {
        Log.d(RealmHelperStatics.LOG_TAG, "Getting Default Realm ")
        Realm.removeDefaultConfiguration()
        Realm.setDefaultConfiguration(defaultRealmConfiguration)
    }

    fun getRealmInstance(): Realm {

        if(realm != null) return realm!!

        realm = try {
            Log.d(realmTag, "Realm File key  ${realmPrefManager.getRealmKey()}")
            setDefault()
            Realm.getInstance(defaultRealmConfiguration)
        } catch (e: RealmMigrationNeededException) {
            Log.d(realmTag, "Migration Needed Exception $e")
            logMigrationNeededExceptionRealm()
            Realm.deleteRealm(defaultRealmConfiguration)
            initRealmWhenUpdateOrCreate()
            Realm.getInstance(defaultRealmConfiguration)
        } catch (e: RealmFileException) {
            Log.d(realmTag, "Realm File Exception ${realmPrefManager.getRealmKey()}")
            Realm.deleteRealm(defaultRealmConfiguration)
            initRealmWhenUpdateOrCreate()
            Realm.getInstance(defaultRealmConfiguration)
        }

        return realm!!
    }

    abstract fun logMigrationNeededExceptionRealm()

    fun initRealmWhenUpdateOrCreate() {

        //increasing opening count of the app
        //sharedPrefsHandler.ApplyPrefs(SharedPrefsHandler.OPENING_COUNT,sharedPrefsHandler.GetPref(SharedPrefsHandler.OPENING_COUNT).getIntegerValue()+1);

        if (file.exists() && RealmHelperStatics.isRealmUpdate) {
            createRealmsWhenUpdateHappens()
            Log.d(
                realmTag,
                "Default Realm dosyası bulundu güncelleme tespit edildi Realm dosyaları eşitleniyor"
            )
            RealmHelperStatics.isRealmUpdate = false
        } else if(!file.exists()){

            Realm.deleteRealm(getDefaultRealmConfig(key, migration, defaultSchemaVersion))
            createRealmsWhenDefaultNotExists(populatedRealm, key)
            Realm.deleteRealm(getPopulatedRealmConfig(populatedRealmKey,defaultSchemaVersion))
            Log.d(realmTag, "Populated realm doysası kopyalanıp şifrelenerek default realm oluşması sağlandı")
        }
    }

    private fun createRealmsWhenUpdateHappens() {
        try{
            CoroutineScope(Dispatchers.Main).launch { backgroundRealm.close() }
            Realm.deleteRealm(populatedRealmConfiguration)

            Log.d(realmTag, "Populated Realm dosyası oluşturuldu")
            synchronizeWhenUpdated(populatedRealm, backgroundRealm)
            Log.d(realmTag, "Güncelleme Eşitlemesi başlatıldı")
        }
        catch (e:RealmFileException)
        {

            if(file.delete())
            {
                createRealmsWhenDefaultNotExists(populatedRealm, key)
                Realm.deleteRealm(getPopulatedRealmConfig(populatedRealmKey, defaultSchemaVersion))
            }
        }

    }

    abstract fun createRealmsWhenDefaultNotExists(
        populatedRealm: Realm?,
        key: ByteArray
    )

    abstract fun synchronizeWhenUpdated(populatedRealm: Realm?, backgroundRealm: Realm?)



/*
    private fun shuffleAndCopyToOtherRealm(copyToRealm: Realm?, copyFromRealm: Realm?) {
        val realmResults = copyFromRealm?.where(QuoteRM::class.java)?.findAll()
        val themeResults = copyFromRealm?.where(ThemeRM::class.java)?.findAll()
        val fontRMRealmResults = copyFromRealm?.where(FontRM::class.java)?.findAll()
        val quoteRMS: List<QuoteRM> = ArrayList(realmResults)

        Log.d(realmTag, "Quote count : ${quoteRMS.size}")

        copyToRealm?.apply {
            executeTransaction {
                for (theQuote in quoteRMS) { insertOrUpdate(theQuote) }
                insertOrUpdate(themeResults)
                insertOrUpdate(fontRMRealmResults)
            }
        }
    }

 */


}