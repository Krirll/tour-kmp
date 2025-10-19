package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import ru.krirll.moscowtour.backend.di.factory.TicketsRepositoryFactory
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.RemoveTicketRequest
import ru.krirll.moscowtour.shared.domain.TicketsRepository

fun Routing.setupTickets(
    ticketsFactory: TicketsRepositoryFactory
) {
    get(TicketsRepository.QUERY_ALL) {
        call.respond(
            ticketsFactory.create(call.obtainAccountId())
                .getAll()
                .first()
        )
    }
    post(TicketsRepository.DELETE) {
        val request = call.receive<RemoveTicketRequest>()
        ticketsFactory.create(call.obtainAccountId())
            .remove(request.ticketId)
        call.respond(HttpStatusCode.OK)
    }
}
