package ru.krirll.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import ru.krirll.ui.nav.Route
import ru.krirll.ui.theme.getColorScheme

const val USE_EXPERIMENTAL_RAIL = false

@Immutable
data class NavigationEntry<out T : Route>(
    val route: T,
    val title: String,
    val painter: Painter
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.pinnedScrollBehavior(),
    snackbarState: SnackbarHostState? = null,
    colorScheme: ColorScheme = getColorScheme(),
    appBar: @Composable (scroll: TopAppBarScrollBehavior?) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    BaseScreen<Route>(
        modifier,
        scrollBehavior,
        snackbarState,
        colorScheme,
        appBar = appBar,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Route> BaseScreen(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.pinnedScrollBehavior(),
    snackbarState: SnackbarHostState? = null,
    colorScheme: ColorScheme = getColorScheme(),
    currentRoute: T? = null,
    navigationEntries: List<NavigationEntry<T>>? = null,
    onSelectNavigation: ((T) -> Unit)? = null,
    appBar: @Composable (scroll: TopAppBarScrollBehavior?) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    val windowType = currentWindowType()
    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            bottomBar = {
                if (windowType.isCompact || !USE_EXPERIMENTAL_RAIL) {
                    BottomBar(navigationEntries, currentRoute, onSelectNavigation)
                }
            },
            topBar = {
                appBar(scrollBehavior)
            }, content = {
                if (windowType.isExpanded && navigationEntries?.isNotEmpty() == true && USE_EXPERIMENTAL_RAIL) {
                    ExpandedContent(it, navigationEntries, currentRoute, onSelectNavigation, content)
                } else {
                    content(it)
                }
            }, modifier = if (scrollBehavior != null) {
                modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                modifier
            }, snackbarHost = {
                snackbarState?.let { SnackbarHost(it) }
            }, contentWindowInsets = WindowInsets.safeDrawing
        )
    }
}

@Composable
private fun <T : Route> BottomBar(
    navigationEntries: List<NavigationEntry<T>>?,
    currentRoute: T?,
    onSelectNavigation: ((T) -> Unit)?
) {
    navigationEntries?.let {
        NavigationBar {
            it.forEach { entry ->
                NavigationBarItem(
                    currentRoute == entry.route,
                    onClick = { onSelectNavigation?.invoke(entry.route) },
                    icon = {
                        Icon(entry.painter, contentDescription = entry.title)
                    },
                    label = { Text(entry.title) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Route> ExpandedContent(
    values: PaddingValues,
    navigationEntries: List<NavigationEntry<T>>,
    currentRoute: T?,
    onSelectNavigation: ((T) -> Unit)?,
    content: @Composable ((PaddingValues) -> Unit)
) {
    Row(Modifier.fillMaxSize().padding(values)) {
        Spacer(Modifier.width(4.dp))
        NavigationRail {
            navigationEntries.forEach { entry ->
                NavigationRailItem(
                    selected = currentRoute == entry.route,
                    onClick = { onSelectNavigation?.invoke(entry.route) },
                    icon = {
                        Icon(entry.painter, contentDescription = entry.title)
                    },
                    label = { Text(entry.title) }
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        Box(Modifier.weight(1f)) {
            content(PaddingValues(0.dp))
        }
    }
}
