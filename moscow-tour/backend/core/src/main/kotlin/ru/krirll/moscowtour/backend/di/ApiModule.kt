package ru.krirll.moscowtour.backend.di

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import ru.krirll.moscowtour.backend.data.ExternalToursApi
import ru.krirll.moscowtour.shared.domain.ToursApi

@Module
class ApiModule {

    @Factory
    fun provideExternalToursApi(impl: ExternalToursApi): ToursApi = impl
}
