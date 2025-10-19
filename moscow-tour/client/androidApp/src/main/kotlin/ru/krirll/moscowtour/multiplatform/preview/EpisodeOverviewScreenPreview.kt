package ru.krirll.moscowtour.multiplatform.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.krirll.moscowtour.shared.presentation.overview.episode.EpisodesOverviewList

@Preview
@Composable
fun EpisodeOverviewScreenPreview() {
    EpisodesOverviewList(
        null,
        VideoLinksResponse.getStub(isSerial = true, watched = true),
        PaddingValues(0.dp),
        {},
        {},
        {}
    )
}

@Preview
@Composable
fun EpisodeOverviewScreenPreviewSomeLong() {
    EpisodesOverviewList(
        null,
        VideoLinksResponse.getStub(isSerial = true, watched = true, "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"),
        PaddingValues(0.dp),
        {},
        {},
        {}
    )
}
