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
import ru.krirll.moscowtour.shared.domain.TicketFactory
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Ticket
import ru.krirll.moscowtour.shared.domain.model.TicketFile
import ru.krirll.moscowtour.shared.domain.model.Tour
import java.security.MessageDigest

class BackendTicketsRepository(
    private val db: AppDatabase,
    private val accountId: Long,
    private val ticketFactory: TicketFactory,
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

    override suspend fun createAndDownload(
        tourId: Long,
        personData: PersonData,
        time: Long
    ): TicketFile = withContext(dispatcherProvider.io) {
        val dbTour = db.toursQueries.selectTourById(tourId).executeAsOneOrNull()
            ?: throw IllegalStateException("No tour by id: $tourId")

        val tickets = db.ticketsQueries
            .selectByAccountId(accountId)
            .executeAsList()
        val currentHash = personData.hash()
        val dbTicket = tickets.firstOrNull {
            it.tour_id == tourId && it.person_data_hash == currentHash
        }
        val tour = dbTour.toModel()
        if (dbTicket == null) {
            db.ticketsQueries.insert(
                tourId,
                accountId,
                time,
                currentHash
            )
        }
        ticketFactory.create(tour, personData, time, dbTicket?.date)
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

    private fun PersonData.hash(): String {
        val rawData = listOf(
            lastName.lowercase(),
            firstName.lowercase(),
            middleName,
            passportSeries,
            passportNumber,
            phone
        ).joinToString("|")

        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawData.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
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
