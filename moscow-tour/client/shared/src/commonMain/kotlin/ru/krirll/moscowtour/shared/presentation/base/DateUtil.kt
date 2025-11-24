package ru.krirll.moscowtour.shared.presentation.base

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import ru.krirll.moscowtour.shared.domain.normalizeTimestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatDate(timestamp: Long): String {
    val date = Instant
        .fromEpochMilliseconds(timestamp.normalizeTimestamp())
        .toLocalDateTime(TimeZone.UTC)

    val day = date.day.toString().padStart(2, '0')
    val month = date.month.number.toString().padStart(2, '0')
    val year = date.year.toString()

    return "$day.$month.$year"
}
