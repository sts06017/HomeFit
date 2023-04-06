package kr.rabbito.homefit.utils.calc

import android.annotation.SuppressLint
import android.graphics.*
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class Converter {
    companion object {
        fun bitmapToByteArray(bitmap: Bitmap, ext: String): ByteArray {
            val stream = ByteArrayOutputStream()
            if (ext == "png") {
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
            } else if (ext == "jpeg") {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            }

            val result = stream.toByteArray()

            return result
        }

        fun convertToBigEndian(value: Int): ByteArray = byteArrayOf(
            (value shr 24).toByte(),
            (value shr 16).toByte(),
            (value shr 8).toByte(),
            value.toByte()
        )

        @SuppressLint("UnsafeOptInUsageError")
        fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
            val image = imageProxy.image!!

            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null);

            return bitmap
        }
    }
}


