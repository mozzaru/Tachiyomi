package eu.kanade.domain

import android.app.Application
import eu.kanade.domain.manga.interactor.CreateSortTag
import eu.kanade.domain.manga.interactor.DeleteSortTag
import eu.kanade.domain.manga.interactor.GetPagePreviews
import eu.kanade.domain.manga.interactor.GetSortTag
import eu.kanade.domain.manga.interactor.ReorderSortTag
import eu.kanade.domain.source.interactor.CreateSourceCategory
import eu.kanade.domain.source.interactor.DeleteSourceCategory
import eu.kanade.domain.source.interactor.GetExhSavedSearch
import eu.kanade.domain.source.interactor.GetShowLatest
import eu.kanade.domain.source.interactor.GetSourceCategories
import eu.kanade.domain.source.interactor.RenameSourceCategory
import eu.kanade.domain.source.interactor.SetSourceCategories
import eu.kanade.domain.source.interactor.ToggleExcludeFromDataSaver
import eu.kanade.tachiyomi.di.InjektModule
import eu.kanade.tachiyomi.di.addFactory
import eu.kanade.tachiyomi.di.addSingletonFactory
import eu.kanade.tachiyomi.source.online.MetadataSource
import exh.search.SearchEngine
import tachiyomi.data.manga.CustomMangaRepositoryImpl
import tachiyomi.data.manga.FavoritesEntryRepositoryImpl
import tachiyomi.data.manga.MangaMergeRepositoryImpl
import tachiyomi.data.manga.MangaMetadataRepositoryImpl
import tachiyomi.data.source.FeedSavedSearchRepositoryImpl
import tachiyomi.data.source.SavedSearchRepositoryImpl
import tachiyomi.domain.chapter.interactor.DeleteChapters
import tachiyomi.domain.chapter.interactor.GetChapterByUrl
import tachiyomi.domain.chapter.interactor.GetMergedChaptersByMangaId
import tachiyomi.domain.manga.interactor.DeleteByMergeId
import tachiyomi.domain.manga.interactor.DeleteFavoriteEntries
import tachiyomi.domain.manga.interactor.DeleteMangaById
import tachiyomi.domain.manga.interactor.DeleteMergeById
import tachiyomi.domain.manga.interactor.GetAllManga
import tachiyomi.domain.manga.interactor.GetCustomMangaInfo
import tachiyomi.domain.manga.interactor.GetExhFavoriteMangaWithMetadata
import tachiyomi.domain.manga.interactor.GetFavoriteEntries
import tachiyomi.domain.manga.interactor.GetFlatMetadataById
import tachiyomi.domain.manga.interactor.GetIdsOfFavoriteMangaWithMetadata
import tachiyomi.domain.manga.interactor.GetManga
import tachiyomi.domain.manga.interactor.GetMangaBySource
import tachiyomi.domain.manga.interactor.GetMergedManga
import tachiyomi.domain.manga.interactor.GetMergedMangaById
import tachiyomi.domain.manga.interactor.GetMergedMangaForDownloading
import tachiyomi.domain.manga.interactor.GetMergedReferencesById
import tachiyomi.domain.manga.interactor.GetReadMangaNotInLibraryView
import tachiyomi.domain.manga.interactor.GetSearchMetadata
import tachiyomi.domain.manga.interactor.GetSearchTags
import tachiyomi.domain.manga.interactor.GetSearchTitles
import tachiyomi.domain.manga.interactor.InsertFavoriteEntries
import tachiyomi.domain.manga.interactor.InsertFavoriteEntryAlternative
import tachiyomi.domain.manga.interactor.InsertFlatMetadata
import tachiyomi.domain.manga.interactor.InsertMergedReference
import tachiyomi.domain.manga.interactor.SetCustomMangaInfo
import tachiyomi.domain.manga.interactor.UpdateMergedSettings
import tachiyomi.domain.manga.repository.CustomMangaRepository
import tachiyomi.domain.manga.repository.FavoritesEntryRepository
import tachiyomi.domain.manga.repository.MangaMergeRepository
import tachiyomi.domain.manga.repository.MangaMetadataRepository
import tachiyomi.domain.source.interactor.CountFeedSavedSearchBySourceId
import tachiyomi.domain.source.interactor.CountFeedSavedSearchGlobal
import tachiyomi.domain.source.interactor.DeleteFeedSavedSearchById
import tachiyomi.domain.source.interactor.DeleteSavedSearchById
import tachiyomi.domain.source.interactor.GetFeedSavedSearchBySourceId
import tachiyomi.domain.source.interactor.GetFeedSavedSearchGlobal
import tachiyomi.domain.source.interactor.GetSavedSearchById
import tachiyomi.domain.source.interactor.GetSavedSearchBySourceId
import tachiyomi.domain.source.interactor.GetSavedSearchBySourceIdFeed
import tachiyomi.domain.source.interactor.GetSavedSearchGlobalFeed
import tachiyomi.domain.source.interactor.InsertFeedSavedSearch
import tachiyomi.domain.source.interactor.InsertSavedSearch
import tachiyomi.domain.source.repository.FeedSavedSearchRepository
import tachiyomi.domain.source.repository.SavedSearchRepository
import tachiyomi.domain.track.interactor.IsTrackUnfollowed
import org.koin.dsl.module
import uy.kohesive.injekt.api.InjektRegistrar
import xyz.nulldev.ts.api.http.serializer.FilterSerializer

val syDomainModule = module {
    factory { GetShowLatest(get()) }
    factory { ToggleExcludeFromDataSaver(get()) }
    factory { SetSourceCategories(get()) }
    factory { GetAllManga(get()) }
    factory { GetMangaBySource(get()) }
    factory { DeleteChapters(get()) }
    factory { DeleteMangaById(get()) }
    factory { FilterSerializer() }
    factory { GetChapterByUrl(get()) }
    factory { GetSourceCategories(get()) }
    factory { CreateSourceCategory(get()) }
    factory { RenameSourceCategory(get(), get()) }
    factory { DeleteSourceCategory(get()) }
    factory { GetSortTag(get()) }
    factory { CreateSortTag(get(), get()) }
    factory { DeleteSortTag(get(), get()) }
    factory { ReorderSortTag(get(), get()) }
    factory { GetPagePreviews(get(), get()) }
    factory { SearchEngine() }
    factory { IsTrackUnfollowed() }
    factory { GetReadMangaNotInLibraryView(get()) }

    // Required for [MetadataSource]
    factory<MetadataSource.GetMangaId> { GetManga(get()) }
    factory<MetadataSource.GetFlatMetadataById> { GetFlatMetadataById(get()) }
    factory<MetadataSource.InsertFlatMetadata> { InsertFlatMetadata(get()) }

    single<MangaMetadataRepository> { MangaMetadataRepositoryImpl(get()) }
    factory { GetFlatMetadataById(get()) }
    factory { InsertFlatMetadata(get()) }
    factory { GetExhFavoriteMangaWithMetadata(get()) }
    factory { GetSearchMetadata(get()) }
    factory { GetSearchTags(get()) }
    factory { GetSearchTitles(get()) }
    factory { GetIdsOfFavoriteMangaWithMetadata(get()) }

    single<MangaMergeRepository> { MangaMergeRepositoryImpl(get()) }
    factory { GetMergedManga(get()) }
    factory { GetMergedMangaById(get()) }
    factory { GetMergedReferencesById(get()) }
    factory { GetMergedChaptersByMangaId(get(), get()) }
    factory { InsertMergedReference(get()) }
    factory { UpdateMergedSettings(get()) }
    factory { DeleteByMergeId(get()) }
    factory { DeleteMergeById(get()) }
    factory { GetMergedMangaForDownloading(get()) }

    single<FavoritesEntryRepository> { FavoritesEntryRepositoryImpl(get()) }
    factory { GetFavoriteEntries(get()) }
    factory { InsertFavoriteEntries(get()) }
    factory { DeleteFavoriteEntries(get()) }
    factory { InsertFavoriteEntryAlternative(get()) }

    single<SavedSearchRepository> { SavedSearchRepositoryImpl(get()) }
    factory { GetSavedSearchById(get()) }
    factory { GetSavedSearchBySourceId(get()) }
    factory { DeleteSavedSearchById(get()) }
    factory { InsertSavedSearch(get()) }
    factory { GetExhSavedSearch(get(), get(), get()) }

    single<FeedSavedSearchRepository> { FeedSavedSearchRepositoryImpl(get()) }
    factory { InsertFeedSavedSearch(get()) }
    factory { DeleteFeedSavedSearchById(get()) }
    factory { GetFeedSavedSearchGlobal(get()) }
    factory { GetFeedSavedSearchBySourceId(get()) }
    factory { CountFeedSavedSearchGlobal(get()) }
    factory { CountFeedSavedSearchBySourceId(get()) }
    factory { GetSavedSearchGlobalFeed(get()) }
    factory { GetSavedSearchBySourceIdFeed(get()) }

    single<CustomMangaRepository> { CustomMangaRepositoryImpl(get()) }
    factory { GetCustomMangaInfo(get()) }
    factory { SetCustomMangaInfo(get()) }
}

class SYDomainModule : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addFactory { org.koin.java.KoinJavaComponent.get<GetShowLatest>(GetShowLatest::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ToggleExcludeFromDataSaver>(ToggleExcludeFromDataSaver::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetSourceCategories>(SetSourceCategories::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetAllManga>(GetAllManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMangaBySource>(GetMangaBySource::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteChapters>(DeleteChapters::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteMangaById>(DeleteMangaById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<FilterSerializer>(FilterSerializer::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetChapterByUrl>(GetChapterByUrl::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSourceCategories>(GetSourceCategories::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CreateSourceCategory>(CreateSourceCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<RenameSourceCategory>(RenameSourceCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteSourceCategory>(DeleteSourceCategory::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSortTag>(GetSortTag::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CreateSortTag>(CreateSortTag::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteSortTag>(DeleteSortTag::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<ReorderSortTag>(ReorderSortTag::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetPagePreviews>(GetPagePreviews::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SearchEngine>(SearchEngine::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<IsTrackUnfollowed>(IsTrackUnfollowed::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetReadMangaNotInLibraryView>(GetReadMangaNotInLibraryView::class.java) }

        // Required for [MetadataSource]
        addFactory<MetadataSource.GetMangaId> { org.koin.java.KoinJavaComponent.get<GetManga>(GetManga::class.java) }
        addFactory<MetadataSource.GetFlatMetadataById> { org.koin.java.KoinJavaComponent.get<GetFlatMetadataById>(GetFlatMetadataById::class.java) }
        addFactory<MetadataSource.InsertFlatMetadata> { org.koin.java.KoinJavaComponent.get<InsertFlatMetadata>(InsertFlatMetadata::class.java) }

        addSingletonFactory<MangaMetadataRepository> { org.koin.java.KoinJavaComponent.get<MangaMetadataRepository>(MangaMetadataRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExhFavoriteMangaWithMetadata>(GetExhFavoriteMangaWithMetadata::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSearchMetadata>(GetSearchMetadata::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSearchTags>(GetSearchTags::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSearchTitles>(GetSearchTitles::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetIdsOfFavoriteMangaWithMetadata>(GetIdsOfFavoriteMangaWithMetadata::class.java) }

        addSingletonFactory<MangaMergeRepository> { org.koin.java.KoinJavaComponent.get<MangaMergeRepository>(MangaMergeRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMergedManga>(GetMergedManga::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMergedMangaById>(GetMergedMangaById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMergedReferencesById>(GetMergedReferencesById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMergedChaptersByMangaId>(GetMergedChaptersByMangaId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertMergedReference>(InsertMergedReference::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<UpdateMergedSettings>(UpdateMergedSettings::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteByMergeId>(DeleteByMergeId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteMergeById>(DeleteMergeById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetMergedMangaForDownloading>(GetMergedMangaForDownloading::class.java) }

        addSingletonFactory<FavoritesEntryRepository> { org.koin.java.KoinJavaComponent.get<FavoritesEntryRepository>(FavoritesEntryRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetFavoriteEntries>(GetFavoriteEntries::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertFavoriteEntries>(InsertFavoriteEntries::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteFavoriteEntries>(DeleteFavoriteEntries::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertFavoriteEntryAlternative>(InsertFavoriteEntryAlternative::class.java) }

        addSingletonFactory<SavedSearchRepository> { org.koin.java.KoinJavaComponent.get<SavedSearchRepository>(SavedSearchRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSavedSearchById>(GetSavedSearchById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSavedSearchBySourceId>(GetSavedSearchBySourceId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteSavedSearchById>(DeleteSavedSearchById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertSavedSearch>(InsertSavedSearch::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetExhSavedSearch>(GetExhSavedSearch::class.java) }

        addSingletonFactory<FeedSavedSearchRepository> { org.koin.java.KoinJavaComponent.get<FeedSavedSearchRepository>(FeedSavedSearchRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<InsertFeedSavedSearch>(InsertFeedSavedSearch::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<DeleteFeedSavedSearchById>(DeleteFeedSavedSearchById::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetFeedSavedSearchGlobal>(GetFeedSavedSearchGlobal::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetFeedSavedSearchBySourceId>(GetFeedSavedSearchBySourceId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CountFeedSavedSearchGlobal>(CountFeedSavedSearchGlobal::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<CountFeedSavedSearchBySourceId>(CountFeedSavedSearchBySourceId::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSavedSearchGlobalFeed>(GetSavedSearchGlobalFeed::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetSavedSearchBySourceIdFeed>(GetSavedSearchBySourceIdFeed::class.java) }

        addSingletonFactory<CustomMangaRepository> { org.koin.java.KoinJavaComponent.get<CustomMangaRepository>(CustomMangaRepository::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<GetCustomMangaInfo>(GetCustomMangaInfo::class.java) }
        addFactory { org.koin.java.KoinJavaComponent.get<SetCustomMangaInfo>(SetCustomMangaInfo::class.java) }
    }
}
