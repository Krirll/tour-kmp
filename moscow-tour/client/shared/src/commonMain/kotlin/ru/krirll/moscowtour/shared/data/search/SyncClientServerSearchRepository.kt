package ru.krirll.moscowtour.shared.data.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.krirll.http.domain.TokenStorage
import ru.krirll.http.domain.hasAccount
import ru.krirll.moscowtour.shared.data.RemoteSourceSynchronizer
import ru.krirll.moscowtour.shared.domain.SearchRepository
import ru.krirll.moscowtour.shared.domain.SyncRepository
import ru.krirll.moscowtour.shared.domain.query

class SyncClientServerSearchRepository(
    private val remote: SearchRepository,
    private val local: SearchRepository,
    private val syncRepository: SyncRepository,
    private val authTokenCache: TokenStorage
) : SearchRepository {

    override fun getAll(): Flow<List<String>> {
        return flow {
            RemoteSourceSynchronizer(
                isSync = { syncRepository.isSearchSynchronized() },
                setSync = { syncRepository.setSearchSynchronized(it) },
                authTokenCache = authTokenCache,
                queryRemote = { remote.getAll().first() },
                queryLocal = { local.getAll().first() },
                writeRemote = { it.forEach { query -> remote.addToSearch(query) } },
                writeLocal = { it.forEach { query -> local.addToSearch(query) } }
            ).syncIfNeeded(false)
            emitAll(
                authTokenCache.query(
                    active = { remote.getAll() },
                    fallback = { local.getAll() }
                )
            )
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
