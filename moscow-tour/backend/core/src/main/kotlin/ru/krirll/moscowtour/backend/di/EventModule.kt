package ru.krirll.moscowtour.backend.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import ru.krirll.moscowtour.backend.data.BackendRemoteEventHandler
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler

@Module
class EventModule {

    @Single(binds = [BackendRemoteEventHandler::class])
    @Named(EventType.SEARCH)
    fun provideSearch(impl: BackendRemoteEventHandler): RemoteEventHandler = impl

    @Single(binds = [BackendRemoteEventHandler::class])
    @Named(EventType.TICKETS)
    fun provideRecent(impl: BackendRemoteEventHandler): RemoteEventHandler = impl

    @Single(binds = [BackendRemoteEventHandler::class])
    @Named(EventType.SAVED)
    fun provideSaved(impl: BackendRemoteEventHandler): RemoteEventHandler = impl
}
