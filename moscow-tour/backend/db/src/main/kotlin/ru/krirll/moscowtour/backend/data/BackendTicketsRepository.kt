package ru.krirll.moscowtour.backend.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.server.Tickets
import ru.krirll.moscowtour.server.Tour_details
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.TicketBuilder
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Ticket
import ru.krirll.moscowtour.shared.domain.model.Tour

class BackendTicketsRepository(
    private val db: AppDatabase,
    private val accountId: Long,
    private val ticketBuilder: TicketBuilder,
    private val dispatcherProvider: DispatcherProvider,
    private val eventHandler: RemoteEventHandler
) : TicketsRepository {

    override fun getAll(): Flow<List<Ticket>> {
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
                tour = tour.toModel(),
                accountId = ticket.account_id,
                date = ticket.date
            )
        }
    }

    override suspend fun remove(ticketId: Long) {
        withContext(dispatcherProvider.io) {
            db.ticketsQueries.remove(ticketId)
            notifyChanged(ticketId)
        }
    }

    override suspend fun create(
        tourId: Long,
        personData: PersonData,
        time: Long
    ) {
        withContext(dispatcherProvider.io) {
            val dbTour = db.toursQueries.selectTourById(tourId).executeAsOneOrNull()
                ?: throw IllegalStateException("No tour by id: $tourId")
            val isExists = db.ticketsQueries
                .selectByAccountId(accountId)
                .executeAsList()
                .firstOrNull { it.tour_id == tourId } != null
            //todo тут надо подумать как сделать нормальную проверку, так как могут быть другие паспортные данные
            if (isExists) throw IllegalStateException("Ticket is already exists")
            val tour = dbTour.toModel()
            val externalPath = ticketBuilder.build(tour, personData, time)
            db.ticketsQueries.insert(
                tourId,
                accountId,
                time,
                externalPath
            )
        }
    }

    override suspend fun getFileName(ticketId: Long): String {
        return withContext(dispatcherProvider.io) {
            db.ticketsQueries
                .selectByTicketId(ticketId)
                .executeAsOneOrNull()
                ?.external_path
                ?: throw java.lang.IllegalStateException("No ticket by id: $ticketId")
        }
    }

    private fun Tour_details.toModel(): Tour {
        return Tour(
            id = this.tour_id,
            title = this.title,
            description = this.description,
            city = this.city_name,
            country = this.country_name,
            dateBegin = this.date_begin,
            dateEnd = this.date_end,
            canBuy = this.canBuy,
            price = this.price.toDouble(),
            imagesUrls = db.tour_imagesQueries.selectAllImagesPathsByTourId(this.tour_id)
                .executeAsList().map {
                    "https://tour.krirll.ru/api/tours/images?imageName=$it"
                }
        )
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
