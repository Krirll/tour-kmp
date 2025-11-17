package ru.krirll.moscowtour.shared.presentation.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class Route : ru.krirll.ui.nav.Route() {

    @Serializable
    data class Tours(val request: String? = null) : Route()

    @Serializable
    data object SearchTours : Route()

    @Serializable
    data object Saved : Route()

    @Serializable
    data class Loading(val next: Collection<Route> = listOf(default)) : Route()

    @Serializable
    data class Overview(val id: Long) : Route() {

        @Serializable
        data class PersonScreen(val id: Long) : Route() //todo передавать тур а не айди

        @Serializable
        data class BuyTicket(val id: Long) : Route() //todo передавать данные юзера и тура а не айди
    }

    @Serializable
    data object Account : Route() {

        @Serializable
        data class Auth(val next: Collection<Route> = listOf(default)) : Route()

        @Serializable
        data object Register : Route()

        @Serializable
        data object EditPassword : Route()

        @Serializable
        data object Tickets : Route()
    }

    companion object {
        val default = Tours(null)
    }
}
