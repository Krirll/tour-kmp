package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonData(
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val passportSeries: Int,
    val passportNumber: Int,
    val phone: String
)
