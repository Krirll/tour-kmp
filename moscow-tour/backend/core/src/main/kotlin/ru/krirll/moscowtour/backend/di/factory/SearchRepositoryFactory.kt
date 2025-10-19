package ru.krirll.moscowtour.backend.di.factory

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.backend.data.BackendSearchRepository
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import ru.krirll.moscowtour.shared.domain.SearchRepository

@Factory
class SearchRepositoryFactory(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
    @Named(EventType.SEARCH) private val eventHandler: RemoteEventHandler
) {
    fun create(accountId: Long): SearchRepository {
        return BackendSearchRepository(db, accountId, dispatcherProvider, eventHandler)
    }
}
