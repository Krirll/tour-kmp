package ru.krirll.moscowtour.shared.presentation.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class Route : ru.krirll.ui.nav.Route() {

    @Serializable
    data class Videos(val request: String? = null) : Route()

    @Serializable
    data object SearchVideos : Route()

    @Serializable
    data object Saved : Route()

    @Serializable
    data class Loading(
        //todo придумать какое то ограничение, чтоб не перегружать сервак
        //val authRequired: Boolean = isJs,
        val next: Collection<Route> = listOf(default)
    ) : Route()

    @Serializable
    class Overview(val id: Long) : Route() {

        @Serializable
        class Season(val id: Long) : Route()

        @Serializable
        class Episode(val id: Long, val selectedSeasonId: Long) : Route()
    }

    @Serializable
    data object Settings : Route() {

        @Serializable
        data object EditServ : Route()

        @Serializable
        data class Auth(
            //todo придумать какое то ограничение, чтоб не перегружать сервак
            //val required: Boolean = isJs,
            val next: Collection<Route> = listOf(default)
        ) : Route()

        @Serializable
        data object Register : Route()

        @Serializable
        data object EditPassword : Route()
    }

    companion object {
        val default = Videos(null)
    }
}
