package eu.kanade.tachiyomi.di

import android.app.Application
import eu.kanade.domain.base.BasePreferences
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.domain.sync.SyncPreferences
import eu.kanade.domain.track.service.TrackPreferences
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.tachiyomi.core.security.PrivacyPreferences
import eu.kanade.tachiyomi.core.security.SecurityPreferences
import eu.kanade.tachiyomi.network.NetworkPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.util.system.isDevFlavor
import org.koin.dsl.module
import tachiyomi.core.common.preference.AndroidPreferenceStore
import tachiyomi.core.common.preference.PreferenceStore
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.domain.backup.service.BackupPreferences
import tachiyomi.domain.download.service.DownloadPreferences
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.domain.updates.service.UpdatesPreferences
import uy.kohesive.injekt.api.InjektRegistrar

val preferenceModule = module {
    single<PreferenceStore> {
        AndroidPreferenceStore(get())
    }
    single {
        NetworkPreferences(
            preferenceStore = get(),
            verboseLoggingDefault = isDevFlavor,
        )
    }
    single {
        SourcePreferences(get())
    }
    single {
        SecurityPreferences(get())
    }
    single {
        PrivacyPreferences(get())
    }
    single {
        LibraryPreferences(get())
    }
    single {
        UpdatesPreferences(get())
    }
    single {
        ReaderPreferences(get())
    }
    single {
        TrackPreferences(get())
    }
    single {
        DownloadPreferences(get())
    }
    single {
        BackupPreferences(get())
    }
    single {
        StoragePreferences(
            folderProvider = get(),
            preferenceStore = get(),
        )
    }
    single {
        UiPreferences(get())
    }
    single {
        BasePreferences(get(), get())
    }

    single {
        SyncPreferences(get())
    }
}

class PreferenceModule(val app: Application) : InjektModule {

    override fun uy.kohesive.injekt.api.InjektRegistrar.registerInjectables() {
        addSingletonFactory<PreferenceStore> { org.koin.java.KoinJavaComponent.get<PreferenceStore>(PreferenceStore::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<NetworkPreferences>(NetworkPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<SourcePreferences>(SourcePreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<SecurityPreferences>(SecurityPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<PrivacyPreferences>(PrivacyPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<LibraryPreferences>(LibraryPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<UpdatesPreferences>(UpdatesPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<ReaderPreferences>(ReaderPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<TrackPreferences>(TrackPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DownloadPreferences>(DownloadPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<BackupPreferences>(BackupPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<StoragePreferences>(StoragePreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<UiPreferences>(UiPreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<BasePreferences>(BasePreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<SyncPreferences>(SyncPreferences::class.java) }
    }
}
