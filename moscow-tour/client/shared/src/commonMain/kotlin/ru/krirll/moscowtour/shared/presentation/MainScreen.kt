package ru.krirll.moscowtour.shared.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.krirll.ui.BaseScreen
import ru.krirll.ui.USE_EXPERIMENTAL_RAIL
import ru.krirll.ui.currentWindowType
import ru.krirll.ui.isCompact
import ru.krirll.ui.isExpanded
import ru.krirll.moscowtour.shared.presentation.list.TourContent
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.saved.SavedMovieContent
import ru.krirll.moscowtour.shared.presentation.search.SearchAppBar
import ru.krirll.moscowtour.shared.presentation.search.SearchVideoContent
import ru.krirll.moscowtour.shared.presentation.settings.SettingsContent

@Composable
fun MainScreen(
    route: Route,
    child: Child,
    rootComponent: RootComponent
) {
    if (child is Child.SearchChild) {
        SearchScreenInternal(child, route, rootComponent)
    } else {
        MainScreenInternal(route, child, rootComponent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenInternal(
    child: Child.SearchChild,
    route: Route,
    rootComponent: RootComponent,
) {
    val component = child.component
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                component.requestCache,
                TextRange(component.requestCache.length)
            )
        )
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val requester by remember { mutableStateOf(FocusRequester()) }
    val windowType = currentWindowType()
    val searchAppBar: (@Composable () -> Unit) = {
        SearchAppBar(
            textFieldValue = textFieldValue,
            onDone = { component.done() },
            onValueChange = {
                textFieldValue = it
                component.onValueChange(it.text)
            },
            focusRequester = requester,
            scrollBehavior = scrollBehavior
        )
    }
    BaseScreen(
        appBar = {
            if (windowType.isCompact || !USE_EXPERIMENTAL_RAIL) searchAppBar()
        },
        currentRoute = route,
        onSelectNavigation = { rootComponent.nav(it, true) },
        navigationEntries = createNavigationList(),
        colorScheme = getColorScheme(),
        scrollBehavior = scrollBehavior
    ) {
        val content: @Composable () -> Unit = {
            SearchVideoContent(child.component, it, requester) {
                textFieldValue = it
            }
        }
        if (windowType.isExpanded && USE_EXPERIMENTAL_RAIL) {
            Column {
                searchAppBar()
                content()
            }
        } else {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenInternal(route: Route, child: Child, rootComponent: RootComponent) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navList = createNavigationList()
    val windowType = currentWindowType()
    BaseScreen(
        appBar = {
            if (windowType.isCompact || !USE_EXPERIMENTAL_RAIL) {
                navList.firstOrNull { it.route == route }?.title?.let {
                    LargeTopAppBar(title = { Text(it) }, scrollBehavior = scrollBehavior)
                }
            }
        },
        currentRoute = route,
        onSelectNavigation = { rootComponent.navReplace(it) },
        navigationEntries = navList,
        colorScheme = getColorScheme(),
        scrollBehavior = scrollBehavior
    ) { padding ->
        when (child) {
            is Child.ToursChild -> TourContent(child.component, padding)
            is Child.SettingsChild -> SettingsContent(child.component, padding)
            is Child.SavedToursChild -> SavedMovieContent(child.component, padding)
            else -> {}
        }
    }
}
