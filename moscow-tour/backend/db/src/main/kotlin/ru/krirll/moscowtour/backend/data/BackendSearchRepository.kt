package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.server.Search_info
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.SearchRepository
import java.sql.SQLException

class BackendSearchRepository(
    private val db: AppDatabase,
    private val accountId: Long,
    private val dispatcherProvider: DispatcherProvider,
    private val eventHandler: RemoteEventHandler
) : SearchRepository {

    override suspend fun getAll(): Flow<List<String>> {
        val request = db.searchQueries.selectAll(accountId)
        return flow {
            emit(request.executeAsList().parse())
        }
    }

    private suspend fun List<Search_info>.parse(): List<String> = withContext(dispatcherProvider.io) {
        mapNotNull { queries ->
            queries.query
        }
    }

    override suspend fun addToSearch(query: String) {
        withContext(dispatcherProvider.io) {
            try {
                db.searchQueries.insertSearchInfo(query, accountId)
                notifyChanged()
            } catch (ignored: SQLException) {
            }
        }
    }

    override suspend fun delete(query: String) {
        withContext(dispatcherProvider.io) {
            db.searchQueries.deleteSearchInfo(query, accountId)
            notifyChanged()
        }
    }

    override suspend fun clearAll() {
        withContext(dispatcherProvider.io) {
            db.searchQueries.deleteAll(accountId)
            notifyChanged()
        }
    }

    private suspend fun notifyChanged() {
        eventHandler.notify(RemoteEvent.OnSearch(System.currentTimeMillis(), accountId))
    }
}
