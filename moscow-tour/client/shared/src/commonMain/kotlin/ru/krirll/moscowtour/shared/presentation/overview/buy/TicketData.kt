package ru.krirll.moscowtour.shared.presentation.overview.buy

import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour

internal data class TicketData(
    val tour: Tour,
    val personData: PersonData
)
