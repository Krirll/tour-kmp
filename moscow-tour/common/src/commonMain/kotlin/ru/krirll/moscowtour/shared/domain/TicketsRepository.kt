package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Ticket

interface TicketsRepository {
    fun getAll(): Flow<List<Ticket>>
    suspend fun remove(ticketId: Long)
    suspend fun createAndDownload(tourId: Long, personData: PersonData, time: Long)

    companion object {
        const val PREFIX = "$DYNAMIC_PREFIX/tickets"
        const val QUERY_ALL = "$PREFIX/query_all"
        const val DELETE = "$PREFIX/delete"
        const val CREATE_AND_DOWNLOAD = "$PREFIX/createAndDownload"
    }
}

@Serializable
data class RemoveTicketRequest(val ticketId: Long)

@Serializable
data class CreateTicketRequest(val tourId: Long, val personData: PersonData, val time: Long)
