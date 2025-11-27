package ru.krirll.moscowtour.shared.data.search

import ru.krirll.http.domain.TokenStorage
import ru.krirll.http.domain.hasAccount
import ru.krirll.moscowtour.shared.data.RemoteSourceSynchronizer
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.domain.SyncRepository

class SyncClientServerSearchRepository(
    private val remote: SearchRepository,
    private val local: SearchRepository,
    private val syncRepository: SyncRepository,
    private val authTokenCache: TokenStorage
) : SearchRepository {

    override suspend fun search(query: String): List<String> {
        return createSync(query).synchronizeIfNeededAndQuery()
    }

    private fun createSync(query: String): RemoteSourceSynchronizer<String> {
        return RemoteSourceSynchronizer(
            isSync = { syncRepository.isSearchSynchronized() },
            setSync = { syncRepository.setSearchSynchronized(it) },
            authTokenCache = authTokenCache,
            queryRemote = { remote.query(query, it) },
            queryLocal = { local.query(query, it) },
            writeRemote = { remote.addToSearch(it) },
            writeLocal = { local.addToSearch(it) }
        )
    }
    private suspend fun SearchRepository.query(
        query: String,
        full: Boolean
    ): List<String> {
        return if (full) {
            search("")
        } else {
            search(query)
        }
    }

    override suspend fun addToSearch(query: String) {
        if (authTokenCache.hasAccount()) {
            remote.addToSearch(query)
        }
        local.addToSearch(query)
    }

    override suspend fun delete(query: String) {
        if (authTokenCache.hasAccount()) {
            remote.delete(query)
        }
        local.delete(query)
    }

    override suspend fun clearAll() {
        if (authTokenCache.hasAccount()) {
            remote.clearAll()
        }
        local.clearAll()
    }
}
