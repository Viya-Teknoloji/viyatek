package com.viyatek.helper

import android.content.Context
import android.util.TypedValue

class GetThemeColor(private val context : Context) {

     fun get(attr : Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}