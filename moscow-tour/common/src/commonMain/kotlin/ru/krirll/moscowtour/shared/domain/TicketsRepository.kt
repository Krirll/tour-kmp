package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.Ticket

interface TicketsRepository {
    suspend fun getAll(): Flow<List<Ticket>>
    suspend fun remove(ticketId: Long)

    companion object {
        const val PREFIX = "$DYNAMIC_PREFIX/tickets"
        const val QUERY_ALL = "$PREFIX/query_all"
        const val DELETE = "$PREFIX/delete"
    }
}

@Serializable
data class RemoveTicketRequest(val ticketId: Long)
