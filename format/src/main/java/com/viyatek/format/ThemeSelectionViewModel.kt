package com.viyatek.format

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThemeSelectionViewModel : ViewModel() {

    private val themeId = MutableLiveData<Int>()

    fun getThemeId(): LiveData<Int> {
        return themeId
    }

    fun setThemeId(enabled: Int) {
        themeId.value = enabled
    }

}