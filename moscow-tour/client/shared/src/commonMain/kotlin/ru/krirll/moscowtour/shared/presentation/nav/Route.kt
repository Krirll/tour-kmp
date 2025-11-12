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
    data class Loading(
        //todo придумать какое то ограничение, чтоб не перегружать сервак
        val next: Collection<Route> = listOf(default)
    ) : Route()

    @Serializable
    data class Overview(val id: Long) : Route()

    @Serializable
    data object Settings : Route() {

        @Serializable
        data class Auth(
            //todo придумать какое то ограничение, чтоб не перегружать сервак
            val next: Collection<Route> = listOf(default)
        ) : Route()

        @Serializable
        data object Register : Route()

        @Serializable
        data object EditPassword : Route()
    }

    companion object {
        val default = Tours(null)
    }
}
