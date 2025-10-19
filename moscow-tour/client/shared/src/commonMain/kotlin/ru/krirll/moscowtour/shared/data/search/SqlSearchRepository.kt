package ru.krirll.moscowtour.shared.data.search

import app.cash.sqldelight.Query
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.domain.SearchInfo
import ru.krirll.moscowtour.shared.data.AppDatabaseProvider
import ru.krirll.moscowtour.shared.domain.SearchRepository

@Factory(binds = [SqlSearchRepository::class])
class SqlSearchRepository(
    private val dbProvider: AppDatabaseProvider
) : SearchRepository {

    override suspend fun getAll(): Flow<List<String>> {
        return callbackFlow {
            val listener = Query.Listener {
                launch { send(obtain()) }
            }
            val request = getRequest()
            request.addListener(listener)
            send(obtain())
            awaitClose { request.removeListener(listener) }
        }
    }

    override suspend fun addToSearch(query: String) {
        if (query.isBlank()) return
        if (dbProvider.get().searchInfoQueries.selectByQuery(query).awaitAsOneOrNull() == null) {
            dbProvider.get().searchInfoQueries.insertSearchInfo(query)
        } else {
            // element already added
        }
    }

    override suspend fun delete(query: String) {
        dbProvider.get().searchInfoQueries.deleteSearchInfo(query)
    }

    override suspend fun clearAll() {
        dbProvider.get().searchInfoQueries.deleteAll()
    }

    private suspend fun obtain(): List<String> {
        val request = getRequest()
        return request.awaitAsList().map { it.query }
    }

    private suspend fun getRequest(): Query<SearchInfo> {
        return dbProvider.get().searchInfoQueries.selectAll()
    }
}
