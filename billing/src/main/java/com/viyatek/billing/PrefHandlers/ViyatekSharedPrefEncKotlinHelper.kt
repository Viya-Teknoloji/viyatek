package com.viyatek.billing.PrefHandlers

import java.util.*

class ViyatekSharedPrefEncKotlinHelper {

    val TAG = "MYTAG"
    val START_CHAR = 37
    val END_CHAR = 38


    fun encrypt(text: String): String {
        val rand = Random()
        val key = rand.nextInt(5)
        var encryptedText = ""

        //Put the key at the beginning of the string
        encryptedText = encryptedText + START_CHAR.toChar() //%
        encryptedText = encryptedText + getRandomChar()
        encryptedText = encryptedText + key
        for (i in 0 until text.length) {
            encryptedText = encryptedText + getRandomChar()
            encryptedText = encryptedText + (text[i].toInt() + key).toChar()
            encryptedText = encryptedText + getRandomChar()
        }

        //Put the ending
        encryptedText = encryptedText + END_CHAR.toChar() //&
        return encryptedText
    }

    fun getRandomChar(): String {
        val rand = Random()
        val num = rand.nextInt(122 - 48 + 1) + 48 //numbers, signs and letters in ascii
        val s: String = (num.toChar()).toString()
        return s
    }


    fun resolveEncrypt(encryptedText: String?): String? {

        if (encryptedText == null) return null

        var normalText = ""
        var canStart = false
        var atStart = true
        var key = 0
        var i = 0
        while (i < encryptedText.length) {
            if (encryptedText[i] == START_CHAR.toChar()) {
                canStart = true
            }
            if (canStart == true) {
                if (atStart == true) {
                    key = Character.getNumericValue(encryptedText[i + 2])
                    //Log.i(TAG, "Key: " + key);
                    i = i + 3 //Dummy char ve key için iki indeks ilerlet
                    atStart = false
                }
                if (encryptedText[i].toInt() == END_CHAR) {
                    break
                }
                i++
                normalText = normalText + (encryptedText[i].toInt() - key).toChar()
                i++
            }
            i++
        }
        return normalText
    }
}