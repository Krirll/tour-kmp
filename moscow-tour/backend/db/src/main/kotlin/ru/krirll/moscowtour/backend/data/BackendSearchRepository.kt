package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.AppDatabase
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

    override suspend fun search(query: String): List<String> {
        return withContext(dispatcherProvider.io) {
            if (query.trim().isEmpty()) {
                db.searchQueries.selectAll(accountId)
            } else {
                //todo доделать локальную и удаленную бд
                //  прослушивание вебсокета по поиску
                //  опустить немного апп бар поиска
                db.searchQueries.selectByQuery(query.addWilcard(), accountId)
            }.executeAsList().map { it.query }
        }
    }

    private fun String.addWilcard(): String {
        return if (this.endsWith("%")) this else "%$this%"
    }

    override suspend fun addToSearch(query: String) {
        withContext(dispatcherProvider.io) {
            try {
                db.searchQueries.insertSearchInfo(query, accountId)
                notifyChanged()
            } catch (_: SQLException) {
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
