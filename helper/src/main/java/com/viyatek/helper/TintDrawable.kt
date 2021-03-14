package com.viyatek.helper

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

class TintDrawable {
    fun getTintedDrawable(inputDrawable: Drawable, @ColorInt color: Int): Drawable {
        val wrapDrawable = DrawableCompat.wrap(inputDrawable.mutate())
        DrawableCompat.setTint(wrapDrawable, color)
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN)
        return wrapDrawable
    }
}