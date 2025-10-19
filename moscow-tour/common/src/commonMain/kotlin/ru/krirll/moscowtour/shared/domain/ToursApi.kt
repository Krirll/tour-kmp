package ru.krirll.moscowtour.shared.domain

import ru.krirll.moscowtour.shared.domain.model.Tour

const val DYNAMIC_PREFIX = "dynamic"

interface ToursApi {

    suspend fun fetchTours(): List<Tour>

    companion object {
        const val SEARCH_ARG = "search"
        const val TOURS_PATH = "tours"
    }
}
