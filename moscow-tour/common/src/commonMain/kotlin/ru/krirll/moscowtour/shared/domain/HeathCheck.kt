package ru.krirll.moscowtour.shared.domain

interface HeathCheck {
    suspend fun check()

    companion object {
        const val PATH = "healthCheck"
    }
}
