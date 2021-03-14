package com.viyatek.billing.PrefHandlers

class ViyatekRowDataKotlinHelper(var tag: String?, var value: String) {

    fun getStringValue(): String {
        return value
    }

    fun getIntegerValue(): Int {
        if (value == "") {
            return 0
        }
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun getFloatValue(): Float {
        if (value.trim { it <= ' ' } == "") {
            return 0f
        }
        var returnValue = 0f
        returnValue = try {
            java.lang.Float.valueOf(value)
        } catch (e: java.lang.NumberFormatException) {
            0f
        }
        return returnValue
    }
}