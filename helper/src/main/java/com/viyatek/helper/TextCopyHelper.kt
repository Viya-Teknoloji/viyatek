package com.viyatek.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class TextCopyHelper (private val theContext: Context) {

        private val myClipboard = theContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        fun copyAndShareText(mainText: String, recommendationText : String ) {
            var willCopyText: String = mainText

            willCopyText += "\n\n" + recommendationText + "\n\n"

            val myClip = ClipData.newPlainText("text", willCopyText)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(theContext, "Copy Successful", Toast.LENGTH_SHORT).show()
        }

    fun copyText(theText : String)
    {
        val myClip = ClipData.newPlainText("text", theText)
        myClipboard.setPrimaryClip(myClip)
        Toast.makeText(theContext, "Copy Successful", Toast.LENGTH_SHORT).show()
    }

}