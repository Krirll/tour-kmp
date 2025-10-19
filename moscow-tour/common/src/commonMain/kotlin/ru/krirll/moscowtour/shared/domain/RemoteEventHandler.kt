package ru.krirll.moscowtour.shared.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface RemoteEventHandler {
    suspend fun notify(eventInfo: RemoteEvent)

    companion object {
        const val SEARCH_CHANGED = "${SearchRepository.PREFIX}/onChange"
        const val SAVED_CHANGED = "${SavedToursRepository.PREFIX}/onChange"
        const val TICKETS_CHANGED = "${TicketsRepository.PREFIX}/onChange"
    }
}

interface RemoteEventListener {
    val event: Flow<RemoteEvent>
}

@Serializable
sealed interface RemoteEvent {
    val accountId: Long

    @Serializable
    data class OnSearch(val millis: Long, override val accountId: Long) : RemoteEvent

    @Serializable
    data class OnTicket(val millis: Long, override val accountId: Long, val tourId: Long) : RemoteEvent

    @Serializable
    data class OnSaved(override val accountId: Long, val tourId: Long) : RemoteEvent
}

object EventType {
    const val SEARCH = "search"
    const val SAVED = "saved"
    const val TICKETS = "tickets"
}
