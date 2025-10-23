package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val ticketId: Long,
    val tour: Tour,
    val accountId: Long,
    val date: Long
)
