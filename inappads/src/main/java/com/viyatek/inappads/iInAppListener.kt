package com.viyatek.inappads

interface iInAppListener {
    fun inAppAdClicked(type:String, isInternetOn : Boolean, place : String?)
    fun inAppAdImpression(type:String, isInternetOn : Boolean, place : String?)
}