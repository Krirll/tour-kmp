package ru.krirll.moscowtour.shared.data.saved

import app.cash.sqldelight.Query
import app.cash.sqldelight.async.coroutines.awaitAsList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.domain.SavedTours
import ru.krirll.moscowtour.shared.data.AppDatabaseProvider
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour

@Factory(binds = [SqlSavedToursRepository::class])
class SqlSavedToursRepository(
    private val dbProvider: AppDatabaseProvider
) : SavedToursRepository {

    override fun getAll(): Flow<List<SavedTour>> {
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

    override suspend fun save(tour: Tour) {
        val canSave = dbProvider.get().savedToursQueries.selectAll().awaitAsList()
            .none { it.tour_id == tour.id }
        if (canSave) {
            dbProvider.get().savedToursQueries.insertSavedTour(
                tour.id,
                tour.title,
                tour.description,
                tour.city,
                tour.country,
                tour.dateBegin.toString(),
                tour.dateEnd.toString(),
                if (tour.canBuy) 1 else 0,
                tour.price,
                tour.imagesUrls.joinToString(",")
            )
        }
    }

    override suspend fun remove(tourId: Long) {
        dbProvider.get().savedToursQueries.deleteSavedTour(tourId)
    }

    override suspend fun isSaved(tourId: Long): Flow<Boolean> {
        return getAll().map { savedTour -> savedTour.any { tourId == it.tour.id } }
    }

    private suspend fun obtain(): List<SavedTour> {
        val request = getRequest()
        return request.awaitAsList().map {
            SavedTour(
                savedTourId = it.saved_tour_id,
                Tour(
                    it.tour_id,
                    it.title,
                    it.description,
                    it.city_name,
                    it.country_name,
                    it.date_begin.toLong(),
                    it.date_end.toLong(),
                    it.can_buy == 1L,
                    it.price,
                    it.image_urls.split(",")
                )
            )

        }
    }

    private suspend fun getRequest(): Query<SavedTours> {
        return dbProvider.get().savedToursQueries.selectAll()
    }
}
