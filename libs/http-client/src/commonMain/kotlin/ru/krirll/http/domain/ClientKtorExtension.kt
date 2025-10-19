package ru.krirll.http.domain

import io.ktor.client.HttpClientConfig

interface ClientKtorExtension {
    fun append(clientConfig: HttpClientConfig<*>)
}
