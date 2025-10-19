package ru.krirll.moscowtour.backend.data.auth

import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import ru.krirll.backend.data.JwtPreparer
import ru.krirll.backend.data.StringFetcher
import ru.krirll.backend.data.StringResource
import ru.krirll.backend.domain.LoginVerifier
import ru.krirll.domain.Log
import ru.krirll.http.domain.TokenInfo
import ru.krirll.moscowtour.backend.AppDatabase
import ru.krirll.moscowtour.server.Refresh_tokens
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.http.domain.BadRequestException
import ru.krirll.moscowtour.shared.domain.ChangePasswordRequest
import ru.krirll.moscowtour.shared.domain.TokenRequest
import ru.krirll.moscowtour.shared.domain.model.LoginInfo
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64

@Factory
class AuthTokenRepositoryFactory(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
    private val jwtPreparer: JwtPreparer,
    private val loginVerifier: LoginVerifier,
    private val log: Log,
    private val stringFetcher: StringFetcher
) {

    fun create(userId: Long = -1): AuthTokenRepository {
        return BackendAuthTokenRepository(
            db,
            dispatcherProvider,
            jwtPreparer,
            loginVerifier,
            userId,
            log,
            stringFetcher
        )
    }
}

class BackendAuthTokenRepository(
    private val db: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
    private val jwtPreparer: JwtPreparer,
    private val loginVerifier: LoginVerifier,
    private val userId: Long,
    private val log: Log,
    private val stringFetcher: StringFetcher
) : AuthTokenRepository {
    private val random = SecureRandom()

    override suspend fun register(loginInfo: LoginInfo): TokenInfo {
        throw BadRequestException(stringFetcher.get(StringResource.REG_BLOCKED))
    }

    override suspend fun login(loginInfo: LoginInfo): TokenInfo =
        withContext(dispatcherProvider.io) {
            val userInfo =
                db.accountsQueries.selectAccountByLogin(loginInfo.login).executeAsOneOrNull()
                    ?: throw BadRequestException(stringFetcher.get(StringResource.UNKNOWN_USER))
            if (loginInfo.passwordHash != userInfo.password_hash) {
                throw BadRequestException(stringFetcher.get(StringResource.UNKNOWN_USER))
            }
            create(loginInfo.login)
        }

    override suspend fun update(refresh: TokenRequest): TokenInfo {
        val info = db.refresh_tokensQueries.selectByHash(refresh.token.toHash())
            .executeAsOneOrNull()
            ?: throw BadRequestException("Token not found")
        val userInfo =
            db.accountsQueries.selectAccountByAccountId(info.account_id).executeAsOneOrNull()
                ?: throw BadRequestException("User not found")
        val newTokens = create(userInfo.login)
        revokeInternal(refresh)
        return newTokens
    }

    override suspend fun revoke(tokenRequest: TokenRequest) {
        revokeInternal(tokenRequest)
    }

    private suspend fun revokeInternal(request: TokenRequest) {
        log.d("BackendAuthTokenRepository", "revoke ${request.token.toHash()}")
        val info = getTokenAndCheck(request)
        db.refresh_tokensQueries.revokeToken(info.token_id).await()
    }

    private suspend fun getTokenAndCheck(tokenRequest: TokenRequest): Refresh_tokens =
        withContext(dispatcherProvider.io) {
            val info = db.refresh_tokensQueries.selectByHash(tokenRequest.token.toHash())
                .executeAsOneOrNull()
                ?: throw BadRequestException("Token not found")
            if (info.revoked) throw BadRequestException("Token already revoked")
            if (info.expires_at < System.currentTimeMillis()) {
                throw BadRequestException(
                    "Refresh token expired. " +
                            "Expires at ${info.expires_at} < ${System.currentTimeMillis()}"
                )
            }
            info
        }

    private suspend fun create(login: String): TokenInfo = withContext(dispatcherProvider.io) {
        loginVerifier.verify(login)
        val userInfo = db.accountsQueries.selectAccountByLogin(login).executeAsOneOrNull()
            ?: throw BadRequestException(stringFetcher.get(StringResource.UNKNOWN_USER))
        val rsp = jwtPreparer.prepare(login, userInfo.account_id)
        val refresh = ByteArray(32)
        random.nextBytes(refresh)
        val refreshStr = refresh.toHexString()
        db.refresh_tokensQueries.insertToken(
            userInfo.account_id,
            refreshStr.toHash(),
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
        ).executeAsOneOrNull()
        log.d(
            "BackendAuthTokenRepository",
            "${refreshStr.toHash()} created for user ${userInfo.account_id}"
        )
        TokenInfo(rsp, refreshStr)
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Unit =
        withContext(dispatcherProvider.io) {
            val user = db.accountsQueries.selectAccountByAccountId(userId).executeAsOneOrNull()
                ?: throw BadRequestException(stringFetcher.get(StringResource.UNKNOWN_USER))
            if (user.password_hash != request.oldPasswordHash) {
                throw BadRequestException(stringFetcher.get(StringResource.INVALID_PASSWORD))
            } else if (user.password_hash == request.newPasswordHash) {
                throw BadRequestException(stringFetcher.get(StringResource.NO_CHANGES_PASS))
            }
            db.accountsQueries.updatePassword(request.newPasswordHash, userId).await()
        }

    private fun String.toHash(): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(SALT.toByteArray())
        val result = md.digest(this.toByteArray())
        return Base64.encode(result)
    }

    private companion object {
        const val SALT = "G0yCPoy7gCj1a5OxPaeYLeJm69NvPQ50HbV0ZVYVqahUPdKun4MKgg86u9HbWq2e"
    }
}
