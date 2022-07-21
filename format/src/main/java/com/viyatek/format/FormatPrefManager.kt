package com.viyatek.format

import android.content.Context
import com.viyatek.preferences.ViyatekSharedPrefsHandler

class FormatPrefManager(val context: Context) {

    private val mPrefsManager by lazy { ViyatekSharedPrefsHandler(context, Statics.FORMAT_PREFS)}

    fun getSelectedFontId() : Int = mPrefsManager.getIntegerValue(Statics.SELECTED_FONT_ID, 0)
    fun setSelectedFontId(selectedFontId : Int) = mPrefsManager.applyPrefs(Statics.SELECTED_FONT_ID, selectedFontId)

    fun getThemeId() : Int = mPrefsManager.getIntegerValue(Statics.SELECTED_THEME_ID, 0)
    fun setThemeId(selectedThemeId : Int) = mPrefsManager.applyPrefs(Statics.SELECTED_THEME_ID, selectedThemeId)

    fun getTextAlign() : TextAlign {

        return when(mPrefsManager.getStringValue(Statics.TEXT_ALIGN, TextAlign.MIDDLE.name)) {
            TextAlign.LEFT.name -> {
                TextAlign.LEFT
            }
            TextAlign.MIDDLE.name -> {
                TextAlign.MIDDLE
            }
            TextAlign.RIGHT.name -> {
                TextAlign.RIGHT
            }
            else -> TextAlign.MIDDLE
        }
    }
    fun setTextAlign(textAlign: TextAlign) = mPrefsManager.applyPrefs(Statics.TEXT_ALIGN, textAlign.name)

    fun getTextSize() : TextSize {

        return when(mPrefsManager.getStringValue(Statics.TEXT_SIZE, TextSize.MEDIUM.name)) {
            TextSize.SMALL.name -> {
                TextSize.SMALL
            }
            TextSize.MEDIUM.name -> {
                TextSize.MEDIUM
            }
            TextSize.LARGE.name -> {
                TextSize.LARGE
            }
            else -> TextSize.MEDIUM
        }
    }
    fun setTextSize(textSize: TextSize) = mPrefsManager.applyPrefs(Statics.TEXT_SIZE, textSize.name)

    fun getTextColorIndex() : Int = mPrefsManager.getIntegerValue(Statics.COLOR_INDEX, 0)
    fun getTextColor() : Int  {
        return getColors()[mPrefsManager.getIntegerValue(Statics.COLOR_INDEX, 0)]
    }
    fun setTextColorIndex(theColorIndex : Int) = mPrefsManager.applyPrefs(Statics.COLOR_INDEX, theColorIndex)

    fun getColors() : IntArray = context.resources.getIntArray(R.array.colors)

    fun getDarkColors() : IntArray = context.resources.getIntArray(R.array.dark_colors)
    fun getDarkTextColor() : Int  {
        return getDarkColors()[mPrefsManager.getIntegerValue(Statics.COLOR_INDEX, 0)]
    }
}