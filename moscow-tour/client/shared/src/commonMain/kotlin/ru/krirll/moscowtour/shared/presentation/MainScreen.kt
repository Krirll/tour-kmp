package ru.krirll.moscowtour.shared.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.krirll.moscowtour.shared.presentation.account.AccountContent
import ru.krirll.moscowtour.shared.presentation.list.TourContent
import ru.krirll.moscowtour.shared.presentation.nav.Child
import ru.krirll.moscowtour.shared.presentation.nav.Route
import ru.krirll.moscowtour.shared.presentation.saved.SavedToursContent
import ru.krirll.moscowtour.shared.presentation.search.SearchAppBar
import ru.krirll.moscowtour.shared.presentation.search.SearchTourContent
import ru.krirll.ui.BaseScreen
import ru.krirll.ui.LocalBlurState
import ru.krirll.ui.applyBlurEffect
import ru.krirll.ui.currentWindowType
import ru.krirll.ui.isCompact
import ru.krirll.ui.isExpanded
import ru.krirll.ui.rememberBlurState
import ru.krirll.ui.theme.ComponentDefaults

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
            focusRequester = requester
        )
    }
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            appBar = {
                if (windowType.isCompact) searchAppBar()
            },
            currentRoute = route,
            onSelectNavigation = { rootComponent.nav(it, true) },
            navigationEntries = createNavigationList(),
            colorScheme = getColorScheme(),
            scrollBehavior = scrollBehavior
        ) {
            val content: @Composable () -> Unit = {
                SearchTourContent(child.component, it, requester) {
                    textFieldValue = it
                }
            }
            if (windowType.isExpanded) {
                Column {
                    searchAppBar()
                    content()
                }
            } else {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenInternal(route: Route, child: Child, rootComponent: RootComponent) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navList = createNavigationList()
    val windowType = currentWindowType()
    val blurState = rememberBlurState()
    CompositionLocalProvider(LocalBlurState provides blurState) {
        BaseScreen(
            appBar = {
                if (windowType.isCompact) {
                    navList.firstOrNull { it.route == route }?.title?.let {
                        LargeTopAppBar(
                            title = { Text(it) },
                            scrollBehavior = scrollBehavior,
                            modifier = Modifier.applyBlurEffect(blurState),
                            colors = ComponentDefaults.topAppBarColors()
                        )
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
                is Child.AccountChild -> AccountContent(child.component, padding)
                is Child.SavedToursChild -> SavedToursContent(child.component, padding)
                else -> {}
            }
        }
    }
}
