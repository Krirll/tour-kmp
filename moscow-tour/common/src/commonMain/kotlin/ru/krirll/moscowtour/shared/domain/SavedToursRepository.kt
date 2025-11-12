package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour

interface SavedToursRepository {
    suspend fun save(tour: Tour)
    fun getAll(): Flow<List<SavedTour>>
    suspend fun remove(tourId: Long)
    suspend fun isSaved(tourId: Long): Flow<Boolean>

    suspend fun clear() {
        getAll().first().forEach { remove(it.tour.id) }
    }

    companion object {

        const val PREFIX = "$DYNAMIC_PREFIX/savedTours"
        const val REMOVE = "${PREFIX}/remove"
        const val SAVE = "${PREFIX}/save"
        const val QUERY_ALL = "${PREFIX}/query_all"
        const val QUERY_SAVED = "${PREFIX}/query_saved"
        const val TOUR_ID_ARG = "tour_id"
    }
}

@Serializable
data class RemoveRequest(val tourId: Long)

@Serializable
data class IsSavedResponse(val isSaved: Boolean)
