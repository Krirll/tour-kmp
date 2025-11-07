package ru.krirll.moscowtour.shared.domain

import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named
import ru.krirll.http.domain.LogoutAction
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.di.DbType

@Factory(binds = [LogoutAction::class, LogoutUseCase::class])
class LogoutUseCase(
    private val syncRepository: SyncRepository,
    @Named(DbType.LOCAL) private val localSearchRepo: SearchRepository,
    @Named(DbType.LOCAL) private val localSavedToursRepository: SavedToursRepository,
    private val bearerAuthProvider: BearerAuthProvider,
    private val tokenStorage: TokenStorage,
    private val authTokenRepository: AuthTokenRepository
) : LogoutAction {
    val token = tokenStorage.token

    override suspend fun logout() {
        tokenStorage.token.firstOrNull()?.refresh?.let {
            runCatching { authTokenRepository.revoke(TokenRequest(it)) }
        }
        tokenStorage.clear()
        localSearchRepo.clearAll()
        localSavedToursRepository.clear()
        syncRepository.setSearchSynchronized(false)
        syncRepository.setSavedToursSynchronized(false)
        bearerAuthProvider.clearToken()
    }
}
