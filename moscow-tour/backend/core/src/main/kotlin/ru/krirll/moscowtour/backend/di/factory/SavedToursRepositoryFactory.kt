package ru.krirll.moscowtour.backend.di.factory

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.backend.data.Logger
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.backend.data.BackendRemoteEventHandler
import ru.krirll.moscowtour.backend.data.BackendSavedToursRepository
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.SavedToursRepository

@Factory
class SavedToursRepositoryFactory(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
    private val logger: Logger,
    @Named(EventType.SAVED) private val eventHandler: BackendRemoteEventHandler
) {
    fun create(accountId: Long): SavedToursRepository {
        return BackendSavedToursRepository(db, accountId, dispatcherProvider, logger, eventHandler)
    }
}
