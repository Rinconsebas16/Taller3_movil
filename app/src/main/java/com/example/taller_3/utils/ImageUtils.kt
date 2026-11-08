package com.example.taller_3.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import kotlin.math.max

object ImageUtils {

    /** Reduce la foto a maxSize px en su lado mayor y la comprime a JPEG antes de subirla a Storage. */
    fun compress(
        bitmap: Bitmap,
        maxSize: Int = Constants.PHOTO_MAX_SIZE_PX,
        quality: Int = Constants.PHOTO_JPEG_QUALITY
    ): ByteArray {
        val largest = max(bitmap.width, bitmap.height)
        val scaled = if (largest > maxSize) {
            val ratio = maxSize.toFloat() / largest
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        return out.toByteArray()
    }
}
