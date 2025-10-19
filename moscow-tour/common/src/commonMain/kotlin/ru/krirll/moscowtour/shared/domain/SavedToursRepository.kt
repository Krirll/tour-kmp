package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour

interface SavedToursRepository {
    suspend fun save(tour: Tour)
    fun getAll(): Flow<List<SavedTour>>
    suspend fun remove(tourId: Long)

    companion object {

        const val PREFIX = "$DYNAMIC_PREFIX/savedTours"
        const val REMOVE = "${PREFIX}/remove"
        const val SAVE = "${PREFIX}/save"
        const val QUERY_ALL = "${PREFIX}/query_all"
    }
}

@Serializable
data class RemoveRequest(val tourId: Long)
