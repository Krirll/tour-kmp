package ru.krirll.moscowtour.shared.di.factory

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.krirll.moscowtour.app.AppDatabase
import ru.krirll.moscowtour.shared.di.koin

actual class DatabaseDriverFactory {

    actual suspend fun createDriver(): SqlDriver {
        val app = koin.get<Application>()
        return AndroidSqliteDriver(AppDatabase.Schema, app, "moscowtour.db")
    }
}
