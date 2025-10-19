package ru.krirll.http.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

actual class HttpClientFactory actual constructor(private val settings: HttpClientSettings) {
    actual fun create(): HttpClient {
        return HttpClient(Js) {
            settings.setup(this)
        }
    }
}
