package ru.krirll.moscowtour.shared.presentation

import androidx.compose.runtime.Composable
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.account
import moscowtour.moscow_tour.client.shared.generated.resources.home
import moscowtour.moscow_tour.client.shared.generated.resources.main
import moscowtour.moscow_tour.client.shared.generated.resources.saved_tours
import moscowtour.moscow_tour.client.shared.generated.resources.search
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
            stringResource(Res.string.main),
            painterResource(Res.drawable.home)
        ),
        NavigationEntry(
            Route.Saved,
            stringResource(Res.string.saved_tours),
            painterResource(Res.drawable.star)
        ),
        NavigationEntry(
            Route.SearchTours,
            stringResource(Res.string.search),
            painterResource(Res.drawable.search)
        ),
        NavigationEntry(
            Route.Account,
            stringResource(Res.string.account),
            painterResource(Res.drawable.account)
        )
    )
    return list
}
