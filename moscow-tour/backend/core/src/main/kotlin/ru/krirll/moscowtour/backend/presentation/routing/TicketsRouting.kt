package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.di.RoutingEntryPoint
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.CreateTicketRequest
import ru.krirll.moscowtour.shared.domain.RemoveTicketRequest
import ru.krirll.moscowtour.shared.domain.TicketsRepository

fun Routing.setupTickets(
    routingEntryPoint: RoutingEntryPoint
) {
    get(TicketsRepository.QUERY_ALL) {
        call.respond(
            routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
                .getAll()
                .first()
        )
    }
    delete(TicketsRepository.DELETE) {
        val request = call.receive<RemoveTicketRequest>()
        routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
            .remove(request.ticketId)
        call.respond(HttpStatusCode.OK)
    }
    post(TicketsRepository.CREATE_AND_DOWNLOAD) {
        val request = call.receive<CreateTicketRequest>()
        val result = routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
            .createAndDownload(request.tourId, request.personData, request.time)
        withContext(routingEntryPoint.dispatcherProvider.io) {
            call.response.header(
                HttpHeaders.ContentDisposition,
                "attachment; filename=\"${result.fileName}\""
            )
            call.respondBytes(result.byteArray, ContentType.Application.OctetStream)
        }
    }
}
