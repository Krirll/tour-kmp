package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface SearchRepository {
    fun getAll(): Flow<List<String>>
    suspend fun addToSearch(query: String)
    suspend fun addToSearch(query: List<String>) {
        query.forEach { addToSearch(it) }
    }
    suspend fun delete(query: String)
    suspend fun clearAll()

    companion object {
        const val PREFIX = "$DYNAMIC_PREFIX/search"

        const val QUERY_ALL = "$PREFIX/query_all"
        const val ADD_TO_SEARCH = "$PREFIX/add"
        const val CLEAR = "$PREFIX/clear"
    }
}

@Serializable
class SearchQueryResponse(
    val search: List<String>
)

@Serializable
class SearchQueryRequest(
    val query: List<String>
)

@Serializable
class SearchDeleteRequest(
    val query: String? = null
)
