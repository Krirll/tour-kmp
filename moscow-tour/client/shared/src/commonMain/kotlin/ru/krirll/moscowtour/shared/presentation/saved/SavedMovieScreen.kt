package ru.krirll.moscowtour.shared.presentation.saved

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.saved_tours_not_found
import ru.krirll.moscowtour.shared.presentation.list.TourScreenContent

@Composable
fun SavedMovieContent(comp: SavedToursScreenComponent, paddingValues: PaddingValues) {
    val error by comp.errorMsg.collectAsState(null)
    val items by comp.all.collectAsState(null)
    TourScreenContent(
        paddingValues,
        error,
        items,
        onRefresh = { comp.load() },
        onShowOverview = { comp.showOverview(it) },
        emptyResource = Res.string.saved_tours_not_found,
        onLoad = { comp.load() }
    )
}
