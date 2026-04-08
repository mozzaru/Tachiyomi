package eu.kanade.tachiyomi.ui.reader.loader

import android.app.Application
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.util.lang.compareToCaseInsensitiveNaturalOrder
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mihon.core.common.archive.ArchiveReader
import tachiyomi.core.common.util.system.ImageUtil
import uy.kohesive.injekt.injectLazy
import java.io.File

/**
 * Loader used to load a chapter from an archive file.
 */
internal class ArchivePageLoader(private val reader: ArchiveReader) : PageLoader() {
    // SY -->
    private val mutex = Mutex()
    private val context: Application by injectLazy()
    private val readerPreferences: ReaderPreferences by injectLazy()
    private val tmpDir = File(context.externalCacheDir, "reader_${reader.archiveHashCode}").also {
        it.deleteRecursively()
    }

    init {
        reader.wrongPassword?.let { wrongPassword ->
            if (wrongPassword) {
                error("Incorrect archive password")
            }
        }
        if (readerPreferences.archiveReaderMode.get() == ReaderPreferences.ArchiveReaderMode.CACHE_TO_DISK) {
            tmpDir.mkdirs()
            reader.useEntries { entries ->
                entries
                    .filter { it.isFile && ImageUtil.isImage(it.name) { reader.getInputStream(it.name)!! } }
                    .sortedWith { f1, f2 -> f1.name.compareToCaseInsensitiveNaturalOrder(f2.name) }
                    .forEach { entry ->
                        File(tmpDir, entry.name.substringAfterLast("/"))
                            .also { it.createNewFile() }
                            .outputStream()
                            .use { output ->
                                reader.getInputStream(entry.name)?.use { input ->
                                    input.copyTo(output)
                                }
                            }
                    }
            }
        }
    }
    // SY <--

    override var isLocal: Boolean = true

    override suspend fun getPages(): List<ReaderPage> = coroutineScope {
        reader.useEntries { entries ->
            // SY -->
            if (readerPreferences.archiveReaderMode.get() == ReaderPreferences.ArchiveReaderMode.CACHE_TO_DISK) {
                return@useEntries DirectoryPageLoader(UniFile.fromFile(tmpDir)!!).getPages()
            }
            // SY <--
            val deferreds = mutableListOf<Deferred<ByteArray?>>()
            val pages = entries
                .filter { it.isFile && ImageUtil.isImage(it.name) { reader.getInputStream(it.name)!! } }
                .sortedWith { f1, f2 -> f1.name.compareToCaseInsensitiveNaturalOrder(f2.name) }
                .mapIndexed { i, entry ->
                    // SY -->
                    val imageBytesDeferred: Deferred<ByteArray?> =
                        if (readerPreferences.archiveReaderMode.get() == ReaderPreferences.ArchiveReaderMode.LOAD_INTO_MEMORY) {
                            async(Dispatchers.IO) {
                                mutex.withLock {
                                    reader.getInputStream(entry.name)?.buffered()?.use { stream ->
                                        stream.readBytes()
                                    }
                                }
                            }
                        } else {
                            async { null }
                        }
                    deferreds.add(imageBytesDeferred)
                    // SY <--
                    ReaderPage(i).apply {
                        // SY -->
                        stream = {
                            val bytes = if (imageBytesDeferred.isCompleted) imageBytesDeferred.getCompleted() else null
                            bytes?.copyOf()?.inputStream() ?: reader.getInputStream(entry.name)!!
                        }
                        // SY <--
                        status = Page.State.Ready
                    }
                }
                .toList()

            if (readerPreferences.archiveReaderMode.get() == ReaderPreferences.ArchiveReaderMode.LOAD_INTO_MEMORY) {
                deferreds.awaitAll()
            }

            pages
        }
    }

    override suspend fun loadPage(page: ReaderPage) {
        check(!isRecycled)
    }

    override fun recycle() {
        super.recycle()
        reader.close()
        // SY -->
        tmpDir.deleteRecursively()
        // SY <--
    }
}
