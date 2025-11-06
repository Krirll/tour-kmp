package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.krirll.moscowtour.backend.data.document.TicketBuilderImpl
import ru.krirll.moscowtour.backend.di.RoutingEntryPoint
import ru.krirll.moscowtour.backend.domain.normalizeTimestamp
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.CreateTicketRequest
import ru.krirll.moscowtour.shared.domain.RemoveTicketRequest
import ru.krirll.moscowtour.shared.domain.TicketsRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    get(TicketsRepository.CREATE_AND_DOWNLOAD) {
        val request = call.receive<CreateTicketRequest>()
        routingEntryPoint.ticketsFactory.create(call.obtainAccountId())
            .createAndDownload(request.tourId, request.personData, request.time)
        withContext(routingEntryPoint.dispatcherProvider.io) {
            val file = File(TicketBuilderImpl.BASE_DIR_PATH, "${request.time}")
            try {
                val formatter = SimpleDateFormat("dd.MM.yyyy_HH-mm", Locale.of("ru", "RU"))
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    "attachment; filename=\"ticket-${
                        formatter.format(Date(request.time.normalizeTimestamp()))
                    }.docx\""
                )
                call.respondFile(file)
            } finally {
                file.delete()
            }
        }
    }
}
