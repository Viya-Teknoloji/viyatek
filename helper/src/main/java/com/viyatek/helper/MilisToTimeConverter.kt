package com.viyatek.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MilisToTimeConverter {

    fun ExecuteChange(milis: Long): String? {

        val formatter: DateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        return formatter.format(Date(milis))
    }


}