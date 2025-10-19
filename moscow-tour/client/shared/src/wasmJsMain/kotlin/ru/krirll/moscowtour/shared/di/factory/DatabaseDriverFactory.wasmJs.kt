package ru.krirll.moscowtour.shared.di.factory

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import ru.krirll.moscowtour.app.AppDatabase

actual class DatabaseDriverFactory actual constructor() {
    actual suspend fun createDriver(): SqlDriver {
        val driver = createDefaultWebWorkerDriver()
        AppDatabase.Schema.create(driver).await()
        return driver
    }
}
