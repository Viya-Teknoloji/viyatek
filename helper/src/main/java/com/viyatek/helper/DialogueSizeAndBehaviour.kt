package com.viyatek.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup

class DialogueSizeAndBehaviour(var cdd: Dialog, var context: Context) {

    fun handleSizeAndBehaviour() {

        cdd.show()
        val width: Int = GetScreenWidth().execute()

        cdd.window?.setLayout(6 * width / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
        cdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cdd.setCancelable(false)
        cdd.setCanceledOnTouchOutside(false)
    }
}