package ru.krirll.moscowtour.shared.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Singleton
import ru.krirll.moscowtour.app.AppDatabase
import ru.krirll.moscowtour.shared.di.factory.DatabaseDriverFactory

@Singleton
class AppDatabaseProvider {
    private val mutex = Mutex()
    private var appDatabase: AppDatabase? = null

    suspend fun get(): AppDatabase {
        appDatabase?.let { return it }
        mutex.withLock {
            appDatabase?.let { return it }
            val driver = DatabaseDriverFactory().createDriver()
            //AppDatabase.Schema.migrate(driver, 2, 3)
            return AppDatabase.invoke(driver).also { appDatabase = it }
        }
    }
}
