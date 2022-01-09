package com.viyatek.helper

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

class FontHelper(val context: Context) {

    fun GetFont(fontName: String?): Typeface? {
        val resID = context.resources.getIdentifier(fontName, "font", context.packageName)
        return ResourcesCompat.getFont(context, resID)
    }

}