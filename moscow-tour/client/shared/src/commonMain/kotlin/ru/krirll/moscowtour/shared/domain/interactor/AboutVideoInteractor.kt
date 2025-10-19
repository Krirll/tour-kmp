package ru.krirll.moscowtour.shared.domain.interactor

import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour

@Factory
class AboutVideoInteractor(
    private val api: ToursApi
) {

    suspend fun fetchVideoDetails(id: Long): Tour {
        return api.fetchDetails(id)
    }

    suspend fun fetchVideoLinks(id: Long, seasonId: Long? = null): VideoLinksResponse {
        val rawLink = api.fetchVideoLinks(id, seasonId)
        return if (seasonId != null) {
            val links = recentlyDbRepo.queryRecentlyWatched(id, seasonId)
            rawLink.markWatched(links)
        } else {
            rawLink
        }
    }
}
