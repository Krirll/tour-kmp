package ru.krirll.moscowtour.backend.domain

internal fun Long.normalizeTimestamp(): Long {
    return if (this < 100_000_000_000L) {
        this * 1000
    } else {
        this
    }
}
