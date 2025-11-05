package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour

@Factory(binds = [ExternalToursApi::class])
class ExternalToursApi(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider
) : ToursApi {

    override suspend fun fetchTours(): List<Tour> = withContext(dispatcherProvider.io) {
        db.toursQueries.selectAllTours().executeAsList().map { tour ->
            val images = db.tour_imagesQueries.selectAllImagesPathsByTourId(tour.tour_id)
                .executeAsList()
            Tour(
                id = tour.tour_id,
                title = tour.title,
                description = tour.description,
                city = tour.city_name,
                country = tour.country_name,
                dateBegin = tour.date_begin,
                dateEnd = tour.date_end,
                canBuy = tour.canBuy,
                price = tour.price.toDouble(),
                imagesUrls = images.map { "https://tour.krirll.ru/api/tours/images?imageName=$it" }
            )
        }
    }
}
