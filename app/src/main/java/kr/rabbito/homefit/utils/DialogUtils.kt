package kr.rabbito.homefit.utils

import android.graphics.Point
import androidx.appcompat.app.AlertDialog

fun AlertDialog.setWidthPercentage(percentage: Double) {
    val window = this.window
    val size = Point()
    window?.windowManager?.defaultDisplay?.getSize(size)
    val width = (size.x * percentage).toInt()
    val params = window?.attributes
    params?.width = width
    window?.attributes = params
}