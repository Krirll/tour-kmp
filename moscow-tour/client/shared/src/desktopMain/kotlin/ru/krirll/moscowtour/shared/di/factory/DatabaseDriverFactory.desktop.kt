package ru.krirll.moscowtour.shared.di.factory

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory actual constructor() {
    actual suspend fun createDriver(): SqlDriver {
        throw NotImplementedError()
    }
}
