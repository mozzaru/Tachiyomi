package tachiyomi.domain.manga.interactor

import kotlinx.coroutines.flow.Flow
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.manga.model.MergedManga
import tachiyomi.domain.manga.repository.MangaMergeRepository

class GetMergedManga(
    private val mangaMergeRepository: MangaMergeRepository,
) {

    suspend fun await(): List<MergedManga> {
        return try {
            mangaMergeRepository.getMergedManga()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }

    suspend fun subscribe(): Flow<List<MergedManga>> {
        return mangaMergeRepository.subscribeMergedManga()
    }
}
