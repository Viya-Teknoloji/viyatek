package com.viyatek.rate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RateUsDoneViewModel : ViewModel() {

    private val rateUsPosition = MutableLiveData<Int>()

    fun getRateUsPosition(): MutableLiveData<Int> { return rateUsPosition}

    fun setRateUsRemove(rateUsPositionInList: Int) { this.rateUsPosition.value = rateUsPositionInList }
}