package ru.krirll.moscowtour.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.koin.core.annotation.Factory
import ru.krirll.moscowtour.shared.domain.HeathCheck
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration

@Factory
class HeathCheckImpl(
    private val httpClient: HttpClient,
    private val serverConfigurationProvider: ServerConfigurationRepository,
) : HeathCheck {

    override suspend fun check() {
        httpClient.get {
            serverConfigurationProvider.getServerConfiguration()
                .apply(this, HeathCheck.PATH)
        }
    }
}
