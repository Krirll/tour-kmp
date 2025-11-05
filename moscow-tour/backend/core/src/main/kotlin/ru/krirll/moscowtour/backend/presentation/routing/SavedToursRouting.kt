package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import ru.krirll.moscowtour.backend.di.factory.SavedToursRepositoryFactory
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.RemoveRequest
import ru.krirll.moscowtour.shared.domain.SavedToursRepository
import ru.krirll.moscowtour.shared.domain.model.Tour

fun Route.setupSavedTours(
    savedToursFactory: SavedToursRepositoryFactory
) {
    post(SavedToursRepository.REMOVE) {
        val removeRequest = call.receive<RemoveRequest>()
        savedToursFactory.create(call.obtainAccountId()).remove(removeRequest.tourId)
        call.respond(HttpStatusCode.OK)
    }
    post(SavedToursRepository.SAVE) {
        val saveRequest = call.receive<Tour>()
        savedToursFactory.create(call.obtainAccountId()).save(saveRequest)
        call.respond(HttpStatusCode.OK)
    }
    get(SavedToursRepository.QUERY_ALL) {
        val saved = savedToursFactory.create(call.obtainAccountId())
            .getAll()
            .first()
        call.respond(listOf(saved))
    }
}
