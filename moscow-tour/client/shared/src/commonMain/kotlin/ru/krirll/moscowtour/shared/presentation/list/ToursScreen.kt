package ru.krirll.moscowtour.shared.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.broken_image
import moscowtour.moscow_tour.client.shared.generated.resources.main
import moscowtour.moscow_tour.client.shared.generated.resources.not_found
import moscowtour.moscow_tour.client.shared.generated.resources.retry
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.search.AppBarWithSearch
import ru.krirll.moscowtour.shared.presentation.state.rememberTopAppBarStateByHolder
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurSource
import ru.krirll.ui.rememberBlurState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToursScreen(component: ToursScreenComponent) {
    val appBarState = rememberTopAppBarStateByHolder(component.topAppBarStateHolder)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(appBarState)
    val error by component.errorCode.collectAsState(null)
    val items by component.items.collectAsState()
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            content = { paddingValues ->
                TourScreenContent(
                    paddingValues,
                    error,
                    items,
                    onRefresh = { component.load() },
                    onShowOverview = { component.showOverview(it) },
                    emptyResource = Res.string.not_found,
                    onLoad = { component.load() }
                )
            },
            appBar = {
                val appName = stringResource(Res.string.main)
                val title = component.search ?: appName
                AppBarWithSearch(
                    title,
                    isDefault = title == appName,
                    onBack = { component.doBack() }
                )
            },
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
fun TourContent(component: ToursScreenComponent, paddingValues: PaddingValues) {
    val error by component.errorCode.collectAsState(null)
    val items by component.items.collectAsState()
    TourScreenContent(
        paddingValues,
        error,
        items,
        onRefresh = { component.load() },
        onShowOverview = { component.showOverview(it) },
        emptyResource = Res.string.not_found,
        onLoad = { component.load() }
    )
}

@Composable
fun TourScreenContent(
    paddingValues: PaddingValues,
    error: String?,
    items: List<Tour>?,
    emptyResource: StringResource,
    onRefresh: () -> Unit,
    onShowOverview: (Long) -> Unit,
    onLoad: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoad()
    }
    if (error != null) {
        ErrorAndRetry(error) {
            onRefresh()
        }
    } else if (items != null) {
        TourInfo(items = items, paddingValues, emptyResource) {
            onShowOverview(it.id)
        }
    } else {
        Loading()
    }
}

@Composable
fun TourInfo(
    items: List<Tour>,
    paddingValues: PaddingValues,
    emptyResource: StringResource,
    onClick: (Tour) -> Unit
) {
    val blur = LocalBlurState.current
    if (items.isNotEmpty()) {
        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier
                .fillMaxSize()
                .applyBlurSource(blur),
            contentPadding = paddingValues,
            state = gridState
        ) {
            items(items) { item ->
                TourItem(
                    tour = item,
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    } else {
        ToursNotFound(emptyResource)
    }
}


@Composable
fun TourItem(
    tour: Tour,
    modifier: Modifier = Modifier,
    onClick: (Tour) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = { onClick(tour) }),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
        ) {
            val painter = rememberImagePainter(
                tour.imagesUrls.firstOrNull() ?: "",
                errorPainter = { painterResource(Res.drawable.broken_image) }
            )

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = tour.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tour.city,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = tour.country,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "${tour.price} ₽",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
fun ErrorAndRetry(errorMsg: String, retry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = errorMsg,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
        Button(onClick = { retry() }) {
            Text(
                text = stringResource(Res.string.retry),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ToursNotFound(emptyResource: StringResource) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(emptyResource),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}
