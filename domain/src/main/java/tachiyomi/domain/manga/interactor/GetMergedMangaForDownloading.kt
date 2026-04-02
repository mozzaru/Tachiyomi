package tachiyomi.domain.manga.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.repository.MangaMergeRepository

class GetMergedMangaForDownloading(
    private val mangaMergeRepository: MangaMergeRepository,
) {

    suspend fun await(mergeId: Long): List<Manga> {
        return mangaMergeRepository.getMergeMangaForDownloading(mergeId)
    }

    fun subscribe(): Flow<List<Manga>> {
        return mangaMergeRepository.subscribeAllMergeMangaForDownloading()
    }
}
