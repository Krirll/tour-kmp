package ru.krirll.moscowtour.shared.presentation

import androidx.compose.runtime.Composable
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.home
import moscowtour.moscow_tour.client.shared.generated.resources.recommendations
import moscowtour.moscow_tour.client.shared.generated.resources.saved_movies
import moscowtour.moscow_tour.client.shared.generated.resources.search
import moscowtour.moscow_tour.client.shared.generated.resources.settings
import moscowtour.moscow_tour.client.shared.generated.resources.star
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.ui.NavigationEntry
import ru.krirll.moscowtour.shared.presentation.nav.Route

@Composable
fun createNavigationList(): List<NavigationEntry<Route>> {
    val list = listOf(
        NavigationEntry(
            Route.default,
            stringResource(Res.string.recommendations),
            painterResource(Res.drawable.home)
        ),
        NavigationEntry(
            Route.Saved,
            stringResource(Res.string.saved_movies),
            painterResource(Res.drawable.star)
        ),
        NavigationEntry(
            Route.SearchTours,
            stringResource(Res.string.search),
            painterResource(Res.drawable.search)
        ),
        NavigationEntry(
            Route.Settings,
            stringResource(Res.string.settings),
            painterResource(Res.drawable.settings)
        )
    )
    return list
}
