package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class File(
    val proPlus: Boolean?,
    val quality: Int?,
    val url: String
)
