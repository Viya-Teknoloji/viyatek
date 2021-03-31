package com.viyatek.billing.Campaign

import android.content.Context
import android.os.CountDownTimer
import com.viyatek.billing.Interface.ICampaignCountDown

class CampaignCountDownHandler(val startDate: Long, val endDate : Long, val iCampaignCountDown: ICampaignCountDown) {

    fun getCountDownTime()
    {
        val currentTimeInMs = System.currentTimeMillis()
        if (currentTimeInMs in (startDate + 1) until endDate) {

            object : CountDownTimer(endDate - currentTimeInMs, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val totalSeconds = (millisUntilFinished / 1000)
                    val hours = (totalSeconds / (60 * 60)).toInt()
                    val min = ((totalSeconds - hours * 60 * 60) / 60).toInt()
                    val remainingSeconds = (totalSeconds - hours * 60 * 60 - min * 60).toInt()

                    iCampaignCountDown.countDown(hours,min,remainingSeconds)
                }

                override fun onFinish() {
                    iCampaignCountDown.countDown(0,0,0)
                }
            }.start()

        }
    }
}