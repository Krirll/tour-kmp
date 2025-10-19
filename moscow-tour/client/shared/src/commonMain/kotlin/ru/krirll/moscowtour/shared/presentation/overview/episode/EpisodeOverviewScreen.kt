package ru.krirll.moscowtour.shared.presentation.overview.episode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moscowtour.moscow_tour.client.shared.generated.resources.Res
import moscowtour.moscow_tour.client.shared.generated.resources.back
import moscowtour.moscow_tour.client.shared.generated.resources.check
import moscowtour.moscow_tour.client.shared.generated.resources.episodes_overview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.krirll.moscowtour.shared.presentation.BaseScreen
import ru.krirll.moscowtour.shared.presentation.applyColumnPadding
import ru.krirll.moscowtour.shared.presentation.base.ExpressiveLazyColumn
import ru.krirll.moscowtour.shared.presentation.clipboardUrl
import ru.krirll.moscowtour.shared.presentation.getClipboardText
import ru.krirll.moscowtour.shared.presentation.list.ErrorAndRetry
import ru.krirll.moscowtour.shared.presentation.list.Loading
import ru.krirll.ui.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesOverviewScreen(component: EpisodeOverviewComponent) {
    val launcher = component.videoLauncher
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val recentlyWatchedEvent = component.recentlyWatchedEvent.collectAsStateWithLifecycle(component, null)
    LaunchedEffect(recentlyWatchedEvent) { component.tryToLoadLinks() }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    BaseScreen(
        snackbarState = snackbarState,
        content = { padding ->
            val errorCode by component.errorCode.collectAsState(null)
            val links by component.links.collectAsState(null)
            val clipboardText = getClipboardText()
            EpisodesOverviewList(
                onLaunch = {
                    if (!it.isWatched) {
                        scope.launch {
                            launcher.launch(it.url)
                        }
                    }
                    component.toggleWatched(it.episode)
                },
                paddingValues = padding,
                errorCode = errorCode,
                links = links,
                loadLinks = { component.tryToLoadLinks() },
                onClipboardAction = {
                    snackbarState.clipboardUrl(it.url, scope, clipboardManager, clipboardText)
                }
            )
        },
        appBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.episodes_overview))
                },
                navigationIcon = {
                    IconButton(onClick = { component.doBack() }) {
                        Icon(painterResource(Res.drawable.back), null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun EpisodesOverviewList(
    errorCode: String?,
    links: VideoLinksResponse?,
    paddingValues: PaddingValues,
    onLaunch: (Episode) -> Unit,
    onClipboardAction: (Episode) -> Unit,
    loadLinks: () -> Unit
) {
    if (errorCode != null) {
        ErrorAndRetry(errorMsg = errorCode) {
            loadLinks()
        }
    } else if (links != null) {
        OverviewLinks(paddingValues, links, onLaunch, onClipboardAction)
    } else {
        Loading()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OverviewLinks(
    paddingValues: PaddingValues,
    links: VideoLinksResponse?,
    onLaunch: (Episode) -> Unit,
    onClipboardAction: (Episode) -> Unit
) {
    val episodes = links?.seasons?.firstOrNull()?.episodes
    if (episodes != null) {
        ExpressiveLazyColumn(
            modifier = Modifier.applyColumnPadding(paddingValues),
            contentPadding = paddingValues,
            items = episodes.mapIndexed { episodeId, it ->
                {
                    EpisodeInfo(onLaunch, it, onClipboardAction, episodeId)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodeInfo(
    onLaunch: (Episode) -> Unit,
    ep: Episode,
    onClipboardAction: (Episode) -> Unit,
    episodeId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onLaunch(ep) },
                onLongClick = { onClipboardAction(ep) }
            )
            .padding(16.dp)
    ) {
        Text(
            text = ep.title ?: episodeId.toString(),
            modifier = Modifier.weight(1f)
        )
        if (ep.isWatched) {
            Icon(
                painterResource(Res.drawable.check),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 4.dp)
            )
        }
    }
}
