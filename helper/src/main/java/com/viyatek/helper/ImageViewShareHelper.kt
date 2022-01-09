package com.viyatek.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import java.io.ByteArrayOutputStream

class ImageViewShareHelper(val activity: Activity) {

     fun getBitmapFromView(view: View, width: Int, height: Int): Bitmap? {

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val mCanvas = Canvas(bitmap)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                width,
                View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                height,
                View.MeasureSpec.EXACTLY
            )
        )

  
         Log.d("MESAJLARIM", "The view height ${view.measuredHeight} width : ${view.measuredWidth}")
        view.layout(0, 0, view.measuredWidth, view.measuredHeight);
        // view.layout(0, 0, view.layoutParams.width, view.layoutParams.height)
        view.draw(mCanvas)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
        val byteArray: ByteArray = stream.toByteArray()

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.count())
    }

     fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        activity.startActivity(intent)
    }
}