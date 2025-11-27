package ru.krirll.moscowtour.backend.presentation.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import ru.krirll.moscowtour.backend.di.factory.SearchRepositoryFactory
import ru.krirll.moscowtour.backend.presentation.obtainAccountId
import ru.krirll.moscowtour.shared.domain.SearchDeleteRequest
import ru.krirll.moscowtour.shared.domain.SearchQueryRequest
import ru.krirll.moscowtour.shared.domain.SearchQueryResponse
import ru.krirll.moscowtour.shared.domain.SearchRepository

fun Route.setupSearch(searchFactory: SearchRepositoryFactory) {
    get(SearchRepository.SEARCH) {
        val p = call.parameters
        val userId = call.obtainAccountId()
        val list = searchFactory.create(userId).search(p[SearchRepository.QUERY_ARG] ?: "")
        call.respond(SearchQueryResponse(list))
    }
    post(SearchRepository.ADD_TO_SEARCH) {
        val userId = call.obtainAccountId()
        val request = call.receive<SearchQueryRequest>()
        searchFactory.create(userId).addToSearch(request.query)
        call.respond(HttpStatusCode.OK)
    }
    delete(SearchRepository.CLEAR) {
        val userId = call.obtainAccountId()
        val request = call.receive<SearchDeleteRequest>()
        val repo = searchFactory.create(userId)
        if (request.query == null) {
            repo.clearAll()
        } else {
            repo.delete(request.query!!)
        }
        call.respond(HttpStatusCode.OK)
    }
}
