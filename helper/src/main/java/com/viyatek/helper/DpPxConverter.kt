package com.viyatek.helper

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

class DpPxConverter(val context: Context) {

    fun pxToDp(px: Float): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToSp(px: Float): Int {
        return (px / Resources.getSystem().displayMetrics.scaledDensity).toInt()
    }

    fun spToPx(sp: Float): Int {
        val fontScale = context.resources.configuration.fontScale
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp / fontScale,
            Resources.getSystem().displayMetrics
        )
            .toInt()
    }

    fun dipToPx(dip: Float): Int {
        // Convert the dips to pixels
        return (dip * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }
}