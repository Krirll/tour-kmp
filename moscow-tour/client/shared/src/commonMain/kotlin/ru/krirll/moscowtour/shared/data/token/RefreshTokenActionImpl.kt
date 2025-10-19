package ru.krirll.moscowtour.shared.data.token

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.RefreshTokenAction
import ru.krirll.http.domain.TokenInfo
import ru.krirll.moscowtour.shared.data.apply
import ru.krirll.moscowtour.shared.data.setJsonBody
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.TokenRequest
import ru.krirll.moscowtour.shared.domain.getServerConfiguration

@Factory
class RefreshTokenActionImpl(
    private val serverConfigurationProvider: ServerConfigurationRepository
) : RefreshTokenAction {
    override suspend fun refresh(client: HttpClient, refreshToken: String): TokenInfo {
        val result = client.post {
            serverConfigurationProvider.getServerConfiguration().apply(this, AuthTokenRepository.UPDATE_PATH)
            setJsonBody(TokenRequest(refreshToken))
        }
        return result.body()
    }
}
