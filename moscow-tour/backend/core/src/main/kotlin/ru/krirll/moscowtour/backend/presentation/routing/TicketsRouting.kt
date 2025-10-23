package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.di.RoutingEntryPoint
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.CreateTicketRequest
import ru.krirll.moscowtour.shared.domain.DownloadUrlResponse
import ru.krirll.moscowtour.shared.domain.RemoveTicketRequest
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import java.io.File

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
    post(TicketsRepository.DELETE) {
        val request = call.receive<RemoveTicketRequest>()
        routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
            .remove(request.ticketId)
        call.respond(HttpStatusCode.OK)
    }
    post(TicketsRepository.CREATE) {
        val request = call.receive<CreateTicketRequest>()
        routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
            .create(request.tourId, request.personData)
        call.respond(HttpStatusCode.OK)
    }
    get(TicketsRepository.GET_DOWNLOAD_URL) {
        val params = call.parameters
        val ticketId = params[TicketsRepository.TICKET_ID_ARG]?.toLong()
        if (ticketId == null) {
            return@get call.respond(HttpStatusCode.BadRequest)
        } else {
            val filePath = routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
                .getFilePath(ticketId)
            responseWithFileCheck(routingEntryPoint, filePath) {
                call.respond(DownloadUrlResponse(filePath))
            }
        }
    }
    get(TicketsRepository.DOWNLOAD) {
        val filePath = call.parameters[TicketsRepository.FILE_PATH_ARG]
            ?: return@get call.respond(HttpStatusCode.BadRequest)
        responseWithFileCheck(routingEntryPoint, filePath) { file ->
            call.response.header(
                HttpHeaders.ContentDisposition,
                "attachment; filename=\"${file.name}\""
            )
            call.respondFile(file)
        }
    }
}

private suspend fun RoutingContext.responseWithFileCheck(
    routingEntryPoint: RoutingEntryPoint,
    filePath: String,
    responseCallback: suspend (File) -> Unit
) {
    val file = withContext(routingEntryPoint.dispatcherProvider.io) { File(filePath) }
    val isExists = withContext(routingEntryPoint.dispatcherProvider.io) { file.exists() }
    if (!isExists) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        responseCallback(file)
    }
}
