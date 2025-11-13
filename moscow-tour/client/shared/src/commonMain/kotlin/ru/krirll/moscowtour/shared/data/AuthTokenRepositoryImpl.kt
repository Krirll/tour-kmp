package ru.krirll.moscowtour.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.utils.io.core.toByteArray
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.HttpException
import ru.krirll.http.domain.TokenInfo
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.moscowtour.shared.domain.ChangePasswordRequest
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.TokenRequest
import ru.krirll.moscowtour.shared.domain.getServerConfiguration
import ru.krirll.moscowtour.shared.domain.model.EmptyLoginException
import ru.krirll.moscowtour.shared.domain.model.EmptyPasswordException
import ru.krirll.moscowtour.shared.domain.model.IncorrectLoginException
import ru.krirll.moscowtour.shared.domain.model.IncorrectPasswordException
import ru.krirll.moscowtour.shared.domain.model.LoginInfo
import ru.krirll.moscowtour.shared.domain.model.ServerLoginException
import ru.krirll.moscowtour.shared.domain.model.UnknownLoginException

@Factory
class AuthTokenRepositoryImpl(
    private val httpClient: HttpClient,
    private val serverConfigurationProvider: ServerConfigurationRepository,
    private val tokenCache: TokenStorage,
    private val hashCalculator: HmacSha512Calc
) : AuthTokenRepository {

    override suspend fun register(loginInfo: LoginInfo): TokenInfo {
        return post(
            loginInfo,
            AuthTokenRepository.REGISTER_PATH,
            saveToCache = false,
            needCheck = true
        )
    }

    override suspend fun login(loginInfo: LoginInfo): TokenInfo {
        return post(
            loginInfo,
            AuthTokenRepository.LOGIN_PATH,
            saveToCache = true,
            needCheck = false
        )
    }

    override suspend fun update(refresh: TokenRequest): TokenInfo {
        return httpClient.post {
            serverConfigurationProvider.getServerConfiguration()
                .apply(this, AuthTokenRepository.UPDATE_PATH)
            setJsonBody(refresh)
        }.body<TokenInfo>()
    }

    override suspend fun changePassword(request: ChangePasswordRequest) {
        val oldPassHash = hashCalculator.calc(request.oldPasswordHash.toByteArray())
        val newPassHash = hashCalculator.calc(request.newPasswordHash.toByteArray())
        httpClient.post {
            serverConfigurationProvider.getServerConfiguration()
                .apply(this, AuthTokenRepository.CHANGE_PASS_PATH)
            setJsonBody(ChangePasswordRequest(oldPassHash.toHexString(), newPassHash.toHexString()))
        }
    }

    override suspend fun revoke(tokenRequest: TokenRequest) {
        httpClient.post {
            serverConfigurationProvider.getServerConfiguration()
                .apply(this, AuthTokenRepository.REVOKE_PATH)
            setJsonBody(tokenRequest)
        }
    }

    override suspend fun delete(tokenRequest: TokenRequest) {
        httpClient.delete {
            serverConfigurationProvider.getServerConfiguration()
                .apply(this, AuthTokenRepository.DELETE_PATH)
            setJsonBody(tokenRequest)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun post(
        loginInfo: LoginInfo,
        path: String,
        saveToCache: Boolean,
        needCheck: Boolean
    ): TokenInfo {
        if (loginInfo.login.trim().isEmpty()) {
            throw EmptyLoginException()
        } else if (loginInfo.passwordHash.trim().isEmpty()) {
            throw EmptyPasswordException()
        }
        if (needCheck) {
            val loginRegex = "^(?=.{3,20}$)[A-Za-z0-9](?:[A-Za-z0-9._-]*[A-Za-z0-9])?$".toRegex()
            if (!loginInfo.login.matches(loginRegex)) {
                throw IncorrectLoginException()
            }
            val passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,30}$".toRegex()
            if (!loginInfo.passwordHash.matches(passRegex)) {
                throw IncorrectPasswordException()
            }
        }
        val realPassHash = hashCalculator.calc(loginInfo.passwordHash.toByteArray())
        try {
            return httpClient.post {
                serverConfigurationProvider.getServerConfiguration().apply(this, path)
                setJsonBody(loginInfo.copy(passwordHash = realPassHash.toHexString()))
            }.body<TokenInfo>().apply {
                if (saveToCache) {
                    tokenCache.updateToken(this)
                }
            }
        } catch (e: HttpException) {
            throw ServerLoginException(e.message ?: "Unknown error")
        } catch (e: Exception) {
            throw UnknownLoginException(e)
        }
    }
}
