package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.server.Tickets
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.model.Ticket
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.time.LocalTime
import java.time.ZoneOffset

class BackendTicketsRepository(
    private val db: AppDatabase,
    private val accountId: Long,
    private val dispatcherProvider: DispatcherProvider,
    private val eventHandler: RemoteEventHandler
) : TicketsRepository {

    override suspend fun getAll(): Flow<List<Ticket>> {
        val request = db.ticketsQueries.selectByAccountId(accountId)
        return flow {
            emit(request.executeAsList().parse())
        }
    }

    private suspend fun List<Tickets>.parse(): List<Ticket> = withContext(dispatcherProvider.io) {
        mapNotNull { ticket ->
            val tour = db.toursQueries.selectTourById(ticket.tour_id)
                .executeAsOneOrNull() ?: return@mapNotNull null
            Ticket(
                ticketId = ticket.ticket_id,
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
                ),
                accountId = ticket.account_id,
                date = ticket.date.toEpochSecond(LocalTime.now(), ZoneOffset.UTC),
                downloadUrl = ticket.download_url
            )
        }
    }

    override suspend fun remove(ticketId: Long) {
        withContext(dispatcherProvider.io) {
            db.ticketsQueries.remove(ticketId)
            notifyChanged(ticketId)
        }
    }

    private suspend fun notifyChanged(ticketId: Long) {
        eventHandler.notify(
            RemoteEvent.OnTicket(
                System.currentTimeMillis(),
                accountId,
                ticketId
            )
        )
    }
}
