package ru.krirll.moscowtour.shared.data.search

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
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.SearchDeleteRequest
import ru.krirll.moscowtour.shared.domain.SearchQueryRequest
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration

@Factory(binds = [RemoteSearchRepository::class])
class RemoteSearchRepository(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val tokenCache: TokenStorage,
    @Named(EventType.SAVED) private val eventListener: RemoteEventListener
) : SearchRepository {

    override fun getAll(): Flow<List<String>> {
        return flow {
            emit(getAllSingle())
            emitAll(
                eventListener.event
                    .filter { it is RemoteEvent.OnSaved }
                    .map { getAllSingle() }
            )
        }
    }

    override suspend fun addToSearch(query: String) {
        addToSearch(listOf(query))
    }

    override suspend fun addToSearch(query: List<String>) {
        if (query.isNotEmpty()) {
            tokenCache.token.first() ?: return
            httpClient.post {
                obtainConfig().apply(this, SearchRepository.ADD_TO_SEARCH)
                setJsonBody(SearchQueryRequest(query))
            }
        }
    }

    override suspend fun delete(query: String) {
        deleteInternal(query)
    }

    override suspend fun clearAll() {
        deleteInternal(null)
    }

    private suspend fun getAllSingle(): List<String> {
        return httpClient.get<List<String>>(
            SearchRepository.QUERY_ALL,
            emptyMap(),
            obtainConfig()
        )
    }


    private suspend fun deleteInternal(query: String?) {
        tokenCache.token.first() ?: return
        httpClient.delete {
            obtainConfig().apply(this, SearchRepository.CLEAR)
            setJsonBody(SearchDeleteRequest(query))
        }
    }

    private suspend fun obtainConfig() = serverConfigurationRepository.getServerConfiguration()
}
