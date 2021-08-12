package com.viyatek.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

class ScreenShotHelper(val theContext: Context) {

    private val watermarkHelper = WatermarkHelper(theContext)

    fun screenShot(view: View, isPremium: Boolean, waterMarkDrawableId : Int): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        // val resizedBitmap1 = Bitmap.createBitmap(bitmap, 0, (canvas.height - (view.width * 1.25f).toInt()) / 2, view.width, (view.width * 1.25f).toInt())

        return if (isPremium) { bitmap }
        else { watermarkHelper.addWaterMark(bitmap,waterMarkDrawableId) }
    }
}