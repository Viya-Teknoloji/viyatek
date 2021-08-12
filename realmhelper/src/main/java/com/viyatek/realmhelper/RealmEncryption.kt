package com.viyatek.realmhelper

import android.content.Context
import android.util.Base64
import android.util.Log
import com.viyatek.realmhelper.RealmHelperStatics.EncRealmKey
import java.security.SecureRandom
import java.util.*

class RealmEncryption(val context: Context, val encRealmKey : String = EncRealmKey) {

    val realmPrefs by lazy { RealmPrefManager(context) }

        fun encrypt(text: String): String {
            val rand = Random()
            val key = rand.nextInt(5)
            var encryptedText = ""

            //Put the key at the beginning of the string
            encryptedText += START_CHAR.toChar() //%
            encryptedText += randomChar
            encryptedText += key
            for (element in text) {
                encryptedText += randomChar
                encryptedText += (element.toInt() + key).toChar()
                encryptedText += randomChar
            }

            //Put the ending
            encryptedText += END_CHAR.toChar() //&
            return encryptedText
        }

        //numbers, signs and letters in ascii
        private val randomChar: Char
            get() {
                val rand = Random()
                val num = rand.nextInt(122 - 48 + 1) + 48 //numbers, signs and letters in ascii
                return num.toChar()
            }

        private fun resolveEncrypt(encryptedText: String): String {
            var normalText = ""
            var canStart = false
            var atStart = true
            var key = 0
            var i = 0
            while (i < encryptedText.length) {
                if (encryptedText[i] == START_CHAR.toChar()) {
                    canStart = true
                }
                if (canStart) {
                    if (atStart) {
                        key = Character.getNumericValue(encryptedText[i + 2])
                        //Log.i(TAG, "Key: " + key);
                        i += 3 //Dummy char ve key için iki indeks ilerlet
                        atStart = false
                    }
                    if (encryptedText[i].toInt() == END_CHAR) {
                        break
                    }
                    i++
                    normalText += (encryptedText[i].toInt() - key).toChar()
                    i++
                }
                i++
            }
            return normalText
        }

        companion object {
            const val TAG = "MYTAG"
            const val START_CHAR = 37
            const val END_CHAR = 38
        }

        fun getPopulatedRealmKey(): String { return resolveEncrypt(encRealmKey) }

        fun generateRealmKey(): String? {

        val secretKey = ByteArray(64)
        SecureRandom().nextBytes(secretKey)
        val realmString =  Base64.encodeToString(secretKey, Base64.NO_PADDING)

        Log.d("Realm", "Generated key in the prefs, $realmString")

         return realmString;
        }
    }

/* Depreceated old encyrtion algorithm. will apply later
byte[] realmKey = encrypt.Encrypt(MainActivity.ALIAS,secretKey);

Log.i("Realm key","Kriptolanmış"+realmKey);


String realmKeyString = Base64.encodeToString(realmKey,Base64.DEFAULT);

// String realmKeyString =new String(Hex.encodeHex(realmKey));

Log.i("Realm key","Kriptodan stringe"+realmKeyString);*/
