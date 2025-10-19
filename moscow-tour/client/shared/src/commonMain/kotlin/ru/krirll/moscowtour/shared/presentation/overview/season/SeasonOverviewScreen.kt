package ru.krirll.moscowtour.shared.presentation.overview.season

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.seasons_overview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.ExpressiveLazyColumn
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.list.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonOverviewScreen(component: SeasonOverviewComponent) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    LaunchedEffect(null) { component.load() }
    BaseScreen(
        content = {
            val seasons = component.season.collectAsState(null)
            val err = component.errorMessage.collectAsState(null)
            if (err.value != null) {
                ErrorAndRetry(err.value!!) {
                    component.load()
                }
            } else if (seasons.value != null) {
                SeasonsInfo(
                    seasons.value!!, it
                ) { seasonId -> component.showEpisode(seasonId) }
            } else {
                Loading()
            }
        }, appBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.seasons_overview))
                }, navigationIcon = {
                    IconButton(onClick = { component.doBack() }) {
                        Icon(painterResource(Res.drawable.back), null)
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, scrollBehavior = scrollBehavior
    )
}

@Composable
fun SeasonsInfo(
    seasons: List<Season>, paddingValues: PaddingValues, onClick: (Long) -> Unit
) {
    ExpressiveLazyColumn(
        Modifier.applyColumnPadding(paddingValues).fillMaxWidth(),
        contentPadding = paddingValues,
        seasons.map {
            {
                SeasonInfo(titleText = it.title) { onClick(it.id) }
            }
        }
    )
}

@Composable
fun SeasonInfo(titleText: String, onClick: () -> Unit) {
    Text(titleText, modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp))
}
