package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Ticket

interface TicketsRepository {
    fun getAll(): Flow<List<Ticket>>
    suspend fun remove(ticketId: Long)
    suspend fun create(tourId: Long, personData: PersonData, time: Long)
    suspend fun getFilePath(ticketId: Long): String

    companion object {
        const val PREFIX = "$DYNAMIC_PREFIX/tickets"
        const val QUERY_ALL = "$PREFIX/query_all"
        const val DELETE = "$PREFIX/delete"
        const val CREATE = "$PREFIX/create"
        const val GET_DOWNLOAD_URL = "$PREFIX/downloadUrl"
        const val TICKET_ID_ARG = "ticketId"
        const val DOWNLOAD = "$PREFIX/download"
        const val FILE_PATH_ARG = "fileName"
    }
}

@Serializable
data class RemoveTicketRequest(val ticketId: Long)

@Serializable
data class CreateTicketRequest(val tourId: Long, val personData: PersonData, val time: Long)

@Serializable
data class DownloadUrlResponse(val url: String)

@Serializable
data class DownloadTicketRequest(val url: String)
