package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.time.LocalTime
import java.time.ZoneOffset

@Factory(binds = [ExternalToursApi::class])
class ExternalToursApi(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider
) : ToursApi {

    override suspend fun fetchTours(): List<Tour> = withContext(dispatcherProvider.io) {
        db.toursQueries.selectAllTours().executeAsList().map {
            Tour(
                id = it.tour_id,
                title = it.title,
                description = it.description,
                city = it.city_name,
                country = it.country_name,
                dateBegin = it.date_begin.toEpochSecond(LocalTime.now(), ZoneOffset.UTC),
                dateEnd = it.date_end.toEpochSecond(LocalTime.now(), ZoneOffset.UTC),
                canBuy = it.canBuy,
                price = it.price.toDouble(),
                imagesUrls = it.images.toList()
            )
        }
    }
}
