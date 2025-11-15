package ru.krirll.moscowtour.shared.presentation.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.broken_image
import moscowtour.moscow_tour.client.shared.generated.resources.buy_ticket
import moscowtour.moscow_tour.client.shared.generated.resources.copy
import moscowtour.moscow_tour.client.shared.generated.resources.desc
import moscowtour.moscow_tour.client.shared.generated.resources.more
import moscowtour.moscow_tour.client.shared.generated.resources.share
import moscowtour.moscow_tour.client.shared.generated.resources.star_checked
import moscowtour.moscow_tour.client.shared.generated.resources.star_unchecked
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.domain.model.Tour
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.asColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.Loading
import ru.krirll.moscowtour.shared.presentation.clipboardUrl
import ru.krirll.moscowtour.shared.presentation.getClipboardText
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(component: OverviewComponent) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val errorState by component.errorCode.collectAsState(initial = null)
    val details by component.details.collectAsState()
    val snackbarState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    BaseScreen(
        appBar = { OverviewAppBar(details, snackbarState, component, scrollBehavior) },
        scrollBehavior = scrollBehavior,
        snackbarState = snackbarState,
        content = {
            when {
                errorState != null -> ErrorAndRetry(
                    errorMsg = errorState!!
                ) { component.loadIfNeeded() }

                details != null -> DetailsInfo(
                    details = details!!,
                    paddingValues = it,
                    onBuyClicked = {
                        details?.let { d ->
                            scope.launch {
                                //todo переделать на покупку билета
                                //component.movieUrl?.let { component.videoLauncher.launch(it) }
                            }
                        }
                    }
                )

                else -> Loading()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewAppBar(
    details: Tour?,
    snackbarHostState: SnackbarHostState,
    component: OverviewComponent,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val isSaved by component.isSaved.collectAsState(null)

    TopAppBar(
        title = {
            details?.title?.let {
                Text(
                    it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { component.doBack() }) {
                Icon(painterResource(Res.drawable.back), contentDescription = null)
            }
        },
        actions = {
            details?.let {
                if (component.shareManager.canShare()) {
                    IconButton(onClick = { component.shareManager.shareDetails(it) }) {
                        Icon(
                            painterResource(Res.drawable.share),
                            contentDescription = stringResource(Res.string.share)
                        )
                    }
                }
                isSaved?.let { saved ->
                    IconButton(onClick = if (saved) component::remove else component::save) {
                        if (saved) {
                            Icon(painterResource(Res.drawable.star_checked), null)
                        } else {
                            Icon(painterResource(Res.drawable.star_unchecked), null)
                        }
                    }
                }
                //todo генерировать ссылку чтоб можно было поделиться
                AdditionalAppBarMenu(snackbarHostState, "component.movieUrl" as String?)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Suppress("DEPRECATION")
@Composable
private fun AdditionalAppBarMenu(snack: SnackbarHostState, url: String?) {
    url ?: return
    var expanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(painterResource(Res.drawable.more), null)
        }
        val clipboardText = getClipboardText()
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.copy)) },
                onClick = {
                    snack.clipboardUrl(url, scope, clipboardManager, clipboardText)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun DetailsInfo(
    details: Tour,
    paddingValues: PaddingValues,
    onBuyClicked: () -> Unit
) {
    val showDetails = rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.applyColumnPadding(paddingValues),
        contentPadding = paddingValues.asColumnPadding()
    ) {
        item { ImageCarousel(details.imagesUrls) }
        item { OverviewDescription(details, showDetails) }
        item {
            Button(
                onClick = onBuyClicked,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) { Text(stringResource(Res.string.buy_ticket)) }
        }
    }
}

@Composable
private fun OverviewDescription(
    details: Tour,
    showDetails: MutableState<Boolean>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Text(
            text = stringResource(Res.string.desc),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = details.description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (showDetails.value) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

//todo сделать карусель но на весь экран (чтоб можно было растягивать и листать)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    images: List<String>
) {
    val pagerState = rememberPagerState { images.size }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberImagePainter(
                        url = images[page],
                        errorPainter = { painterResource(Res.drawable.broken_image) }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8))
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(images.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(if (selected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) Color.White
                            else Color.LightGray.copy(alpha = 0.5f)
                        )
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
            }
        }
    }
}
