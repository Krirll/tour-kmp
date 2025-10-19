package ru.krirll.moscowtour.shared.data.api

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.serialization.json.Json
import ru.krirll.moscowtour.domain.ApiCacheQueries
import ru.krirll.moscowtour.shared.data.AppDatabaseProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour

class FallbackToursApi(
    private val dbProvider: AppDatabaseProvider,
    private val wrapper: ToursApi,
    private val json: Json
) : ToursApi {

    override suspend fun fetchTours(): List<Tour> {
        val key = "fetchTours;"
        return try {
            wrapper.fetchTours().apply {
                writeByKey(key, this)
            }
        } catch (e: Exception) {
            getByKey(key) ?: throw e
        }
    }

    private suspend fun getCache(): ApiCacheQueries {
        return dbProvider.get().apiCacheQueries
    }

    private suspend inline fun <reified T> getByKey(key: String): T? {
        val value = getCache().selectByKey(key)
            .awaitAsOneOrNull()?.cache_value
            ?: return null
        return json.decodeFromString(value)
    }

    private suspend inline fun <reified T> writeByKey(key: String, value: T) {
        getCache().insert(
            key,
            json.encodeToString(value)
        )
    }
}
