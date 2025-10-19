package ru.krirll.moscowtour.shared.presentation.nav

import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.isJs

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
        val authRequired: Boolean = isJs,
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
            val required: Boolean = isJs,
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
