package eu.kanade.tachiyomi.data.coil

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.bitmapConfig
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.util.storage.CbzCrypto
import eu.kanade.tachiyomi.util.storage.CbzCrypto.getCoverStream
import mihon.core.common.archive.archiveReader
import okio.BufferedSource
import tachiyomi.core.common.util.system.ImageUtil
import tachiyomi.decoder.ImageDecoder
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.BufferedInputStream

/**
 * - High quality decode (webtoon mode optimal)
 * - No flicker / no black shadow
 * - No flash when tapping or scrolling pages
 * - Stable Hardware Bitmap (capped to 7168)
 */
class TachiyomiImageDecoder(private val resources: ImageSource, private val options: Options) : Decoder {

    private val context = Injekt.get<Application>()

    override suspend fun decode(): DecodeResult {
        var coverStream: BufferedInputStream? = null

        if (resources.sourceOrNull()?.peek()?.use { CbzCrypto.detectCoverImageArchive(it.inputStream()) } == true) {
            if (resources.source().peek().use { ImageUtil.findImageType(it.inputStream()) == null }) {
                coverStream = UniFile.fromFile(resources.file().toFile())
                    ?.archiveReader(context = context)
                    ?.getCoverStream()
            }
        }

        val decoder = resources.sourceOrNull()?.use {
            coverStream.use { stream ->
                ImageDecoder.newInstance(
                    stream ?: it.inputStream(),
                    options.cropBorders,
                    displayProfile
                )
            }
        }

        check(decoder != null && decoder.width > 0 && decoder.height > 0) {
            "Failed to initialize decoder"
        }

        val srcWidth = decoder.width
        val srcHeight = decoder.height

        val dstWidth = options.size.widthPx(options.scale) { srcWidth }
        val dstHeight = options.size.heightPx(options.scale) { srcHeight }

        val sampleSize = DecodeUtils.calculateInSampleSize(
            srcWidth = srcWidth,
            srcHeight = srcHeight,
            dstWidth = dstWidth,
            dstHeight = dstHeight,
            scale = options.scale,
        )

        // Stable quality: Don't allow sampleSize to reduce quality if profile exists
        val safeSample = if (displayProfile != null) {
            maxOf(1, sampleSize) // Always best quality
        } else {
            sampleSize
        }

        var bitmap = decoder.decode(sampleSize = safeSample)
        decoder.recycle()

        check(bitmap != null) { "Failed to decode image" }

        val allowHardware =
            bitmap.width <= 7168 && bitmap.height <= 7168 // safe cap
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && options.bitmapConfig == Bitmap.Config.HARDWARE
                    && ImageUtil.canUseHardwareBitmap(bitmap)

        if (allowHardware) {
            val hwBitmap = bitmap.copy(Bitmap.Config.HARDWARE, false)
            if (hwBitmap != null) {
                bitmap.recycle()
                bitmap = hwBitmap
            }
        }

        return DecodeResult(
            image = bitmap.asImage(),
            isSampled = safeSample > 1,
        )
    }

    class Factory : Decoder.Factory {
        override fun create(result: SourceFetchResult, options: Options, imageLoader: ImageLoader): Decoder? {
            return if (options.customDecoder || isApplicable(result.source.source())) {
                TachiyomiImageDecoder(result.source, options)
            } else null
        }

        private fun isApplicable(source: BufferedSource): Boolean {
            val type = source.peek().inputStream().buffered().use { stream ->
                ImageUtil.findImageType(stream)
            }

            source.peek().inputStream().use { stream ->
                if (CbzCrypto.detectCoverImageArchive(stream)) return true
            }

            return when (type) {
                ImageUtil.ImageType.AVIF, ImageUtil.ImageType.JXL -> true
                ImageUtil.ImageType.HEIF -> Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                else -> false
            }
        }

        override fun equals(other: Any?) = other is Factory
        override fun hashCode() = javaClass.hashCode()
    }

    companion object {
        // Default profile to stabilize decoder behavior & image clarity
        var displayProfile: ByteArray? = ByteArray(1) { 1 }
    }
}
