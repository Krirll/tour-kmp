package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedTour(
    val savedTourId: Long,
    val tour: Tour
)
