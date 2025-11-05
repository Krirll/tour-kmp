package ru.krirll.moscowtour.shared.domain

import ru.krirll.moscowtour.shared.domain.model.Tour

const val DYNAMIC_PREFIX = "dynamic"

interface ToursApi {

    suspend fun fetchTours(): List<Tour>

    companion object {
        const val TOURS_PATH = "tours"
        const val TOUR_IMAGES = "${TOURS_PATH}/images"
        const val IMAGE_NAME_ARG = "imageName"
    }
}
