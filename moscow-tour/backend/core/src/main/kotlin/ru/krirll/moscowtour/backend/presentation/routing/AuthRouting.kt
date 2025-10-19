package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import ru.krirll.moscowtour.backend.di.AuthEntryPoint
import ru.krirll.moscowtour.backend.di.RoutingEntryPoint
import ru.krirll.moscowtour.backend.presentation.AUTH_JWT_NAME
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.AuthTokenRepository
import ru.krirll.moscowtour.shared.domain.HeathCheck

fun Routing.setupAuthMethods(
    routingEntryPoint: RoutingEntryPoint, authEntryPoint: AuthEntryPoint
) {
    val default = authEntryPoint.authTokenRepositoryFactory.create()
    post(AuthTokenRepository.REGISTER_PATH) {
        call.respond(default.register(call.receive()))
    }
    post(AuthTokenRepository.LOGIN_PATH) {
        call.respond(default.login(call.receive()))
    }
    post(AuthTokenRepository.UPDATE_PATH) {
        call.respond(default.update(call.receive()))
    }
    post(AuthTokenRepository.REVOKE_PATH) {
        default.revoke(call.receive())
        call.respond(HttpStatusCode.OK)
    }
    authenticate(AUTH_JWT_NAME) {

        get("/chech") { call.respond(HttpStatusCode.OK) }
        get(HeathCheck.PATH) { call.respond(HttpStatusCode.OK) }

        post(AuthTokenRepository.CHANGE_PASS_PATH) {
            authEntryPoint.authTokenRepositoryFactory.create(call.obtainAccountId())
                .changePassword(call.receive())
            call.respond(HttpStatusCode.OK)
        }
        setupSearch(routingEntryPoint.searchFactory)
        setupSavedTours(routingEntryPoint.savedToursFactory)
        setupTickets(routingEntryPoint.ticketsFactory)
    }
}
