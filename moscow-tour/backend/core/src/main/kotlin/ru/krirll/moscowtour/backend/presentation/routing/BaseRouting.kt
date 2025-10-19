package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ru.krirll.moscowtour.shared.domain.ToursApi

fun Routing.setupBaseMethods(api: ToursApi) {
    get(ToursApi.TOURS_PATH) {
        call.respond(api.fetchTours())
    }
    get("/") {
        call.respond(HttpStatusCode.NotFound)
    }
}
