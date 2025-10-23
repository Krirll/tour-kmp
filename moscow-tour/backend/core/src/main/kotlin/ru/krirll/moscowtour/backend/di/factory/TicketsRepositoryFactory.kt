package ru.krirll.moscowtour.backend.di.factory

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.backend.data.BackendRemoteEventHandler
import ru.krirll.moscowtour.backend.data.BackendTicketsRepository
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.TicketBuilder
import ru.krirll.moscowtour.shared.domain.TicketsRepository

@Factory
class TicketsRepositoryFactory(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
    private val ticketBuilder: TicketBuilder,
    @Named(EventType.TICKETS) private val eventHandler: BackendRemoteEventHandler
) {
    fun create(accountId: Long): TicketsRepository {
        return BackendTicketsRepository(db, accountId, ticketBuilder, dispatcherProvider, eventHandler)
    }
}
