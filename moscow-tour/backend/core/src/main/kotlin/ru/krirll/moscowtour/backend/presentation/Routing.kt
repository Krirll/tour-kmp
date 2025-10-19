package ru.krirll.moscowtour.backend.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.krirll.http.domain.HttpException
import ru.krirll.moscowtour.backend.di.AuthEntryPoint
import ru.krirll.moscowtour.backend.di.RoutingEntryPoint
import ru.krirll.moscowtour.backend.presentation.routing.setupAuthMethods
import ru.krirll.moscowtour.backend.presentation.routing.setupBaseMethods
import ru.krirll.moscowtour.shared.domain.model.ServerExceptionInfo

val routingLogger: Logger = LoggerFactory.getLogger("routing")

fun Application.configureRouting(
    routingEntryPoint: RoutingEntryPoint,
    authEntryPoint: AuthEntryPoint
) {
    install(StatusPages) {
        exception<HttpException> { call, cause ->
            routingLogger.error("http exception occurred. returns ${cause.httpCode}", cause)
            call.respond(
                status = HttpStatusCode.fromValue(cause.httpCode),
                message = ServerExceptionInfo(cause.message ?: "Неизвестная ошибка")
            )
        }
        exception<Throwable> { call, cause ->
            routingLogger.error("error occurred. returns 500...", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ServerExceptionInfo(cause.message ?: "Неизвестная ошибка")
            )
        }
    }
    routing {
        setupBaseMethods(routingEntryPoint.toursApi)
        setupAuthMethods(routingEntryPoint, authEntryPoint)
    }
}
