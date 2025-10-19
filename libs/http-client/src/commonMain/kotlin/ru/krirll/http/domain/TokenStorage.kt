package ru.krirll.http.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface TokenStorage {
    val realm: String
    val token: Flow<TokenInfo?>

    suspend fun updateToken(newToken: TokenInfo)
    suspend fun clear()
}

suspend fun TokenStorage.hasAccount(): Boolean {
    return token.firstOrNull()?.token != null
}
