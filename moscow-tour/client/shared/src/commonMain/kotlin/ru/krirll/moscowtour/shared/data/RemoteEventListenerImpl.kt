package ru.krirll.moscowtour.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import ru.krirll.domain.Log
import ru.krirll.http.domain.TokenStorage
import ru.krirll.moscowtour.shared.domain.RemoteEvent
import ru.krirll.moscowtour.shared.domain.RemoteEventListener
import ru.krirll.moscowtour.shared.domain.ServerConfigurationRepository
import ru.krirll.moscowtour.shared.domain.getServerConfiguration
import kotlin.math.pow

@Factory
class RemoteEventListenerFactory(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val authTokenCache: TokenStorage,
    private val json: Json,
    private val log: Log
) {
    fun create(suffix: String): RemoteEventListener {
        return RemoteEventListenerImpl(
            httpClient,
            serverConfigurationRepository,
            authTokenCache,
            json,
            log,
            suffix
        )
    }
}

private class RemoteEventListenerImpl(
    private val httpClient: HttpClient,
    private val serverConfigurationRepository: ServerConfigurationRepository,
    private val authTokenCache: TokenStorage,
    private val json: Json,
    private val log: Log,
    private val suffix: String
) : RemoteEventListener {
    private val eventFlow = flow {
        val config = serverConfigurationRepository.getServerConfiguration()
        val token = authTokenCache.token.first() ?: return@flow
        log.d(TAG, "listener $suffix started")
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                request = { url(config.asWebSocketStr() + "/$suffix") }
            ) {
                outgoing.send(Frame.Text(token.token))
                while (currentCoroutineContext().isActive) {
                    val text = (incoming.receive() as? Frame.Text)?.readText()
                    if (text != null) {
                        val decoded = json.decodeFromString<RemoteEvent>(text)
                        log.d(TAG, "receive $text $decoded")
                        emit(decoded)
                    }
                }
            }
        } catch (e: Exception) {
            log.e(TAG, e, "websocket closed")
            throw e
        } finally {
            log.d(TAG, "listener $suffix finished")
        }
    }

    override val event: Flow<RemoteEvent> = eventFlow.retryExponential { true }

    private fun <T> Flow<T>.retryExponential(
        maxRetries: Int = Int.MAX_VALUE,
        initialDelay: Long = 5000L,
        maxDelay: Long = 300_000L,
        factor: Double = 2.0,
        shouldRetry: (Throwable) -> Boolean = { true }
    ): Flow<T> = retryWhen { cause, attempt ->
        log.d(TAG, "error occurred $suffix", cause)
        if (!shouldRetry(cause) || attempt >= maxRetries) {
            false
        } else {
            val delayTime = (initialDelay * factor.pow(attempt.toDouble()))
                .toLong()
                .coerceAtMost(maxDelay)
            log.d(TAG, "retry after delay $delayTime", cause)
            delay(delayTime)
            true
        }
    }

    private companion object {
        const val TAG = "RemoteEventListener"
    }
}
