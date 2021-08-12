package com.viyatek.format

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FontFormatViewModel : ViewModel() {


        private val isFormatChanged = MutableLiveData<Boolean>()

        val formatChange = MutableLiveData<HashMap<String, String>>()
        fun setTheFormatChange(isFormatChanged: HashMap<String, String>) {
            Log.d(Statics.LOG_TAG, "View Model Id Changed")
            this.formatChange.value = isFormatChanged
        }

        fun getFontFormat(): MutableLiveData<Boolean> {
            return isFormatChanged
        }

        fun setFormatChange(isFormatChanged: Boolean) {
            Log.d(Statics.LOG_TAG, "View Model Id Changed")
            this.isFormatChanged.value = isFormatChanged
        }


}