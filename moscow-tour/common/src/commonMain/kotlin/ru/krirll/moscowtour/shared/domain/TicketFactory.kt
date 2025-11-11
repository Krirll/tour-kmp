package ru.krirll.moscowtour.shared.domain

import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour

interface TicketFactory {
    suspend fun create(
        tour: Tour,
        personData: PersonData,
        requestTime: Long,
        buyTime: Long?
    ): Pair<String, ByteArray>
}
