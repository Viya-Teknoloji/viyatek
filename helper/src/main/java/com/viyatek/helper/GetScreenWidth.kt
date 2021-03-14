package com.viyatek.helper

import android.content.res.Resources

class GetScreenWidth {

    fun execute(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }
}