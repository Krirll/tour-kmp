package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val ticketId: Long,
    val tourId: Long,
    val tourTitle: String,
    val accountId: Long,
    val date: Long
)
