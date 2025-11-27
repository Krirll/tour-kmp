package ru.krirll.moscowtour.shared.data.search

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.data.apply
import ru.krirll.moscowtour.shared.data.get
import ru.krirll.moscowtour.shared.data.setJsonBody
import ru.krirll.moscowtour.shared.domain.SearchDeleteRequest
import ru.krirll.moscowtour.shared.domain.SearchQueryRequest
import ru.krirll.moscowtour.shared.domain.SearchQueryResponse
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration

@Factory(binds = [RemoteSearchRepository::class])
class RemoteSearchRepository(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val tokenCache: TokenStorage
) : SearchRepository {

    override suspend fun search(query: String): List<String> {
        tokenCache.token.first() ?: return emptyList()
        return httpClient.get<SearchQueryResponse>(
            SearchRepository.SEARCH,
            hashMapOf(SearchRepository.QUERY_ARG to query),
            obtainConfig()
        ).search
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

    private suspend fun deleteInternal(query: String?) {
        tokenCache.token.first() ?: return
        httpClient.delete {
            obtainConfig().apply(this, SearchRepository.CLEAR)
            setJsonBody(SearchDeleteRequest(query))
        }
    }

    private suspend fun obtainConfig() = serverConfigurationRepository.getServerConfiguration()
}
