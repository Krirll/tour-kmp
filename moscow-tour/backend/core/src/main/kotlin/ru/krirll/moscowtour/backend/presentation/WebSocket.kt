package ru.krirll.moscowtour.backend.presentation

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.krirll.backend.data.TokenVerifier
import ru.krirll.backend.domain.USER_ID_ARG
import ru.krirll.moscowtour.backend.data.BackendRemoteEventHandler
import ru.krirll.moscowtour.backend.data.SessionCache
import ru.krirll.moscowtour.backend.di.WebSocketEntryPoint
import ru.krirll.moscowtour.shared.domain.EventType
import ru.krirll.moscowtour.shared.domain.RemoteEventHandler
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebSocket(
    socketEntryPoint: WebSocketEntryPoint
) {
    install(WebSockets) {
        pingPeriod = (15).seconds
        timeout = (15).seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket(
            EventType.SEARCH,
            socketEntryPoint.searchEventHandler,
            socketEntryPoint.tokenVerifier
        )
        webSocket(
            EventType.SAVED,
            socketEntryPoint.savedEventHandler,
            socketEntryPoint.tokenVerifier
        )
        webSocket(
            EventType.TICKETS,
            socketEntryPoint.ticketsEventHandler,
            socketEntryPoint.tokenVerifier
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun Route.webSocket(
    eventType: String,
    handler: BackendRemoteEventHandler,
    tokenVerifier: TokenVerifier
) {
    val path = when (eventType) {
        EventType.SEARCH -> RemoteEventHandler.SEARCH_CHANGED
        EventType.SAVED -> RemoteEventHandler.SAVED_CHANGED
        EventType.TICKETS -> RemoteEventHandler.TICKETS_CHANGED
        else -> throw IllegalArgumentException("Unsupported type $eventType")
    }
    webSocket(path) {
        val frame = (incoming.receive() as Frame.Text).readText()
        val userId = tokenVerifier.verify(frame).getClaim(USER_ID_ARG).asLong()
        val session = object : SessionCache.Writer {
            override suspend fun writeText(text: String) {
                outgoing.trySend(Frame.Text(text))
            }
        }
        handler.put(userId, session)
        val deferred = CompletableDeferred<Unit?>()
        try {
            outgoing.invokeOnClose { deferred.complete(null) }
            deferred.await()
        } finally {
            handler.remove(userId, session)
        }
    }
}
