package ru.krirll.moscowtour.multiplatform.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.krirll.moscowtour.shared.presentation.overview.season.SeasonsInfo

@Preview
@Composable
fun SeasonsInfoPreview() {
    SeasonsInfo(
        paddingValues = PaddingValues(0.dp),
        seasons = listOf(Season(listOf(), 23, "Lalalal"))
    ) {

    }
}
