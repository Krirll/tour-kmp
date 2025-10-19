package ru.krirll.moscowtour.shared.di

import org.koin.core.annotation.Module
import ru.krirll.moscowtour.shared.data.ticket.RemoteTicketsRepository
import ru.krirll.moscowtour.shared.domain.TicketsRepository

@Module
class TicketsModule {

    fun provideTicketsRemoteRepository(impl: RemoteTicketsRepository): TicketsRepository {
        return impl
    }
}
