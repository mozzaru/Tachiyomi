package eu.kanade.tachiyomi.di

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.FileProvider
import eu.kanade.domain.track.store.DelayedTrackingStore
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.core.security.SecurityPreferences
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.cache.PagePreviewCache
import eu.kanade.tachiyomi.data.download.DownloadCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadProvider
import eu.kanade.tachiyomi.data.saver.ImageSaver
import eu.kanade.tachiyomi.data.sync.service.GoogleDriveService
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.network.JavaScriptEngine
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.AndroidSourceManager
import eu.kanade.tachiyomi.util.storage.CbzCrypto
import exh.eh.EHentaiUpdateHelper
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.core.common.storage.UniFileTempFileManager
import tachiyomi.data.AndroidDatabaseHandler
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.DateColumnAdapter
import tachiyomi.data.History
import tachiyomi.data.Mangas
import tachiyomi.data.StringListColumnAdapter
import tachiyomi.data.UpdateStrategyColumnAdapter
import tachiyomi.domain.manga.interactor.GetCustomMangaInfo
import tachiyomi.domain.source.service.SourceManager
import org.koin.dsl.module
import uy.kohesive.injekt.injectLazy
import tachiyomi.domain.storage.service.StorageManager
import tachiyomi.source.local.image.LocalCoverManager
import tachiyomi.source.local.io.LocalSourceFileSystem
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.lang.ref.WeakReference

private val lock = Any()

val appModule = module {
    single<SqlDriver> {
        val app: Application = get()
        val securityPreferences: SecurityPreferences = get()
        var sqlDriverRef: WeakReference<SqlDriver>? = null

        synchronized(lock) {
            sqlDriverRef?.get()?.let { return@synchronized it }

            // SY -->
            if (securityPreferences.encryptDatabase.get()) {
                System.loadLibrary("sqlcipher")

                return@synchronized AndroidSqliteDriver(
                    schema = Database.Schema,
                    context = app,
                    name = CbzCrypto.DATABASE_NAME,
                    factory = SupportOpenHelperFactory(CbzCrypto.getDecryptedPasswordSql(), null, false, 25),
                    callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            setPragma(db, "foreign_keys = ON")
                            setPragma(db, "journal_mode = WAL")
                            setPragma(db, "synchronous = NORMAL")
                        }

                        private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
                            val cursor = db.query("PRAGMA $pragma")
                            cursor.moveToFirst()
                            cursor.close()
                        }
                    },
                ).also { sqlDriverRef = WeakReference(it) }
            }
        }
        // SY <--

        AndroidxSqliteDriver(
            driver = BundledSQLiteDriver(),
            databaseType = AndroidxSqliteDatabaseType.FileProvider(app, "tachiyomi.db"),
            schema = Database.Schema,
            configuration = AndroidxSqliteConfiguration(
                isForeignKeyConstraintsEnabled = true,
            ),
        ).also { sqlDriverRef = WeakReference(it) }
    }

    single {
        Database(
            driver = get(),
            historyAdapter = History.Adapter(
                last_readAdapter = DateColumnAdapter,
            ),
            mangasAdapter = Mangas.Adapter(
                genreAdapter = StringListColumnAdapter,
                update_strategyAdapter = UpdateStrategyColumnAdapter,
            ),
        )
    }

    single<DatabaseHandler> { AndroidDatabaseHandler(get(), get()) }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    single {
        XML {
            defaultPolicy {
                ignoreUnknownChildren()
            }
            autoPolymorphic = true
            xmlDeclMode = XmlDeclMode.Charset
            indent = 2
            xmlVersion = XmlVersion.XML10
        }
    }

    single { ProtoBuf }

    single { UniFileTempFileManager(get()) }

    single { ChapterCache(get(), get(), get()) }
    single { CoverCache(get()) }

    single { NetworkHelper(get(), get(), BuildConfig.DEBUG) }
    single { JavaScriptEngine(get()) }

    single<SourceManager> { AndroidSourceManager(get(), get(), get()) }
    single { ExtensionManager(get()) }

    single { DownloadProvider(get()) }
    single { DownloadManager(get()) }
    single { DownloadCache(get()) }

    single { TrackerManager() }
    single { DelayedTrackingStore(get()) }

    single { ImageSaver(get()) }

    single { AndroidStorageFolderProvider(get()) }
    single { LocalSourceFileSystem(get()) }
    single { LocalCoverManager(get(), get()) }
    single { StorageManager(get(), get()) }

    // SY -->
    single { EHentaiUpdateHelper(get()) }

    single { PagePreviewCache(get()) }

    single { GoogleDriveService(get()) }
    // SY <--
}

class AppModule(val app: Application) : InjektModule {
    // SY -->
    private val securityPreferences: SecurityPreferences by injectLazy()
    // SY <--

    private var sqlDriverRef: WeakReference<SqlDriver>? = null

    override fun uy.kohesive.injekt.api.InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory<SqlDriver> { org.koin.java.KoinJavaComponent.get<SqlDriver>(SqlDriver::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<Database>(Database::class.java) }
        addSingletonFactory<DatabaseHandler> { org.koin.java.KoinJavaComponent.get<DatabaseHandler>(DatabaseHandler::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<Json>(Json::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<XML>(XML::class.java) }
        addSingletonFactory<ProtoBuf> { org.koin.java.KoinJavaComponent.get<ProtoBuf>(ProtoBuf::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<UniFileTempFileManager>(UniFileTempFileManager::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<ChapterCache>(ChapterCache::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<CoverCache>(CoverCache::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<NetworkHelper>(NetworkHelper::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<JavaScriptEngine>(JavaScriptEngine::class.java) }

        addSingletonFactory<SourceManager> { org.koin.java.KoinJavaComponent.get<SourceManager>(SourceManager::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<ExtensionManager>(ExtensionManager::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DownloadProvider>(DownloadProvider::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DownloadManager>(DownloadManager::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DownloadCache>(DownloadCache::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<TrackerManager>(TrackerManager::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DelayedTrackingStore>(DelayedTrackingStore::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<ImageSaver>(ImageSaver::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<AndroidStorageFolderProvider>(AndroidStorageFolderProvider::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<LocalSourceFileSystem>(LocalSourceFileSystem::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<LocalCoverManager>(LocalCoverManager::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<StorageManager>(StorageManager::class.java) }

        // SY -->
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<EHentaiUpdateHelper>(EHentaiUpdateHelper::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<PagePreviewCache>(PagePreviewCache::class.java) }

        addSingletonFactory { org.koin.java.KoinJavaComponent.get<GoogleDriveService>(GoogleDriveService::class.java) }
        // SY <--
    }
}

fun initExpensiveComponents(app: Application) {
    // Asynchronously init expensive components for a faster cold start
    ContextCompat.getMainExecutor(app).execute {
        Injekt.get<NetworkHelper>()

        Injekt.get<SourceManager>()

        Injekt.get<Database>()

        Injekt.get<DownloadManager>()

        // SY -->
        Injekt.get<GetCustomMangaInfo>()
        // SY <--
    }
}
