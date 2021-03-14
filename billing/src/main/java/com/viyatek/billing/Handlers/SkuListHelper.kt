package com.viyatek.billing.Handlers

class SkuListHelper {

    private val skuList = arrayListOf<String>()

    fun addSku(theSku: String) {
        if (!skuList.contains(theSku)) {
            skuList.add(theSku)
        }
    }

    fun removeSku(theSku: String) {
        if (skuList.contains(theSku)) {
            skuList.remove(theSku)
        }
    }

    fun getSkuList(): ArrayList<String> {
        return skuList
    }

}