package ru.krirll.moscowtour.shared.presentation.saved

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.saved_movies_not_found
import ru.krirll.moscowtour.shared.presentation.list.VideoScreenContent

@Composable
fun SavedMovieContent(comp: SavedMovieScreenComponent, paddingValues: PaddingValues) {
    val error by comp.errorMsg.collectAsState(null)
    val items by comp.all.collectAsState(null)
    VideoScreenContent(
        paddingValues,
        error,
        items,
        onRefresh = { comp.load() },
        onShowOverview = { comp.showOverview(it) },
        emptyResource = Res.string.saved_movies_not_found,
        onLoad = { comp.load() }
    )
}
