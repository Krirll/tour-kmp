package ru.krirll.moscowtour.backend

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.postgresql.ds.PGSimpleDataSource
import ru.krirll.backend.data.EnvFetcher

@Module
@ComponentScan
class DbModule {

    @Factory
    fun provideSqlDriver(envFetcher: EnvFetcher): SqlDriver {
        return PGSimpleDataSource().apply {
            val db = envFetcher.get("POSTGRES_DB", "moscowtour")
            val uri = envFetcher.get("MOSCOWTOUR_SERVER_URI", "localhost")
            val login = envFetcher.get("POSTGRES_USER", "login")
            val pass = envFetcher.get("POSTGRES_PASSWORD", "password")

            setUrl("jdbc:postgresql://$uri:5432/$db")
            user = login
            password = pass
        }.asJdbcDriver()
    }

    @Single
    fun provideDb(driver: SqlDriver): AppDatabase {
        return AppDatabase.invoke(driver)
    }
}
