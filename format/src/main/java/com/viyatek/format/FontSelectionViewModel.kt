package com.viyatek.format

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FontSelectionViewModel : ViewModel() {

        val fontName = MutableLiveData<Int>()

        fun setFontName(fontId: Int) {
            fontName.value = fontId
        }

}