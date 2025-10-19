package ru.krirll.http.data

import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Factory
import ru.krirll.http.domain.ClientKtorExtension
import ru.krirll.http.domain.HttpException
import ru.krirll.http.domain.UserAgentProvider

const val USE_HTTP_CUSTOM_CERTS = "use_http_custom_certs"

private const val MESSAGE_LONG = "message"
private const val MESSAGE = "msg"

@Factory
class HttpClientSettings(
    private val json: Json,
    private val userAgentProvider: UserAgentProvider?,
    private val bearerAuthProvider: BearerAuthProvider,
    private val appenderList: List<ClientKtorExtension>
) {

    fun setup(
        clientConfig: HttpClientConfig<*>
    ) = with(clientConfig) {
        install(Auth) {
            providers.add(bearerAuthProvider)
        }
        expectSuccess = false

        HttpResponseValidator {
            validateResponse {
                val code = it.status.value
                if (code in 400..599) {
                    val body = it.body<JsonElement>()
                    if (body is JsonObject) {
                        val msg = when {
                            body.containsKey(MESSAGE_LONG) -> body[MESSAGE_LONG]?.jsonPrimitive?.content
                            body.containsKey(MESSAGE) -> body[MESSAGE]?.jsonPrimitive?.content
                            else -> body.toString()
                        }
                        throw HttpException(code, msg)
                    } else {
                        throw HttpException(code, body.toString())
                    }
                }
            }
        }

        install(ContentNegotiation) {
            json(json)
        }

        install(WebSockets) {
            // как часто слать ping кадры
            pingIntervalMillis = 30_000   // или: pingInterval = 20.seconds (в новых API)
        }

        defaultRequest {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            userAgentProvider?.provide()?.let {
                header(HttpHeaders.UserAgent, it)
            }
        }

        // Таймауты на уровне Ktor
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = 20_000
        }

        appenderList.forEach { it.append(this) }
    }
}
