package ru.krirll.moscowtour.shared.data.saved

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.shared.data.apply
import ru.krirll.moscowtour.shared.data.get
import ru.krirll.moscowtour.shared.data.setJsonBody
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.RemoveRequest
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour

@Factory(binds = [RemoteSavedToursRepository::class])
class RemoteSavedToursRepository(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    @Named(EventType.SAVED) private val eventListener: RemoteEventListener
) : SavedToursRepository {

    override fun getAll(): Flow<List<SavedTour>> {
        return flow {
            emit(getAllSingle())
            emitAll(
                eventListener.event
                    .filter { it is RemoteEvent.OnSaved }
                    .map { getAllSingle() }
            )
        }
    }

    private suspend fun obtainConfig() = serverConfigurationRepository.getServerConfiguration()

    private suspend fun getAllSingle(): List<SavedTour> {
        return httpClient.get<List<SavedTour>>(
            SavedToursRepository.QUERY_ALL,
            emptyMap(),
            obtainConfig()
        )
    }

    override suspend fun save(tour: Tour) {
        httpClient.post {
            obtainConfig().apply(this, SavedToursRepository.SAVE)
            setJsonBody(tour)
        }
    }

    override suspend fun remove(tourId: Long) {
        httpClient.post {
            obtainConfig().apply(this, SavedToursRepository.REMOVE)
            setJsonBody(RemoveRequest(tourId))
        }
    }
}
