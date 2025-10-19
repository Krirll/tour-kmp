package ru.krirll.http.domain

import io.ktor.client.HttpClient

interface RefreshTokenAction {
    suspend fun refresh(client: HttpClient, refreshToken: String): TokenInfo
}
