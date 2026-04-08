package eu.kanade.domain

import eu.kanade.domain.chapter.interactor.GetAvailableScanlators
import eu.kanade.domain.chapter.interactor.SetReadStatus
import eu.kanade.domain.chapter.interactor.SyncChaptersWithSource
import eu.kanade.domain.download.interactor.DeleteDownload
import eu.kanade.domain.extension.interactor.GetExtensionLanguages
import eu.kanade.domain.extension.interactor.GetExtensionSources
import eu.kanade.domain.extension.interactor.GetExtensionsByType
import eu.kanade.domain.extension.interactor.TrustExtension
import eu.kanade.domain.manga.interactor.GetExcludedScanlators
import eu.kanade.domain.manga.interactor.SetExcludedScanlators
import eu.kanade.domain.manga.interactor.SetMangaViewerFlags
import eu.kanade.domain.manga.interactor.UpdateManga
import eu.kanade.domain.source.interactor.GetEnabledSources
import eu.kanade.domain.source.interactor.GetIncognitoState
import eu.kanade.domain.source.interactor.GetLanguagesWithSources
import eu.kanade.domain.source.interactor.GetSourcesWithFavoriteCount
import eu.kanade.domain.source.interactor.SetMigrateSorting
import eu.kanade.domain.source.interactor.ToggleIncognito
import eu.kanade.domain.source.interactor.ToggleLanguage
import eu.kanade.domain.source.interactor.ToggleSource
import eu.kanade.domain.source.interactor.ToggleSourcePin
import eu.kanade.domain.track.interactor.AddTracks
import eu.kanade.domain.track.interactor.RefreshTracks
import eu.kanade.domain.track.interactor.SyncChapterProgressWithTrack
import eu.kanade.domain.track.interactor.TrackChapter
import eu.kanade.tachiyomi.di.InjektModule
import eu.kanade.tachiyomi.di.addFactory
import eu.kanade.tachiyomi.di.addSingletonFactory
import mihon.data.repository.ExtensionRepoRepositoryImpl
import mihon.domain.chapter.interactor.FilterChaptersForDownload
import mihon.domain.extensionrepo.interactor.CreateExtensionRepo
import mihon.domain.extensionrepo.interactor.DeleteExtensionRepo
import mihon.domain.extensionrepo.interactor.GetExtensionRepo
import mihon.domain.extensionrepo.interactor.GetExtensionRepoCount
import mihon.domain.extensionrepo.interactor.ReplaceExtensionRepo
import mihon.domain.extensionrepo.interactor.UpdateExtensionRepo
import mihon.domain.extensionrepo.repository.ExtensionRepoRepository
import mihon.domain.extensionrepo.service.ExtensionRepoService
import mihon.domain.migration.usecases.MigrateMangaUseCase
import mihon.domain.upcoming.interactor.GetUpcomingManga
import org.koin.dsl.module
import tachiyomi.data.category.CategoryRepositoryImpl
import tachiyomi.data.chapter.ChapterRepositoryImpl
import tachiyomi.data.history.HistoryRepositoryImpl
import tachiyomi.data.manga.MangaRepositoryImpl
import tachiyomi.data.release.ReleaseServiceImpl
import tachiyomi.data.source.SourceRepositoryImpl
import tachiyomi.data.source.StubSourceRepositoryImpl
import tachiyomi.data.track.TrackRepositoryImpl
import tachiyomi.data.updates.UpdatesRepositoryImpl
import tachiyomi.domain.category.interactor.CreateCategoryWithName
import tachiyomi.domain.category.interactor.DeleteCategory
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.category.interactor.RenameCategory
import tachiyomi.domain.category.interactor.ReorderCategory
import tachiyomi.domain.category.interactor.ResetCategoryFlags
import tachiyomi.domain.category.interactor.SetDisplayMode
import tachiyomi.domain.category.interactor.SetMangaCategories
import tachiyomi.domain.category.interactor.SetSortModeForCategory
import tachiyomi.domain.category.interactor.UpdateCategory
import tachiyomi.domain.category.repository.CategoryRepository
import tachiyomi.domain.chapter.interactor.GetBookmarkedChaptersByMangaId
import tachiyomi.domain.chapter.interactor.GetChapter
import tachiyomi.domain.chapter.interactor.GetChapterByUrlAndMangaId
import tachiyomi.domain.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.chapter.interactor.SetMangaDefaultChapterFlags
import tachiyomi.domain.chapter.interactor.ShouldUpdateDbChapter
import tachiyomi.domain.chapter.interactor.UpdateChapter
import tachiyomi.domain.chapter.repository.ChapterRepository
import tachiyomi.domain.history.interactor.GetHistory
import tachiyomi.domain.history.interactor.GetNextChapters
import tachiyomi.domain.history.interactor.GetTotalReadDuration
import tachiyomi.domain.history.interactor.RemoveHistory
import tachiyomi.domain.history.interactor.UpsertHistory
import tachiyomi.domain.history.repository.HistoryRepository
import tachiyomi.domain.manga.interactor.FetchInterval
import tachiyomi.domain.manga.interactor.GetDuplicateLibraryManga
import tachiyomi.domain.manga.interactor.GetFavorites
import tachiyomi.domain.manga.interactor.GetLibraryManga
import tachiyomi.domain.manga.interactor.GetManga
import tachiyomi.domain.manga.interactor.GetMangaByUrlAndSourceId
import tachiyomi.domain.manga.interactor.GetMangaWithChapters
import tachiyomi.domain.manga.interactor.NetworkToLocalManga
import tachiyomi.domain.manga.interactor.ResetViewerFlags
import tachiyomi.domain.manga.interactor.SetMangaChapterFlags
import tachiyomi.domain.manga.interactor.UpdateMangaNotes
import tachiyomi.domain.manga.repository.MangaRepository
import tachiyomi.domain.release.interactor.GetApplicationRelease
import tachiyomi.domain.release.service.ReleaseService
import tachiyomi.domain.source.interactor.GetRemoteManga
import tachiyomi.domain.source.interactor.GetSourcesWithNonLibraryManga
import tachiyomi.domain.source.repository.SourceRepository
import tachiyomi.domain.source.repository.StubSourceRepository
import tachiyomi.domain.track.interactor.DeleteTrack
import tachiyomi.domain.track.interactor.GetTracks
import tachiyomi.domain.track.interactor.GetTracksPerManga
import tachiyomi.domain.track.interactor.InsertTrack
import tachiyomi.domain.track.repository.TrackRepository
import tachiyomi.domain.updates.interactor.GetUpdates
import tachiyomi.domain.updates.repository.UpdatesRepository
import uy.kohesive.injekt.api.InjektRegistrar

val domainModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    factory { GetCategories(get()) }
    factory { ResetCategoryFlags(get(), get()) }
    factory { SetDisplayMode(get()) }
    factory { SetSortModeForCategory(get(), get()) }
    factory { CreateCategoryWithName(get(), get()) }
    factory { RenameCategory(get()) }
    factory { ReorderCategory(get()) }
    factory { UpdateCategory(get()) }
    factory { DeleteCategory(get(), get(), get()) }

    single<MangaRepository> { MangaRepositoryImpl(get()) }
    factory { GetDuplicateLibraryManga(get()) }
    factory { GetFavorites(get()) }
    factory { GetLibraryManga(get()) }
    factory { GetMangaWithChapters(get(), get()) }
    factory { GetMangaByUrlAndSourceId(get()) }
    factory { GetManga(get()) }
    factory { GetNextChapters(get(), get(), get(), get()) }
    factory { GetUpcomingManga(get()) }
    factory { ResetViewerFlags(get()) }
    factory { SetMangaChapterFlags(get()) }
    factory { FetchInterval(get()) }
    factory { SetMangaDefaultChapterFlags(get(), get(), get()) }
    factory { SetMangaViewerFlags(get()) }
    factory { NetworkToLocalManga(get()) }
    factory { UpdateManga(get(), get()) }
    factory { UpdateMangaNotes(get()) }
    factory { SetMangaCategories(get()) }
    factory { GetExcludedScanlators(get()) }
    factory { SetExcludedScanlators(get()) }
    factory {
        MigrateMangaUseCase(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        )
    }

    single<ReleaseService> { ReleaseServiceImpl(get(), get()) }
    factory { GetApplicationRelease(get(), get()) }

    single<TrackRepository> { TrackRepositoryImpl(get()) }
    factory { TrackChapter(get(), get(), get(), get()) }
    factory { AddTracks(get(), get(), get(), get()) }
    factory { RefreshTracks(get(), get(), get(), get()) }
    factory { DeleteTrack(get()) }
    factory { GetTracksPerManga(get(), get()) }
    factory { GetTracks(get()) }
    factory { InsertTrack(get()) }
    factory { SyncChapterProgressWithTrack(get(), get(), get()) }

    single<ChapterRepository> { ChapterRepositoryImpl(get()) }
    factory { GetChapter(get()) }
    factory { GetChaptersByMangaId(get()) }
    factory { GetBookmarkedChaptersByMangaId(get(), get(), get()) }
    factory { GetChapterByUrlAndMangaId(get()) }
    factory { UpdateChapter(get()) }
    factory { SetReadStatus(get(), get(), get(), get(), get()) }
    factory { ShouldUpdateDbChapter() }
    factory { SyncChaptersWithSource(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { GetAvailableScanlators(get()) }
    factory { FilterChaptersForDownload(get(), get(), get(), get()) }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    factory { GetHistory(get()) }
    factory { UpsertHistory(get()) }
    factory { RemoveHistory(get()) }
    factory { GetTotalReadDuration(get()) }

    factory { DeleteDownload(get(), get()) }

    factory { GetExtensionsByType(get(), get()) }
    factory { GetExtensionSources(get()) }
    factory { GetExtensionLanguages(get(), get()) }

    single<UpdatesRepository> { UpdatesRepositoryImpl(get()) }
    factory { GetUpdates(get()) }

    single<SourceRepository> { SourceRepositoryImpl(get(), get()) }
    single<StubSourceRepository> { StubSourceRepositoryImpl(get()) }
    factory { GetEnabledSources(get(), get()) }
    factory { GetLanguagesWithSources(get(), get()) }
    factory { GetRemoteManga(get()) }
    factory { GetSourcesWithFavoriteCount(get(), get()) }
    factory { GetSourcesWithNonLibraryManga(get()) }
    factory { SetMigrateSorting(get()) }
    factory { ToggleLanguage(get()) }
    factory { ToggleSource(get()) }
    factory { ToggleSourcePin(get()) }
    factory { TrustExtension(get(), get()) }

    single<ExtensionRepoRepository> { ExtensionRepoRepositoryImpl(get()) }
    factory { ExtensionRepoService(get(), get()) }
    factory { GetExtensionRepo(get()) }
    factory { GetExtensionRepoCount(get()) }
    factory { CreateExtensionRepo(get(), get()) }
    factory { DeleteExtensionRepo(get()) }
    factory { ReplaceExtensionRepo(get()) }
    factory { UpdateExtensionRepo(get(), get()) }
    factory { ToggleIncognito(get()) }
    factory { GetIncognitoState(get(), get(), get()) }
}

class DomainModule : InjektModule {

    override fun uy.kohesive.injekt.api.InjektRegistrar.registerInjectables() {
        addSingletonFactory<CategoryRepository> { org.koin.java.KoinJavaComponent.get<CategoryRepository>(CategoryRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetCategories>(GetCategories::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ResetCategoryFlags>(ResetCategoryFlags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetDisplayMode>(SetDisplayMode::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetSortModeForCategory>(SetSortModeForCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CreateCategoryWithName>(CreateCategoryWithName::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<RenameCategory>(RenameCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ReorderCategory>(ReorderCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateCategory>(UpdateCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteCategory>(DeleteCategory::class.java) }

        addSingletonFactory<MangaRepository> { org.koin.java.KoinJavaComponent.get<MangaRepository>(MangaRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetDuplicateLibraryManga>(GetDuplicateLibraryManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetFavorites>(GetFavorites::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetLibraryManga>(GetLibraryManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMangaWithChapters>(GetMangaWithChapters::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMangaByUrlAndSourceId>(GetMangaByUrlAndSourceId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetManga>(GetManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetNextChapters>(GetNextChapters::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetUpcomingManga>(GetUpcomingManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ResetViewerFlags>(ResetViewerFlags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetMangaChapterFlags>(SetMangaChapterFlags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<FetchInterval>(FetchInterval::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetMangaDefaultChapterFlags>(SetMangaDefaultChapterFlags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetMangaViewerFlags>(SetMangaViewerFlags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<NetworkToLocalManga>(NetworkToLocalManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateManga>(UpdateManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateMangaNotes>(UpdateMangaNotes::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetMangaCategories>(SetMangaCategories::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExcludedScanlators>(GetExcludedScanlators::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetExcludedScanlators>(SetExcludedScanlators::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<MigrateMangaUseCase>(MigrateMangaUseCase::class.java) }

        addSingletonFactory<ReleaseService> { org.koin.java.KoinJavaComponent.get<ReleaseService>(ReleaseService::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetApplicationRelease>(GetApplicationRelease::class.java) }

        addSingletonFactory<TrackRepository> { org.koin.java.KoinJavaComponent.get<TrackRepository>(TrackRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<TrackChapter>(TrackChapter::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<AddTracks>(AddTracks::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<RefreshTracks>(RefreshTracks::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteTrack>(DeleteTrack::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetTracksPerManga>(GetTracksPerManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetTracks>(GetTracks::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertTrack>(InsertTrack::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SyncChapterProgressWithTrack>(SyncChapterProgressWithTrack::class.java) }

        addSingletonFactory<ChapterRepository> { org.koin.java.KoinJavaComponent.get<ChapterRepository>(ChapterRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetChapter>(GetChapter::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetChaptersByMangaId>(GetChaptersByMangaId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetBookmarkedChaptersByMangaId>(GetBookmarkedChaptersByMangaId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetChapterByUrlAndMangaId>(GetChapterByUrlAndMangaId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateChapter>(UpdateChapter::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetReadStatus>(SetReadStatus::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ShouldUpdateDbChapter>(ShouldUpdateDbChapter::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SyncChaptersWithSource>(SyncChaptersWithSource::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetAvailableScanlators>(GetAvailableScanlators::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<FilterChaptersForDownload>(FilterChaptersForDownload::class.java) }

        addSingletonFactory<HistoryRepository> { org.koin.java.KoinJavaComponent.get<HistoryRepository>(HistoryRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetHistory>(GetHistory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpsertHistory>(UpsertHistory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<RemoveHistory>(RemoveHistory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetTotalReadDuration>(GetTotalReadDuration::class.java) }

        addFactory { org.koin.java.KoinJavaComponent.get<DeleteDownload>(DeleteDownload::class.java) }

        addFactory { org.koin.java.KoinJavaComponent.get<GetExtensionsByType>(GetExtensionsByType::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExtensionSources>(GetExtensionSources::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExtensionLanguages>(GetExtensionLanguages::class.java) }

        addSingletonFactory<UpdatesRepository> { org.koin.java.KoinJavaComponent.get<UpdatesRepository>(UpdatesRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetUpdates>(GetUpdates::class.java) }

        addSingletonFactory<SourceRepository> { org.koin.java.KoinJavaComponent.get<SourceRepository>(SourceRepository::class.java) }
        addSingletonFactory<StubSourceRepository> { org.koin.java.KoinJavaComponent.get<StubSourceRepository>(StubSourceRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetEnabledSources>(GetEnabledSources::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetLanguagesWithSources>(GetLanguagesWithSources::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetRemoteManga>(GetRemoteManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSourcesWithFavoriteCount>(GetSourcesWithFavoriteCount::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSourcesWithNonLibraryManga>(GetSourcesWithNonLibraryManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetMigrateSorting>(SetMigrateSorting::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ToggleLanguage>(ToggleLanguage::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ToggleSource>(ToggleSource::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ToggleSourcePin>(ToggleSourcePin::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<TrustExtension>(TrustExtension::class.java) }

        addSingletonFactory<ExtensionRepoRepository> { org.koin.java.KoinJavaComponent.get<ExtensionRepoRepository>(ExtensionRepoRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ExtensionRepoService>(ExtensionRepoService::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExtensionRepo>(GetExtensionRepo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExtensionRepoCount>(GetExtensionRepoCount::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CreateExtensionRepo>(CreateExtensionRepo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteExtensionRepo>(DeleteExtensionRepo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ReplaceExtensionRepo>(ReplaceExtensionRepo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateExtensionRepo>(UpdateExtensionRepo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ToggleIncognito>(ToggleIncognito::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetIncognitoState>(GetIncognitoState::class.java) }
    }
}
