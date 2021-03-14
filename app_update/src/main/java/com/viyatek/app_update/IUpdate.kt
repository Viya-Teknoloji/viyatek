package com.viyatek.app_update

interface IUpdate {
    fun appUpdateOperation(oldVersionCode: Int)
    fun noUpdateDetected()
}