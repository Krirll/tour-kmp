package ru.krirll.moscowtour.shared.data.search

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.data.AppDatabaseProvider
import ru.krirll.moscowtour.shared.domain.SearchRepository

@Factory(binds = [SqlSearchRepository::class])
class SqlSearchRepository(
    private val dbProvider: AppDatabaseProvider
) : SearchRepository {

    override suspend fun search(query: String): List<String> {
        return if (query.isNotEmpty()) {
            dbProvider.get().searchInfoQueries
                .selectByQuery(query.addWildcard())
                .awaitAsList()
                .map { it.query }
        } else {
            dbProvider.get().searchInfoQueries
                .selectAll()
                .awaitAsList()
                .map { it.query }
        }.filter { it.isNotBlank() }
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

    private fun String.addWildcard(): String {
        return if (this.endsWith("%")) this else "%$this%"
    }
}
