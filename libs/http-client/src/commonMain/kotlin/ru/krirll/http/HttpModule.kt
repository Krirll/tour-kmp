package ru.krirll.http

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import ru.krirll.http.data.HttpClientFactory
import ru.krirll.http.data.HttpClientSettings
import ru.krirll.http.data.RefreshTokenRunner
import ru.krirll.http.domain.HttpException
import ru.krirll.http.domain.LogoutAction
import ru.krirll.http.domain.RefreshTokenAction
import ru.krirll.http.domain.TokenInfo
import ru.krirll.http.domain.TokenStorage
import ru.krirll.koin

@ComponentScan
@Module
class HttpModule {

    @Singleton
    fun createHttpClient(settings: HttpClientSettings): HttpClient {
        return HttpClientFactory(settings).create()
    }

    @Singleton
    fun createBearerAuthProvider(
        refreshTokenRunner: RefreshTokenRunner
    ): BearerAuthProvider {
        val tokenStorage = koin.getOrNull<TokenStorage>()
        return BearerAuthProvider(
            refreshTokens = {
                refreshTokenRunner.runOrJoin {
                    refreshTokenInternal(tokenStorage)
                }
            },
            loadTokens = {
                tokenStorage?.token?.firstOrNull()?.toKtor()
            },
            realm = tokenStorage?.realm
        )
    }

    private suspend fun RefreshTokensParams.refreshTokenInternal(tokenStorage: TokenStorage?): BearerTokens? {
        val token = tokenStorage?.token?.firstOrNull()?.refresh ?: return null
        return try {
            val newToken = koin.getOrNull<RefreshTokenAction>()
                ?.refresh(this.client, token)
                ?: return null
            tokenStorage.updateToken(newToken)
            newToken.toKtor()
        } catch (e: Throwable) {
            if (e is HttpException) {
                koin.getOrNull<LogoutAction>()?.logout(-1) //todo получать айди авторизованного пользователя
            }
            null
        }
    }
}

private fun TokenInfo.toKtor(): BearerTokens {
    return BearerTokens(token, refresh)
}
