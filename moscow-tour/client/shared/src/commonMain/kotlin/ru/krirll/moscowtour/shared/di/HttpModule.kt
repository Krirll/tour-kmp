package ru.krirll.moscowtour.shared.di

import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import ru.krirll.moscowtour.shared.data.AppDatabaseProvider
import ru.krirll.moscowtour.shared.data.api.FallbackToursApi
import ru.krirll.moscowtour.shared.data.api.ToursApiImpl
import ru.krirll.moscowtour.shared.domain.ToursApi

@Module
class HttpModule {

    @Singleton
    fun provideToursApi(
        impl: ToursApiImpl,
        json: Json,
        dbProvider: AppDatabaseProvider
    ): ToursApi {
        return FallbackToursApi(dbProvider, impl, json)
    }
}
