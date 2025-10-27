package ru.krirll.moscowtour.shared.data.api

import io.ktor.client.HttpClient
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.data.get
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.ToursApi
import ru.krirll.moscowtour.shared.domain.getServerConfiguration
import ru.krirll.moscowtour.shared.domain.model.Tour

@Factory(binds = [ToursApiImpl::class])
class ToursApiImpl(
    private val httpClient: HttpClient,
    private val serverConfigurationProvider: ServerConfigurationRepository
) : ToursApi {

    override suspend fun fetchTours(): List<Tour> {
        return get(ToursApi.TOURS_PATH)
    }

    private suspend inline fun <reified T> get(path: String): T {
        return httpClient.get(
            path,
            emptyMap(),
            serverConfigurationProvider.getServerConfiguration()
        )
    }
}
