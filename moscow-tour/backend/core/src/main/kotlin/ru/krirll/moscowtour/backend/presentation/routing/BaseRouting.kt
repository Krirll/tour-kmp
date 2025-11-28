package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.shared.di.factory.DispatcherProvider
import ru.krirll.moscowtour.shared.domain.ToursApi
import java.io.File

fun Routing.setupBaseMethods(api: ToursApi, dispatcherProvider: DispatcherProvider) {
    get(ToursApi.TOURS_PATH) {
        val p = call.parameters
        call.respond(api.fetchTours(p[ToursApi.SEARCH_ARG]))
    }
    get(ToursApi.TOUR_IMAGES) {
        val filename = call.parameters[ToursApi.IMAGE_NAME_ARG]
            ?: return@get call.respond(HttpStatusCode.BadRequest)
        val file = withContext(dispatcherProvider.io) {
            File("/usr/local/app/files/images/$filename")
        }

        val isExists = withContext(dispatcherProvider.io) { file.exists() }
        if (!isExists) return@get call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
    get("/") {
        call.respond(HttpStatusCode.NotFound)
    }
}
