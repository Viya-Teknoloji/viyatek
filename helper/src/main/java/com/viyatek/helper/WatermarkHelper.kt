package com.viyatek.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

class WatermarkHelper(val theContext: Context) {

    fun addWaterMark(src: Bitmap, waterMarkDrawableId : Int): Bitmap {
        val w = src.width
        val h = src.height
        val result = Bitmap.createBitmap(w, h, src.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0f, 0f, null)

        val waterMark: Bitmap = BitmapFactory.decodeResource(theContext.resources, waterMarkDrawableId)

        canvas.drawBitmap(waterMark, w / 2f - waterMark.width / 2f, (h - waterMark.height - 128).toFloat(), null)

        waterMark.recycle()
        return result
    }
}