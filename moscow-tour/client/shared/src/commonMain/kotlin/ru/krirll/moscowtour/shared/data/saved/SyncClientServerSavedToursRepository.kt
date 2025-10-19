package ru.krirll.moscowtour.shared.data.saved

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.data.RemoteSourceSynchronizer
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.SyncRepository
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.domain.query
import ru.krirll.moscowtour.shared.domain.use

class SyncClientServerSavedToursRepository(
    private val remote: SavedToursRepository,
    private val local: SavedToursRepository,
    private val syncRepository: SyncRepository,
    private val authTokenCache: TokenStorage
) : SavedToursRepository {

    override suspend fun save(tour: Tour) {
        authTokenCache.use(
            active = { remote.save(tour) },
            fallback = { local.save(tour) }
        )
    }

    override suspend fun remove(tourId: Long) {
        authTokenCache.use(
            active = { remote.remove(tourId) },
            fallback = { local.remove(tourId) }
        )
    }

    override fun getAll(): Flow<List<SavedTour>> {
        return flow {
            RemoteSourceSynchronizer(
                isSync = { syncRepository.isSavedMovieSynchronized() },
                setSync = { syncRepository.setSavedMovieSynchronized(it) },
                authTokenCache = authTokenCache,
                queryRemote = { remote.getAll().first() },
                queryLocal = { local.getAll().first() },
                writeRemote = { remote.saveAll(it) },
                writeLocal = { local.saveAll(it) }
            ).syncIfNeeded(false)
            emitAll(
                authTokenCache.query(
                    active = { remote.getAll() },
                    fallback = { local.getAll() }
                )
            )
        }
    }

    override suspend fun isSaved(id: Long): Flow<Boolean> {
        return authTokenCache.query(
            active = { remote.isSaved(id) },
            fallback = { local.isSaved(id) }
        )
    }
}
