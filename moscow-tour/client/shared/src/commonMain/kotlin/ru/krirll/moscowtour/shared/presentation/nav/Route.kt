package ru.krirll.moscowtour.shared.presentation.nav

import kotlinx.serialization.Serializable
import ru.krirll.moscowtour.shared.domain.model.PersonData
import ru.krirll.moscowtour.shared.domain.model.Tour

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
        data class FullscreenImages(val startIndex: Int, val images: List<String>) : Route()

        @Serializable
        data class PersonScreen(val tour: Tour) : Route()

        @Serializable
        data class BuyTicket(val tour: Tour, val personData: PersonData) : Route()
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
