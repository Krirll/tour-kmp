package ru.krirll.moscowtour.backend

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.krirll.backend.BackendCommon
import ru.krirll.backend.data.EnvFetcher
import ru.krirll.backend.domain.JwtInfo
import ru.krirll.moscowtour.backend.di.EventModule

@Module
class MoscowtourModule {

    @Single
    fun provideJwtInfo(envFetcher: EnvFetcher): JwtInfo {
        return JwtInfo(
            envFetcher.get("MOSCOWTOUR_SECRET", "secret"),
            "https://krirll.ru/",
            "https://tour.krirll.ru/",
            "server side data storage"
        )
    }
}

@Module(
    includes = [
        DbModule::class,
        EventModule::class,
        BackendCommon::class,
        MoscowtourModule::class
    ]
)
@ComponentScan
class BackendModule
