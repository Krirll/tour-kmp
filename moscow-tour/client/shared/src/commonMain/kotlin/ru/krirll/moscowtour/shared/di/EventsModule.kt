package ru.krirll.moscowtour.shared.di

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.shared.data.RemoteEventListenerFactory
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.RemoteEventListener

@Module
class EventsModule {

    @Factory
    @Named(EventType.TICKETS)
    fun provideRecentRemoteEventListener(
        factory: RemoteEventListenerFactory
    ): RemoteEventListener {
        return factory.create(RemoteEventHandler.TICKETS_CHANGED)
    }

    @Factory
    @Named(EventType.SEARCH)
    fun provideSearchRemoteEventListener(
        factory: RemoteEventListenerFactory
    ): RemoteEventListener {
        return factory.create(RemoteEventHandler.SEARCH_CHANGED)
    }

    @Factory
    @Named(EventType.SAVED)
    fun provideSavedRemoteEventListener(
        factory: RemoteEventListenerFactory
    ): RemoteEventListener {
        return factory.create(RemoteEventHandler.SAVED_CHANGED)
    }
}
