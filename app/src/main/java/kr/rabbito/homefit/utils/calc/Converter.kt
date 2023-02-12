package kr.rabbito.homefit.utils.calc

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class Converter {
    companion object {
        fun bitmapToByteArray(bitmap: Bitmap, ext: String): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

            val result = stream.toByteArray()

            return result
        }

        fun convertToBigEndian(value: Int): ByteArray = byteArrayOf(
            (value shr 24).toByte(),
            (value shr 16).toByte(),
            (value shr 8).toByte(),
            value.toByte()
        )
    }
}