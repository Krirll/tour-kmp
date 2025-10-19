package ru.krirll.http.data

import io.ktor.client.HttpClient

expect class HttpClientFactory(settings: HttpClientSettings) {
    fun create(): HttpClient
}
