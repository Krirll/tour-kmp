package ru.krirll.moscowtour.shared.data.token

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Factory
import ru.krirll.domain.KeyValueStorage
import ru.krirll.http.domain.TokenInfo
import ru.krirll.http.domain.TokenStorage

@Factory
class TokenStorageImpl(
    private val storage: KeyValueStorage
) : TokenStorage {
    private val auth = storage.get(KEY)
    private val refresh = storage.get(REFRESH_TOKEN)

    override val realm = "server side data storage"

    override val token: Flow<TokenInfo?> = auth.combine(refresh) { auth, refresh ->
        auth ?: return@combine null
        TokenInfo(auth, refresh)
    }.distinctUntilChanged()

    override suspend fun updateToken(newToken: TokenInfo) {
        storage.put(KEY, newToken.token)
        newToken.refresh?.let { storage.put(REFRESH_TOKEN, it) }
    }

    override suspend fun clear() {
        storage.remove(KEY)
        storage.remove(REFRESH_TOKEN)
    }

    private companion object {
        const val KEY = "auth_token"
        const val REFRESH_TOKEN = "refresh_token"
    }
}
