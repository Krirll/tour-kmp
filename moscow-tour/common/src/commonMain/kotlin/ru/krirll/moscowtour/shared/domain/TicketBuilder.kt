package ru.krirll.moscowtour.shared.domain

import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour

interface TicketBuilder {
    suspend fun build(tour: Tour, personData: PersonData, time: Long)
}
