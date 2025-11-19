package eu.kanade.tachiyomi.core.image

import android.graphics.BitmapRegionDecoder
import android.os.Build
import okio.IOException
import tachiyomi.core.common.util.system.logcat
import java.io.InputStream

object BitmapDecoderCompat {

    fun createRegionDecoder(inputStream: InputStream): BitmapRegionDecoder? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BitmapRegionDecoder.newInstance(inputStream)
            } else {
                @Suppress("DEPRECATION")
                BitmapRegionDecoder.newInstance(inputStream, false)
            }
        } catch (e: IOException) {
            logcat(text = "Failed to create region decoder") { e.message.toString() }
            null
        }
    }
}
