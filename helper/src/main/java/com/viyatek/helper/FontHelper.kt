package com.viyatek.helper

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import kotlin.Exception

class FontHelper(val context: Context) {

    fun GetFont(fontName: String?): Typeface? {
        return try {
            val resID = context.resources.getIdentifier(fontName, "font", context.packageName)

            Log.d("Font", "Res Id : $resID and font name : $fontName")

            ResourcesCompat.getFont(context, resID)
        } catch (e:Exception) {
            ResourcesCompat.getFont(context, R.font.the_rubik)
        }

    }

}