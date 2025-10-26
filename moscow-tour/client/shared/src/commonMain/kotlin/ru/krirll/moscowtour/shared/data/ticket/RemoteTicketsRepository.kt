package ru.krirll.moscowtour.shared.data.ticket

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.data.apply
import ru.krirll.moscowtour.shared.data.get
import ru.krirll.moscowtour.shared.data.setJsonBody
import ru.krirll.moscowtour.shared.domain.CreateTicketRequest
import ru.krirll.moscowtour.shared.domain.DownloadUrlResponse
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.RemoveTicketRequest
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Ticket

@Factory(binds = [RemoteTicketsRepository::class])
class RemoteTicketsRepository(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val tokenCache: TokenStorage,
    @Named(EventType.TICKETS) private val eventListener: RemoteEventListener
) : TicketsRepository {

    override suspend fun getAll(): Flow<List<Ticket>> {
        return flow {
            emit(getAllSingle())
            emitAll(
                eventListener.event
                    .filter { it is RemoteEvent.OnTicket }
                    .map { getAllSingle() }
            )
        }
    }

    override suspend fun create(tourId: Long, personData: PersonData, time: Long) {
        tokenCache.token.first() ?: return
        httpClient.post {
            obtainConfig().apply(this, TicketsRepository.CREATE)
            setJsonBody(CreateTicketRequest(tourId, personData, time))
        }
    }

    override suspend fun remove(ticketId: Long) {
        tokenCache.token.first() ?: return
        httpClient.delete {
            obtainConfig().apply(this, TicketsRepository.DELETE)
            setJsonBody(RemoveTicketRequest(ticketId))
        }
    }

    override suspend fun getFilePath(ticketId: Long): String {
        tokenCache.token.first() ?: throw IllegalStateException("Вы не авторизованы")
        val response = httpClient.get<DownloadUrlResponse>(
            TicketsRepository.GET_DOWNLOAD_URL,
            mapOf(TicketsRepository.TICKET_ID_ARG to ticketId.toString()),
            obtainConfig()
        )
        return response.url
    }

    private suspend fun getAllSingle(): List<Ticket> {
        return httpClient.get<List<Ticket>>(
            TicketsRepository.QUERY_ALL,
            emptyMap(),
            obtainConfig()
        )
    }

    private suspend fun obtainConfig() = serverConfigurationRepository.getServerConfiguration()
}
