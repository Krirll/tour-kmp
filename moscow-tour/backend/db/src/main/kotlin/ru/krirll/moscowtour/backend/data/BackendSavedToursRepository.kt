package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.krirll.backend.data.Logger
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.server.Saved_tours
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.model.SavedTour
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.sql.SQLException
import java.time.LocalTime
import java.time.ZoneOffset

class BackendSavedToursRepository(
    private val db: AppDatabase,
    private val accountId: Long,
    private val dispatcherProvider: DispatcherProvider,
    private val logger: Logger,
    private val eventHandler: RemoteEventHandler
) : SavedToursRepository {

    override fun getAll(): Flow<List<SavedTour>> {
        val request = db.saved_toursQueries.selectByAccountId(accountId)
        return flow {
            emit(request.executeAsList().parse())
        }
    }

    private suspend fun List<Saved_tours>.parse(): List<SavedTour> = withContext(dispatcherProvider.io) {
        mapNotNull { savedTour ->
            val tour = db.toursQueries.selectTourById(savedTour.tour_id)
                .executeAsOneOrNull() ?: return@mapNotNull null
            SavedTour(
                savedTourId = savedTour.saved_tour_id,
                tour = Tour(
                    id = tour.tour_id,
                    title = tour.title,
                    description = tour.description,
                    city = tour.city_name,
                    country = tour.country_name,
                    dateBegin = tour.date_begin.toEpochSecond(LocalTime.now(), ZoneOffset.UTC),
                    dateEnd = tour.date_end.toEpochSecond(LocalTime.now(), ZoneOffset.UTC),
                    canBuy = tour.canBuy,
                    price = tour.price.toDouble(),
                    imagesUrls = tour.images.toList()
                )
            )
        }
    }

    override suspend fun save(tour: Tour) {
        logger.debug(TAG, "save(${tour.id}) ${tour.title}")
        withContext(dispatcherProvider.io) {
            try {
                db.saved_toursQueries.addSavedTour(
                    tour.id,
                    accountId
                )
                eventHandler.notify(RemoteEvent.OnSaved(accountId, tour.id))
            } catch (_: SQLException) {
            }
        }
    }

    override suspend fun remove(tourId: Long) {
        logger.debug(TAG, "remove(${tourId})")
        withContext(dispatcherProvider.io) {
            db.saved_toursQueries.removeSavedTour(accountId, tourId)
            eventHandler.notify(RemoteEvent.OnSaved(accountId, tourId))
        }
    }

    private companion object {
        const val TAG = "BackendSavedToursRepository"
    }
}
