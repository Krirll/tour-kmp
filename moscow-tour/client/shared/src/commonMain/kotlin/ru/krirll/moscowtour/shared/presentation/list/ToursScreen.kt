package ru.krirll.moscowtour.shared.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.not_found
import moscowtour.moscow_tour.client.shared.generated.resources.recommendations
import moscowtour.moscow_tour.client.shared.generated.resources.retry
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.search.AppBarWithSearch
import ru.krirll.moscowtour.shared.presentation.state.rememberTopAppBarStateByHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToursScreen(component: ToursScreenComponent) {
    val appBarState = rememberTopAppBarStateByHolder(component.topAppBarStateHolder)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(appBarState)
    val error by component.errorCode.collectAsState(null)
    val items by component.items.collectAsState()
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
            val appName = stringResource(Res.string.recommendations)
            val title = component.search ?: appName
            AppBarWithSearch(
                scrollBehavior,
                title,
                isDefault = title == appName,
                onBack = { component.doBack() }
            )
        },
        scrollBehavior = scrollBehavior
    )
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
    load: Boolean = true,
    onClick: (item: Tour) -> Unit
) {
    if (items.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 125.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .focusGroup()
        ) {
            items(items) { item ->
                Poster(
                    imageUrl = item.imagesUrls?.firstOrNull() ?: "", //todo дефолтную картинку надо
                    label = item.title,
                    load = load,
                    onClick = { onClick(item) },
                )
            }
        }
    } else {
        VideosNotFound(emptyResource)
    }
}

@Composable
fun Poster(
    imageUrl: String,
    label: String,
    load: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    var focusState by remember { mutableStateOf<FocusState?>(null) }
    val isFocused = focusState?.isFocused == true

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = sizeImage.height.toFloat() / 3,
        endY = sizeImage.height.toFloat()
    )
    Box(
        modifier = modifier
            .aspectRatio(0.8f)
            .onFocusEvent { focusState = it }
            .border(
                width = if (isFocused) 6.dp else 0.dp,
                color = if (isFocused) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
    ) {
        //todo тут надо показывать какую нибудь дефолтную хуйню вместо красного цвета если картинки нет
        val painter = if (load) rememberImagePainter(imageUrl) else ColorPainter(Color.Red)
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .onGloballyPositioned {
                    sizeImage = it.size
                }.matchParentSize()
        )
        Box(modifier = Modifier.matchParentSize().background(gradient))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
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
fun VideosNotFound(emptyResource: StringResource) {
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

@Composable
fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
