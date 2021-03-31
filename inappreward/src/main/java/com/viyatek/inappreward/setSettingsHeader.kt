package com.viyatek.inappreward

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.viyatek.inappreward.databinding.BaseTopPrefBinding

interface setSettingsHeader {
    fun setHeader(layoutId: Int, image: Drawable, title: String, text : String, headerType: HeaderType)
}