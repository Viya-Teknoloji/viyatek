package com.viyatek.app_update

import android.content.Context
import android.util.TypedValue

class ThemeColorHelper(val context: Context) {

    fun get(attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

}